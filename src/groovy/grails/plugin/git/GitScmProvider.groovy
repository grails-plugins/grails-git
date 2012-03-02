package grails.plugin.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepository
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.console.ConsoleCredentialsProvider
import org.eclipse.jgit.awtui.AwtCredentialsProvider


/**
 * <p>This is the git provider for integration with the Release plugin.
 * Each instance is designed to work with a single Grails project and
 * a single local git repository. An optional remote repository can also
 * be configured. The remote repository does not have to exist when the
 * provider is first instantiated, but then the
 * {@link importIntoRepo(String, String)} method must be called to ensure
 * it exists.</p>
 * <p>If <code>importIntoRepo(String, String)</code> is not called, then
 * the base directory must be an existing git repository.</p>
 */
class GitScmProvider {
    /** A hard-coded set of ignores. */
    static final String GIT_IGNORES = """\
            *.iws
            .settings
            grails-*.zip
            plugin.xml
            stacktrace.log
            target
            tmp
            web-app/WEB-INF/classes
            """.stripIndent()

    File baseDir
    def credentials
    def repository
    def interactive

    /**
     * Creates a new provider instance.
     * @param baseDir The root directory of the Grails project that will be
     * managed by this provider instance.
     * @param interactive An object that is used to interact with the user.
     * Should have an 'out' property that resembles a print output stream and
     * an 'askUser' method that displays a message to the user, allows him or
     * her to enter some text, and then returns that entered text.
     */
    GitScmProvider(baseDir, interactive) {
        this.baseDir = baseDir as File
        this.interactive = interactive
    }

    /**
     * Set the authentication credentials, i.e. username and password, for
     * the remote git repository that this provider will be working against.
     * You do not need to call this if there is no remote repository or the
     * remote does not require authentication.
     */
    void auth(String username, String password) {
        credentials = [username, password]
    }

    /**
     * Imports the current Grails project source into a Git repository. If a
     * remote URL is configured, that is added as a remote to the local git
     * repo. Otherwise you don't get any remotes by default.
     * @param hostUrl The remote repository URL to import the source into. This
     * URL should be specific to the project, so it would normally include the
     * project name as the last path element.
     * @param msg Optional message to use for the initial commit of the source
     * code to the repository. If this isn't provided, some default text is
     * used.
     * @return The newly created newly created jgit repository instance.
     * @throws Exception If the git repository could not be initialised for
     * any reason.
     */
    def importIntoRepo(String hostUrl, String msg = "") {
        def command = Git.init()
        command.directory = baseDir

        if (isManaged()) {
            interactive.out.println "ERROR: Project is already managed by git."
            return
        }
        else {
            repository = command.call().repository
        }
        interactive.out.println "Initialised empty git repository for the project."

        // Set up the gitignore file.
        new File(grailsSettings.baseDir, ".gitignore").text = GIT_IGNORES

        // Now commit the files that aren't ignored to the repository.
        def git = new Git(repository)
        git.add().addFilepattern(".").call()
        git.commit().setMessage(msg ?: "Initial commit of Grails project source.").call()
        interactive.out.println "Committed initial code to the git repository."

        if (hostUrl) {
            // Set up the host URL as the 'origin' remote.
            def config = repository.config
            config.load()

            def originConfig = new RemoteConfig(config, "origin")
            originConfig.addURI(new URIish(scmUrl))
            originConfig.update(config)
            config.save()

            // Push!
            git = new Git(repository)
            def pushCmd = git.push()
            pushCmd.remote = "origin"
            pushCmd.refSpecs = [new RefSpec("master")]
            if (credentials) pushCmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(*credentials))
            pushCmd.call()
        }

        return repository
    }

    /**
     * Determines whether the given file or directory is managed by the source
     * control system. If no argument is given, then the project directory is
     * implied, i.e. the method answers the question "is this project under
     * source control?". This implementation simply checks for the existence of
     * a '.git' directory.
     * @param fileOrDir Optional file or directory path to check. If not provided,
     * "." is implied.
     */
    boolean isManaged(File fileOrDir = null) {
        if (!fileOrDir) fileOrDir = baseDir

        return new File(fileOrDir, ".git").exists()
    }
   
    /**
     * Returns a list of all files in the project that aren't currently under
     * source control and aren't in an ignore list.
     */
    List getUnmanagedFiles() {
        return gitClient.status().call().untracked.sort()
    }

    /**
     * Puts the given directory or file under source control. If the argument
     * is not given, the project directory is implied. Note that for directories,
     * this method acts recursively, i.e. the directory and all the files and
     * sub-directories in it are added.
     */
    void manage(File fileOrDir = null) {
        def cmd = gitClient.add()

        def path = "."
        if (!fileOrDir) {
            // Get the path relative to the base directory.
            path = fileOrDir.canonicalPath - basePath.canonicalPath
            if (path[0] == '/' || path[0] == '\\') path = path.substring(1)
        }
        cmd.addFilepattern path
        cmd.call()
    }

    /**
     * Determines whether the project's working copy is up to date with respect
     * to the remote repository.
     */
    boolean upToDate() {
        // TODO Perform a fetch and check whether there's anything new from
        // the remote repository.
        return true
    }

    /**
     * Commits the current set of changes to the local git repository, using the
     * given text as the commit message.
     */
    void commit(String msg) {
        gitClient.commit().setMessage(msg ?: "Initial commit of Grails project source.").call()
        interactive.out.println "Committed current changes to the git repository."
    }

    /**
     * Tags the current branch's HEAD with the given label.
     */
    void tag(String label, String msg) {
        def command = gitClient.tag().setMessage(msg).setName(label)
        command.call()
    }

    /**
     * Synchronizes the local repository with the remote 'origin'. Basically
     * this is a pull and then a push.
     */
    void synchronize() {
        // Push! TODO What if 'origin' remote or 'master' branch don't exist?
        def pushCmd = gitClient.push()
        pushCmd.remote = "origin"
        pushCmd.refSpecs = [new RefSpec("master")]
//        pushCmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(*credentials))
        pushCmd.setCredentialsProvider(new AwtCredentialsProvider())
        pushCmd.call()
    }

    protected getGitClient() {
        return Git.open(baseDir)
    }

    /**
     * Executes a closure that may throw an SVNAuthenticationException.
     * If that exception is thrown, this method asks the user for his
     * username and password, updates the Subversion credentials and
     * tries to execute the closure again. Any exception thrown at that
     * point will propagate out.
     * @param c The closure to execute within the try/catch.
     */
    private handleAuthentication(c, authCount = 0) {
        /*
        try {
            return c()
        }
        catch (SVNAuthenticationException ex) {
            // Only allow three authentication attempts.
            if (authCount == 3) throw ex
            else if (authCount > 0) interactive.out.println "Authentication failed - please try again."

            def username = interactive.askUser("Enter your Subversion username: ")
            def password = interactive.askUser("Enter your Subversion password: ")
            svnClient.setCredentials(username, password)
            return handleAuthentication(c, ++authCount)
        }
        */
    }
}

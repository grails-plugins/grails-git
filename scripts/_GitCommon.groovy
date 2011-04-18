import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.lib.Repository

includeTargets << grailsScript("_GrailsEvents")

gitRepository = null

target(gitInit: "Initialises a git repository in the project directory.") {
    def command = Git.init()
    command.directory = grailsSettings.baseDir

    def repository
    try {
        repository = command.call().repository
        println "Initialised empty git repository for the project."
    }
    catch (Exception ex) {
        println "Unable to initialise git repository - ${ex.message}"
        exit 1
    }

    // Set up the gitignore file. TODO The ignores should be standard
    // to Grails and provided by the core scripts.
    new File(grailsSettings.baseDir, ".gitignore").text = """\
        *.iws
        .settings/
        grails-*.zip
        /plugin.xml
        stacktrace.log
        target/
        web-app/WEB-INF/classes/
        """.stripIndent()

    // Now commit the files that aren't ignored to the repository.
    def git = new Git(repository)
    git.add().addFilepattern(".").call()
    git.commit().setMessage("Initial commit of Grails project source.").call()
    println "Committed initial code to the git repository."

    gitRepository = repository
}

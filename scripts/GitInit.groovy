import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.lib.Repository

includeTargets << new File(gitPluginDir, "scripts/_GitCommon.groovy")

target(default: "Initialises the git repository") {
    depends(gitInit)
}

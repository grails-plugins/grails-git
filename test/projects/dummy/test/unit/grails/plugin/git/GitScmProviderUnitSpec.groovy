package grails.plugin.git

import org.eclipse.jgit.api.Git
import spock.lang.*

class GitScmProviderUnitSpec extends Specification {
    @Shared File remoteRepository

    def setupSpec() {
        remoteRepository = new File(System.getProperty("java.io.tmpdir"), "git-plugin")
        remoteRepository.deleteDir()
        remoteRepository.mkdirs()
        println ">> Tmp dir: ${remoteRepository}"

        def cmd = Git.init()
        cmd.directory = remoteRepository
        cmd.bare = true
        cmd.call()
    }

    def "Is a directory managed by git"() {
        given: "A git provider for this project"
        def provider = new GitScmProvider(".", [out: System.out])

        expect: "The git provider correctly determines whether a given directory is a git repository or not."
        provider.isManaged(dir) == expected

        where:
        dir                   | expected
        null                  | true
        new File("../../..")  | true
    }
}

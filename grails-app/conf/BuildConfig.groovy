grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log "warn"

    repositories {
        grailsPlugins()
        grailsCentral()
        grailsHome()
        mavenRepo "http://download.eclipse.org/jgit/maven"
    }

    dependencies {
          build "org.eclipse.jgit:org.eclipse.jgit:1.2.0.201112221803-r",
                "org.eclipse.jgit:org.eclipse.jgit.ui:1.2.0.201112221803-r",
                "org.eclipse.jgit:org.eclipse.jgit.console:1.2.0.201112221803-r"
    }
}

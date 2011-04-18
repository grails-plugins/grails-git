grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log "warn"

    repositories {
        grailsHome()
//        mavenRepo "http://download.eclipse.org/jgit/maven"
    }

    dependencies {
//        build "org.eclipse.jgit:org.eclipse.jgit:0.11.3"
    }
}

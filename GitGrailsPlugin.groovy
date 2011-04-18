/* Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class GitGrailsPlugin {
    def version = "1.0-SNAPSHOT"
    def grailsVersion = "1.1 > *"
    def pluginExcludes = [] 

    def author = "Peter Ledbrook"
    def authorEmail = "p.ledbrook@cacoethes.co.uk"
    def title = "Git Plugin"
    def description = '''\
Integrates git, the distributed version control system, into Grails projects. It can initialise a local git repository for your project, add a .gitignore file and commit the first version of your source code.
'''
    def documentation = "http://grails.org/plugin/git"

    def license = "APACHE"
    def organization = [ name: "SpringSource", url: "http://www.springsource.org/" ]
    def developers = []
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GRAILSPLUGINS" ]
    def scm = [ url: "https://github.com/grails-plugin/grails-git" ]
}

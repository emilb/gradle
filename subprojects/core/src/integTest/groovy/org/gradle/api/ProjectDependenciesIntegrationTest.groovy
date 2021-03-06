/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.api

import org.gradle.integtests.fixtures.AbstractDependencyResolutionTest

/**
 * by Szczepan Faber, created at: 11/21/12
 */
class ProjectDependenciesIntegrationTest extends AbstractDependencyResolutionTest {

    def "resolving project dependency triggers configuration of the target project"() {
        settingsFile << "include 'impl'"
        buildFile << """
            apply plugin: 'java'
            dependencies {
                compile project(":impl")
            }
            repositories {
                //resolving project must declare the repo
                maven { url '${mavenRepo.uri}' }
            }
            println "Resolved at configuration time: " + configurations.compile.files*.name
        """

        mavenRepo.module("org", "foo").publish()
        file("impl/build.gradle") << """
            apply plugin: 'java'
            dependencies {
                compile "org:foo:1.0"
            }
        """

        when:
        run()

        then:
        result.output.contains "Resolved at configuration time: [impl.jar, foo-1.0.jar]"
    }
}

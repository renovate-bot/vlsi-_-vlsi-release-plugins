/*
 * Copyright 2019 Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.vlsi.gradle.properties

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class ProjectExtensionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (System.getenv("GITHUB_ACTIONS") == "true" && target == target.rootProject) {
            target.gradle.addListener(object: TaskExecutionListener {
                override fun beforeExecute(task: Task) = Unit

                override fun afterExecute(task: Task, state: TaskState) {
                    state.failure?.let {
                        println("::error file=$task::${it.message.substringBefore('\n')}")
                    }
                }
            })
        }
    }
}

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
package com.github.vlsi.gradle.release

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import org.gradle.api.Project

class NexusRepositoryIdStore(private val project: Project) {
    private val savedIds = ConcurrentHashMap<String, String>()

    private fun storeDir() = "${project.buildDir}/stagingRepositories"

    private fun filePath(repositoryName: String) = "${storeDir()}/$repositoryName.txt"

    operator fun get(name: String) = savedIds[name]

    operator fun set(name: String, id: String) {
        if (savedIds.putIfAbsent(name, id) == null) {
            project.logger.lifecycle("Initialized stagingRepositoryId {} for repository {}", id, name)
            val file = project.file(filePath(name))
            file.parentFile.mkdirs()
            file.writeText(id)
        }
    }

    fun getOrLoad(name: String) = savedIds[name] ?: load(name)

    fun load(name: String) =
        File(storeDir() + "/$name.txt").readText().also { set(name, it) }

    fun load() {
        for (f in project.file(storeDir()).listFiles { f -> f.name.endsWith("*.txt") }
            ?: arrayOf()) {
            savedIds[f.name.removeSuffix(".txt")] = f.readText()
        }
    }
}

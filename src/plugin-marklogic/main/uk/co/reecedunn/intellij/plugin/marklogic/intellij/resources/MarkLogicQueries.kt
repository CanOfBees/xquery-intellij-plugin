/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
 */
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources

import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xquery.intellij.resources.XQueryQueries

object MarkLogicQueries : VirtualFileSystemImpl("res") {
    private val cache: HashMap<String, VirtualFile?> = HashMap()

    private fun resourceFile(path: String): VirtualFile? {
        val file = ResourceVirtualFile.createIfValid(this::class.java.classLoader, path, this) ?: return null
        file.charset = CharsetToolkit.UTF8_CHARSET
        return file
    }

    override fun findFileByPath(path: String): VirtualFile? {
        return cache[path] ?: XQueryQueries.resourceFile(path).let {
            cache[path] = it
            it
        }
    }

    override fun refresh(asynchronous: Boolean) {
    }

    val Run: String = resourceFile("queries/marklogic/run.xq")?.decode()!!

    val Version: VirtualFile = resourceFile("queries/marklogic/version.xq")!!
    val Servers: VirtualFile = resourceFile("queries/marklogic/servers.xq")!!
    val Databases: VirtualFile = resourceFile("queries/marklogic/databases.xq")!!

    val ApiDocs: VirtualFile = resourceFile("queries/marklogic/apidocs.xq")!!

    val CtsElementWalkVariables: VirtualFile =
        resourceFile("queries/marklogic/static-context/cts-element-walk-variables.xq")!!

    val CtsEntityHighlightVariables: VirtualFile =
        resourceFile("queries/marklogic/static-context/cts-entity-highlight-variables.xq")!!

    val CtsHighlightVariables: VirtualFile =
        resourceFile("queries/marklogic/static-context/cts-highlight-variables.xq")!!

    object Debug {
        val Breakpoint: VirtualFile = resourceFile("queries/marklogic/debug/breakpoint.xq")!!
        val Break: VirtualFile = resourceFile("queries/marklogic/debug/break.xq")!!
        val Continue: VirtualFile = resourceFile("queries/marklogic/debug/continue.xq")!!
        val Stack: VirtualFile = resourceFile("queries/marklogic/debug/stack.xq")!!
        val Status: VirtualFile = resourceFile("queries/marklogic/debug/status.xq")!!
        val StepInto: VirtualFile = resourceFile("queries/marklogic/debug/step-into.xq")!!
        val StepOver: VirtualFile = resourceFile("queries/marklogic/debug/step-over.xq")!!
        val StepOut: VirtualFile = resourceFile("queries/marklogic/debug/step-out.xq")!!
        val Value: VirtualFile = resourceFile("queries/marklogic/debug/value.xq")!!
    }

    object Log {
        val Logs: VirtualFile = resourceFile("queries/marklogic/log/logs.xq")!!
        val Log: VirtualFile = resourceFile("queries/marklogic/log/log.xq")!!
    }

    object Request {
        val Cancel: VirtualFile = resourceFile("queries/marklogic/request/cancel.xq")!!
    }
}

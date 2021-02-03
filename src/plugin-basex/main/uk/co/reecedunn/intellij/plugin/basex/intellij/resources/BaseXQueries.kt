/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.basex.intellij.resources

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import java.nio.charset.StandardCharsets

object BaseXQueries {
    private fun resourceFile(path: String): VirtualFile {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, path)
        file.charset = StandardCharsets.UTF_8
        return file
    }

    val Version: VirtualFile = resourceFile("queries/basex/version.xq")

    object Log {
        val Logs: VirtualFile = resourceFile("queries/basex/log/logs.xq")
        val Log: VirtualFile = resourceFile("queries/basex/log/log.xq")
    }
}

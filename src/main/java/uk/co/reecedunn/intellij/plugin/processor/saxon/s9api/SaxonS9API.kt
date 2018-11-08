/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.MissingJarFileException
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorInstanceManager
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedJarFileException
import java.io.File
import java.io.InputStream

object SaxonS9API : QueryProcessorApi {
    override val id: String = "saxon.s9api"
    override val displayName: String = "Saxon"

    override val requireJar: Boolean = true
    override val hasAdminPort: Boolean = false
    override val hasConfiguration: Boolean = true

    override val canCreate: Boolean = true
    override val canConnect: Boolean = false

    override fun newInstanceManager(jar: String?, config: InputStream?): QueryProcessorInstanceManager {
        if (jar == null)
            throw MissingJarFileException(displayName)
        return try {
            Saxon(File(jar), config)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedJarFileException(displayName)
        }
    }
}

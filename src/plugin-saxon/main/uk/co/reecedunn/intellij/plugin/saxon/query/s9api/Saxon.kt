/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorInstanceManager
import java.io.File
import java.io.InputStream
import java.net.URLClassLoader
import javax.xml.transform.stream.StreamSource

class Saxon(private val classLoader: ClassLoader, private val config: InputStream?) : QueryProcessorInstanceManager {
    constructor(path: File, config: InputStream?) : this(URLClassLoader(arrayOf(path.toURI().toURL())), config)

    override fun create(): QueryProcessor {
        return SaxonQueryProcessor(classLoader, config?.let { StreamSource(it) })
    }

    override fun connect(settings: ConnectionSettings): QueryProcessor {
        // Saxon does not provide support for running it as a database server.
        throw UnsupportedOperationException()
    }
}

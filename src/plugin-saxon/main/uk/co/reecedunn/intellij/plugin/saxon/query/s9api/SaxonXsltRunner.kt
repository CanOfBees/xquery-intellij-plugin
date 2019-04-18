/*
 * Copyright (C) 2019 Reece H. Dunn
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

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.xml.toStreamSource
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.RawDestination
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource

internal class SaxonXsltRunner(
    val processor: Processor,
    val query: String,
    val queryPath: String,
    val classes: SaxonClasses
) : RunnableQuery, ValidatableQuery, SaxonRunner {
    private val compiler by lazy { processor.newXsltCompiler() }

    private val executable by lazy { compiler.compile(query.toStreamSource()) }

    private val transformer by lazy { executable.load() }

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private var context: StreamSource? = null

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = check(queryPath, classes.loader) {
        throw UnsupportedOperationException()
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = check(queryPath, classes.loader) {
        context = when (value) {
            is DatabaseModule -> value.path.toStreamSource()
            is VirtualFile -> value.decode()?.toStreamSource()
            else -> value?.toString()?.toStreamSource()
        }
    }

    override fun asSequence(): Sequence<QueryResult> {
        return check(queryPath, classes.loader) {
            if (context == null) {
                // The Saxon processor throws a NPE if source is null.
                val message = PluginApiBundle.message("error.missing-xslt-source")
                return@check sequenceOf(QueryResult.fromItemType(0, message, "fn:error"))
            }
            transformer.setSource(context!!)

            val destination = RawDestination(classes.loader)
            transformer.setDestination(destination)

            transformer.transform()
            val result = destination.getXdmValue()

            SaxonQueryResultIterator(result.iterator(), classes).asSequence()
        }
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        asSequence()
    }

    override fun validate(): ExecutableOnPooledThread<QueryError?> = pooled_thread {
        try {
            check(queryPath, classes.loader) { executable } // Compile the query.
            null
        } catch (e: QueryError) {
            e
        }
    }

    override fun close() {
    }
}

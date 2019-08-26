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
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResults
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmItem
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration

internal class SaxonXPathRunner(
    val processor: Processor,
    val query: String,
    private val queryFile: VirtualFile
) : RunnableQuery, ValidatableQuery, SaxonRunner {
    private val compiler by lazy { processor.newXPathCompiler() }

    private val executable by lazy {
        when (xpathSubset) {
            XPathSubset.XsltPattern -> compiler.compilePattern(query)
            else -> compiler.compile(query)
        }
    }

    private val selector by lazy { executable.load() }

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private var context: XdmItem? = null

    override fun bindVariable(name: String, value: Any?, type: String?) {
        throw UnsupportedOperationException()
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = check(queryFile, processor.classLoader) {
        val classLoader = processor.classLoader
        context = when (value) {
            is DatabaseModule -> XdmItem.newInstance(value.path, type ?: "xs:string", classLoader)
            is VirtualFile -> XdmItem.newInstance(value.decode()!!, type ?: "xs:string", classLoader)
            else -> XdmItem.newInstance(value, type ?: "xs:string", classLoader)
        }
    }

    override fun asSequence(): Sequence<QueryResult> = check(queryFile, processor.classLoader) {
        context?.let { selector.setContextItem(it) }
        SaxonQueryResultIterator(selector.iterator()).asSequence()
    }

    override fun run(): QueryResults {
        val start = System.nanoTime()
        val results = asSequence().toList()
        val end = System.nanoTime()
        return QueryResults(results, XsDuration.ns(end - start))
    }

    override fun validate(): QueryError? {
        return try {
            check(queryFile, processor.classLoader) { executable } // Compile the query.
            null
        } catch (e: QueryError) {
            e
        }
    }

    override fun close() {
    }
}

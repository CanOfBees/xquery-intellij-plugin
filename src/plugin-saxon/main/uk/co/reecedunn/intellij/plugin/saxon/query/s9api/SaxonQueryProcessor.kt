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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQueryProvider
import uk.co.reecedunn.intellij.plugin.saxon.profiler.SaxonProfileTraceListener
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import javax.xml.transform.Source

internal class SaxonQueryProcessor(val classLoader: ClassLoader, private val source: Source?) :
    ProfileableQueryProvider,
    RunnableQueryProvider,
    ValidatableQueryProvider {

    private val processor by lazy {
        if (source == null)
            Processor(classLoader, true)
        else
            Processor(classLoader, source)
    }

    override val version get(): String = processor.version

    override val servers: List<String> = listOf<String>()

    override val databases: List<String> = listOf<String>()

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        val runner = createRunnableQuery(query, language)

        val listener = SaxonProfileTraceListener(processor.version, query)
        processor.setTraceListener(listener)

        return SaxonQueryProfiler(runner, listener)
    }

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query)
            XQuery -> SaxonXQueryRunner(processor, queryText, query)
            XSLT -> SaxonXsltRunner(processor, queryText, query)
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createValidatableQuery(query: VirtualFile, language: Language): ValidatableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query)
            XQuery -> SaxonXQueryRunner(processor, queryText, query)
            XSLT -> SaxonXsltRunner(processor, queryText, query)
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun close() {
    }
}

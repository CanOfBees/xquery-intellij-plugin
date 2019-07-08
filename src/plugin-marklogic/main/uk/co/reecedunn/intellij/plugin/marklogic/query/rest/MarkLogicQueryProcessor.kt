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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest

import com.google.gson.JsonObject
import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import org.apache.http.client.methods.RequestBuilder
import uk.co.reecedunn.intellij.plugin.core.async.*
import uk.co.reecedunn.intellij.plugin.core.lang.getLanguageMimeTypes
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQueryProvider

internal class MarkLogicQueryProcessor(private val baseUri: String, private val connection: HttpConnection) :
    ProfileableQueryProvider,
    RunnableQueryProvider,
    ValidatableQueryProvider,
    LogViewProvider {

    override val version: ExecutableOnPooledThread<String> by cached {
        createRunnableQuery(MarkLogicQueries.Version, XQuery).use { query ->
            query.run().then { results -> results.results.first().value as String }
        }
    }

    override val servers: ExecutableOnPooledThread<List<String>>
        get() {
            return createRunnableQuery(MarkLogicQueries.Servers, XQuery).use { query ->
                query.run().then { results -> results.results.map { it.value as String } }
            }
        }

    override val databases: ExecutableOnPooledThread<List<String>>
        get() {
            return createRunnableQuery(MarkLogicQueries.Databases, XQuery).use { query ->
                query.run().then { results -> results.results.map { it.value as String } }
            }
        }

    private fun buildParameters(query: VirtualFile, language: Language, mode: String): JsonObject {
        val queryParams = JsonObject()
        queryParams.addProperty("mode", mode)
        queryParams.addProperty("mimetype", language.getLanguageMimeTypes()[0])

        if (query is DatabaseModule) {
            queryParams.addProperty("module-path", query.path)
            queryParams.addProperty("query", "")
        } else {
            queryParams.addProperty("module-path", "")
            queryParams.addProperty("query", query.decode()!!)
        }
        return queryParams
    }

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicProfileQuery(builder, buildParameters(query, language, "profile"), query, connection)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        return when (language) {
            ServerSideJavaScript, SPARQLQuery, SPARQLUpdate, SQL, XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicRunQuery(builder, buildParameters(query, language, "run"), query, connection)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createValidatableQuery(query: VirtualFile, language: Language): ValidatableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicRunQuery(builder, buildParameters(query, language, "validate"), query, connection)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun logs(): ExecutableOnPooledThread<List<String>> {
        return createRunnableQuery(MarkLogicQueries.Log.Logs, XQuery).use { query ->
            query.run().then { results -> results.results.map { it.value as String } }
        }
    }

    override fun log(name: String): ExecutableOnPooledThread<List<String>> {
        return createRunnableQuery(MarkLogicQueries.Log.Log, XQuery).use { query ->
            query.bindVariable("name", name, "xs:string")
            query.run().then { results -> results.results.map { it.value as String } }
        }
    }

    override fun close() = connection.close()
}

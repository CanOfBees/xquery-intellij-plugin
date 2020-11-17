/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.completion

import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.ProcessingContext
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.completion.MockCompletionResultSet
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathDefaultNamespace
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathStaticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xpath.completion.providers.XPathAtomicOrUnionTypeProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - Code Completion - Completion Providers")
private class XQueryCompletionProviderTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryCompletionProviderTest")

    override fun registerExtensions() {
        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        registerVariableProvider(XQueryVariableProvider, "INSTANCE")
        registerFunctionProvider(XQueryFunctionProvider, "INSTANCE")
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
    internal inner class AtomicOrUnionType {
        fun completionResults(query: String, completionPoint: String): List<String> {
            val element = completion(query, completionPoint)
            val context = ProcessingContext()
            val results = MockCompletionResultSet(PlainPrefixMatcher(completionPoint))
            XPathStaticallyKnownNamespaces.computeProperty(element, context)
            XPathDefaultNamespace(XPathCompletionProperty.DEFAULT_TYPE_NAMESPACE).computeProperty(element, context)
            XPathAtomicOrUnionTypeProvider.apply(element, context, results)
            return results.elements.map { it.lookupString }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (235) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("built-in prefix")
            fun builtInPrefix() {
                val results = completionResults("2 cast as inte", "inte")
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("xs:integer"))
                assertThat(results[1], `is`("xs:negativeInteger"))
                assertThat(results[2], `is`("xs:nonNegativeInteger"))
                assertThat(results[3], `is`("xs:nonPositiveInteger"))
                assertThat(results[4], `is`("xs:positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix")
            fun customPrefix() {
                val results = completionResults(
                    """
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("xsd:integer"))
                assertThat(results[1], `is`("xsd:negativeInteger"))
                assertThat(results[2], `is`("xsd:nonNegativeInteger"))
                assertThat(results[3], `is`("xsd:nonPositiveInteger"))
                assertThat(results[4], `is`("xsd:positiveInteger"))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    2 cast as inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(10))
                assertThat(results[0], `is`("xs:integer"))
                assertThat(results[1], `is`("xs:negativeInteger"))
                assertThat(results[2], `is`("xs:nonNegativeInteger"))
                assertThat(results[3], `is`("xs:nonPositiveInteger"))
                assertThat(results[4], `is`("xs:positiveInteger"))
                assertThat(results[5], `is`("integer"))
                assertThat(results[6], `is`("negativeInteger"))
                assertThat(results[7], `is`("nonNegativeInteger"))
                assertThat(results[8], `is`("nonPositiveInteger"))
                assertThat(results[9], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix and default element/type namespace")
            fun customPrefixAndDefaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(10))
                assertThat(results[0], `is`("xsd:integer"))
                assertThat(results[1], `is`("xsd:negativeInteger"))
                assertThat(results[2], `is`("xsd:nonNegativeInteger"))
                assertThat(results[3], `is`("xsd:nonPositiveInteger"))
                assertThat(results[4], `is`("xsd:positiveInteger"))
                assertThat(results[5], `is`("integer"))
                assertThat(results[6], `is`("negativeInteger"))
                assertThat(results[7], `is`("nonNegativeInteger"))
                assertThat(results[8], `is`("nonPositiveInteger"))
                assertThat(results[9], `is`("positiveInteger"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (234) QName")
        internal inner class QName {
            @Test
            @DisplayName("built-in prefix")
            fun builtInPrefix() {
                val results = completionResults("2 cast as xs:inte", "inte")
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix")
            fun customPrefix() {
                val results = completionResults(
                    """
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as xsd:inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    2 cast as xs:inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix and default element/type namespace")
            fun customPrefixAndDefaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as xsd:inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("built-in prefix")
            fun builtInPrefix() {
                val results = completionResults("2 cast as Q{http://www.w3.org/2001/XMLSchema}inte", "inte")
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix")
            fun customPrefix() {
                val results = completionResults(
                    """
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as Q{http://www.w3.org/2001/XMLSchema}inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    2 cast as Q{http://www.w3.org/2001/XMLSchema}inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }

            @Test
            @DisplayName("custom prefix and default element/type namespace")
            fun customPrefixAndDefaultElementTypeNamespace() {
                val results = completionResults(
                    """
                    declare default element namespace "http://www.w3.org/2001/XMLSchema";
                    declare namespace xsd = "http://www.w3.org/2001/XMLSchema";
                    2 cast as Q{http://www.w3.org/2001/XMLSchema}inte
                    """.trimIndent(),
                    "inte"
                )
                assertThat(results.size, `is`(5))
                assertThat(results[0], `is`("integer"))
                assertThat(results[1], `is`("negativeInteger"))
                assertThat(results[2], `is`("nonNegativeInteger"))
                assertThat(results[3], `is`("nonPositiveInteger"))
                assertThat(results[4], `is`("positiveInteger"))
            }
        }
    }
}

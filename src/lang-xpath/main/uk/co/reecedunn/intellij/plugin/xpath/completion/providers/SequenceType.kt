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
package uk.co.reecedunn.intellij.plugin.xpath.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.W3C
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XmlSchemaSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.defaultProductVersion
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

fun createTypeNameLookup(localName: String, prefix: String? = null): LookupElementBuilder {
    return LookupElementBuilder.create(prefix?.let { "$it:$localName" } ?: localName)
        .withIcon(XPathIcons.Nodes.TypeDecl)
}

object XPathSequenceTypeProvider : CompletionProviderEx {
    private val XPATH_20_WD_2003_SEQUENCE_TYPE = XPathKeywordLookup("empty", XPathInsertText.EMPTY_PARAMS)

    private val XPATH_20_REC_SEQUENCE_TYPE = XPathKeywordLookup("empty-sequence", XPathInsertText.EMPTY_PARAMS)

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> {}
            XPathSpec.WD_2_0_20030502 -> result.addElement(XPATH_20_WD_2003_SEQUENCE_TYPE)
            else -> result.addElement(XPATH_20_REC_SEQUENCE_TYPE)
        }
    }
}

object XPathItemTypeProvider : CompletionProviderEx {
    private val XPATH_20_ITEM_TYPES = listOf(
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_30_ITEM_TYPES = listOf(
        XPathKeywordLookup("function", "(sequence-types-or-wildcard)", XPathInsertText.PARAMS),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_30_IN_XSLT_ITEM_TYPES = listOf(
        XPathKeywordLookup("function", "(sequence-types-or-wildcard)", XPathInsertText.PARAMS),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("map", "(key-type-or-wildcard, value-type?)", XPathInsertText.PARAMS) // XSLT 3.0 includes support for maps.
    )

    private val XPATH_31_ITEM_TYPES = listOf(
        XPathKeywordLookup("array", "(type-or-wildcard)", XPathInsertText.PARAMS),
        XPathKeywordLookup("function", "(sequence-types-or-wildcard)", XPathInsertText.PARAMS),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("map", "(key-type-or-wildcard, value-type?)", XPathInsertText.PARAMS)
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_ITEM_TYPES)
            XPathSpec.REC_2_0_20070123 -> result.addAllElements(XPATH_20_ITEM_TYPES)
            XPathSpec.REC_3_0_20140408 -> {
                if (context[XPathCompletionProperty.XSLT_VERSION] == null) {
                    result.addAllElements(XPATH_30_ITEM_TYPES)
                } else {
                    result.addAllElements(XPATH_30_IN_XSLT_ITEM_TYPES)
                }
            }
            XPathSpec.CR_3_1_20151217 -> result.addAllElements(XPATH_31_ITEM_TYPES)
            XPathSpec.REC_3_1_20170321 -> result.addAllElements(XPATH_31_ITEM_TYPES)
            else -> {}
        }
    }
}

object XPathAtomicOrUnionTypeProvider : CompletionProviderEx {
    private const val XS_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema"

    private fun createXsd10Types(prefix: String?): List<LookupElementBuilder> {
        return listOf(
            createTypeNameLookup("anyAtomicType", prefix), // XSD 1.1 type supported in XPath/XQuery
            createTypeNameLookup("anySimpleType", prefix),
            createTypeNameLookup("anyURI", prefix),
            createTypeNameLookup("base64Binary", prefix),
            createTypeNameLookup("boolean", prefix),
            createTypeNameLookup("byte", prefix),
            createTypeNameLookup("date", prefix),
            createTypeNameLookup("dateTime", prefix),
            createTypeNameLookup("dayTimeDuration", prefix), // XSD 1.1 type supported in XPath/XQuery
            createTypeNameLookup("decimal", prefix),
            createTypeNameLookup("double", prefix),
            createTypeNameLookup("duration", prefix),
            createTypeNameLookup("ENTITY", prefix),
            createTypeNameLookup("float", prefix),
            createTypeNameLookup("gDay", prefix),
            createTypeNameLookup("gMonth", prefix),
            createTypeNameLookup("gMonthDay", prefix),
            createTypeNameLookup("gYear", prefix),
            createTypeNameLookup("gYearMonth", prefix),
            createTypeNameLookup("hexBinary", prefix),
            createTypeNameLookup("ID", prefix),
            createTypeNameLookup("IDREF", prefix),
            createTypeNameLookup("int", prefix),
            createTypeNameLookup("integer", prefix),
            createTypeNameLookup("language", prefix),
            createTypeNameLookup("long", prefix),
            createTypeNameLookup("Name", prefix),
            createTypeNameLookup("NCName", prefix),
            createTypeNameLookup("negativeInteger", prefix),
            createTypeNameLookup("NMTOKEN", prefix),
            createTypeNameLookup("nonNegativeInteger", prefix),
            createTypeNameLookup("nonPositiveInteger", prefix),
            createTypeNameLookup("normalizedString", prefix),
            createTypeNameLookup("NOTATION", prefix),
            createTypeNameLookup("numeric", prefix),
            createTypeNameLookup("positiveInteger", prefix),
            createTypeNameLookup("QName", prefix),
            createTypeNameLookup("short", prefix),
            createTypeNameLookup("string", prefix),
            createTypeNameLookup("time", prefix),
            createTypeNameLookup("token", prefix),
            createTypeNameLookup("unsignedByte", prefix),
            createTypeNameLookup("unsignedInt", prefix),
            createTypeNameLookup("unsignedLong", prefix),
            createTypeNameLookup("unsignedShort", prefix),
            createTypeNameLookup("untypedAtomic", prefix),
            createTypeNameLookup("yearMonthDuration", prefix) // XSD 1.1 type supported in XPath/XQuery
        )
    }

    private fun createXsd11Types(prefix: String?): List<LookupElementBuilder> {
        return listOf(
            createTypeNameLookup("dateTimeStamp", prefix),
            createTypeNameLookup("error", prefix)
        )
    }

    private fun addXsdTypes(context: ProcessingContext, result: CompletionResultSet, prefix: String?) {
        val product = context[XPathCompletionProperty.XPATH_PRODUCT] ?: W3C.SPECIFICATIONS
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION] ?: defaultProductVersion(product)

        if (product.conformsTo(version, XmlSchemaSpec.REC_1_0_20041028)) result.addAllElements(createXsd10Types(prefix))
        if (product.conformsTo(version, XmlSchemaSpec.REC_1_1_20120405)) result.addAllElements(createXsd11Types(prefix))
    }

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES]
        val prefix = namespaces.find { it.namespaceUri?.data == XS_NAMESPACE_URI } ?: return
        val prefixName = prefix.namespacePrefix?.data

        val qname = element.parent as XsQNameValue
        when (qname.completionType(element)) {
            EQNameCompletionType.QNamePrefix, EQNameCompletionType.NCName -> addXsdTypes(context, result, prefixName)
            EQNameCompletionType.QNameLocalName -> {
                if (qname.prefix?.data == prefixName) {
                    addXsdTypes(context, result, null) // Prefix already specified.
                }
            }
            EQNameCompletionType.URIQualifiedNameLocalName -> {
                if (qname.namespace?.data == XS_NAMESPACE_URI) {
                    addXsdTypes(context, result, null) // Prefix already specified.
                }
            }
            else -> {}
        }
    }
}

object XPathKindTestProvider : CompletionProviderEx {
    private val XPATH_10_KIND_TESTS = listOf(
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_20_WD_2003_KIND_TESTS = listOf(
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_SCHEMA_CONTEXT),
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.PARAMS_ROOT_ELEMENT),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_SCHEMA_CONTEXT),
        XPathKeywordLookup("namespace-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_20_REC_KIND_TESTS = listOf(
        XPathKeywordLookup("attribute", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_WILDCARD_AND_TYPE),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_NAME_AND_TYPE),
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.PARAMS_ROOT_ELEMENT),
        XPathKeywordLookup("element", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_WILDCARD_AND_TYPE),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_NAME_AND_TYPE),
        XPathKeywordLookup("namespace-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("schema-attribute", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("schema-element", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> result.addAllElements(XPATH_10_KIND_TESTS)
            XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_WD_2003_KIND_TESTS)
            else -> result.addAllElements(XPATH_20_REC_KIND_TESTS)
        }
    }
}

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
package uk.co.reecedunn.intellij.plugin.saxon.lang

import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxErrorReporter
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresAny
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresProductVersionRange
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryItemTypeDecl

object SaxonSyntaxValidator : XpmSyntaxValidator {
    override fun validate(
        element: XpmSyntaxValidationElement,
        reporter: XpmSyntaxErrorReporter
    ): Unit = when (element) {
        is PluginContextItemFunctionExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.DOT, XPathTokenType.CONTEXT_FUNCTION -> reporter.requires(element, SAXON_PE_10)
            else -> reporter.requires(element, SAXON_PE_9_9_ONLY)
        }
        is PluginLambdaFunctionExpr -> reporter.requires(element, SAXON_PE_10)
        is XPathOtherwiseExpr -> reporter.requires(element, SAXON_PE_10)
        is PluginParamRef -> reporter.requires(element, SAXON_PE_10)
        is XPathSimpleForBinding -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_MEMBER -> reporter.requires(element, SAXON_PE_10)
            else -> {
            }
        }
        is XPathFieldDeclaration -> when (element.conformanceElement.elementType) {
            XPathTokenType.OPTIONAL, XPathTokenType.ELVIS -> reporter.requires(element, SAXON_PE_9_9)
            XPathTokenType.K_AS -> reporter.requires(element, SAXON_PE_10)
            else -> {
            }
        }
        is XPathRecordTest -> when (element.isExtensible) {
            true -> reporter.requires(element, SAXON_PE_9_9)
            else -> reporter.requires(element, SAXON_PE_9_8)
        }
        is PluginTypeAlias -> when (element.conformanceElement.elementType) {
            XPathTokenType.TYPE_ALIAS -> reporter.requires(element, SAXON_PE_9_8_TO_9_9) // ~T
            else -> reporter.requires(element, SAXON_PE_10) // type(T)
        }
        is XQueryItemTypeDecl -> reporter.requires(element, SAXON_PE_9_8)
        is XPathLocalUnionType -> reporter.requires(element, SAXON_PE_9_8)
        is XPathAndExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ANDALSO -> reporter.requires(element, SAXON_PE_9_9)
            else -> {
            }
        }
        is XPathAttributeTest -> when (element.conformanceElement) {
            is XPathWildcard -> {
                val name = XPathBundle.message("construct.wildcard-attribute-test")
                reporter.requires(element, SAXON_PE_10, name)
            }
            else -> {
            }
        }
        is XPathElementTest -> when (element.conformanceElement) {
            is XPathWildcard -> {
                val name = XPathBundle.message("construct.wildcard-element-test")
                reporter.requires(element, SAXON_PE_10, name)
            }
            else -> {
            }
        }
        is XPathOrExpr -> when (element.conformanceElement.elementType) {
            XPathTokenType.K_ORELSE -> reporter.requires(element, SAXON_PE_9_9)
            else -> {
            }
        }
        else -> {
        }
    }

    private val SAXON_PE_9_8 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_9_8),
        XpmRequiresProductVersion(SaxonEE.VERSION_9_8)
    )

    private val SAXON_PE_9_8_TO_9_9 = XpmRequiresAny(
        XpmRequiresProductVersionRange(SaxonPE.VERSION_9_8, SaxonPE.VERSION_9_9),
        XpmRequiresProductVersionRange(SaxonEE.VERSION_9_8, SaxonEE.VERSION_9_9)
    )

    private val SAXON_PE_9_9 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_9_9),
        XpmRequiresProductVersion(SaxonEE.VERSION_9_9)
    )

    private val SAXON_PE_9_9_ONLY = XpmRequiresAny(
        XpmRequiresProductVersionRange(SaxonPE.VERSION_9_9, SaxonPE.VERSION_9_9),
        XpmRequiresProductVersionRange(SaxonEE.VERSION_9_9, SaxonEE.VERSION_9_9)
    )

    private val SAXON_PE_10 = XpmRequiresAny(
        XpmRequiresProductVersion(SaxonPE.VERSION_10_0),
        XpmRequiresProductVersion(SaxonEE.VERSION_10_0)
    )
}

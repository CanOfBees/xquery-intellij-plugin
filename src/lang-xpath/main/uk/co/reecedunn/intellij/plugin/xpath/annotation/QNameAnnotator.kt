/*
 * Copyright (C) 2016, 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.annotation

import com.intellij.compat.lang.annotation.AnnotationHolder
import com.intellij.compat.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType

class QNameAnnotator : Annotator() {
    private fun getHighlightAttributes(element: PsiElement, resolveReferences: Boolean = true): TextAttributesKey {
        return when (element.getUsageType()) {
            XpmUsageType.Annotation -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
            XpmUsageType.Attribute -> XPathSyntaxHighlighterColors.ATTRIBUTE
            XpmUsageType.DecimalFormat -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
            XpmUsageType.Element -> XPathSyntaxHighlighterColors.ELEMENT
            XpmUsageType.FunctionDecl -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
            XpmUsageType.FunctionRef -> XPathSyntaxHighlighterColors.FUNCTION_CALL
            XpmUsageType.Namespace -> XPathSyntaxHighlighterColors.NS_PREFIX
            XpmUsageType.Option -> XPathSyntaxHighlighterColors.IDENTIFIER // XQuery
            XpmUsageType.Parameter -> XPathSyntaxHighlighterColors.PARAMETER
            XpmUsageType.Pragma -> XPathSyntaxHighlighterColors.PRAGMA
            XpmUsageType.Type -> XPathSyntaxHighlighterColors.TYPE
            XpmUsageType.Variable -> {
                if (resolveReferences)
                    element.reference?.resolve()?.let {
                        getHighlightAttributes(it, false)
                    } ?: XPathSyntaxHighlighterColors.VARIABLE
                else
                    XPathSyntaxHighlighterColors.VARIABLE
            }
            XpmUsageType.Unknown -> XPathSyntaxHighlighterColors.IDENTIFIER
        }
    }

    private fun checkQNameWhitespaceBefore(qname: XsQNameValue, separator: PsiElement, holder: AnnotationHolder) {
        val before = separator.prevSibling
        if (before.elementType === XPathTokenType.WHITE_SPACE || before is XPathComment) {
            val message =
                if (qname is XPathWildcard)
                    XPathBundle.message("parser.error.wildcard.whitespace-before-local-part")
                else
                    XPathBundle.message("parser.error.qname.whitespace-before-local-part")
            holder.newAnnotation(HighlightSeverity.ERROR, message).range(before).create()
        }
    }

    private fun checkQNameWhitespaceAfter(qname: XsQNameValue, separator: PsiElement, holder: AnnotationHolder) {
        val after = separator.nextSibling
        if (after.elementType === XPathTokenType.WHITE_SPACE || after is XPathComment) {
            val message =
                if (qname is XPathWildcard)
                    XPathBundle.message("parser.error.wildcard.whitespace-after-local-part")
                else
                    XPathBundle.message("parser.error.qname.whitespace-after-local-part")
            holder.newAnnotation(HighlightSeverity.ERROR, message).range(after).create()
        }
    }

    override fun annotateElement(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XsQNameValue) return
        if (element.language != XPath) return

        if (element.prefix != null) {
            if (element.prefix !is XdmWildcardValue) {
                val prefix = element.prefix?.element!!
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                    .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                    .create()
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(prefix)
                    .textAttributes(XPathSyntaxHighlighterColors.NS_PREFIX)
                    .create()
            }

            // Detect whitespace errors here instead of the parser so the QName annotator gets run.
            element.children().filterIsElementType(XPathTokenType.QNAME_SEPARATOR).firstOrNull()?.let {
                checkQNameWhitespaceBefore(element, it, holder)
                checkQNameWhitespaceAfter(element, it, holder)
            }
        }

        if (element.localName != null) {
            val localName = element.localName?.element!!
            val highlight = getHighlightAttributes(element)
            when {
                highlight !== XPathSyntaxHighlighterColors.IDENTIFIER -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(highlight)
                        .create()
                }
                localName.elementType is IKeywordOrNCNameType -> {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                        .create()
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(localName)
                        .textAttributes(XPathSyntaxHighlighterColors.IDENTIFIER)
                        .create()
                }
            }
        }
    }
}

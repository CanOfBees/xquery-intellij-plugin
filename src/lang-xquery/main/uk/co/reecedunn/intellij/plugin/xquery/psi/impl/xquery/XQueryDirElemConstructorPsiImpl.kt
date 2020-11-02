/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.lang.foldable.FoldablePsiElement
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class XQueryDirElemConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDirElemConstructor,
    FoldablePsiElement {
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = this

    // endregion
    // region XdmElementNode

    override val attributes: Sequence<XdmAttributeNode>
        get() = children().filterIsInstance<XdmAttributeNode>()

    override val nodeName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val closingTag: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().lastOrNull()

    // endregion
    // region FoldablePsiElement

    private val hasEnclosedExprOnlyContent: Boolean
        get() {
            var n = 0
            children().forEach { child ->
                n += when (child.elementType) {
                    in ELEMENT_CONSTRUCTOR_TOKENS -> 0
                    XQueryElementType.ENCLOSED_EXPR -> 1
                    else -> return false
                }
            }
            return n == 1
        }

    override val foldingRange: TextRange?
        get() {
            var start: PsiElement? = firstChild
            if (start!!.elementType === XQueryTokenType.OPEN_XML_TAG)
                start = start!!.nextSibling
            if (start!!.elementType === XQueryTokenType.XML_WHITE_SPACE)
                start = start!!.nextSibling
            if (
                start!!.elementType === XQueryElementType.NCNAME ||
                start!!.elementType === XQueryElementType.QNAME
            )
                start = start!!.nextSibling
            if (start?.elementType === XQueryTokenType.XML_WHITE_SPACE)
                start = start.nextSibling

            val dirAttributeList = parseDirAttributeList(start)
            val hasMultiLineAttributes = dirAttributeList.first
            if (!hasMultiLineAttributes) {
                start = dirAttributeList.second
            }

            val end = lastChild
            val endOffset =
                if (
                    end.elementType === XQueryTokenType.CLOSE_XML_TAG ||
                    end.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG
                ) {
                    end.prevSibling.textRange.endOffset
                } else {
                    end.textRange.startOffset
                }

            if (hasEnclosedExprOnlyContent && !hasMultiLineAttributes || start == null) {
                return null
            }
            return TextRange(start.textRange.startOffset, endOffset)
        }

    override val foldingPlaceholderText: String? = "..."

    companion object {
        private fun parseDirAttributeList(first: PsiElement?): Pair<Boolean, PsiElement?> {
            var start = first
            var hasMultiLineAttributes = false
            while (start?.elementType === XQueryElementType.DIR_ATTRIBUTE) {
                if (start.textContains('\n')) {
                    hasMultiLineAttributes = true
                }
                start = start.nextSibling

                if (start?.elementType === XQueryTokenType.XML_WHITE_SPACE) {
                    if (start.textContains('\n')) {
                        hasMultiLineAttributes = true
                    }
                    if (start.nextSibling?.elementType in CLOSE_TAG) {
                        return hasMultiLineAttributes to start
                    }
                    start = start.nextSibling
                }
            }
            return hasMultiLineAttributes to start
        }

        private val CLOSE_TAG = TokenSet.create(XQueryTokenType.END_XML_TAG, XQueryTokenType.SELF_CLOSING_XML_TAG)

        private val ELEMENT_CONSTRUCTOR_TOKENS = TokenSet.create(
            XQueryTokenType.OPEN_XML_TAG,
            XQueryTokenType.XML_WHITE_SPACE,
            XQueryElementType.NCNAME,
            XQueryElementType.QNAME,
            XQueryElementType.DIR_ATTRIBUTE,
            XQueryTokenType.END_XML_TAG,
            XQueryTokenType.CLOSE_XML_TAG,
            XQueryTokenType.SELF_CLOSING_XML_TAG
        )
    }

    // endregion
}

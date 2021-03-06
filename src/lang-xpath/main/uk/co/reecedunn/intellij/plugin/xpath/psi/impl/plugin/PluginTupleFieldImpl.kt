/*
 * Copyright (C) 2017-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTupleField
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsStringValue
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class PluginTupleFieldImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginTupleField, XpmSyntaxValidationElement {
    // region PluginTupleField

    override val fieldName: XsStringValue
        get() = when (val name = firstChild) {
            is XsQNameValue -> name.localName!!
            else -> name as XsStringValue
        }

    override val fieldType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val isOptional: Boolean
        get() = findChildByType<PsiElement>(OPTIONAL_TOKENS) != null

    companion object {
        private val OPTIONAL_TOKENS = TokenSet.create(
            XPathTokenType.OPTIONAL,
            XPathTokenType.ELVIS // ?: for compact whitespace
        )

        private val CONFORMANCE_ELEMENT_TOKENS = TokenSet.create(
            XPathTokenType.OPTIONAL,
            XPathTokenType.ELVIS, // ?: for compact whitespace
            XPathTokenType.K_AS
        )
    }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(CONFORMANCE_ELEMENT_TOKENS) ?: firstChild

    // endregion
}

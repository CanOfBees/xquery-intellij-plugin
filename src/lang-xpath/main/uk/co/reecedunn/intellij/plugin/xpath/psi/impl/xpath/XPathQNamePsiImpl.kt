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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

open class XPathQNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathQName, XsQNameValue {
    // region XsQNameValue

    private val names: Sequence<XsNCNameValue>
        get() = children().filterIsInstance<XsNCNameValue>()

    override val namespace: XsAnyUriValue? = null

    override val prefix: XsNCNameValue?
        get() = names.first()

    override val localName: XsNCNameValue?
        get() = names.toList().let { if (it.size == 2) it[1] else null }

    override val isLexicalQName: Boolean = true

    // endregion
    // region PsiElement

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        return PsiReference.EMPTY_ARRAY
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = localName as? PsiElement

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    override fun setName(@NonNls name: String): PsiElement {
        val renamed = createElement<XPathQName>("${prefix?.data ?: ""}:$name") ?: return this
        return replace(renamed)
    }

    // endregion
}

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

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathQNamePsiImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.reference.XQueryQNamePrefixReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.reference.XQueryVariableNameReference

class XQueryQNamePsiImpl(node: ASTNode) : XPathQNamePsiImpl(node) {
    // region PsiElement

    override fun getReferences(): Array<PsiReference> {
        val eqnameStart = node.startOffset
        val localName = localName as? PsiElement
        val localNameRef: PsiReference? =
            if (localName != null) when (parent) {
                is XpmFunctionReference ->
                    XPathFunctionNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                is XpmVariableReference ->
                    XQueryVariableNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                else -> null
            } else {
                null
            }

        val prefix = prefix as? PsiElement
        if (prefix == null) { // local name only
            if (localNameRef != null) {
                return arrayOf(localNameRef)
            }
            return PsiReference.EMPTY_ARRAY
        } else {
            if (localNameRef != null) {
                return arrayOf(
                    XQueryQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)),
                    localNameRef
                )
            }
            return arrayOf(XQueryQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)))
        }
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? =  (parent as NavigatablePsiElement).presentation

    // endregion
}

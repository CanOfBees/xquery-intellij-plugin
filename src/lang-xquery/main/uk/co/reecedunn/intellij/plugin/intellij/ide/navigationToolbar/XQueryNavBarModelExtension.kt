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
package uk.co.reecedunn.intellij.plugin.intellij.ide.navigationToolbar

import com.intellij.compat.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

class XQueryNavBarModelExtension : StructureAwareNavBarModelExtension() {
    override fun getPresentableText(`object`: Any?): String? = getPresentableText(`object`, false)

    override fun getPresentableText(`object`: Any?, forPopup: Boolean): String? {
        if ((`object` as? PsiElement)?.language !== language) return null
        return when (`object`) {
            is ItemPresentationEx -> if (forPopup) `object`.structurePresentableText else `object`.presentableText
            is ItemPresentation -> `object`.presentableText
            else -> null
        }
    }

    override fun getParent(psiElement: PsiElement?): PsiElement? = (psiElement?.containingFile as? XQueryModule)

    override val language: Language = XQuery
}

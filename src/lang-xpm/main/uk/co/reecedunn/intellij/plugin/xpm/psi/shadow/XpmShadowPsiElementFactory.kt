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
package uk.co.reecedunn.intellij.plugin.xpm.psi.shadow

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.xml.qname
import javax.xml.namespace.QName

interface XpmShadowPsiElementFactory {
    companion object {
        val EP_NAME: ExtensionPointName<XpmShadowPsiElementFactoryBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.shadowPsiElementFactory"
        )

        private val factories: Sequence<XpmShadowPsiElementFactoryBean>
            get() = EP_NAME.extensionList.asSequence()

        private val SHADOW_PSI_ELEMENT: Key<Pair<QName?, XpmShadowPsiElement>> = Key.create("SHADOW_PSI_ELEMENT")

        fun create(element: PsiElement): XpmShadowPsiElement? {
            val name = (element as? XmlTag)?.qname()

            element.getUserData(SHADOW_PSI_ELEMENT)?.let {
                if (it.first == name && it.second.isValid) {
                    return it.second
                }
            }

            factories.map { it.getInstance().create(element, name) }.firstOrNull()?.let {
                element.putUserData(SHADOW_PSI_ELEMENT, name to it)
                return it
            }

            return factories.map { it.getInstance().createDefault(element) }.firstOrNull()?.let {
                element.putUserData(SHADOW_PSI_ELEMENT, name to it)
                return it
            }
        }
    }

    fun create(element: PsiElement, name: QName?): XpmShadowPsiElement?

    fun createDefault(element: PsiElement): XpmShadowPsiElement?
}

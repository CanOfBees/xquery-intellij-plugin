/*
 * Copyright (C) 2016, 2018, 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue

class XPathWildcardPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathWildcard {
    private val names: Sequence<XsNCNameValue>
        get() = children().filterIsInstance<XsNCNameValue>()

    override val namespace: XsAnyUriValue?
        get() = firstChild as? XsAnyUriValue

    override val prefix: XsNCNameValue?
        get() = if (isLexicalQName) names.first() else null

    override val localName: XsNCNameValue?
        get() = if (isLexicalQName) names.last() else names.first()

    override val isLexicalQName: Boolean
        get() = namespace == null
}

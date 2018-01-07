/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.NotCacheable
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.xdm.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped
import uk.co.reecedunn.intellij.plugin.xdm.createLexicalQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class XPathQNamePsiImpl(node: ASTNode) : XPathEQNamePsiImpl(node), XPathQName, XdmStaticValue {
    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedConstantValue.invalidate()
    }

    override val cacheable get(): CachingBehaviour = cachedConstantValue.cachingBehaviour

    override val staticType get(): XdmSequenceType = constantValue?.let { XsQName } ?: XsUntyped

    override val constantValue get(): Any? = cachedConstantValue.get()

    private val cachedConstantValue = CacheableProperty {
        val names: List<PsiElement> = findChildrenByType(XQueryElementType.NCNAME)
        when (names.size) {
            2 -> createLexicalQName(
                    names[0].firstChild as XdmLexicalValue,
                    names[1].firstChild as XdmLexicalValue,
                    this)
            else -> null
        } `is` NotCacheable
    }
}

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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSchemaElementTest
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

class XPathSchemaElementTestPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathSchemaElementTest {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region XPathSchemaElementTest

    override val nodeName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        nodeName?.let { "schema-element(${op_qname_presentation(it)})" } ?: "schema-element(<unknown>)"
    }

    override val typeName: String
        get() = cachedTypeName.get()!!

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmElementNode::class.java

    // endregion
}

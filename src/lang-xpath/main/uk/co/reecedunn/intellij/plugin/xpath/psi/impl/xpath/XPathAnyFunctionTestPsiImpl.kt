/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAnyFunctionTest
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmFunction
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType

private val XQUERY30 = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathAnyFunctionTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathAnyFunctionTest, VersionConformance {
    // region XdmSequenceType

    override val typeName: String = "function(*)"

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmFunction::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = XQUERY30

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}

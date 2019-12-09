/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAnyKindTest
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmNode

private val XQUERY10: List<Version> = listOf()
private val MARKLOGIC80: List<Version> = listOf(MarkLogic.VERSION_8_0)

class XPathAnyKindTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathAnyKindTest,
    XdmItemType,
    VersionConformance {
    // region XdmSequenceType

    override val typeName: String = "node()"

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmNode::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            if (conformanceElement === firstChild) {
                return XQUERY10
            }
            return MARKLOGIC80
        }

    override val conformanceElement get(): PsiElement = findChildByType(XPathTokenType.STAR) ?: firstChild

    // endregion
}

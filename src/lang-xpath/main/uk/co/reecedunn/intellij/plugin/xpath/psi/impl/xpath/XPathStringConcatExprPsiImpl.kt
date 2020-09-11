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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringConcatExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathStringConcatExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathStringConcatExpr, VersionConformance {

    override val requiresConformance: List<Version>
        get() {
            if (findChildByType<PsiElement>(XPathTokenType.CONCATENATION) == null) {
                return XQUERY10
            }
            return XQUERY30
        }

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.CONCATENATION) ?: firstChild
}

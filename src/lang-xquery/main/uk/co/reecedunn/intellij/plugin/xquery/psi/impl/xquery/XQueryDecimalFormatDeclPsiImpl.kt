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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDFPropertyName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDecimalFormatDecl
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec

private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408)
private val XQUERY31: List<Version> = listOf(XQuerySpec.REC_3_1_20170321)

class XQueryDecimalFormatDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQueryDecimalFormatDecl, VersionConformance {
    override val requiresConformance: List<Version>
        get() = if (conformanceElement is XQueryDFPropertyName) XQUERY31 else XQUERY30

    override val conformanceElement: PsiElement
        get() {
            return children().filterIsInstance<XQueryDFPropertyName>().filter { e ->
                e.firstChild.elementType === XQueryTokenType.K_EXPONENT_SEPARATOR
            }.firstOrNull() ?: (findChildByType(XQueryTokenType.K_DECIMAL_FORMAT) ?: this)
        }
}

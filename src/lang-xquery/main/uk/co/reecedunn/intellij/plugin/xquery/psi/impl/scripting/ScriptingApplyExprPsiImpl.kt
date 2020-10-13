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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.scripting

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.intellij.lang.ScriptingSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance

private val SCRIPTING10 = listOf(ScriptingSpec.NOTE_1_0_20140918)
private val XQUERY = listOf<Version>()

private val SEPARATOR_TOKENS = TokenSet.create(XQueryTokenType.SEPARATOR, XQueryElementType.TRANSACTION_SEPARATOR)

open class ScriptingApplyExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    ScriptingApplyExpr,
    VersionConformance {
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() {
            val element = conformanceElement
            if (element === firstChild || element.elementType === XQueryElementType.TRANSACTION_SEPARATOR) {
                return XQUERY
            }
            return SCRIPTING10
        }

    override val conformanceElement: PsiElement
        get() = findChildByType(SEPARATOR_TOKENS) ?: firstChild

    // endregion
}

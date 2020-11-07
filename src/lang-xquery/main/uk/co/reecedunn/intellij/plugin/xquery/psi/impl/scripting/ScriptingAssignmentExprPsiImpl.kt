/*
 * Copyright (C) 2016-2017, 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingAssignmentExpr
import uk.co.reecedunn.intellij.plugin.intellij.lang.ScriptingSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

class ScriptingAssignmentExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), ScriptingAssignmentExpr, VersionConformance {
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = findChildByType(XPathTokenType.ASSIGN_EQUAL)!!

    // endregion
    // region XpmVariableBinding

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = listOf(ScriptingSpec.NOTE_1_0_20140918)

    override val conformanceElement: PsiElement
        get() = expressionElement

    // endregion
}

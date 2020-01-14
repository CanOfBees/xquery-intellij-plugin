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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.ScriptingSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.UpdateFacilitySpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import javax.swing.Icon

private val MARKLOGIC_60 = listOf(MarkLogic.VERSION_6_0)
private val SCRIPTING_10 = listOf(ScriptingSpec.NOTE_1_0_20140918)
private val UPDATE_10 = listOf(UpdateFacilitySpec.REC_1_0_20110317)
private val UPDATE_30 = listOf(UpdateFacilitySpec.NOTE_3_0_20170124)

class PluginCompatibilityAnnotationPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginCompatibilityAnnotation,
    XsQNameValue,
    XdmAnnotation,
    VersionConformance,
    ItemPresentation {
    // region XsQNameValue

    override val namespace: XsAnyUriValue? get() = null

    override val prefix: XsNCNameValue? get() = null

    override val localName: XsNCNameValue? get() = firstChild as XsNCNameValue

    override val isLexicalQName: Boolean get() = true

    // endregion
    // region XdmAnnotation

    override val name: XsQNameValue? get() = this

    override val values: Sequence<XsAnyAtomicType> = emptySequence()

    // endregion
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            return when (conformanceElement.node.elementType) {
                XQueryTokenType.K_PRIVATE -> MARKLOGIC_60
                XQueryTokenType.K_UPDATING -> {
                    val varDecl = parent.node.findChildByType(XQueryElementType.VAR_DECL)
                    if (varDecl == null) UPDATE_10 else UPDATE_30
                }
                else -> SCRIPTING_10
            }
        }

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XQueryIcons.Nodes.Annotation

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = firstChild.text

    // endregion
}

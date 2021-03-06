/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.optree

import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.model.annotatedDeclarations
import uk.co.reecedunn.intellij.plugin.xquery.model.importedPrologs
import uk.co.reecedunn.intellij.plugin.xquery.model.importedPrologsForQName
import uk.co.reecedunn.intellij.plugin.xquery.model.staticallyKnownFunctions

object XQueryFunctionProvider : XpmFunctionProvider {
    override fun staticallyKnownFunctions(file: PsiFile): Sequence<XpmFunctionDeclaration> {
        val module = file as? XQueryModule
        val prolog = module?.mainOrLibraryModule?.prolog?.firstOrNull()
            ?: module?.predefinedStaticContext
            ?: return emptySequence()

        return prolog.importedPrologs().flatMap {
            it.annotatedDeclarations<XpmFunctionDeclaration>()
        }.filter { decl -> decl.functionName != null }
    }

    override fun staticallyKnownFunctions(eqname: XsQNameValue): Sequence<XpmFunctionDeclaration> {
        if (eqname.element?.containingFile !is XQueryModule) return emptySequence()
        return eqname.importedPrologsForQName().flatMap { (name, prolog) ->
            prolog.staticallyKnownFunctions(name!!)
        }.filterNotNull()
    }
}

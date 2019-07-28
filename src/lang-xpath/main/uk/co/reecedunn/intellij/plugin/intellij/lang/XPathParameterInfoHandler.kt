/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall

object XPathParameterInfoHandler : ParameterInfoHandler<XPathFunctionCall, Any> {
    override fun couldShowInLookup(): Boolean = true

    override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? {
        return null
    }

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): XPathFunctionCall? {
        val e = context.file.findElementAt(context.offset)
        return e?.ancestors()?.filterIsInstance<XPathFunctionCall>()?.firstOrNull()
    }

    override fun showParameterInfo(element: XPathFunctionCall, context: CreateParameterInfoContext) {
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): XPathFunctionCall? {
        val e = context.file.findElementAt(context.offset)
        return e?.ancestors()?.filterIsInstance<XPathFunctionCall>()?.firstOrNull()
    }

    override fun updateParameterInfo(parameterOwner: XPathFunctionCall, context: UpdateParameterInfoContext) {
    }

    override fun updateUI(p: Any?, context: ParameterInfoUIContext) {
    }
}

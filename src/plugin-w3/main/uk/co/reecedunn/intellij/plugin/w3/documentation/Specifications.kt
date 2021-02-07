/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.w3.documentation

import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xpath.lang.FunctionsAndOperatorsSpec
import uk.co.reecedunn.intellij.plugin.xpm.lang.documentation.XpmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration

object FunctionsAndOperatorsDocumentation : XQDocDocumentationSourceProvider, XQDocDocumentationIndex {
    // region Namespaces

    private val NAMESPACES_31 = mapOf(
        "http://www.w3.org/2005/xpath-functions/array" to "array",
        "http://www.w3.org/2005/xpath-functions" to "fn",
        "http://www.w3.org/2005/xpath-functions/map" to "map",
        "http://www.w3.org/2005/xpath-functions/math" to "math"
    )

    // endregion
    // region XQDocDocumentationIndex

    private val active = FunctionsAndOperatorsSpec.REC_3_1_20170321 as XpmDocumentationSource

    private val doc = CacheableProperty {
        val file = XQDocDocumentationDownloader.getInstance().load(active, download = true)
        file?.let { Jsoup.parse(it.inputStream, null, "") }
    }

    override fun invalidate(source: XpmDocumentationSource) {
        if (source === active) {
            doc.invalidate()
        }
    }

    override fun lookup(ref: XpmFunctionReference): XQDocFunctionDocumentation? {
        val prefix = NAMESPACES_31[ref.functionName?.namespace?.data] ?: return null
        val localName = ref.functionName?.localName?.data ?: return null
        val lookupName = "$prefix:$localName"
        val match = doc.get()?.select("h3 > a, h4 > a")?.firstOrNull {
            val parts = (it.nextSibling() as? TextNode)?.text()?.split(" ") ?: return@firstOrNull false
            val name = parts.asReversed().find { part -> part.isNotEmpty() }
            name == lookupName
        }
        return match?.let { W3CFunctionReference(it.parent().parent(), active.href) }
    }

    override fun lookup(decl: XpmNamespaceDeclaration): XQDocDocumentation? = null

    override val sources: List<XpmDocumentationSource> by lazy {
        FunctionsAndOperatorsSpec.versions.filterIsInstance<XpmDocumentationSource>()
    }

    // endregion
}

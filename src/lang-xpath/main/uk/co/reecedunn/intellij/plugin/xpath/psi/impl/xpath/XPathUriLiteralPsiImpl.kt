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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyAtomicType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmUriContext

class XPathUriLiteralPsiImpl(node: ASTNode) : XPathStringLiteralPsiImpl(node), XPathUriLiteral {
    override val cachedValue: CacheableProperty<XsAnyAtomicType> = CacheableProperty {
        val context = when (parent) {
            else -> XdmUriContext.Namespace
        }
        XsAnyUri(content, context, this)
    }
}

/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

enum class XstUsageType {
    Attribute,
    Element,
    Namespace
}

fun XPathNodeTest.getPrincipalNodeKind(): XstUsageType {
    return when (parent.node.elementType) {
        XPathElementType.ABBREV_FORWARD_STEP -> XstUsageType.Attribute
        XPathElementType.FORWARD_STEP -> when (parent.firstChild.firstChild.node.elementType) {
            XPathTokenType.K_ATTRIBUTE -> XstUsageType.Attribute
            XPathTokenType.K_NAMESPACE -> XstUsageType.Namespace
            else -> XstUsageType.Element
        }
        else -> XstUsageType.Element
    }
}

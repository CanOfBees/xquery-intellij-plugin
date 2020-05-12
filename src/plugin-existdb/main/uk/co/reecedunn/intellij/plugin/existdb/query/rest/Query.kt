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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import org.w3c.dom.Document
import org.w3c.dom.Element
import uk.co.reecedunn.intellij.plugin.core.xml.cdata
import uk.co.reecedunn.intellij.plugin.core.xml.document
import uk.co.reecedunn.intellij.plugin.core.xml.element
import uk.co.reecedunn.intellij.plugin.core.xml.text

private const val EXIST_NAMESPACE = "http://exist.sourceforge.net/NS/exist"

private const val SERIALIZED_NAMESPACE = "http://exist-db.org/xquery/types/serialized"

fun exist_query(init: Element.() -> Unit): Document = document {
    element(EXIST_NAMESPACE, "query") {
        setAttribute("start", "1")
        setAttribute("max", "18446744073709551615")
        setAttribute("cache", "no")
        setAttribute("session-id", "")
        init()
    }
}

fun Element.exist_text(text: String) = element(EXIST_NAMESPACE, "text") { cdata(text) }

fun Element.exist_variables(init: Element.() -> Unit) = element(EXIST_NAMESPACE, "variables", init)

fun Element.exist_variable(name: String, init: Element.() -> Unit) = element(EXIST_NAMESPACE, "variable") {
    element(EXIST_NAMESPACE, "qname") {
        element(EXIST_NAMESPACE, "localname") { text(name) }
    }
    element(SERIALIZED_NAMESPACE, "sequence", init)
}

fun Element.exist_value(value: String, type: String) = element(SERIALIZED_NAMESPACE, "value") {
    setAttribute("type", type)
    cdata(value)
}

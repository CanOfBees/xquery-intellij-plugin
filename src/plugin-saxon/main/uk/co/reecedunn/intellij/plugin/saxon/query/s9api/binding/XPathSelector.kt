/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding

class XPathSelector(private val `object`: Any, private val `class`: Class<*>) {
    fun setContextItem(item: XdmItem) {
        val xdmItemClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmItem")
        `class`.getMethod("setContextItem", xdmItemClass).invoke(`object`, item.saxonObject)
    }

    fun setVariable(qname: QName, value: XdmValue) {
        val xdmValueClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmValue")
        `class`.getMethod("setVariable", qname.saxonClass, xdmValueClass).invoke(
            `object`, qname.`object`, value.saxonObject
        )
    }

    fun iterator(): XdmSequenceIterator {
        val xdmSequenceIteratorClass = `class`.classLoader.loadClass("net.sf.saxon.s9api.XdmSequenceIterator")
        return XdmSequenceIterator(`class`.getMethod("iterator").invoke(`object`), xdmSequenceIteratorClass)
    }
}

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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.om

import uk.co.reecedunn.intellij.plugin.core.reflection.loadClassOrNull
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmValue

open class Sequence(protected val `object`: Any, protected val saxonClass: Class<*>) {
    open fun getXdmValue(): XdmValue {
        val xdmValueClass = saxonClass.classLoader.loadClass("net.sf.saxon.s9api.XdmValue")
        return XdmValue(xdmValueClass.getMethod("wrap", saxonClass).invoke(null, `object`), xdmValueClass)
    }

    companion object {
        fun create(sequence: Any, classLoader: ClassLoader): Sequence {
            val sequenceClass = classLoader.loadClassOrNull("net.sf.saxon.om.Sequence") // Saxon >= 9.5
            return if (sequenceClass != null) { // Saxon >= 9.5
                Sequence(sequence, sequenceClass)
            } else { // Saxon <= 9.4
                ValueRepresentation(sequence, classLoader.loadClass("net.sf.saxon.om.ValueRepresentation"))
            }
        }

        fun create(sequence: Array<*>, classLoader: ClassLoader): List<Sequence> {
            val sequenceClass = classLoader.loadClassOrNull("net.sf.saxon.om.Sequence") // Saxon >= 9.5
            return if (sequenceClass != null) { // Saxon >= 9.5
                sequence.map { Sequence(it!!, sequenceClass) }
            } else { // Saxon <= 9.4
                val valueRepresentationClass = classLoader.loadClass("net.sf.saxon.om.ValueRepresentation")
                sequence.map { ValueRepresentation(it!!, valueRepresentationClass) }
            }
        }
    }
}

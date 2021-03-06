/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import java.io.Closeable

interface Query : Closeable {
    var rdfOutputFormat: Language?

    var updating: Boolean

    var xpathSubset: XPathSubset

    var server: String

    var database: String

    var modulePath: String

    fun bindVariable(name: String, value: Any?, type: String?)

    fun bindContextItem(value: Any?, type: String?)
}

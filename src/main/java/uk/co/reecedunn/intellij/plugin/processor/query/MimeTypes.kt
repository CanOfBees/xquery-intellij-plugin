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
package uk.co.reecedunn.intellij.plugin.processor.query

object MimeTypes {
    // xq;xqy;xquery -- standard defined extensions
    // xql           -- XQuery Language (main) file [eXist-db; BaseX]
    // xqu           -- XQuery file [BaseX]
    private val XQUERY_SCRIPT_EXT = arrayOf("xq", "xqy", "xquery", "xql", "xqu")

    val XQUERY = "application/xquery"

    fun extensions(mimetype: String): Array<String> {
        return when (mimetype) {
            XQUERY -> XQUERY_SCRIPT_EXT
            else -> arrayOf()
        }
    }
}

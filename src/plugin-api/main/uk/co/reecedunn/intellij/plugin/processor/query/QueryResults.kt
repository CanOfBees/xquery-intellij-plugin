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
package uk.co.reecedunn.intellij.plugin.processor.query

import org.apache.http.ProtocolVersion
import org.apache.http.StatusLine
import org.apache.http.message.BasicStatusLine
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

data class QueryResults(
    val status: StatusLine,
    val results: List<QueryResult>,
    val elapsed: XsDurationValue
) {
    companion object {
        val OK: StatusLine = BasicStatusLine(ProtocolVersion("HTTP", 1, 1), 200, "OK")
    }
}

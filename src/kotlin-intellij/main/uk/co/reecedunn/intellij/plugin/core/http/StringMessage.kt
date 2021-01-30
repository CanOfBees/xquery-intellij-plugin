/*
 * Copyright (C) 2017, 2019, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.http

import org.apache.http.Header

class StringMessage(val headers: Array<Header>, val body: String) {
    fun getHeaders(header: String): Sequence<String> {
        return this.headers.asSequence().filter { h -> h.name == header }.map { h -> h.value }
    }

    fun getHeader(header: String): String? = getHeaders(header).firstOrNull()
}

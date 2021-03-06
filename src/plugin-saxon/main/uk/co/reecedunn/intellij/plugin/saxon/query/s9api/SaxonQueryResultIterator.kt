/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.XdmSequenceIterator

internal class SaxonQueryResultIterator(
    private val results: XdmSequenceIterator,
    processor: Processor
) : Iterator<QueryResult> {

    private var position: Long = -1
    private val typeHierarchy: Any = processor.typeHierarchy

    override fun hasNext(): Boolean = results.hasNext()

    override fun next(): QueryResult {
        val next = results.next()
        val type = next.getItemType(typeHierarchy)
        return QueryResult.fromItemType(++position, next.toString(), type.toString())
    }
}

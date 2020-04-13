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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner

import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.proxy.TraceListener

open class SaxonTraceListener : TraceListener {
    // region TraceListener

    override fun setOutputDestination(logger: Any) {
    }

    override fun open(controller: Any?) {
    }

    override fun close() {
    }

    override fun enter(instruction: InstructionInfo, context: Any) {
    }

    override fun leave(instruction: InstructionInfo) {
    }

    override fun startCurrentItem(currentItem: Any) {
    }

    override fun endCurrentItem(currentItem: Any) {
    }

    override fun startRuleSearch() {
    }

    override fun endRuleSearch(rule: Any, mode: Any, item: Any) {
    }
}

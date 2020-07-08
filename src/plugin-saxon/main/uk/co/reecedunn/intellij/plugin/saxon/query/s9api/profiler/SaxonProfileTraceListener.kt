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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api.profiler

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.expr.XPathContext
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trace.InstructionInfo
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger.SaxonStackFrame
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.runner.SaxonTraceListener
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

private val XMLSCHEMA_DATETIME_FORMAT: DateFormat by lazy {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    format
}

class SaxonProfileInstruction(
    val instruction: InstructionInfo,
    var totalTime: Long,
    var count: Int = 1
)

class SaxonProfileTraceListener(val version: String, val query: VirtualFile) : SaxonTraceListener() {
    var elapsed: Long = 0
    var created: Date? = null

    private val instructions: Stack<SaxonProfileInstruction> = Stack()
    val results: HashMap<InstructionInfo, SaxonProfileInstruction> = HashMap()

    override fun setOutputDestination(logger: Any) {
    }

    override fun onstart() {
        elapsed = System.nanoTime()
        created = Date()
    }

    override fun onfinish() {
        elapsed = System.nanoTime() - elapsed
    }

    override fun enter(instruction: InstructionInfo, properties: Map<String, Any>, context: XPathContext) {
        super.enter(instruction, properties, context)

        // The ClauseInfo instructions are different for each iteration, so ignore them.
        if (instruction.isClauseInfo()) return

        instructions.push(SaxonProfileInstruction(instruction, System.nanoTime()))
    }

    override fun leave(instruction: InstructionInfo) {
        super.leave(instruction)

        // The ClauseInfo instructions are different for each iteration, so ignore them.
        if (instruction.isClauseInfo()) return

        val current = instructions.pop()
        current.totalTime = System.nanoTime() - current.totalTime

        val result = results[instruction]
        if (result == null) {
            results[instruction] = current
        } else {
            result.totalTime += current.totalTime
            result.count += 1
        }
    }
}

fun SaxonProfileInstruction.toProfileEntry(query: VirtualFile): FlatProfileEntry {
    val totalTimeDuration = XsDuration.ns(totalTime)
    return FlatProfileEntry(
        id = instruction.hashCode().toString(),
        context = instruction.getObjectName()?.toString() ?: "",
        count = count,
        selfTime = totalTimeDuration,
        totalTime = totalTimeDuration,
        frame = SaxonStackFrame.create(instruction, query)
    )
}

fun SaxonProfileTraceListener.toProfileReport(): FlatProfileReport {
    val elapsed = XsDuration.ns(elapsed)
    return FlatProfileReport(
        xml = null,
        elapsed = elapsed,
        created = created?.let { XMLSCHEMA_DATETIME_FORMAT.format(it) } ?: "",
        version = version,
        results = sequenceOf(
            sequenceOf(FlatProfileEntry("", "", 1, XsDuration.ZERO, elapsed, VirtualFileStackFrame(query))),
            results.values.asSequence().map { result -> result.toProfileEntry(query) }
        ).flatten()
    )
}

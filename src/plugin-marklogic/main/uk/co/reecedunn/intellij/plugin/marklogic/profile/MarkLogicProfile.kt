/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.profile

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration
import uk.co.reecedunn.intellij.plugin.xpath.model.toXsDuration

private val PROFILE_NAMESPACES = mapOf("prof" to "http://marklogic.com/xdmp/profile")

private fun XmlElement.toProfileEntry(queryFile: VirtualFile): FlatProfileEntry {
    val path = children("prof:uri").first().text()
    return FlatProfileEntry(
        id = children("prof:expr-id").first().text()!!,
        context = children("prof:expr-source").first().text()!!,
        frame = StackFrame(
            path?.nullize()?.let { DatabaseModule(it) } ?: queryFile,
            children("prof:line").first().text()?.toIntOrNull() ?: 1,
            children("prof:column").first().text()?.toIntOrNull() ?: 1
        ),
        count = children("prof:count").first().text()!!.toInt(),
        selfTime = children("prof:shallow-time").first().text()?.toXsDuration()!!,
        totalTime = children("prof:deep-time").first().text()?.toXsDuration()!!
    )
}

fun String.toMarkLogicProfileReport(queryFile: VirtualFile): FlatProfileReport {
    val doc = XmlDocument.parse(this, PROFILE_NAMESPACES)
    val metadata = doc.root.children("prof:metadata").first()
    val histogram = doc.root.children("prof:histogram").first()
    val elapsed = metadata.children("prof:overall-elapsed").first().text()?.toXsDuration()!!
    return FlatProfileReport(
        xml = this,
        elapsed = elapsed,
        created = metadata.children("prof:created").first().text()!!,
        version = metadata.children("prof:server-version").first().text()!!,
        results = sequenceOf(
            sequenceOf(FlatProfileEntry("", "", 1, XsDuration.ZERO, elapsed, StackFrame(queryFile, 1, 1))),
            histogram.children("prof:expression").map { expression -> expression.toProfileEntry(queryFile) }
        ).flatten()
    )
}

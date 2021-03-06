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
package uk.co.reecedunn.intellij.plugin.core.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import uk.co.reecedunn.intellij.plugin.core.parser.Markdown
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile

abstract class Inspection(
    private val descriptionPath: String,
    private val loader: ClassLoader
) : LocalInspectionTool() {
    override fun getStaticDescription(): String? {
        val description = ResourceVirtualFile.create(loader, "inspections/en/$descriptionPath")
        return description.inputStream?.let { Markdown.parse(it) }
    }
}

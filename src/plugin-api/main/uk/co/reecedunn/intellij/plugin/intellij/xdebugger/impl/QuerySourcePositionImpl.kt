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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.xdebugger.XSourcePosition
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition

class QuerySourcePositionImpl(private val position: XSourcePosition, override val column: Int) : QuerySourcePosition {
    override fun getFile(): VirtualFile = position.file

    override fun getLine(): Int = position.line

    override fun getOffset(): Int = position.offset

    override fun createNavigatable(project: Project): Navigatable = position.createNavigatable(project)
}

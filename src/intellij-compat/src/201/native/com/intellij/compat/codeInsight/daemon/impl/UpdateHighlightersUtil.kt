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
package com.intellij.compat.codeInsight.daemon.impl

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.Project

fun setHighlightersToSingleEditor(
    project: Project,
    editor: Editor,
    startOffset: Int,
    endOffset: Int,
    highlights: Collection<HighlightInfo?>,
    colorScheme: EditorColorsScheme?,
    group: Int
) {
    UpdateHighlightersUtil.setHighlightersToSingleEditor(
        project, editor, startOffset, endOffset, highlights, colorScheme, group
    )
}

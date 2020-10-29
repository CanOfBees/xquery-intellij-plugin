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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting

import com.intellij.application.options.editor.WebEditorOptions
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil.setHighlightersToSingleEditor
import com.intellij.codeInsight.daemon.impl.tagTreeHighlighting.XmlTagTreeHighlightingPass
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.markup.*
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.xml.breadcrumbs.BreadcrumbsUtilEx
import com.intellij.xml.breadcrumbs.PsiFileBreadcrumbsCollector.getLinePsiElements
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting.XQueryElemTagRangesProvider.getElementTagRanges
import java.awt.Color
import java.awt.Font

class XQueryElemTagTreeHighlightingPass(val file: PsiFile, val editor: EditorEx) :
    TextEditorHighlightingPass(file.project, editor.document, true) {

    val provider = BreadcrumbsUtilEx.findProvider(false, file.manager.findViewProvider(file.virtualFile)!!)
    var tagsToHighlight: List<Pair<TextRange?, TextRange?>> = listOf()

    val highlights: List<HighlightInfo>
        get() {
            val colors = toColorsForEditor(getBaseColors(), editor.backgroundColor)

            val highlights = ArrayList<HighlightInfo>(tagsToHighlight.size * 2)
            tagsToHighlight.withIndex().forEach { (index, ranges) ->
                val color = colors[index % colors.size] ?: return@forEach
                ranges.first.takeIf { it != null && !it.isEmpty }?.let {
                    highlights.add(createHighlightInfo(color, it))
                }
                ranges.second.takeIf { it != null && !it.isEmpty }?.let {
                    highlights.add(createHighlightInfo(color, it))
                }
            }
            return highlights
        }

    val lineMarkers: List<RangeHighlighter>
        get() {
            val colors = toColorsForLineMarkers(getBaseColors())
            return tagsToHighlight.withIndex().mapNotNull { (index, ranges) ->
                val color = colors[index % colors.size] ?: return@mapNotNull null
                val start = ranges.first?.startOffset ?: ranges.second?.startOffset ?: return@mapNotNull null
                val end = ranges.second?.endOffset ?: ranges.first?.endOffset ?: return@mapNotNull null
                if (tagsToHighlight.size > 1 && start != end)
                    createHighlighter(editor.markupModel, start, end, color)
                else
                    null
            }
        }

    override fun doCollectInformation(progress: ProgressIndicator) {
        if (WebEditorOptions.getInstance().isTagTreeHighlightingEnabled) {
            val offset: Int = editor.caretModel.offset
            val elements = getLinePsiElements(document, offset, file.virtualFile, file.project, provider)
            tagsToHighlight = getElementRanges(elements)
        }
    }

    override fun doApplyInformationToEditor() {
        setHighlightersToSingleEditor(myProject, editor, 0, file.textLength, highlights, colorsScheme, id)

        clearLineMarkers()
        editor.putUserData(XQUERY_TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY, lineMarkers)
    }

    private fun getElementRanges(elements: Array<out PsiElement?>?): List<Pair<TextRange?, TextRange?>> = when {
        elements.isNullOrEmpty() -> listOf()
        !isTagTreeHighlightingActive(elements.last()!!.containingFile) -> listOf()
        !containsTagsWithSameName(elements) -> listOf()
        else -> elements.reversed().mapNotNull {
            if (it is XQueryDirElemConstructor)
                getElementTagRanges(it)
            else
                null
        }
    }

    private fun clearLineMarkers() {
        val markupModel = editor.markupModel
        editor.getUserData(XQUERY_TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY)?.forEach {
            if (markupModel.containsHighlighter(it)) {
                it.dispose()
            }
        }
    }

    companion object {
        private fun createHighlightInfo(color: Color, range: TextRange): HighlightInfo {
            return HighlightInfo.newHighlightInfo(TYPE)
                .range(range)
                .textAttributes(TextAttributes(null, color, null, null, Font.PLAIN))
                .severity(HighlightSeverity.INFORMATION)
                .createUnconditionally()
        }

        private fun createHighlighter(markupModel: MarkupModel, start: Int, end: Int, color: Color): RangeHighlighter {
            val highlighter = markupModel.addRangeHighlighter(
                null,
                start,
                end,
                0,
                HighlighterTargetArea.LINES_IN_RANGE
            )
            highlighter.lineMarkerRenderer = LineMarkerRenderer { _, g, r ->
                g.color = color
                g.fillRect(r.x - 1, r.y, 2, r.height)
            }
            return highlighter
        }

        // NOTE: XmlTagTreeHighlightingPass.TYPE is private, so we need to redefine it here.
        private val TYPE = HighlightInfoType.HighlightInfoTypeImpl(
            HighlightSeverity.INFORMATION,
            XmlTagTreeHighlightingPass.TAG_TREE_HIGHLIGHTING_KEY
        )

        private val XQUERY_TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY =
            Key.create<List<RangeHighlighter>>("XQUERY_TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY")
    }
}

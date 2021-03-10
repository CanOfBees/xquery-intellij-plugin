/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.run.execution.ui

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.core.io.printCharsToString
import uk.co.reecedunn.intellij.plugin.processor.run.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.processor.run.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import javax.swing.JComponent

class QueryTextConsoleView(project: Project) : TextConsoleView(project), QueryResultListener {
    // region ConsoleView

    private var queryProcessHandler: QueryProcessHandlerBase? = null

    override fun getComponent(): JComponent {
        val component = super.getComponent() // Ensure the text view is initialized.

        // Add the text console's action toolbar to the text console itself,
        // not the result view console. This ensures that the text view editor
        // does not grab the table's keyboard navigation events.
        createActionToolbar(ActionPlaces.RUNNER_TOOLBAR)

        return component
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
        queryProcessHandler = processHandler as? QueryProcessHandlerBase
        queryProcessHandler?.addQueryResultListener(this)
    }

    // endregion
    // region Disposable

    override fun dispose() {
        super.dispose()
        queryProcessHandler?.removeQueryResultListener(this)
    }

    // endregion
    // region QueryResultListener

    private var activeLanguage: Language? = null
    private var psiFile: PsiFile? = null

    override fun onBeginResults() {
        activeLanguage = null
        psiFile = null
        clear()
    }

    override fun onEndResults(handler: (PsiFile) -> Unit) {
        if (contentSize == 0) {
            print("()", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        if (activeLanguage != null) {
            val doc = editor?.document ?: return
            performWhenNoDeferredOutput {
                PsiFileFactory.getInstance(project).createFileFromText(activeLanguage!!, doc.text)?.let { psiFile ->
                    psiFile.viewProvider.virtualFile.putUserData(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY, doc)
                    handler(psiFile)
                }
            }
        }
    }

    override fun onQueryResult(result: QueryResult) {
        when (result.type) {
            "binary()", "xs:hexBinary", "xs:base64Binary" -> {
                val length = (result.value as? String)?.length ?: 0
                print("Binary data ($length bytes)", ConsoleViewContentType.NORMAL_OUTPUT)
            }
            else -> {
                val newLanguage = Language.getRegisteredLanguages().find { it.mimeTypes.contains(result.mimetype) }
                when {
                    newLanguage?.associatedFileType == null -> { // No language found to highlight.
                    }
                    newLanguage === activeLanguage -> { // Same language as the current highlight language.
                    }
                    activeLanguage === PlainTextLanguage.INSTANCE -> { // Multiple file types.
                    }
                    activeLanguage != null -> { // Multiple file types... don't highlight.
                        activeLanguage = PlainTextLanguage.INSTANCE
                        activeLanguage?.associatedFileType!!.let {
                            val provider = FileTypeEditorHighlighterProviders.INSTANCE.forFileType(it)
                            editor!!.highlighter = provider.getEditorHighlighter(
                                project, it, null, editor!!.colorsScheme
                            )
                        }
                    }
                    else -> {
                        newLanguage.associatedFileType!!.let {
                            activeLanguage = newLanguage
                            val provider = FileTypeEditorHighlighterProviders.INSTANCE.forFileType(it)
                            editor!!.highlighter = provider.getEditorHighlighter(
                                project, it, null, editor!!.colorsScheme
                            )
                        }
                    }
                }
                print(result.value.toString(), ConsoleViewContentType.NORMAL_OUTPUT)
            }
        }
        print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun onException(e: Throwable) {
        if (e is QueryError) {
            print("$e\n", ConsoleViewContentType.ERROR_OUTPUT)
            e.value.withIndex().forEach {
                if (it.index == 0) {
                    print("  with ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                } else {
                    print("   and ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                }
            }
            e.frames.asSequence().mapNotNull { it.sourcePosition }.forEach { position ->
                print("    at ", ConsoleViewContentType.ERROR_OUTPUT)

                val navigatable = position.createNavigatable(project)
                if (navigatable.canNavigate()) {
                    printHyperlink(position.file.path) { navigatable.navigate(true) }
                } else {
                    print(position.file.path, ConsoleViewContentType.ERROR_OUTPUT)
                }

                when (position) {
                    is QuerySourcePosition -> {
                        print(":${position.line + 1}:${position.column + 1}\n", ConsoleViewContentType.ERROR_OUTPUT)
                    }
                    else -> {
                        print(":${position.line + 1}\n", ConsoleViewContentType.ERROR_OUTPUT)
                    }
                }
            }
        } else {
            print("${printCharsToString { e.printStackTrace(it) }}\n", ConsoleViewContentType.ERROR_OUTPUT)
        }
    }

    override fun onQueryElapsedTime(time: XsDurationValue) {
    }

    override fun onQueryResultsPsiFile(psiFile: PsiFile) {
    }

    // endregion
}

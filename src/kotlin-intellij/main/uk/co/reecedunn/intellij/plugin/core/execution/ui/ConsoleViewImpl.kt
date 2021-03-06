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
package uk.co.reecedunn.intellij.plugin.core.execution.ui

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.*
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

open class ConsoleViewImpl : JPanel(BorderLayout()), ConsoleView, DataProvider {
    private var helpId: String? = null

    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
    }

    override fun setHelpId(helpId: String) {
        this.helpId = helpId
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
    }

    override fun getContentSize(): Int = 0

    override fun setOutputPaused(value: Boolean) {
    }

    override fun createConsoleActions(): Array<AnAction> = AnAction.EMPTY_ARRAY

    override fun getComponent(): JComponent = this

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
    }

    override fun getPreferredFocusableComponent(): JComponent = this

    override fun isOutputPaused(): Boolean = false

    override fun addMessageFilter(filter: Filter) {
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
        print(hyperlinkText, ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun canPause(): Boolean = false

    override fun allowHeavyFilters() {
    }

    override fun dispose() {
    }

    override fun scrollTo(offset: Int) {
    }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        PlatformDataKeys.HELP_ID.name -> helpId
        LangDataKeys.CONSOLE_VIEW.name -> this
        else -> null
    }

    // endregion
}

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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import uk.co.reecedunn.intellij.plugin.core.event.Stopwatch
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewEx
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewImpl
import uk.co.reecedunn.intellij.plugin.core.text.Units
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration
import uk.co.reecedunn.intellij.plugin.xdm.types.toSeconds
import java.awt.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class QueryConsoleView(val project: Project, val console: ConsoleViewEx) : ConsoleViewImpl(), QueryResultListener {
    companion object {
        private const val SPLITTER_KEY = "XQueryIntelliJPlugin.QueryResultView.Splitter"
    }

    private var summary: JLabel? = null

    private var tables: ArrayList<QueryTable> = ArrayList()
    private var currentSize = 0

    private var timer = object : Stopwatch() {
        override fun isRunning(): Boolean = tables.first().isRunning

        override fun onInterval() {
            val elapsed = XsDuration.ns(elapsedTime).toSeconds(Units.Precision.milliWithZeros)
            tables.forEach {
                it.runningText = PluginApiBundle.message("query.result.table.results-elapsed", elapsed)
            }
        }
    }

    fun registerQueryTable(table: QueryTable) {
        tables.add(table)
    }

    private fun createResultTable(): JComponent {
        val table = QueryResultTable()

        table.selectionModel.addListSelectionListener {
            table.selectedObject?.second?.let { range ->
                console.scrollToTop(range.offset)
            }
        }

        registerQueryTable(table)
        return JBScrollPane(table)
    }

    private fun createResultPanel(): JComponent {
        summary = JLabel()

        val infoPanel = JPanel(VerticalLayout(0))
        summary!!.border = EmptyBorder(5, 4, 5, 4)
        infoPanel.add(summary)

        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
            Insets(0, 0, 0, 0),
            0, 0
        )
        panel.add(infoPanel, constraints)

        constraints.gridy = 1
        constraints.weighty = 1.0
        constraints.fill = GridBagConstraints.BOTH
        panel.add(createResultTable(), constraints)

        infoPanel.border = Borders.TableHeaderBottom
        return panel
    }

    // region ConsoleView

    private var queryProcessHandler: QueryProcessHandlerBase? = null

    override fun clear() {
        console.clear()

        currentSize = 0

        summary!!.text = "\u00A0"

        (tables.first() as QueryResultTable).removeAll()

        tables.forEach {
            it.isRunning = false
            it.hasException = false
        }
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        console.print(text, contentType)
    }

    override fun getContentSize(): Int = console.contentSize

    override fun attachToProcess(processHandler: ProcessHandler?) {
        console.attachToProcess(processHandler)
        queryProcessHandler = processHandler as? QueryProcessHandlerBase
        queryProcessHandler?.addQueryResultListener(this)
    }

    override fun getComponent(): JComponent {
        if (tables.isEmpty()) {
            val splitPane = OnePixelSplitter(false)
            splitPane.firstComponent = console.component
            splitPane.secondComponent = createResultPanel()
            splitPane.secondComponent.minimumSize = Dimension(250, -1)
            splitPane.setHonorComponentsMinimumSize(true)
            splitPane.setAndLoadSplitterProportionKey(SPLITTER_KEY)
            add(splitPane, BorderLayout.CENTER)
        }
        return super.getComponent()
    }

    override fun scrollTo(offset: Int) {
        console.scrollTo(offset)
    }

    // endregion
    // region Disposable

    override fun dispose() {
        queryProcessHandler?.removeQueryResultListener(this)
        console.dispose()
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        clear()
        summary!!.text = "\u00A0"

        tables.forEach { it.isRunning = true }
        timer.start(10)
    }

    override fun onEndResults(): PsiFile? {
        tables.forEach { it.isRunning = false }
        return null
    }

    override fun onQueryResult(result: QueryResult, isSingleResult: Boolean) {
        (tables.first() as QueryResultTable).addRow(result, currentSize)
        currentSize = contentSize
    }

    override fun onException(e: Throwable) {
        tables.forEach { it.hasException = true }
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
        when (resultTime) {
            QueryResultTime.Elapsed -> {
                val count = tables.first().itemCount
                val elapsed = time.toSeconds(Units.Precision.nano)
                summary!!.text = PluginApiBundle.message("console.summary.label", count, elapsed)
            }
        }
    }

    // endregion
}

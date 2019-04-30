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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.results

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.project.Project
import com.intellij.ui.components.panels.Wrapper
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.database.resolve
import uk.co.reecedunn.intellij.plugin.processor.debug.createNavigatable
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import java.awt.BorderLayout
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JComponent

class QueryTextConsoleView(project: Project) : TextConsoleView(project), QueryResultListener {
    // region ConsoleView

    override fun getComponent(): JComponent {
        val component = super.getComponent() // Ensure the text view is initialized.

        // Add the text console's action toolbar to the text console itself,
        // not the result view console. This ensures that the text view editor
        // does not grab the table's keyboard navigation events.

        val actions = DefaultActionGroup()
        actions.addAll(*createConsoleActions())

        val toolbar = ActionManagerEx.getInstanceEx().createActionToolbar(ActionPlaces.RUNNER_TOOLBAR, actions, false)
        toolbar.setTargetComponent(this)

        // Setting a border on the toolbar removes the standard padding/spacing,
        // so set the border on a panel that wraps the toolbar element.
        val wrapper = Wrapper()
        wrapper.add(toolbar.component)
        wrapper.border = Borders.ConsoleToolbarRight
        add(wrapper, BorderLayout.LINE_START)

        return component
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? QueryProcessHandlerBase)?.addQueryResultListener(this, this)
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        clear()
    }

    override fun onEndResults() {
        if (contentSize == 0) {
            print("()", ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    override fun onQueryResult(result: QueryResult) {
        when (result.type) {
            "binary()", "xs:hexBinary", "xs:base64Binary" -> {
                val length = (result.value as? String)?.length ?: 0
                print("Binary data ($length bytes)", ConsoleViewContentType.NORMAL_OUTPUT)
            }
            else -> print(result.value.toString(), ConsoleViewContentType.NORMAL_OUTPUT)
        }
        print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun onException(e: Throwable) {
        print("${e.message ?: e.javaClass.name}\n", ConsoleViewContentType.ERROR_OUTPUT)
        if (e is QueryError) {
            e.value.withIndex().forEach {
                if (it.index == 0) {
                    print("  with ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                } else {
                    print("   and ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                }
            }
            e.frames.forEach {
                print("    at ", ConsoleViewContentType.ERROR_OUTPUT)
                if (it.module != null) {
                    val resolved = when(it.module) {
                        is DatabaseModule -> it.module.resolve(project).firstOrNull()
                        else -> it.module
                    }
                    if (resolved == null)
                        print(it.module.path, ConsoleViewContentType.ERROR_OUTPUT)
                    else
                        printHyperlink(it.module.path) { project -> it.createNavigatable(project)?.navigate(true) }
                }
                print(":${it.lineNumber}:${it.columnNumber}\n", ConsoleViewContentType.ERROR_OUTPUT)
            }
        } else {
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            print(writer.buffer.toString(), ConsoleViewContentType.ERROR_OUTPUT)
        }
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    // endregion
}

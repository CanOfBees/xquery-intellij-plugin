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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import uk.co.reecedunn.intellij.plugin.processor.query.*
import javax.swing.JList

class QueryProcessorSettingsCellRenderer : ColoredListCellRenderer<QueryProcessorSettingsWithVersionCache>() {
    private fun render(value: QueryProcessorSettings, version: String?) {
        clear()
        append(value.api.displayName)
        version?.let { append(" $it") }
        value.name?.let { append(" ($it)", SimpleTextAttributes.GRAY_ATTRIBUTES) }
    }

    private fun renderError(value: QueryProcessorSettings, message: String) {
        clear()
        append(value.api.displayName, SimpleTextAttributes.ERROR_ATTRIBUTES)
        value.name?.let { append(" ($it)", SimpleTextAttributes.ERROR_ATTRIBUTES) }
        append(" [$message]", SimpleTextAttributes.ERROR_ATTRIBUTES)
    }

    override fun customizeCellRenderer(
        list: JList<out QueryProcessorSettingsWithVersionCache>,
        value: QueryProcessorSettingsWithVersionCache?,
        index: Int, selected: Boolean, hasFocus: Boolean
    ) {
        if (value != null) {
            render(value.settings, value.version as? String)
            (value.version as? Throwable)?.let {
                try {
                    renderError(value.settings, it.toQueryUserMessage())
                } catch (e: Throwable) {
                    // Cannot display the error, so do nothing.
                }
            }
        }
    }
}

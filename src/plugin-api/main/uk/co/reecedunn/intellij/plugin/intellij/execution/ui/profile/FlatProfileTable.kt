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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.profile

import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.QueryTable
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry

@Suppress("ClassName")
private object MODULE_PATH_COLUMN : ColumnInfo<FlatProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.module.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): String? = item?.frame?.module?.name

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return (o1?.frame?.module?.path ?: "").compareTo(o2?.frame?.module?.path ?: "")
    }
}

@Suppress("ClassName")
private object LINE_NUMBER_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.line-number.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): Int? = item?.frame?.lineNumber

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1?.frame?.lineNumber!!.compareTo(o2?.frame?.lineNumber!!)
    }
}

@Suppress("ClassName")
private object COLUMN_NUMBER_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.column-number.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): Int? = item?.frame?.columnNumber

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1?.frame?.columnNumber!!.compareTo(o2?.frame?.columnNumber!!)
    }
}

@Suppress("ClassName")
private object COUNT_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.count.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): Int? = item?.count

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.count.compareTo(o2!!.count)
    }
}

@Suppress("ClassName")
private object SELF_TIME_COLUMN : ColumnInfo<FlatProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.self-time.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): String? = item?.selfTime?.seconds?.data?.toPlainString()

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        val compared = o1!!.selfTime.months.data.compareTo(o2!!.selfTime.months.data)
        return if (compared == 0)
            o1.selfTime.seconds.data.compareTo(o2.selfTime.seconds.data)
        else
            compared
    }
}

@Suppress("ClassName")
private object TOTAL_TIME_COLUMN : ColumnInfo<FlatProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.total-time.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): String? = item?.totalTime?.seconds?.data?.toPlainString()

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        val compared = o1!!.totalTime.months.data.compareTo(o2!!.totalTime.months.data)
        return if (compared == 0)
            o1.totalTime.seconds.data.compareTo(o2.totalTime.seconds.data)
        else
            compared
    }
}

@Suppress("ClassName")
private object CONTEXT_COLUMN : ColumnInfo<FlatProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.context.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry?): String? = item?.context

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.context.compareTo(o2!!.context)
    }
}

private val COLUMNS: Array<ColumnInfo<*, *>> = arrayOf(
    MODULE_PATH_COLUMN,
    LINE_NUMBER_COLUMN,
    COLUMN_NUMBER_COLUMN,
    COUNT_COLUMN,
    SELF_TIME_COLUMN,
    TOTAL_TIME_COLUMN,
    CONTEXT_COLUMN
)

class FlatProfileTable : TableView<FlatProfileEntry>(), QueryTable {
    init {
        setModelAndUpdateColumns(ListTableModel<FlatProfileEntry>(*COLUMNS))
        setEnableAntialiasing(true)

        updateEmptyText(false, false)
    }

    private fun updateEmptyText(running: Boolean, exception: Boolean) {
        when {
            exception -> emptyText.text = PluginApiBundle.message("profile.entry.table.has-exception")
            running -> emptyText.text = runningText
            else -> emptyText.text = PluginApiBundle.message("profile.entry.table.no-results")
        }
    }

    override var runningText: String = PluginApiBundle.message("query.result.table.results-pending")
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override var isRunning: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override var hasException: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override val itemCount: Int = 0

    fun addRow(entry: FlatProfileEntry) = listTableModel.addRow(entry)
}

/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.runners

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import uk.co.reecedunn.intellij.plugin.intellij.execution.configurations.QueryProcessorRunConfiguration
import uk.co.reecedunn.intellij.plugin.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.profile.FlatProfileTableView

class QueryProcessorProfiler : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId(): String = "XIJPQueryProcessorProfiler"

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        if (executorId != DefaultProfileExecutor.EXECUTOR_ID || profile !is QueryProcessorRunConfiguration) {
            return false
        }
        return profile.processor?.api?.canExecute(profile.language, executorId) == true
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        return state.execute(environment.executor, this)?.let {
            RunContentBuilder(it, environment).showRunContent(environment.contentToReuse)
        }
    }
}

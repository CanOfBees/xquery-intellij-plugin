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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.lang.Language
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.execution.configurations.ConfigurationTypeEx

class QueryProcessorConfigurationFactory(type: ConfigurationType, private vararg val language: Language) :
    ConfigurationFactory(type) {

    override fun getId(): String = (type as ConfigurationTypeEx).factoryId

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return QueryProcessorRunConfiguration(project, this, *language)
    }

    override fun getOptionsClass(): Class<out BaseState>? = QueryProcessorRunConfigurationData::class.java
}

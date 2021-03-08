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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.execution.configurations.type

import com.intellij.execution.configurations.ConfigurationFactory
import uk.co.reecedunn.intellij.plugin.core.execution.configurations.ConfigurationTypeEx
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.configurations.QueryProcessorConfigurationFactory
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryIcons
import javax.swing.Icon

class XQueryConfigurationType : ConfigurationTypeEx {
    override fun getIcon(): Icon = XQueryIcons.RunConfiguration

    override fun getConfigurationTypeDescription(): String = displayName

    override fun getId(): String = "XIJPXQueryProcessorConfiguration"

    override fun getDisplayName(): String = "XQuery"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(QueryProcessorConfigurationFactory(this, XQuery))
    }

    override val factoryId: String = "XQuery"
}

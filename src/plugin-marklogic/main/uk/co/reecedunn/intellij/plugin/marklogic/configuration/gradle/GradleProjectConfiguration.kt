/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.configuration.gradle

import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicRest
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurationFactory
import java.util.*

class GradleProjectConfiguration(private val project: Project, override val baseDir: VirtualFile) :
    UserDataHolderBase(),
    XpmProjectConfiguration {
    // region ml-gradle

    private fun getPropertiesFile(name: String?): PropertiesFile? {
        val filename = name?.let { "gradle-${name}.properties" } ?: GRADLE_PROPERTIES
        return baseDir.findChild(filename)?.toPsiFile(project) as? PropertiesFile
    }

    private val build: PropertiesFile? = getPropertiesFile("default") // Project-specific properties
    private var env: PropertiesFile? = getPropertiesFile("local") // Environment-specific properties

    private fun <T> cached(key: Key<CachedValue<T>>, compute: () -> Pair<T, PsiElement?>): T {
        return CachedValuesManager.getManager(project).getCachedValue(this, key, {
            val (value, context) = compute()
            val dependencies = listOfNotNull(context, build, env)
            CachedValueProvider.Result.create(value, dependencies)
        }, false)
    }

    private fun getProperty(property: String): Sequence<IProperty> = sequenceOf(
        env?.findPropertyByKey(property),
        build?.findPropertyByKey(property)
    ).filterNotNull()

    private fun getPropertyValue(property: String): String? = getProperty(property).firstOrNull()?.value

    // endregion
    // region XpmProjectConfiguration

    override val applicationName: String?
        get() = cached(APPLICATION_NAME) {
            Optional.ofNullable(getPropertyValue(ML_APP_NAME)) to null
        }.orElse(null)

    override var environmentName: String = "local"
        set(name) {
            field = name
            env = getPropertiesFile(name)
        }

    override val modulePaths: Sequence<VirtualFile>
        get() = cached(MODULE_PATHS) {
            val modulePaths = getPropertyValue(ML_MODULE_PATHS) ?: ML_MODULE_PATHS_DEFAULT
            modulePaths.split(",").mapNotNull { baseDir.findFileByRelativePath("$it/root") } to null
        }.asSequence()

    override val processorId: Int?
        get() = QueryProcessors.getInstance().processors.find {
            it.api === MarkLogicRest && it.connection?.hostname in LOCALHOST_STRINGS
        }?.id

    override val databaseName: String?
        get() = cached(DATABASE_NAME) {
            val name = getPropertyValue(ML_CONTENT_DATABASE_NAME) ?: applicationName?.let { "$it-content" }
            Optional.ofNullable(name) to null
        }.orElse(null)

    // endregion
    // region XpmProjectConfigurationFactory

    companion object : XpmProjectConfigurationFactory {
        override fun create(project: Project, baseDir: VirtualFile): XpmProjectConfiguration? {
            val properties = baseDir.findChild(GRADLE_PROPERTIES)?.toPsiFile(project) as? PropertiesFile ?: return null
            return properties.findPropertyByKey(ML_APP_NAME)?.let { GradleProjectConfiguration(project, baseDir) }
        }

        private const val GRADLE_PROPERTIES = "gradle.properties"

        private val APPLICATION_NAME = Key.create<CachedValue<Optional<String>>>("APPLICATION_NAME")
        private const val ML_APP_NAME = "mlAppName"

        private val MODULE_PATHS = Key.create<CachedValue<List<VirtualFile>>>("MODULE_PATHS")
        private const val ML_MODULE_PATHS = "mlModulePaths"
        private const val ML_MODULE_PATHS_DEFAULT = "src/main/ml-modules"

        private val DATABASE_NAME = Key.create<CachedValue<Optional<String>>>("DATABASE_NAME")
        private const val ML_CONTENT_DATABASE_NAME = "mlContentDatabaseName"

        private val LOCALHOST_STRINGS = setOf("localhost", "127.0.0.1")
    }

    // endregion
}

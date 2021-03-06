/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.compat.testFramework.registerCodeStyleCachingService
import com.intellij.compat.testFramework.registerPomModel
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpm.java.JavaTypePath
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolver
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactoryBean
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecorator
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDecoratorBean
import uk.co.reecedunn.intellij.plugin.xpm.module.ImportPathResolverBean
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderSettings
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactoryBean
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionProviderBean
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProviderBean
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProviderBean

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()) {

    open fun registerModules(manager: MockModuleManager) {}

    @BeforeAll
    override fun setUp() {
        super.setUp()
        registerPomModel(myProject)
        registerPsiModification()
        myProject.registerCodeStyleCachingService()

        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())
        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())

        val manager = MockModuleManager(myProject)
        registerModules(manager)
        myProject.registerService(ModuleManager::class.java, manager)

        myProject.registerService(JavaTypePath::class.java, JavaTypePath(myProject))
        myProject.registerService(XpmModuleLoaderSettings::class.java, XpmModuleLoaderSettings(myProject))

        registerExtensionPoint(XpmModulePathFactory.EP_NAME, XpmModulePathFactoryBean::class.java)
        registerModulePathFactory(XpmModuleLocationPath, "")

        registerExtensionPoint(XpmModuleLoaderFactory.EP_NAME, XpmModuleLoaderFactoryBean::class.java)
        registerModuleLoader(
            "module",
            "uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.JspModuleSourceRootLoader\$Companion",
            ""
        )
        registerModuleLoader(
            "relative",
            "uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl.RelativeModuleLoader",
            "INSTANCE"
        )

        registerExtensionPoint(ImportPathResolver.EP_NAME, ImportPathResolverBean::class.java)
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.basex.model.BuiltInFunctions, "INSTANCE")
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.marklogic.model.BuiltInFunctions, "INSTANCE")
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.saxon.model.BuiltInFunctions, "INSTANCE")
        registerBuiltInFunctions(uk.co.reecedunn.intellij.plugin.w3.model.BuiltInFunctions, "INSTANCE")

        registerExtensionPoint(XpmFunctionDecorator.EP_NAME, XpmFunctionDecoratorBean::class.java)
        registerExtensionPoint(XpmNamespaceProvider.EP_NAME, XpmNamespaceProviderBean::class.java)
        registerExtensionPoint(XpmVariableProvider.EP_NAME, XpmVariableProviderBean::class.java)
        registerExtensionPoint(XpmFunctionProvider.EP_NAME, XpmFunctionProviderBean::class.java)

        registerExtensions()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    open fun registerExtensions() {
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerModulePathFactory(factory: XpmModulePathFactory, fieldName: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmModulePathFactoryBean()
        bean.implementationClass = factory.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerModulePathFactory"), classLoader))
        registerExtension(XpmModulePathFactory.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerModuleLoader(name: String, implementation: String, fieldName: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmModuleLoaderFactoryBean()
        bean.name = name
        bean.implementationClass = implementation
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerModuleLoader"), classLoader))
        registerExtension(XpmModuleLoaderFactory.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerBuiltInFunctions(resolver: ImportPathResolver, fieldName: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = ImportPathResolverBean()
        bean.implementationClass = resolver.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerBuiltInFunctions"), classLoader))
        registerExtension(ImportPathResolver.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    protected fun registerInScopeVariableProvider(provider: XpmVariableProvider, fieldName: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmVariableProviderBean()
        bean.implementationClass = provider.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerInScopeVariables"), classLoader))
        registerExtension(XpmVariableProvider.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    protected fun registerStaticallyKnownFunctionProvider(
        provider: XpmFunctionProvider, fieldName: String
    ) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmFunctionProviderBean()
        bean.implementationClass = provider.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerFunctions"), classLoader))
        registerExtension(XpmFunctionProvider.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    protected fun registerNamespaceProvider(provider: XpmNamespaceProvider, fieldName: String) {
        val classLoader = ParserTestCase::class.java.classLoader
        val bean = XpmNamespaceProviderBean()
        bean.implementationClass = provider.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerNamespaceProvider"), classLoader))
        registerExtension(XpmNamespaceProvider.EP_NAME, bean)
    }

    protected val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(myProject)
}

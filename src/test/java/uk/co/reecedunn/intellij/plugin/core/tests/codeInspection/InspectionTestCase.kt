/*
 * Copyright (C) 2016-2018, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ex.InspectionManagerEx
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.SmartPointerManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.basex.lang.BaseXSyntaxValidator
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockSmartPointerManager
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.marklogic.lang.MarkLogicSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidator
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidatorBean
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProviderBean
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class InspectionTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition(), XPathParserDefinition()) {

    private val inspectionManager: InspectionManager
        get() = InspectionManager.getInstance(myProject)

    protected val settings: XQueryProjectSettings
        get() = XQueryProjectSettings.getInstance(myProject)

    @BeforeAll
    override fun setUp() {
        super.setUp()

        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        myProject.registerService(SmartPointerManager::class.java, MockSmartPointerManager())
        myProject.registerService(InspectionManager::class.java, InspectionManagerEx(myProject))

        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())

        registerExtensionPoint(XpmSyntaxValidator.EP_NAME, XpmSyntaxValidatorBean::class.java)
        registerSyntaxValidator(BaseXSyntaxValidator, "INSTANCE")
        registerSyntaxValidator(MarkLogicSyntaxValidator, "INSTANCE")

        registerExtensionPoint(XpmNamespaceProvider.EP_NAME, XpmNamespaceProviderBean::class.java)
        registerNamespaceProvider(XQueryNamespaceProvider, "INSTANCE")
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerSyntaxValidator(factory: XpmSyntaxValidator, fieldName: String) {
        val classLoader = InspectionTestCase::class.java.classLoader
        val bean = XpmSyntaxValidatorBean()
        bean.implementationClass = factory.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerSyntaxValidator"), classLoader))
        registerExtension(XpmSyntaxValidator.EP_NAME, bean)
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun registerNamespaceProvider(provider: XpmNamespaceProvider, fieldName: String) {
        val classLoader = InspectionTestCase::class.java.classLoader
        val bean = XpmNamespaceProviderBean()
        bean.implementationClass = provider.javaClass.name
        bean.fieldName = fieldName
        bean.setPluginDescriptor(DefaultPluginDescriptor(PluginId.getId("registerNamespaceProvider"), classLoader))
        registerExtension(XpmNamespaceProvider.EP_NAME, bean)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    fun inspect(file: XQueryModule, inspection: LocalInspectionTool): List<ProblemDescriptor>? {
        return inspection.checkFile(file, inspectionManager as InspectionManagerEx, false)?.filterNotNull()
    }
}

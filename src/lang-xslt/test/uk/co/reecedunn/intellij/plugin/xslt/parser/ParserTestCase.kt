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
package uk.co.reecedunn.intellij.plugin.xslt.parser

import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.psi.xml.XmlFile
import com.intellij.xml.XmlExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockProjectRootsManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserTestCase : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        registerExtensionPoint(StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java)
        registerExtensionPoint(XmlExtension.EP_NAME, XmlExtension::class.java)

        myProject.registerService(ProjectRootManager::class.java, MockProjectRootsManager())
        myProject.registerService(ModuleManager::class.java, MockModuleManager(myProject))
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    fun parseXml(resource: String): XmlFile {
        if (resource.endsWith(".xsl")) {
            val file = ResourceVirtualFile(ParserTestCase::class.java.classLoader, resource)
            return file.toPsiFile(myProject)!!
        }
        return super.parseText(resource)
    }
}

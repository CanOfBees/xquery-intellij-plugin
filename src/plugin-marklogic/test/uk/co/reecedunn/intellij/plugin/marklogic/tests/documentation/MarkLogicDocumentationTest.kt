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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.documentation

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.marklogic.documentation.MarkLogicProductDocumentation
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XdmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XdmDocumentationSourceProvider
import uk.co.reecedunn.intellij.plugin.xdm.lang.XdmProductType
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Documentation - MarkLogic Product Documentation")
private class MarkLogicDocumentationTest {
    @Test
    @DisplayName("product type")
    fun productType() {
        val type: XdmProductType = MarkLogicProductDocumentation
        assertThat(type.id, `is`("marklogic"))
        assertThat(type.name, `is`("MarkLogic"))
        assertThat(type.moduleTypes, `is`(arrayOf(XdmModuleType.XQuery, XdmModuleType.XPath, XdmModuleType.JavaScript)))
    }

    @Test
    @DisplayName("MarkLogic 6.0")
    fun marklogic6() {
        val spec: XdmDocumentationSource = MarkLogicProductDocumentation.MARKLOGIC_6
        assertThat(spec.name, `is`("MarkLogic"))
        assertThat(spec.version, `is`("6.0"))
        assertThat(spec.href, `is`("https://docs.marklogic.com/MarkLogic_6_pubs.zip"))
        assertThat(spec.path, `is`("marklogic/MarkLogic_6_pubs.zip"))

        val provider: XdmDocumentationSourceProvider = MarkLogicProductDocumentation
        assertThat(provider.sources.size, `is`(5))
        assertThat(provider.sources.indexOf(spec), `is`(0))
    }

    @Test
    @DisplayName("MarkLogic 7.0")
    fun marklogic7() {
        val spec: XdmDocumentationSource = MarkLogicProductDocumentation.MARKLOGIC_7
        assertThat(spec.name, `is`("MarkLogic"))
        assertThat(spec.version, `is`("7.0"))
        assertThat(spec.href, `is`("https://docs.marklogic.com/MarkLogic_7_pubs.zip"))
        assertThat(spec.path, `is`("marklogic/MarkLogic_7_pubs.zip"))

        val provider: XdmDocumentationSourceProvider = MarkLogicProductDocumentation
        assertThat(provider.sources.size, `is`(5))
        assertThat(provider.sources.indexOf(spec), `is`(1))
    }

    @Test
    @DisplayName("MarkLogic 8.0")
    fun marklogic8() {
        val spec: XdmDocumentationSource = MarkLogicProductDocumentation.MARKLOGIC_8
        assertThat(spec.name, `is`("MarkLogic"))
        assertThat(spec.version, `is`("8.0"))
        assertThat(spec.href, `is`("https://docs.marklogic.com/MarkLogic_8_pubs.zip"))
        assertThat(spec.path, `is`("marklogic/MarkLogic_8_pubs.zip"))

        val provider: XdmDocumentationSourceProvider = MarkLogicProductDocumentation
        assertThat(provider.sources.size, `is`(5))
        assertThat(provider.sources.indexOf(spec), `is`(2))
    }

    @Test
    @DisplayName("MarkLogic 9.0")
    fun marklogic9() {
        val spec: XdmDocumentationSource = MarkLogicProductDocumentation.MARKLOGIC_9
        assertThat(spec.name, `is`("MarkLogic"))
        assertThat(spec.version, `is`("9.0"))
        assertThat(spec.href, `is`("https://docs.marklogic.com/MarkLogic_9_pubs.zip"))
        assertThat(spec.path, `is`("marklogic/MarkLogic_9_pubs.zip"))

        val provider: XdmDocumentationSourceProvider = MarkLogicProductDocumentation
        assertThat(provider.sources.size, `is`(5))
        assertThat(provider.sources.indexOf(spec), `is`(3))
    }

    @Test
    @DisplayName("MarkLogic 10.0")
    fun marklogic10() {
        val spec: XdmDocumentationSource = MarkLogicProductDocumentation.MARKLOGIC_10
        assertThat(spec.name, `is`("MarkLogic"))
        assertThat(spec.version, `is`("10.0"))
        assertThat(spec.href, `is`("https://docs.marklogic.com/MarkLogic_10_pubs.zip"))
        assertThat(spec.path, `is`("marklogic/MarkLogic_10_pubs.zip"))

        val provider: XdmDocumentationSourceProvider = MarkLogicProductDocumentation
        assertThat(provider.sources.size, `is`(5))
        assertThat(provider.sources.indexOf(spec), `is`(4))
    }
}

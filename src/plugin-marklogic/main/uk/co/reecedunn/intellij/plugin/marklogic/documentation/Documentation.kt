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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSourceProvider

private data class MarkLogicZippedDocumentation(
    override val version: String,
    private val zip: String
) : XdmDocumentationSource {
    override val name: String = "MarkLogic"

    override val href: String = "https://docs.marklogic.com/$zip"

    override val path: String = "marklogic/$zip"
}

object MarkLogicProductDocumentation : XdmDocumentationSourceProvider {
    val MARKLOGIC_6: XdmDocumentationSource = MarkLogicZippedDocumentation("6.0", "MarkLogic_6_pubs.zip")
    val MARKLOGIC_7: XdmDocumentationSource = MarkLogicZippedDocumentation("7.0", "MarkLogic_7_pubs.zip")
    val MARKLOGIC_8: XdmDocumentationSource = MarkLogicZippedDocumentation("8.0", "MarkLogic_8_pubs.zip")
    val MARKLOGIC_9: XdmDocumentationSource = MarkLogicZippedDocumentation("9.0", "MarkLogic_9_pubs.zip")
    val MARKLOGIC_10: XdmDocumentationSource = MarkLogicZippedDocumentation("10.0", "MarkLogic_10_pubs.zip")

    override val sources: List<XdmDocumentationSource> = listOf(
        MARKLOGIC_6,
        MARKLOGIC_7,
        MARKLOGIC_8,
        MARKLOGIC_9,
        MARKLOGIC_10
    )
}
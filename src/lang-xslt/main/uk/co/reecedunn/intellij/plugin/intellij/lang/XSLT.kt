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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType

/**
 * XML Stylesheet Language: Transform
 */
object XSLT : Language("XSLT"), LanguageExtensions {
    // region Language

    override fun isCaseSensitive(): Boolean = true

    override fun getDisplayName(): String = "XSLT"

    override fun getAssociatedFileType(): LanguageFileType? = null

    // endregion
    // region LanguageExtensions

    override val scriptExtensions: Array<String> = arrayOf(
        "xsl",
        "xslt"
    )

    override val fileExtensions: Array<String> = scriptExtensions

    override val defaultExtension: String = "xsl"

    // endregion
}

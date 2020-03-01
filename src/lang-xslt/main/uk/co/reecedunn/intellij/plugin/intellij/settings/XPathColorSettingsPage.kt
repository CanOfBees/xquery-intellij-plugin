/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import uk.co.reecedunn.intellij.plugin.intellij.editor.XsltSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathQueries
import javax.swing.Icon

class XPathColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = XPathSyntaxHighlighter()

    override fun getDemoText(): String = demo

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? =
        XPathSyntaxHighlighterColors.ADDITIONAL_DESCRIPTORS

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "XPath and XSLT"

    companion object {
        private val DESCRIPTORS = arrayOf(
            XPathSyntaxHighlighterColors.DESCRIPTORS,
            XsltSyntaxHighlighterColors.DESCRIPTORS
        ).flatten().toTypedArray()

        private val demo: String by lazy {
            var ret = XPathQueries.ColorSettingsDemo
            ret = ret.replace("([^a-zA-Z0-9_])fn:".toRegex()) { it.groups[1]!!.value + "<nsprefix>fn</nsprefix>:" }
            ret
        }
    }
}

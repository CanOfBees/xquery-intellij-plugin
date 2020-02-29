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
package uk.co.reecedunn.intellij.plugin.intellij.tests.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.text.StringUtil
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryColorSettingsPage
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery Color Settings Page")
class XQueryColorSettingsPageTest {
    private val settings = XQueryColorSettingsPage()

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        StringUtil.assertValidSeparators(settings.demoText)
    }

    private fun getTextAttributeKeysForTokens(text: String): List<TextAttributesKey> {
        val highlighter = settings.highlighter
        val lexer = highlighter.highlightingLexer
        lexer.start(text)

        val keys = ArrayList<TextAttributesKey>()
        while (lexer.tokenType != null) {
            for (key in highlighter.getTokenHighlights(lexer.tokenType)) {
                if (!keys.contains(key)) {
                    keys.add(key)
                }
            }

            lexer.advance()
        }
        return keys
    }

    @Test
    @DisplayName("demo text contains all syntax-based text attribute keys")
    fun syntaxHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForTokens(settings.demoText)
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ANNOTATION), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_TAG_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE), `is`(true))
        assertThat(keys.contains(XQuerySyntaxHighlighterColors.XQDOC_MARKUP), `is`(true))
        assertThat(keys.size, `is`(18)) // No other matching highlight colours
    }
}

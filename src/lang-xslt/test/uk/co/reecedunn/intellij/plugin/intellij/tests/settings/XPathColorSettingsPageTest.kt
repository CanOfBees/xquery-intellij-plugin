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
import uk.co.reecedunn.intellij.plugin.intellij.editor.XsltSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.intellij.settings.XPathColorSettingsPage
import java.util.*

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath Color Settings Page")
class XPathColorSettingsPageTest {
    private val settings = XPathColorSettingsPage()

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

    private fun getTextAttributeKeysForAdditionalDescriptors(text: String): List<Pair<String, TextAttributesKey>> {
        return settings.additionalHighlightingTagToDescriptorMap!!.asSequence().flatMap { (name, attributesKey) ->
            val matches = "<$name>([^<]*)</$name>".toRegex().findAll(text)
            assertThat("additional highlight '$name' XML annotation is present", matches.any(), `is`(true))
            matches.map { match ->
                match.groups[1]?.value!! to attributesKey
            }
        }.toList()
    }

    @Test
    @DisplayName("demo text contains valid separators")
    fun testDemoTextSeparators() {
        StringUtil.assertValidSeparators(settings.demoText)
    }

    @Test
    @DisplayName("demo text contains all syntax-based text attribute keys")
    fun syntaxHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForTokens(settings.demoText)
        assertThat(keys.contains(XPathSyntaxHighlighterColors.ATTRIBUTE), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.BAD_CHARACTER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.COMMENT), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.IDENTIFIER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.KEYWORD), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.NUMBER), `is`(true))
        assertThat(keys.contains(XPathSyntaxHighlighterColors.STRING), `is`(true))
        assertThat(keys.size, `is`(8)) // No other matching highlight colours
    }

    @Test
    @DisplayName("demo text contains all semantic-based text attribute keys")
    fun semanticHighlightingTextAttributeKeys() {
        val keys = getTextAttributeKeysForAdditionalDescriptors(settings.demoText)
        assertThat(keys.size, `is`(12))
        assertThat(keys[0], `is`("value" to XPathSyntaxHighlighterColors.ATTRIBUTE))
        assertThat(keys[1], `is`("two" to XPathSyntaxHighlighterColors.ATTRIBUTE))
        assertThat(keys[2], `is`("lorem" to XPathSyntaxHighlighterColors.ELEMENT))
        assertThat(keys[3], `is`("ipsum" to XPathSyntaxHighlighterColors.ELEMENT))
        assertThat(keys[4], `is`("one" to XPathSyntaxHighlighterColors.ELEMENT))
        assertThat(keys[5], `is`("position" to XPathSyntaxHighlighterColors.FUNCTION_CALL))
        assertThat(keys[6], `is`("true" to XPathSyntaxHighlighterColors.FUNCTION_CALL))
        assertThat(keys[7], `is`("fn" to XPathSyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[8], `is`("fn" to XPathSyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[9], `is`("xs" to XPathSyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[10], `is`("three" to XPathSyntaxHighlighterColors.NS_PREFIX))
        assertThat(keys[11], `is`("a" to XPathSyntaxHighlighterColors.PARAMETER))
    }

    @Test
    @DisplayName("demo text contains all keys from the attribute descriptors")
    fun allDescriptorsPresentInDemoText() {
        val tokens = getTextAttributeKeysForTokens(settings.demoText)
        val additional = getTextAttributeKeysForAdditionalDescriptors(settings.demoText).map { (_, key) -> key }
        val keys = tokens.union(additional).toMutableSet()
        keys.add(XsltSyntaxHighlighterColors.XSLT_DIRECTIVE)

        val descriptorKeys = settings.attributeDescriptors.map { it.key }

        keys.forEach { key ->
            assertThat("$key from demo text in attributeDescriptors", descriptorKeys.contains(key), `is`(true))
        }

        descriptorKeys.forEach { key ->
            assertThat("$key from attributeDescriptors in keys from demo text", keys.contains(key), `is`(true))
        }
    }
}

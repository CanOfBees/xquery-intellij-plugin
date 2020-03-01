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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle

object XPathSyntaxHighlighterColors {
    // region Syntax Highlighting (Lexical Tokens)

    val BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
        "XPATH_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER
    )

    val COMMENT = TextAttributesKey.createTextAttributesKey(
        "XPATH_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT
    )

    val ESCAPED_CHARACTER = TextAttributesKey.createTextAttributesKey(
        "XPATH_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    )

    val IDENTIFIER = TextAttributesKey.createTextAttributesKey(
        "XPATH_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER
    )

    val KEYWORD = TextAttributesKey.createTextAttributesKey(
        "XPATH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
    )

    val NUMBER = TextAttributesKey.createTextAttributesKey(
        "XPATH_NUMBER", DefaultLanguageHighlighterColors.NUMBER
    )

    val STRING = TextAttributesKey.createTextAttributesKey(
        "XPATH_STRING", DefaultLanguageHighlighterColors.STRING
    )

    // endregion
    // region Semantic Highlighting (Usage and Reference Types)

    val ATTRIBUTE = TextAttributesKey.createTextAttributesKey(
        "XPATH_ATTRIBUTE", XmlHighlighterColors.XML_ATTRIBUTE_NAME
    )

    val NS_PREFIX = TextAttributesKey.createTextAttributesKey(
        "XPATH_NS_PREFIX", DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )

    // endregion
    // region Descriptors

    val DESCRIPTORS = arrayOf(
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.attribute"), ATTRIBUTE),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.bad.character"), BAD_CHARACTER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.comment"), COMMENT),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.escaped.character"), ESCAPED_CHARACTER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.identifier"), IDENTIFIER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.keyword"), KEYWORD),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.ns-prefix"), NS_PREFIX),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.number"), NUMBER),
        AttributesDescriptor(XPathBundle.message("xpath.settings.colors.string"), STRING)
    )

    val ADDITIONAL_DESCRIPTORS = mapOf(
        "attribute" to ATTRIBUTE,
        "nsprefix" to NS_PREFIX
    )

    // endregion
}

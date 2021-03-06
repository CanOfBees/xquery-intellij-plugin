/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.ValueTemplate
import uk.co.reecedunn.intellij.plugin.xslt.lexer.XsltValueTemplateLexer

@DisplayName("XSLT 3.0 - Lexer - Value Templates")
class XsltValueTemplateLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XsltValueTemplateLexer(XmlCodePointRangeImpl())

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (1) ValueTemplate")
    internal inner class ValueTemplateTest {
        @Test
        @DisplayName("escaped braces")
        fun escapedBraces() {
            val lexer = createLexer()

            lexer.start("Lorem {{ipsum}} dolor.")
            matchToken(lexer, "Lorem ", 0, 0, 6, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "{{", 0, 6, 8, ValueTemplate.ESCAPED_CHARACTER)
            matchToken(lexer, "ipsum", 0, 8, 13, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "}}", 0, 13, 15, ValueTemplate.ESCAPED_CHARACTER)
            matchToken(lexer, " dolor.", 0, 15, 22, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "", 0, 22, 22, null)
        }

        @Test
        @DisplayName("unpaired right brace")
        fun unpairedRightBrace() {
            val lexer = createLexer()

            lexer.start("Lorem } ipsum")
            matchToken(lexer, "Lorem ", 0, 0, 6, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "}", 0, 6, 7, XPathTokenType.BLOCK_CLOSE)
            matchToken(lexer, " ipsum", 0, 7, 13, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "", 0, 13, 13, null)
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (2) ValueContentChar")
    fun valueContentChar() {
        val lexer = createLexer()

        lexer.start("Lorem ipsum dolor.")
        matchToken(lexer, "Lorem ipsum dolor.", 0, 0, 18, ValueTemplate.VALUE_CONTENTS)
        matchToken(lexer, "", 0, 18, 18, null)
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (5) EnclosedExpr")
    internal inner class EnclosedExpr {
        @Test
        @DisplayName("enclosed expression")
        fun enclosedExpr() {
            val lexer = createLexer()

            lexer.start("One {2} Three")
            matchToken(lexer, "One ", 0, 0, 4, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "{", 0, 4, 5, XPathTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 32, 5, 6, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 32, 6, 7, XPathTokenType.BLOCK_CLOSE)
            matchToken(lexer, " Three", 0, 7, 13, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("nested")
        fun nested() {
            val lexer = createLexer()

            lexer.start("One {2{3}4} Five")
            matchToken(lexer, "One ", 0, 0, 4, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "{", 0, 4, 5, XPathTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 32, 5, 6, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "{", 32, 6, 7, XPathTokenType.BLOCK_OPEN)
            matchToken(lexer, "3", 32, 7, 8, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 32, 8, 9, XPathTokenType.BLOCK_CLOSE)
            matchToken(lexer, "4", 32, 9, 10, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 32, 10, 11, XPathTokenType.BLOCK_CLOSE)
            matchToken(lexer, " Five", 0, 11, 16, ValueTemplate.VALUE_CONTENTS)
            matchToken(lexer, "", 0, 16, 16, null)
        }
    }
}

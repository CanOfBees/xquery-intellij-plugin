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
package uk.co.reecedunn.intellij.plugin.xslt.lexer

import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.XslValueTemplate

class XsltValueTemplateLexer(tokenRange: CodePointRange) : XPathLexer(tokenRange) {
    // region States

    fun stateDefault() {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> {
                mType = null
            }
            '{'.toInt() -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '{'.toInt()) {
                    mTokenRange.match()
                    mType = XslValueTemplate.ESCAPED_CHARACTER
                } else {
                    mType = XPathTokenType.BLOCK_OPEN
                    pushState(STATE_VALUE_TEMPLATE_EXPRESSION)
                }
            }
            '}'.toInt() -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint == '}'.toInt()) {
                    mTokenRange.match()
                    XslValueTemplate.ESCAPED_CHARACTER
                } else {
                    XPathTokenType.BLOCK_CLOSE
                }
            }
            else -> while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER, '{'.toInt(), '}'.toInt() -> {
                        mType = XslValueTemplate.VALUE_CONTENTS
                        return
                    }
                    else -> {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                }
            }
        }
    }

    // endregion
    // region Lexer

    override fun advance(state: Int): Unit = when (state) {
        STATE_DEFAULT -> stateDefault()
        STATE_VALUE_TEMPLATE_EXPRESSION -> stateDefault(state)
        else -> super.advance(state)
    }

    // endregion

    companion object {
        // region State Constants

        private const val STATE_VALUE_TEMPLATE_EXPRESSION = 32

        // endregion
    }
}

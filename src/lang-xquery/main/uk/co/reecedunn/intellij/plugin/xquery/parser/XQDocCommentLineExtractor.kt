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
package uk.co.reecedunn.intellij.plugin.xquery.parser

import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType

class XQDocCommentLineExtractor(private val comment: CharSequence) {
    private val lexer = XQDocLexer()
    private var startOfComment = true

    val isXQDoc: Boolean

    var text: CharSequence? = null
        private set

    var textRange: Range<Int>? = null
        private set

    fun next(): Boolean {
        do {
            if (lexer.tokenType == XQDocTokenType.TRIM)
                lexer.advance()
            if (lexer.tokenType == XQDocTokenType.WHITE_SPACE)
                lexer.advance()
        } while (lexer.tokenType == XQDocTokenType.TRIM && startOfComment)
        startOfComment = false

        when (lexer.tokenType) {
            XQDocTokenType.CONTENTS -> parseDescriptionLine()
            XQDocTokenType.TRIM -> parseEmptyDescriptionLine()
            else -> {
                text = null
                textRange = null
                return false
            }
        }
        return true
    }

    init {
        lexer.start(comment)
        if (lexer.tokenType === XQDocTokenType.XQDOC_COMMENT_MARKER) {
            isXQDoc = true
            lexer.advance()
        } else {
            isXQDoc = false
        }
    }

    private fun parseEmptyDescriptionLine() {
        text = ""
        textRange = Range(lexer.tokenStart, lexer.tokenStart)
        lexer.advance()
    }

    private fun parseDescriptionLine() {
        val start = lexer.tokenStart
        var end = lexer.tokenEnd
        while (XQDocTokenType.DESCRIPTION_LINE_TOKENS.contains(lexer.tokenType)) {
            end = lexer.tokenEnd
            lexer.advance()
        }

        text = comment.subSequence(start, end)
        textRange = Range(start, end)
    }
}

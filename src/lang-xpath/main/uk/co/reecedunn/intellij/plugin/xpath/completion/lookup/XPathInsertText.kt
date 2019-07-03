/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.completion.lookup

import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import uk.co.reecedunn.intellij.plugin.core.editor.completeString

data class XPathInsertText(val beforeCaret: String, val hint: String?, val afterCaret: String?) {
    companion object {
        val AXIS_MARKER = XPathInsertText("::", null, null)
        val QNAME_PREFIX = XPathInsertText(":", null, null)

        val EMPTY_PARAMS = XPathInsertText("()", null, null)
        val PARAMS = XPathInsertText("(", null, ")")

        val PARAMS_KEYNAME = XPathInsertText("(", "key-name", ")")
        val PARAMS_NAME = XPathInsertText("(", "name", ")")
        val PARAMS_NAME_AND_TYPE = XPathInsertText("(", "name, type", ")")
        val PARAMS_WILDCARD = XPathInsertText("(*)", null, null)
        val PARAMS_WILDCARD_AND_TYPE = XPathInsertText("(*, ", "type", ")")
    }

    val tailText: String = listOf(beforeCaret, hint ?: "", afterCaret ?: "").joinToString("")

    fun completeText(document: Document, offset: Int) {
        document.completeString(offset, beforeCaret)
        if (afterCaret != null) {
            document.completeString(offset + beforeCaret.length, afterCaret)
        }
    }

    fun moveCaret(caretModel: CaretModel) {
        caretModel.moveToOffset(caretModel.offset + beforeCaret.length)
    }
}

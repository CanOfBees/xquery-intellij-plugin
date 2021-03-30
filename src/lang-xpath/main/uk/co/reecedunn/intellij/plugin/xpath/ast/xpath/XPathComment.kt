/*
 * Copyright (C) 2016, 2019, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.ast.xpath

import com.intellij.psi.PsiElement

/**
 * An XPath 2.0 and XQuery 1.0 `Comment` node in the XQuery AST containing
 * `CommentContents`.
 *
 * Nested comments are not exposed as inner comments within the AST. They are
 * folded into the contents of the outermost comment. This is because the
 * comment nesting is handled within the
 * [uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer].
 */
interface XPathComment : PsiElement {
    val isXQDoc: Boolean
}

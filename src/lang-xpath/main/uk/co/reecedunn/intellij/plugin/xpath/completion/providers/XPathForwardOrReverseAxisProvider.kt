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
package uk.co.reecedunn.intellij.plugin.xpath.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup

object XPathForwardOrReverseAxisProvider : CompletionProviderEx {
    private val XPATH_AXIS_STEPS = listOf(
        XPathKeywordLookup("ancestor", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("ancestor-or-self", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("attribute", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("child", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("descendant-or-self", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("following", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("following-sibling", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("namespace", XPathInsertText.AXIS_MARKER), // XPath only
        XPathKeywordLookup("parent", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("preceding", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("preceding-sibling", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("self", XPathInsertText.AXIS_MARKER)
    )

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        result.addAllElements(XPATH_AXIS_STEPS)
    }
}

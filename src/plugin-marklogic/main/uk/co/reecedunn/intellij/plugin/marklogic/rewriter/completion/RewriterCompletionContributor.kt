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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.completion

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionContributorEx

class RewriterCompletionContributor : CompletionContributorEx() {
    @Suppress("PropertyName")
    val Xml = PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(XmlFile::class.java))

    init {
        builder(Xml).withFilter(MethodValueTypeFilter).addCompletions(MethodValueTypeProvider)
    }
}

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
package uk.co.reecedunn.intellij.plugin.core.lang

import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.Key

interface LanguageData {
    companion object {
        val KEY: Key<LanguageData> = Key.create("uk.co.reecedunn.intellij.plugin.key.languageData")
    }

    val associations: List<FileNameMatcher>

    val mimeTypes: Array<String>
}

fun Language.getAssociations(): List<FileNameMatcher> {
    val associations = associatedFileType?.let { FileTypeManager.getInstance().getAssociations(it) } ?: listOf()
    return if (associations.isEmpty())
        this.getUserData(LanguageData.KEY)?.associations ?: listOf()
    else
        associations
}

fun Array<out Language>.getAssociations(): List<FileNameMatcher> {
    return asSequence().flatMap { language -> language.getAssociations().asSequence() }.toList()
}

fun Array<out Language>.findByAssociations(path: String): Language? = find { language ->
    language.getAssociations().find { association -> association.acceptsCharSequence(path) } != null
}

fun Language.getLanguageMimeTypes(): Array<String> {
    val mimeTypes = mimeTypes
    return if (mimeTypes.isEmpty())
        this.getUserData(LanguageData.KEY)?.mimeTypes ?: mimeTypes
    else
        mimeTypes
}

fun Array<out Language>.findByMimeType(predicate: (String) -> Boolean): Language? = find { language ->
    language.getLanguageMimeTypes().find { predicate(it) } != null
}

val Language.parserDefinition: ParserDefinition
    get() = LanguageParserDefinitions.INSTANCE.forLanguage(this)

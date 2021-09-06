/*
 * Copyright (C) 2016; 2018 Reece H. Dunn
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

import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathEscapeCharacterImpl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XQueryDirWhiteSpaceImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryCharRefImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryDirAttributeValueContentsImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin.PluginElementContentsImpl
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery.XQueryPredefinedEntityRefImpl

class XQueryASTFactory : ASTFactory() {
    override fun createComposite(type: IElementType): CompositeElement = CompositeElement(type)

    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement = when (type) {
        XQueryTokenType.CHARACTER_REFERENCE -> XQueryCharRefImpl(type, text)
        XQueryTokenType.PREDEFINED_ENTITY_REFERENCE -> XQueryPredefinedEntityRefImpl(type, text)
        XQueryTokenType.XML_ATTRIBUTE_NCNAME -> XmlNCNameImpl(type, text)
        XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS -> XQueryDirAttributeValueContentsImpl(type, text)
        XQueryTokenType.XML_ELEMENT_CONTENTS -> PluginElementContentsImpl(type, text)
        XQueryTokenType.XML_ESCAPED_CHARACTER -> XPathEscapeCharacterImpl(type, text)
        XQueryTokenType.XML_CHARACTER_REFERENCE -> XQueryCharRefImpl(type, text)
        XQueryTokenType.XML_COMMENT -> PsiCommentImpl(type, text)
        XQueryTokenType.XML_PI_TARGET_NCNAME -> XmlNCNameImpl(type, text)
        XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE -> XQueryPredefinedEntityRefImpl(type, text)
        XQueryTokenType.XML_TAG_NCNAME -> XmlNCNameImpl(type, text)
        XQueryTokenType.XML_WHITE_SPACE -> XQueryDirWhiteSpaceImpl(text)
        XQDocTokenType.CONTENTS -> PsiCommentImpl(type, text)
        is IKeywordOrNCNameType -> XmlNCNameImpl(type, text)
        else -> LeafPsiElement(type, text)
    }
}

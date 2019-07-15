/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.xml.toXmlAttributeValue

enum class XPathNamespaceType {
    DefaultElementOrType,
    DefaultFunction,
    None,
    Prefixed,
    Undefined,
    Using,
    XQuery,
}

interface XPathNamespaceDeclaration {
    val namespacePrefix: XsNCNameValue?

    val namespaceUri: XsAnyUriValue?
}

interface XPathDefaultNamespaceDeclaration : XPathNamespaceDeclaration {
    val namespaceType: XPathNamespaceType
}

private fun XmlAttribute.toDefaultNamespaceDeclaration(): XPathDefaultNamespaceDeclaration? {
    if (!isNamespaceDeclaration) return null
    val prefix = namespacePrefix
    val value = value ?: return null
    return if (prefix.isEmpty()) {
        object : XPathDefaultNamespaceDeclaration {
            override val namespacePrefix: XsNCNameValue? = null
            override val namespaceUri: XsAnyUriValue? = XsAnyUri(value, originalElement)
            override val namespaceType: XPathNamespaceType = XPathNamespaceType.DefaultElementOrType
        }
    } else {
        null
    }
}

private fun XmlAttribute.toNamespaceDeclaration(): XPathNamespaceDeclaration? {
    if (!isNamespaceDeclaration) return null
    val prefix = namespacePrefix
    val localName = localName
    val value = value ?: return null
    return if (prefix.isEmpty()) {
        null
    } else {
        object : XPathNamespaceDeclaration {
            override val namespacePrefix: XsNCNameValue? = XsNCName(localName, originalElement)
            override val namespaceUri: XsAnyUriValue? = XsAnyUri(value, originalElement)
        }
    }
}

private object DefaultFunctionXPathNamespace : XPathDefaultNamespaceDeclaration {
    private const val FN_NAMESPACE_URI = "http://www.w3.org/2005/xpath-functions"

    override val namespacePrefix: XsNCNameValue? = null
    override val namespaceUri: XsAnyUriValue? = XsAnyUri(FN_NAMESPACE_URI, null as PsiElement?)
    override val namespaceType: XPathNamespaceType = XPathNamespaceType.DefaultFunction
}

@Suppress("unused")
fun PsiElement.defaultFunctionXPathNamespace(): Sequence<XPathDefaultNamespaceDeclaration> {
    return sequenceOf(DefaultFunctionXPathNamespace)
}

fun PsiElement.defaultElementOrTypeXPathNamespace(): Sequence<XPathDefaultNamespaceDeclaration> {
    return toXmlAttributeValue()?.ancestors()?.filterIsInstance<XmlTag>()?.flatMap { tag ->
        tag.attributes.asSequence().map { attribute -> attribute.toDefaultNamespaceDeclaration() }
    }?.filterNotNull() ?: sequenceOf()
}

fun PsiElement.staticallyKnownXPathNamespaces(): Sequence<XPathNamespaceDeclaration> {
    return toXmlAttributeValue()?.ancestors()?.filterIsInstance<XmlTag>()?.flatMap { tag ->
        tag.attributes.asSequence().map { attribute -> attribute.toNamespaceDeclaration() }
    }?.filterNotNull() ?: sequenceOf()
}

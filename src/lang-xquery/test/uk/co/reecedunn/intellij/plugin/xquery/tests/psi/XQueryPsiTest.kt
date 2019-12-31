/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.findUsages.XQueryFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginAnyItemType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginAnyTextTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItem
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryElement
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("XQuery 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XQueryPsiTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    override fun registerModules(manager: MockModuleManager) {
        manager.addModule(ResourceVirtualFile.create(this::class.java.classLoader, "tests/module-xquery"))
    }

    @Nested
    @DisplayName("XQuery 3.1 (2) Basics")
    internal inner class Basics {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (217) URILiteral")
        internal inner class URILiteral {
            @Test
            @DisplayName("uri literal content")
            fun uriLiteral() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"Lorem ipsum.\uFFFF\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed uri literal content")
            fun unclosedUriLiteral() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"Lorem ipsum.")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val literal = parse<XPathUriLiteral>("module namespace test = '''\"\"'")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"''\"\"\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("non-keyword local name")
            fun identifier() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword local name")
            fun keyword() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}option")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("option"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`(""))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathURIQualifiedName>("declare option Q{http://www.example.com}* \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathURIQualifiedName>("(: :) Q{http://www.example.com}test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(31))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand; namespace prefix in statically-known namespaces")
            fun expandToExistingNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.w3.org/2001/XMLSchema}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                // The URIQualifiedName itself, not bound to a namespace.
                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("expand; namespace prefix not in statically-known namespaces")
            fun expandToMissingNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                // The URIQualifiedName itself, not bound to a namespace.
                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.com"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (224) BracedURILiteral")
        internal inner class BracedURILiteral {
            @Test
            @DisplayName("braced uri literal content")
            fun bracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.\uFFFF}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed braced uri literal content")
            fun unclosedBracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (234) QName")
        internal inner class QName {
            @Test
            @DisplayName("non-keyword prefix; non-keyword local name")
            fun identifier() {
                val qname = parse<XPathQName>("fn:true")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword prefix; non-keyword local name")
            fun keywordPrefix() {
                val qname = parse<XPathQName>("option:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("option"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("non-keyword prefix; keyword local name")
            fun keywordLocalName() {
                val qname = parse<XPathQName>("test:case")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("test"))
                assertThat(qname.localName!!.data, `is`("case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathQName>("xs:")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; before ':'")
            fun whitespaceInQName_beforeColon() {
                val qname = parse<XPathQName>("xs :string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; after ':'")
            fun whitespaceInQName_afterColon() {
                val qname = parse<XPathQName>("xs: string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; before and after ':'")
            fun whitespaceInQName_beforeAndAfterColon() {
                val qname = parse<XPathQName>("xs : string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathQName>("(: :) a:test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(8))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand; namespace prefix in statically-known namespaces")
            fun expandToExistingNamespace() {
                val qname = parse<XPathQName>("xs:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[0].prefix!!.data, `is`("xs"))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("expand; namespace prefix not in statically-known namespaces")
            fun expandToMissingNamespace() {
                val qname = parse<XPathQName>("xsd:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xsd"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (235) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("identifier")
            fun identifier() {
                val qname = parse<XPathNCName>("test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword")
            fun keyword() {
                val qname = parse<XPathNCName>("order")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("order"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathNCName>("declare option * \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathNCName>("(: :) test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(6))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand")
            fun expand() {
                val qname = parse<XPathNCName>("test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Test
        @DisplayName("Namespaces in XML 1.0 (3) Declaring Namespaces : EBNF (4) NCName")
        fun xmlNCName() {
            val literal = parse<XmlNCNameImpl>("test")[0] as XsNCNameValue
            assertThat(literal.data, `is`("test"))
            assertThat(literal.element, sameInstance(literal as PsiElement))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.4) SequenceType Syntax")
    internal inner class SequenceTypeSyntax {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val type = parse<XPathSequenceType>("() instance of empty-sequence ( (::) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("empty-sequence()"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("optional item")
            fun optionalItem() {
                val type = parse<XPathSequenceType>("() instance of xs:string ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string?"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional sequence")
            fun optionalSequence() {
                val type = parse<XPathSequenceType>("() instance of xs:string *")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string*"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("sequence")
            fun sequence() {
                val type = parse<XPathSequenceType>("() instance of xs:string +")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string+"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("parenthesized item type")
            fun parenthesizedItemType() {
                val type = parse<XPathSequenceType>("() instance of ( xs:string ) ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string)?"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("parenthesized sequence type")
            fun parenthesizedSequenceType() {
                val type = parse<XPathSequenceType>("() instance of ( xs:string + ) ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string+)?"))

                // NOTE: For the purposes of the plugin w.r.t. keeping consistent
                // analysis/logic given that mixing SequenceTypes like this is an
                // error, the outer-most SequenceType overrides the inner one.
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (186) ItemType")
        fun itemType() {
            val type = parse<PluginAnyItemType>("() instance of item ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("item()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
        internal inner class AtomicOrUnionType {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathAtomicOrUnionType>("() instance of xs:string")[0]
                assertThat(op_qname_presentation(test.type), `is`("xs:string"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnySimpleType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (189) AnyKindTest")
        fun anyKindTest() {
            val type = parse<XPathAnyKindTest>("() instance of node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (190) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("any")
            fun any() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( (::) )")[0]
                assertThat(test.rootNodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocument::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("element test")
            fun elementTest() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( element ( (::) ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(XPathElementTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(element())"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocument::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("schema element test")
            fun schemaElementTest() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( schema-element ( test ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(XPathSchemaElementTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(schema-element(test))"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocument::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (191) TextTest")
        fun textTest() {
            val type = parse<PluginAnyTextTest>("() instance of text ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("text()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmText::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (192) CommentTest")
        fun commentTest() {
            val type = parse<XPathCommentTest>("() instance of comment ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("comment()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmComment::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (193) NamespaceNodeTest")
        fun namespaceNodeTest() {
            val type = parse<XPathNamespaceNodeTest>("() instance of namespace-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("namespace-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNamespace::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (194) PITest")

        internal inner class PITest {
            @Test
            @DisplayName("any")
            fun any() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstruction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("NCName")
            fun ncname() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( test )")[0]
                assertThat(test.nodeName, `is`(instanceOf(XsNCNameValue::class.java)))
                assertThat((test.nodeName as XsNCNameValue).data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstruction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("StringLiteral")
            fun stringLiteral() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( \" test \" )")[0]

                val nodeName = test.nodeName as XsNCNameValue
                assertThat(nodeName.data, `is`("test"))
                assertThat(nodeName.element, `is`(sameInstance(test.children().filterIsInstance<XPathStringLiteral>().firstOrNull())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstruction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (206) TypeName")
        internal inner class TypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of element(*, test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathTypeName>("() instance of element( *, xs:string )")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (216) ParenthesizedItemType")
        internal inner class ParenthesizedItemType {
            @Test
            @DisplayName("item type")
            fun itemType() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( text ( (::) ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(text())"))
                assertThat(type.itemType?.typeName, `is`("text()"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("error recovery: missing type")
            fun missingType() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( (::) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(empty-sequence())"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("error recovery: empty sequence")
            fun emptySequence() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( empty-sequence ( (::) ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(empty-sequence())"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("error recovery: optional item")
            fun optionalItem() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string ? ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string?)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("error recovery: optional sequence")
            fun optionalSequence() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string * ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string*)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("error recovery: optional item")
            fun sequence() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string + ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string+)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.3) Element Test")
    internal inner class ElementTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (199) ElementTest")
        internal inner class ElementTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<XPathElementTest>("() instance of element ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<XPathElementTest>("() instance of element ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name only")
            fun nameOnly() {
                val test = parse<XPathElementTest>("() instance of element ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("type only")
            fun typeOnly() {
                val test = parse<XPathElementTest>("() instance of element ( * , elem-type )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*,elem-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("nillable type only")
            fun nillableTypeOnly() {
                val test = parse<XPathElementTest>("() instance of element ( * , elem-type ? )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type?"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(0))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*,elem-type?)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name and type")
            fun nameAndType() {
                val test = parse<XPathElementTest>("() instance of element ( test , elem-type )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(test,elem-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name and nillable type")
            fun nameAndNillableType() {
                val test = parse<XPathElementTest>("() instance of element ( test , elem-type ? )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type?"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(0))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(test,elem-type?)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (204) ElementName")
        internal inner class ElementName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of element(test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.4) Schema Element Test")
    internal inner class SchemaElementTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (201) SchemaElementTest")
        internal inner class SchemaElementTest {
            @Test
            @DisplayName("missing element declaration")
            fun missingElementDeclaration() {
                val test = parse<XPathSchemaElementTest>("() instance of schema-element ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-element(<unknown>)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("element declaration")
            fun elementDeclaration() {
                val test = parse<XPathSchemaElementTest>("() instance of schema-element ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-element(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElement::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (202) ElementDeclaration")
        internal inner class ElementDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of schema-element(test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.5) Attribute Test")
    internal inner class AttributeTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (195) AttributeTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name only")
            fun nameOnly() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("type only")
            fun typeOnly() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( * , attr-type )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("attr-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(*,attr-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name and type")
            fun nameAndType() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( test , attr-type )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("attr-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(test,attr-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (203) AttributeName")
        internal inner class AttributeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of attribute(test)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.6) Schema Attribute Test")
    internal inner class SchemaAttributeTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (197) SchemaAttributeTest")
        internal inner class SchemaAttributeTest {
            @Test
            @DisplayName("missing attribute declaration")
            fun missingAttributeDeclaration() {
                val test = parse<XPathSchemaAttributeTest>("() instance of schema-attribute ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-attribute(<unknown>)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("attribute declaration")
            fun attributeDeclaration() {
                val test = parse<XPathSchemaAttributeTest>("() instance of schema-attribute ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-attribute(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttribute::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (198) AttributeDeclaration")
        internal inner class AttributeDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of schema-attribute(test)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.7) Function Test")
    internal inner class FunctionTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (207) FunctionTest")
        internal inner class FunctionTest {
            @Test
            @DisplayName("one annotation ; any function test")
            fun oneAnnotation() {
                val test = parse<XQueryFunctionTest>("() instance of % test function ( * )")[0]
                assertThat((test.functionTest as XdmItemType).typeName, `is`("function(*)"))

                val annotations = test.annotations.toList()
                assertThat(annotations.size, `is`(1))
                assertThat(
                    op_qname_presentation(
                        annotations[0].name!!
                    ), `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("%test function(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple annotations ; typed function test")
            fun multipleAnnotations() {
                val test = parse<XQueryFunctionTest>("() instance of % one % two function ( xs:long ) as xs:long")[0]
                assertThat((test.functionTest as XdmItemType).typeName, `is`("function(xs:long) as xs:long"))

                val annotations = test.annotations.toList()
                assertThat(annotations.size, `is`(2))
                assertThat(
                    op_qname_presentation(
                        annotations[0].name!!
                    ), `is`("one"))
                assertThat(
                    op_qname_presentation(
                        annotations[1].name!!
                    ), `is`("two"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("%one %two function(xs:long) as xs:long"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("annotation with missing name")
            fun annotationWithoutName() {
                val test = parse<XQueryFunctionTest>("() instance of % % two function ( xs:long ) as xs:long")[0]
                assertThat((test.functionTest as XdmItemType).typeName, `is`("function(xs:long) as xs:long"))

                val annotations = test.annotations.toList()
                assertThat(annotations.size, `is`(2))
                assertThat(annotations[0].name, `is`(nullValue()))
                assertThat(
                    op_qname_presentation(
                        annotations[1].name!!
                    ), `is`("two"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("%two function(xs:long) as xs:long"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (208) AnyFunctionTest")
        fun anyFunctionTest() {
            val test = parse<XPathAnyFunctionTest>("() instance of function ( * )")[0]

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("function(*)"))
            assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (209) TypedFunctionTest")
        internal inner class TypedFunctionTest {
            @Test
            @DisplayName("no parameters")
            fun noParameters() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( ) as empty-sequence ( (::) )")[0]
                assertThat(test.returnType?.typeName, `is`("empty-sequence()"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(0))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function() as empty-sequence()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one parameter")
            fun oneParameter() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( xs:float ) as xs:integer")[0]
                assertThat(test.returnType?.typeName, `is`("xs:integer"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(1))
                assertThat(paramTypes[0].typeName, `is`("xs:float"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(xs:float) as xs:integer"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple parameters")
            fun multipleParameters() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( xs:long , array ( * ) ) as xs:double +")[0]
                assertThat(test.returnType?.typeName, `is`("xs:double+"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(2))
                assertThat(paramTypes[0].typeName, `is`("xs:long"))
                assertThat(paramTypes[1].typeName, `is`("array(*)"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(xs:long, array(*)) as xs:double+"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing return type")
            fun missingReturnType() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( map ( * ) )")[0]
                assertThat(test.returnType, `is`(nullValue()))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(1))
                assertThat(paramTypes[0].typeName, `is`("map(*)"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(map(*)) as item()*"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.8) Map Test")
    internal inner class MapTest {
        @Test
        @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
        fun anyMapTest() {
            val type = parse<XPathAnyMapTest>("() instance of map ( * )")[0] as XdmItemType
            assertThat(type.typeName, `is`("map(*)"))
            assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (212) TypedMapTest")
        internal inner class TypedMapTest {
            @Test
            @DisplayName("key and value type")
            fun keyAndValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( xs:string , xs:int )")[0]
                assertThat(test.keyType?.typeName, `is`("xs:string"))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:string, xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing key type")
            fun missingKeyType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( , xs:int )")[0]
                assertThat(test.keyType, `is`(nullValue()))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:anyAtomicType, xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing value type")
            fun missingValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( xs:string , )")[0]
                assertThat(test.keyType?.typeName, `is`("xs:string"))
                assertThat(test.valueType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:string, item()*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing key and value type")
            fun missingKeyAndValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( , )")[0]
                assertThat(test.keyType, `is`(nullValue()))
                assertThat(test.valueType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.9) Array Test")
    internal inner class ArrayTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
        internal inner class AnyArrayTest {
            @Test
            @DisplayName("any array test")
            fun anyArrayTest() {
                val type = parse<XPathAnyArrayTest>("() instance of array ( * )")[0] as XdmItemType
                assertThat(type.typeName, `is`("array(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing star or sequence type")
            fun missingStarOrSequenceType() {
                val type = parse<XPathAnyArrayTest>("() instance of array ( )")[0] as XdmItemType
                assertThat(type.typeName, `is`("array(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
        fun typedArrayTest() {
            val test = parse<XPathTypedArrayTest>("() instance of array ( node ( ) )")[0]
            assertThat(test.memberType.typeName, `is`("node()"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("array(node())"))
            assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.1) Literals")
    internal inner class Literals {
        @Test
        @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
        fun integerLiteral() {
            val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
            assertThat(literal.data, `is`(BigInteger.valueOf(123)))
            assertThat(literal.toInt(), `is`(123))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (220) DecimalLiteral")
        fun decimalLiteral() {
            val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
            assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (221) DoubleLiteral")
        fun doubleLiteral() {
            val literal = parse<XPathDoubleLiteral>("1e3")[0] as XsDoubleValue
            assertThat(literal.data, `is`(1e3))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteral() {
                val literal = parse<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")[0] as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed string literal content")
            fun unclosedStringLiteral() {
                val literal = parse<XPathStringLiteral>("\"Lorem ipsum.")[0] as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val literal = parse<XPathStringLiteral>("'''\"\"'")[0] as XsStringValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val literal = parse<XPathStringLiteral>("\"''\"\"\"")[0] as XsStringValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0] as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")[0] as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.2) Variable References")
    internal inner class VariableReferences {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (131) VarRef")
        internal inner class VarRef {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathVarRef>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                )[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$")[0] as XPathVariableReference
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (132) VarName")
        internal inner class VarName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarName>("let \$x := 2 return \$y")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarName>("let \$a:x := 2 return \$a:y")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathVarName>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathNCName>("\$test")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("variable"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.5) Static Function Calls")
    internal inner class StaticFunctionCalls {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (122) ArgumentList")
        internal inner class ArgumentList {
            @Test
            @DisplayName("empty parameters")
            fun empty() {
                val args = parse<XPathArgumentList>("fn:true()")[0]
                assertThat(args.arity, `is`(0))
            }

            @Test
            @DisplayName("multiple ExprSingle parameters")
            fun multiple() {
                val args = parse<XPathArgumentList>("math:pow(2, 8)")[0]
                assertThat(args.arity, `is`(2))
            }

            @Test
            @DisplayName("ArgumentPlaceholder parameter")
            fun argumentPlaceholder() {
                val args = parse<XPathArgumentList>("math:sin(?)")[0]
                assertThat(args.arity, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("non-empty ArgumentList")
            fun nonEmptyArguments() {
                val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(2))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("pow"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(2))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(2))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("x"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("2"))

                assertThat(op_qname_presentation(bindings[1].param.variableName!!), `is`("y"))
                assertThat(bindings[1].size, `is`(1))
                assertThat(bindings[1][0].text, `is`("8"))
            }

            @Test
            @DisplayName("empty ArgumentList")
            fun emptyArguments() {
                val f = parse<XPathFunctionCall>("fn:true()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("ArgumentPlaceholder")
            fun argumentPlaceholder() {
                val f = parse<XPathFunctionCall>("math:sin(?)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("sin"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(1))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(1))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("θ"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("?"))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathFunctionCall>(":true(1)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(1))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    test()
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunctionRef))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.6) Named Function References")
    internal inner class NamedFunctionReferences {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Test
            @DisplayName("named function reference")
            fun namedFunctionRef() {
                val f = parse<XPathNamedFunctionRef>("true#3")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(3))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing arity")
            fun missingArity() {
                val f = parse<XPathNamedFunctionRef>("true#")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathNamedFunctionRef>(":true#0")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    test#1
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunctionRef))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.7) Inline Function Expressions")
    internal inner class InlineFunctionExpression {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
        internal inner class InlineFunctionExpr {
            @Test
            @DisplayName("empty ParamList")
            fun emptyParamList() {
                val decl = parse<XPathFunctionDeclaration>("function () {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XPathFunctionDeclaration>("function (\$one, \$two) {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
            }

            @Test
            @DisplayName("non-empty ParamList with types")
            fun nonEmptyParamListWithTypes() {
                val decl =
                    parse<XPathFunctionDeclaration>("function (\$one  as  array ( * ), \$two  as  node((::))) {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
            }

            @Test
            @DisplayName("return type")
            fun returnType() {
                val decl = parse<XPathFunctionDeclaration>("function ()  as  xs:boolean  {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType?.typeName, `is`("xs:boolean"))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.2.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        internal inner class ForwardAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("""
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                """)
                assertThat(steps.size, `is`(8))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // child
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute)) // attribute
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // self
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant-or-self
                assertThat(steps[5].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following-sibling
                assertThat(steps[6].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following
                assertThat(steps[7].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Namespace)) // namespace
            }

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>(
                    """
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                    """
                ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                assertThat(steps.size, `is`(8))
                assertThat(XQueryFindUsagesProvider.getType(steps[0]), `is`("element")) // child
                assertThat(XQueryFindUsagesProvider.getType(steps[1]), `is`("element")) // descendant
                assertThat(XQueryFindUsagesProvider.getType(steps[2]), `is`("attribute")) // attribute
                assertThat(XQueryFindUsagesProvider.getType(steps[3]), `is`("element")) // self
                assertThat(XQueryFindUsagesProvider.getType(steps[4]), `is`("element")) // descendant-or-self
                assertThat(XQueryFindUsagesProvider.getType(steps[5]), `is`("element")) // following-sibling
                assertThat(XQueryFindUsagesProvider.getType(steps[6]), `is`("element")) // following
                assertThat(XQueryFindUsagesProvider.getType(steps[7]), `is`("namespace")) // namespace
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (116) ReverseAxis")
        internal inner class ReverseAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>(
                    "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                )
                assertThat(steps.size, `is`(5))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // parent
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding-sibling
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor-or-self
            }

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>(
                    "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                assertThat(steps.size, `is`(5))
                assertThat(XQueryFindUsagesProvider.getType(steps[0]), `is`("element")) // parent
                assertThat(XQueryFindUsagesProvider.getType(steps[1]), `is`("element")) // ancestor
                assertThat(XQueryFindUsagesProvider.getType(steps[2]), `is`("element")) // preceding-sibling
                assertThat(XQueryFindUsagesProvider.getType(steps[3]), `is`("element")) // preceding
                assertThat(XQueryFindUsagesProvider.getType(steps[4]), `is`("element")) // ancestor-or-self
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.2.2) Node Tests")
    internal inner class NodeTests {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (119) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("NCName namespace resolution; element principal node kind")
            fun elementNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    ancestor::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; attribute principal node kind")
            fun attributeNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    attribute::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; namespace principal node kind")
            fun namespaceNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    namespace::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("namespace"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (120) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("any")
            fun any() {
                val qname = parse<XPathWildcard>("*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("wildcard prefix; wildcard local name")
            fun bothWildcard() {
                val qname = parse<XPathWildcard>("*:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("wildcard prefix; non-wildcard local name")
            fun wildcardPrefix() {
                val qname = parse<XPathWildcard>("*:test")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.localName!!.data, `is`("test"))
            }

            @Test
            @DisplayName("non-wildcard prefix; wildcard local name")
            fun wildcardLocalName() {
                val qname = parse<XPathWildcard>("test:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.prefix!!.data, `is`("test"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathWildcard>("*:")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun keyword() {
                val qname = parse<XPathWildcard>("Q{http://www.example.com}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("URIQualifiedName with an empty namespace")
            fun emptyNamespace() {
                val qname = parse<XPathWildcard>("Q{}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`(""))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.5) Abbreviated Syntax")
    internal inner class AbbreviatedSyntax {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("one, @two")
                assertThat(steps.size, `is`(2))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element))
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute))
            }

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>("one, @two").map {
                    it.walkTree().filterIsInstance<XsQNameValue>().first().element!!
                }
                assertThat(steps.size, `is`(2))
                assertThat(XQueryFindUsagesProvider.getType(steps[0]), `is`("element"))
                assertThat(XQueryFindUsagesProvider.getType(steps[1]), `is`("attribute"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.1) Direct Element Constructors")
    internal inner class DirectElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("open and close tags")
            fun openAndCloseTags() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("self-closing")
            fun selfClosing() {
                val element = parse<XQueryDirElemConstructor>("<h:br/>")[0]
                assertThat(element.isSelfClosing, `is`(true))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("h"))
                assertThat(open.localName!!.data, `is`("br"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("error recovery: missing close tag")
            fun missingClosingTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("error recovery: incomplete open tag")
            fun incompleteOpenTag() {
                val element = parse<XQueryDirElemConstructor>("<a:></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName, `is`(nullValue()))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("error recovery: incomplete close tag")
            fun incompleteCloseTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName, `is`(nullValue()))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("error recovery: partial close tag only")
            fun partialCloseTagOnly() {
                val element = parse<XQueryDirElemConstructor>("</<test>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag
                assertThat(open, `is`(nullValue()))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(nullValue()))
            }

            @Test
            @DisplayName("error recovery: close tag only")
            fun soloCloseTag() {
                val element = parse<XQueryDirElemConstructor>("</a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))

                val xdm = element as XQueryElement
                assertThat(xdm.nodeName, `is`(sameInstance(element.openTag)))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    <test/>
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("<elem test=\"\"/>")[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com\uFFFF\"/>")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("'\"\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("''\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"x{\$y}z\"")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3.9.1.2) Namespace Declaration Attributes")
        internal inner class NamespaceDeclarationAttributes {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("namespace prefix containing an EnclosedExpr")
            fun namespacePrefixWithEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("default element/type namespace containing an EnclosedExpr")
            fun defaultElementTypeNamespaceEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.3) Computed Constructors")
    internal inner class ComputedElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
        internal inner class CompElemConstructor {
            @Test
            @DisplayName("name")
            fun name() {
                val element = parse<XQueryCompElemConstructor>("element a:b {}")[0] as XQueryElement

                val name = element.nodeName!!
                assertThat(name.prefix!!.data, `is`("a"))
                assertThat(name.localName!!.data, `is`("b"))
            }

            @Test
            @DisplayName("expression")
            fun expr() {
                val element = parse<XQueryCompElemConstructor>("element { \"a:\" || \"b\" } {}")[0] as XQueryElement
                assertThat(element.nodeName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    element test {}
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        internal inner class CompAttrConstructor {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("attribute test {}")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.11.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (171) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("key, value")
            fun keyValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("key, no value")
            fun noValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \$ a }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathElementType.MAP_KEY_EXPR))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12) FLWORExpressions")
    internal inner class FLWORExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
        internal inner class PositionalVar {
            @Test
            @DisplayName("NCName")
            fun testPositionalVar_NCName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testPositionalVar_QName() {
                val expr = parse<XQueryPositionalVar>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPositionalVar_URIQualifiedName() {
                val expr = parse<XQueryPositionalVar>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testPositionalVar_MissingVarName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.2) For Clause")
    internal inner class ForClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
        internal inner class ForBinding {
            @Test
            @DisplayName("NCName")
            fun testForBinding_NCName() {
                val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testForBinding_QName() {
                val expr = parse<XQueryForBinding>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testForBinding_URIQualifiedName() {
                val expr = parse<XQueryForBinding>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testForBinding_MissingVarName() {
                val expr = parse<XQueryForBinding>("for \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.3) Let Clause")
    internal inner class LetClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (49) LetBinding")
        internal inner class LetBinding {
            @Test
            @DisplayName("NCName")
            fun testLetBinding_NCName() {
                val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testLetBinding_QName() {
                val expr = parse<XQueryLetBinding>("let \$a:x := 2 return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testLetBinding_URIQualifiedName() {
                val expr = parse<XQueryLetBinding>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testLetBinding_MissingVarName() {
                val expr = parse<XQueryLetBinding>("let \$ := 2 return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.4) Window Clause")
    internal inner class WindowClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause")
        internal inner class TumblingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testTumblingWindowClause_NCName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testTumblingWindowClause_QName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testTumblingWindowClause_URIQualifiedName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testTumblingWindowClause_MissingVarName() {
                val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
        internal inner class SlidingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testSlidingWindowClause_NCName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testSlidingWindowClause_QName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testSlidingWindowClause_URIQualifiedName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testSlidingWindowClause_MissingVarName() {
                val expr = parse<XQuerySlidingWindowClause>("for sliding window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
        internal inner class CurrentItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("variable"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testCurrentItem_NCName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$x in \$y start \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testCurrentItem_QName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCurrentItem_URIQualifiedName() {
                val expr = parse<XQueryCurrentItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
        internal inner class PreviousItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start previous \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("variable"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testPreviousItem_NCName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$x in \$y start \$v previous \$w when true() return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testPreviousItem_QName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v previous \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPreviousItem_URIQualifiedName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v previous \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (58) NextItem")
        internal inner class NextItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start next \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("variable"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testNextItem_NCName() {
                val expr = parse<XQueryNextItem>("for sliding window \$x in \$y start \$v next \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testNextItem_QName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v next \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testNextItem_URIQualifiedName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v next \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.6) Count Clause")
    internal inner class CountClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (59) CountClause")
        internal inner class CountClauseTest {
            @Test
            @DisplayName("NCName")
            fun testCountClause_NCName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testCountClause_QName() {
                val expr = parse<XQueryCountClause>("for \$a:x in \$a:y count \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCountClause_URIQualifiedName() {
                val expr = parse<XQueryCountClause>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCountClause_MissingVarName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.7) Group By Clause")
    internal inner class GroupByClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (63) GroupingSpec")
        internal inner class GroupingSpec {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }

            @Test
            @DisplayName("non-empty collation uri")
            fun nonEmptyUri() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$x in \$y group by \$x collation 'http://www.example.com'"
                )[0]
                assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("missing collation uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$x")[0]
                assertThat(expr.collation, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (64) GroupingVariable")
        internal inner class GroupingVariable {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$")[0] as XPathVariableName
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.8) Order By Clause")
    internal inner class OrderByClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (68) OrderModifier")
        internal inner class OrderModifier {
            @Test
            @DisplayName("non-empty collation uri")
            fun nonEmptyUri() {
                val expr = parse<XQueryOrderModifier>(
                    "for \$x in \$y order by \$x collation 'http://www.example.com'"
                )[0]
                assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("missing collation uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryOrderModifier>("for \$x in \$y order by \$x ascending")[0]
                assertThat(expr.collation, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.16) Quantified Expressions")
    internal inner class QuantifiedExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr")
        internal inner class QuantifiedExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<PluginQuantifiedExprBinding>(
                    "some \$Q{http://www.example.com}x in \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
    internal inner class Typeswitch {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (75) CaseClause")
        internal inner class CaseClause {
            @Test
            @DisplayName("NCName")
            fun testCaseClause_NCName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$x) case \$y as xs:string return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testCaseClause_QName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$a:x) case \$a:y as xs:string return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCaseClause_URIQualifiedName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$Q{http://www.example.com}x) " +
                            "case \$Q{http://www.example.com}y as xs:string " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCaseClause_NoVarName() {
                val expr = parse<XQueryCaseClause>("typeswitch (\$x) case xs:string return \$z")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (76) SequenceTypeUnion")
        fun sequenceTypeUnion() {
            val test = parse<XQuerySequenceTypeUnion>(
                "typeswitch (\$x) case \$y as node ( (::) ) | xs:string | array ( * ) return \$y"
            )[0]
            assertThat(test.isParenthesized, `is`(false))

            val type = test as XdmSequenceTypeUnion
            assertThat(type.typeName, `is`("node() | xs:string | array(*)"))

            val types = type.types.toList()
            assertThat(types.size, `is`(3))
            assertThat(types[0].typeName, `is`("node()"))
            assertThat(types[1].typeName, `is`("xs:string"))
            assertThat(types[2].typeName, `is`("array(*)"))

            assertThat(type.itemType?.typeName, `is`("item()"))
            assertThat(type.lowerBound, `is`(0))
            assertThat(type.upperBound, `is`(Int.MAX_VALUE))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.18.3) Cast")
    internal inner class Cast {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (205) SimpleTypeName")
        internal inner class SimpleTypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () cast as test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathSimpleTypeName>("() cast as xs:string")[0]
                assertThat(op_qname_presentation(test.type), `is`("xs:string"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (182) SingleType")
        fun singleType() {
            val type = parse<XPathSingleType>("() cast as xs:string ?")[0] as XdmItemType
            assertThat(type.typeName, `is`("xs:string?"))
            assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

            assertThat(type.itemType, `is`(sameInstance((type as PsiElement).firstChild)))
            assertThat(type.lowerBound, `is`(0))
            assertThat(type.upperBound, `is`(Int.MAX_VALUE))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.20) Arrow Operator")
    internal inner class ArrowOperator {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Test
            @DisplayName("EQName specifier, non-empty ArgumentList")
            fun nonEmptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => format-date(1, 2, 3,  4)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(5))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("format-date"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(4))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(5))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("value"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("\$x "))

                assertThat(op_qname_presentation(bindings[1].param.variableName!!), `is`("picture"))
                assertThat(bindings[1].size, `is`(1))
                assertThat(bindings[1][0].text, `is`("1"))

                assertThat(op_qname_presentation(bindings[2].param.variableName!!), `is`("language"))
                assertThat(bindings[2].size, `is`(1))
                assertThat(bindings[2][0].text, `is`("2"))

                assertThat(op_qname_presentation(bindings[3].param.variableName!!), `is`("calendar"))
                assertThat(bindings[3].size, `is`(1))
                assertThat(bindings[3][0].text, `is`("3"))

                assertThat(op_qname_presentation(bindings[4].param.variableName!!), `is`("place"))
                assertThat(bindings[4].size, `is`(1))
                assertThat(bindings[4][0].text, `is`("4"))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList")
            fun emptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(1))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("\$x "))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList, second call in the chain")
            fun secondFunctionSpecifier() {
                val f = parse<XPathArrowFunctionSpecifier>(
                    "\$x => upper-case() => string-to-codepoints()"
                )[1] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("string-to-codepoints"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(1))

                assertThat(op_qname_presentation(bindings[0].param.variableName!!), `is`("arg"))
                assertThat(bindings[0].size, `is`(1))
                assertThat(bindings[0][0].text, `is`("upper-case()"))
            }

            @Test
            @DisplayName("EQName specifier, missing ArgumentList")
            fun missingArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat((f as XPathArrowFunctionSpecifier).argumentList, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => :upper-case()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () => test()
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunctionRef))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.22) Extension Expressions")
    internal inner class ExtensionExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("(# test #)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("pragma"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4) Modules and Prologs")
    internal inner class ModulesAndPrologs {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (1) Module")
        internal inner class Module {
            @Test
            @DisplayName("empty file")
            fun emptyFile() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl missing")
            fun noVersionDecl() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with version")
            fun versionDeclWithVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"1.0\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))
            }

            @Test
            @DisplayName("VersionDecl with unsupported version")
            fun versionDeclWithUnsupportedVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"9.4\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with empty version")
            fun versionDeclWithEmptyVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with missing version")
            fun versionDeclWithMissingVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery; 1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryMainModule>("()")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryMainModule>("declare function local:func() {}; ()")[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("local:func"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (4) LibraryModule")
        internal inner class LibraryModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryLibraryModule>("module namespace test = \"http://www.example.com\";")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryLibraryModule>(
                    """
                        module namespace test = "http://www.example.com";
                        declare function test:func() {};
                    """
                )[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (38) QueryBody")
        fun queryBody() {
            val decl = parse<XQueryQueryBody>("1 div 2")[0]

            val presentation = decl.presentation!!
            assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
            assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
            assertThat(presentation.presentableText, `is`("query body"))
            assertThat(presentation.locationString, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.1) Version Declaration")
    internal inner class VersionDecl {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (2) VersionDecl")
        internal inner class VersionDecl {
            @Test
            @DisplayName("no version, no encoding")
            fun noVersionOrEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery;")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("version, no encoding")
            fun versionOnly() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\";")[0]
                assertThat(decl.version!!.data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, encoding")
            fun encodingOnly() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"latin1\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat(decl.encoding!!.data, `is`("latin1"))
            }

            @Test
            @DisplayName("empty version, no encoding")
            fun emptyVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"\";")[0]
                assertThat(decl.version!!.data, `is`(""))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, empty encoding")
            fun emptyEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat(decl.encoding!!.data, `is`(""))
            }

            @Test
            @DisplayName("version, encoding")
            fun versionAndEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"latin1\";")[0]
                assertThat(decl.version!!.data, `is`("1.0"))
                assertThat(decl.encoding!!.data, `is`("latin1"))
            }

            @Test
            @DisplayName("version, empty encoding")
            fun emptyEncodingWithVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"\";")[0]
                assertThat(decl.version!!.data, `is`("1.0"))
                assertThat(decl.encoding!!.data, `is`(""))
            }

            @Test
            @DisplayName("comment before declaration")
            fun commentBefore() {
                val decl = parse<XQueryVersionDecl>("(: test :)\nxquery version \"1.0\";")[0]
                assertThat(decl.version!!.data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("comment as whitespace")
            fun commentAsWhitespace() {
                val decl = parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
                assertThat(decl.version!!.data, `is`("1.0"))
                assertThat(decl.encoding!!.data, `is`("latin1"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.2) Module Declaration")
    internal inner class ModuleDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 [4.2] Module Declaration : [5] ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("without prolog")
            fun withoutProlog() {
                val decl = parse<XQueryModuleDecl>("module namespace test = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                assertThat((decl as XQueryPrologResolver).prolog.count(), `is`(0))

                val prefix = decl.namespacePrefix?.element?.parent!!
                assertThat(XQueryFindUsagesProvider.getType(prefix), `is`("namespace"))
            }

            @Test
            @DisplayName("with prolog")
            fun withProlog() {
                val decl = parse<XQueryModuleDecl>("""
                    module namespace test = "http://www.example.com";
                    declare function test:func() {};
                """)[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                val prologs = (decl as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val decl = parse<XQueryModuleDecl>("module namespace = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val decl = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.4) Default Collation Declaration")
    internal inner class DefaultCollationDecl {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (10) DefaultCollationDecl")
        internal inner class DefaultCollationDecl {
            @Test
            @DisplayName("non-empty collation uri")
            fun nonEmptyUri() {
                val expr = parse<XQueryDefaultCollationDecl>("declare default collation 'http://www.example.com';")[0]
                assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("missing collation uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryDefaultCollationDecl>("declare default collation;")[0]
                assertThat(expr.collation, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.5) Base URI Declaration")
    internal inner class BaseURIDecl {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (11) BaseURIDecl")
        internal inner class BaseURIDecl {
            @Test
            @DisplayName("non-empty uri")
            fun nonEmptyUri() {
                val expr = parse<XQueryBaseURIDecl>("declare base-uri 'http://www.example.com';")[0]
                assertThat(expr.baseUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.baseUri!!.context, `is`(XdmUriContext.BaseUri))
                assertThat(expr.baseUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryBaseURIDecl>("declare base-uri;")[0]
                assertThat(expr.baseUri, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.10) Decimal Format Declaration")
    internal inner class DecimalFormatDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
        internal inner class DecimalFormatDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare decimal-format test;")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("decimal format"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.11) Schema Import")
    internal inner class SchemaImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val import = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))

                assertThat(import.accepts(XPathNamespaceType.DefaultElementOrType), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.Prefixed), `is`(true))
                assertThat(import.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.XQuery), `is`(false))

                val prefix = import.namespacePrefix?.element?.parent!!
                assertThat(XQueryFindUsagesProvider.getType(prefix), `is`("namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val import = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))

                assertThat(import.accepts(XPathNamespaceType.DefaultElementOrType), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.Prefixed), `is`(true))
                assertThat(import.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val import = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XPathDefaultNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri, `is`(nullValue()))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))

                assertThat(import.accepts(XPathNamespaceType.DefaultElementOrType), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.Prefixed), `is`(true))
                assertThat(import.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default element namespace")
            fun defaultElementNamespace() {
                val import = parse<XQuerySchemaImport>("import schema default element namespace 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))

                assertThat(import.accepts(XPathNamespaceType.DefaultElementOrType), `is`(true))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.Prefixed), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(import.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("location uris; single uri")
            fun singleLocationUri() {
                val import = parse<XQuerySchemaImport>(
                    "import schema namespace test = 'http://www.example.com' at 'test1.xsd';"
                )[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(1))

                assertThat(uris[0].data, `is`("test1.xsd"))
                assertThat(uris[0].context, `is`(XdmUriContext.Location))
                assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))
            }

            @Test
            @DisplayName("location uris; multiple uris")
            fun multipleLocationUris() {
                val import = parse<XQuerySchemaImport>(
                    "import schema namespace test = 'http://www.example.com' at 'test1.xsd' , 'test2.xsd';"
                )[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(2))

                assertThat(uris[0].data, `is`("test1.xsd"))
                assertThat(uris[0].context, `is`(XdmUriContext.Location))
                assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                assertThat(uris[1].data, `is`("test2.xsd"))
                assertThat(uris[1].context, `is`(XdmUriContext.Location))
                assertThat(uris[1].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.12) Module Import")
    internal inner class ModuleImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val import = parse<XQueryModuleImport>(
                    "import module namespace test = 'http://www.example.com';"
                )[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))

                val prefix = import.namespacePrefix?.element?.parent!!
                assertThat(XQueryFindUsagesProvider.getType(prefix), `is`("namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix NCName")
            fun noNamespacePrefixNCName() {
                val import = parse<XQueryModuleImport>(
                    "import module namespace = 'http://www.example.com';"
                )[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val import = parse<XQueryModuleImport>(
                    "import module 'http://www.example.com';"
                )[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri, `is`(nullValue()))

                val uris = (import as XQueryImport).locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("location uris; single uri")
            fun singleLocationUri() {
                val import = parse<XQueryModuleImport>(
                    "import module namespace test = 'http://www.example.com' at 'test1.xqy';"
                )[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(1))

                assertThat(uris[0].data, `is`("test1.xqy"))
                assertThat(uris[0].context, `is`(XdmUriContext.Location))
                assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
            }

            @Test
            @DisplayName("location uris; multiple uris")
            fun multipleLocationUris() {
                val import = parse<XQueryModuleImport>(
                    "import module namespace test = 'http://www.example.com' at 'test1.xqy' , 'test2.xqy';"
                )[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(2))

                assertThat(uris[0].data, `is`("test1.xqy"))
                assertThat(uris[0].context, `is`(XdmUriContext.Location))
                assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                assertThat(uris[1].data, `is`("test2.xqy"))
                assertThat(uris[1].context, `is`(XdmUriContext.Location))
                assertThat(uris[1].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve-xquery/files/test.xq"))
                }

                @Test
                @DisplayName("parent directory")
                fun parentDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_ParentDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/namespaces/ModuleDecl.xq"))
                }

                @Test
                @DisplayName("module in relative directory")
                fun moduleInRelativeDirectory() {
                    val file = parseResource("tests/resolve-xquery/ModuleImport_URILiteral_InDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/namespaces/ModuleDecl.xq"))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// (import namespace) file matching")
                fun httpProtocolOnNamespace() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_NamespaceOnly.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// (import namespace) file missing")
                fun httpProtocolOnNamespaceMissing() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_NamespaceOnly_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("multiple location URIs")
                fun multipleLocationUris() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_MultipleLocationUris.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(2))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve-xquery/files/test.xq"))
                    assertThat(prologs[1].resourcePath(), endsWith("/tests/resolve-xquery/files/test2.xq"))
                }

                @Test
                @DisplayName("module root")
                fun moduleRoot() {
                    val psi = parse<XQueryModuleImport>(
                        """
                        import module "http://example.com/test" at "/files/test.xq";
                        ()
                        """
                    )[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/files/test.xq"))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.13) Namespace Declaration")
    internal inner class NamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        internal inner class NamespaceDecl {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val decl = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                val prefix = decl.namespacePrefix?.element?.parent!!
                assertThat(XQueryFindUsagesProvider.getType(prefix), `is`("namespace"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val decl = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.14) Default Namespace Declaration")
    internal inner class DefaultNamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("default element/type namespace declaration")
            fun element() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/1999/xhtml"))
                assertThat(decl.namespaceUri?.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri?.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(decl.accepts(XPathNamespaceType.DefaultElementOrType), `is`(true))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("default function namespace declaration")
            fun function() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(decl.namespaceUri?.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri?.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(decl.accepts(XPathNamespaceType.DefaultElementOrType), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(true))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(true))
                assertThat(decl.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace '';")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`(""))
                assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                assertThat(decl.accepts(XPathNamespaceType.DefaultElementOrType), `is`(true))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.XQuery), `is`(false))
            }

            @Test
            @DisplayName("missing namespace")
            fun missingNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace;")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri, `is`(nullValue()))

                assertThat(decl.accepts(XPathNamespaceType.DefaultElementOrType), `is`(true))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionDecl), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.DefaultFunctionRef), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.None), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Prefixed), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.Undefined), `is`(false))
                assertThat(decl.accepts(XPathNamespaceType.XQuery), `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.15) Annotations")
    internal inner class Annotations {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare function %test f() {};")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("annotation"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("name only")
            fun nameOnly() {
                val annotation = parse<XQueryAnnotation>("declare function % private f() {};")[0] as XdmAnnotation
                assertThat(op_qname_presentation(annotation.name!!), `is`("private"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("%private"))
            }

            @Test
            @DisplayName("missing name")
            fun missingName() {
                val annotation = parse<XQueryAnnotation>("declare function % () {};")[0] as XdmAnnotation
                assertThat(annotation.name, `is`(nullValue()))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(0))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`(nullValue()))
            }

            @Test
            @DisplayName("values")
            fun values() {
                val annotation = parse<XQueryAnnotation>("declare function % test ( 1 , 2.3 , 4e3 , 'lorem ipsum' ) f() {};")[0] as XdmAnnotation
                assertThat(op_qname_presentation(annotation.name!!), `is`("test"))

                val values = annotation.values.toList()
                assertThat(values.size, `is`(4))
                assertThat((values[0] as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat((values[1] as XsDecimalValue).data, `is`(BigDecimal.valueOf(2.3)))
                assertThat((values[2] as XsDoubleValue).data, `is`(4e3))
                assertThat((values[3] as XsStringValue).data, `is`("lorem ipsum"))

                val presentation = annotation as ItemPresentation
                assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                assertThat(presentation.locationString, `is`(nullValue()))
                assertThat(presentation.presentableText, `is`("%test(1, 2.3, 4e3, 'lorem ipsum')"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.16) Variable Declaration")
    internal inner class VariableDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
        internal inner class VarDecl {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val decl = parse<XPathVariableDeclaration>("declare variable \$x := \$y;")[0]
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = decl.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val decl = parse<XPathVariableDeclaration>("declare variable \$a:x := \$a:y;")[0]
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = decl.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$a:x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val decl = parse<XPathVariableDeclaration>(
                    "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;"
                )[0]
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = decl.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$Q{http://www.example.com}x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val decl = parse<XPathVariableDeclaration>("declare variable \$ := \$y;")[0]
                assertThat(decl.variableName, `is`(nullValue()))
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid VarName")
            fun invalidVarName() {
                val decl = parse<XPathVariableDeclaration>("declare variable \$: := \$y;")[0]
                assertThat(decl.variableName, `is`(nullValue()))
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("with type")
            fun withType() {
                val decl = parse<XPathVariableDeclaration>("declare variable \$a:x  as  node ( (::) )? := \$a:y;")[0]
                assertThat((decl as XPathVariableType).variableType?.typeName, `is`("node()?"))

                val qname = decl.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$a:x as node()?"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.18) Function Declaration")
    internal inner class FunctionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("empty ParamList")
            fun emptyParamList() {
                val decl = parse<XPathFunctionDeclaration>("declare function fn:true() external;")[0]
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))

                val qname = decl.functionName!!
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("fn:true()"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XPathFunctionDeclaration>("declare function test(\$one, \$two) external;")[0]
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("test(\$one, \$two)"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("non-empty ParamList with types")
            fun nonEmptyParamListWithTypes() {
                val decl = parse<XPathFunctionDeclaration>("declare function test(\$one  as  array ( * ), \$two  as  node((::))) external;")[0]
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("test(\$one as array(*), \$two as node())"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("return type")
            fun returnType() {
                val decl = parse<XPathFunctionDeclaration>("declare function fn:true()  as  xs:boolean  external;")[0]
                assertThat(decl.returnType?.typeName, `is`("xs:boolean"))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))

                val qname = decl.functionName!!
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("fn:true() as xs:boolean"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val decl = parse<XPathFunctionDeclaration>("declare function :true() external;")[0]
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(0))

                val presentation = (decl as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare function test() {};
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunctionDecl))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        internal inner class Param {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableBinding
                assertThat((expr as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableBinding
                assertThat((expr as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$a:x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathParam>("function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableBinding
                assertThat((expr as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$Q{http://www.example.com}x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
                assertThat((expr as XPathVariableType).variableType?.typeName, `is`(nullValue()))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("with type")
            fun withType() {
                val expr = parse<XPathParam>("function ( \$x  as  element() ) {}")[0] as XPathVariableBinding
                assertThat((expr as XPathVariableType).variableType?.typeName, `is`("element()"))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$x as element()"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathEQName>("function (\$test) {}")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("parameter"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.19) Option Declaration")
    internal inner class OptionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (37) OptionDecl")
        internal inner class OptionDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare option test \"lorem ipsum\";")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))
                assertThat(XQueryFindUsagesProvider.getType(qname.element!!), `is`("option"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }
}

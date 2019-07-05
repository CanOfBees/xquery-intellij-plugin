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
package uk.co.reecedunn.intellij.plugin.xquery.tests.model

import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XQuery 3.1 - Static Context")
private class XQueryStaticContextTest : ParserTestCase() {
    override fun registerModules(manager: MockModuleManager) {
        manager.addModule(ResourceVirtualFile(XQueryStaticContextTest::class.java.classLoader, "tests/module-xquery"))
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Statically known namespaces")
    internal inner class StaticallyKnownNamespaces {
        private fun namespace(namespaces: List<XPathNamespaceDeclaration>, prefix: String): String {
            return namespaces.asIterable().first { ns -> ns.namespacePrefix!!.data == prefix }.namespaceUri!!.data
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("module declaration")
            fun testStaticallyKnownNamespaces_ModuleDecl() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "module namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "module namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_SchemaImport_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import schema namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_SchemaImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>(
                    "import schema namespace a='http://www.example.com'; a:test();"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import schema namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_SchemaImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import schema namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_ModuleImport_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import module namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_ModuleImport_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>(
                    "import module namespace a='http://www.example.com'; a:test();"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "import module namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_ModuleImport_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("import module namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        internal inner class NamespaceDecl {
            @Test
            @DisplayName("referenced from Prolog via FunctionDecl")
            fun testStaticallyKnownNamespaces_NamespaceDecl_Prolog() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "declare namespace a='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("referenced from MainModule via QueryBody")
            fun testStaticallyKnownNamespaces_NamespaceDecl_MainModule() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("declare namespace a='http://www.example.com'; a:test();")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("a"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespacePrefix() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>(
                    "declare namespace ='http://www.example.com'; declare function a:test() {};"
                )[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun testStaticallyKnownNamespaces_NamespaceDecl_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XQueryFunctionDecl>("declare namespace a=; declare function a:test() {};")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("namespace prefix atribute")
            fun testStaticallyKnownNamespaces_DirAttribute_Xmlns() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(9))

                assertThat(namespaces[0].namespacePrefix!!.data, `is`("b"))
                assertThat(namespaces[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("namespace prefix, missing namespace uri")
            fun testStaticallyKnownNamespaces_DirAttribute_Xmlns_NoNamespaceUri() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a xmlns:b=>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("non-namespace prefix atribute")
            fun testStaticallyKnownNamespaces_DirAttribute() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("<a b='http://www.example.com'>{b:test()}</a>")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()
                assertThat(namespaces.size, `is`(8))

                // predefined XQuery namespaces:
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }

        @Nested
        @DisplayName("predefined namespaces")
        internal inner class PredefinedNamespaces {
            private fun namespace(namespaces: List<XPathNamespaceDeclaration>, prefix: String): String {
                return namespaces.asIterable().first { ns -> ns.namespacePrefix!!.data == prefix }.namespaceUri!!.data
            }

            @Test
            @DisplayName("XQuery 1.0")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_XQuery10() {
                settings.implementationVersion = "w3c/1ed"
                settings.XQueryVersion = "1.0"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()

                assertThat(namespaces.size, `is`(8))
                assertThat(namespace(namespaces, "array"), `is`("http://www.w3.org/2005/xpath-functions/array"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "map"), `is`("http://www.w3.org/2005/xpath-functions/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }

            @Test
            @DisplayName("MarkLogic")
            fun testStaticallyKnownNamespaces_PredefinedNamespaces_MarkLogic() {
                settings.implementationVersion = "marklogic/v6"
                settings.XQueryVersion = "1.0-ml"

                val element = parse<XPathFunctionCall>("fn:true()")[0]
                val namespaces = element.staticallyKnownNamespaces().toList()

                assertThat(namespaces.size, `is`(29)) // Includes built-in namespaces for all MarkLogic versions.
                assertThat(namespace(namespaces, "cts"), `is`("http://marklogic.com/cts"))
                assertThat(namespace(namespaces, "dav"), `is`("DAV:"))
                assertThat(namespace(namespaces, "dbg"), `is`("http://marklogic.com/xdmp/dbg"))
                assertThat(namespace(namespaces, "dir"), `is`("http://marklogic.com/xdmp/directory"))
                assertThat(namespace(namespaces, "err"), `is`("http://www.w3.org/2005/xqt-error"))
                assertThat(namespace(namespaces, "error"), `is`("http://marklogic.com/xdmp/error"))
                assertThat(namespace(namespaces, "fn"), `is`("http://www.w3.org/2005/xpath-functions"))
                assertThat(namespace(namespaces, "geo"), `is`("http://marklogic.com/geospatial"))
                assertThat(namespace(namespaces, "json"), `is`("http://marklogic.com/xdmp/json"))
                assertThat(namespace(namespaces, "local"), `is`("http://www.w3.org/2005/xquery-local-functions"))
                assertThat(namespace(namespaces, "lock"), `is`("http://marklogic.com/xdmp/lock"))
                assertThat(namespace(namespaces, "map"), `is`("http://marklogic.com/xdmp/map"))
                assertThat(namespace(namespaces, "math"), `is`("http://marklogic.com/xdmp/math"))
                assertThat(namespace(namespaces, "prof"), `is`("http://marklogic.com/xdmp/profile"))
                assertThat(namespace(namespaces, "prop"), `is`("http://marklogic.com/xdmp/property"))
                assertThat(namespace(namespaces, "rdf"), `is`("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
                assertThat(namespace(namespaces, "sc"), `is`("http://marklogic.com/xdmp/schema-components"))
                assertThat(namespace(namespaces, "sec"), `is`("http://marklogic.com/security"))
                assertThat(namespace(namespaces, "sem"), `is`("http://marklogic.com/xdmp/semantics"))
                assertThat(namespace(namespaces, "spell"), `is`("http://marklogic.com/xdmp/spell"))
                assertThat(namespace(namespaces, "sql"), `is`("http://marklogic.com/xdmp/sql"))
                assertThat(namespace(namespaces, "tde"), `is`("http://marklogic.com/xdmp/tde"))
                assertThat(namespace(namespaces, "temporal"), `is`("http://marklogic.com/xdmp/temporal"))
                assertThat(namespace(namespaces, "xdmp"), `is`("http://marklogic.com/xdmp"))
                assertThat(namespace(namespaces, "xml"), `is`("http://www.w3.org/XML/1998/namespace"))
                assertThat(namespace(namespaces, "xqe"), `is`("http://marklogic.com/xqe"))
                assertThat(namespace(namespaces, "xqterr"), `is`("http://www.w3.org/2005/xqt-error"))
                assertThat(namespace(namespaces, "xs"), `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(namespace(namespaces, "xsi"), `is`("http://www.w3.org/2001/XMLSchema-instance"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default element/type namespace")
    internal inner class DefaultElementTypeNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>(
                    "import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/1999/xhtml"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()"
                )[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.example.com"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultElementOrTypeNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`(""))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Default function namespace")
    internal inner class DefaultFunctionNamespace {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val ctx = parse<XQueryMainModule>("<br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryMainModule>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (6) Prolog")
        internal inner class Prolog {
            @Test
            @DisplayName("no default namespace declarations")
            fun noNamespaceDeclarations() {
                val ctx = parse<XQueryProlog>("declare function local:test() {}; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("default")
            fun default() {
                val ctx = parse<XQueryMainModule>(
                    "import schema default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default; missing namespace")
            fun defaultMissingNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("import schema default element namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("element")
            fun element() {
                val ctx = parse<XQueryMainModule>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml'; <br/>"
                )[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("element; missing namespace")
            fun elementMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("element; empty namespace")
            fun elementEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default element namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function")
            fun function() {
                val ctx = parse<XQueryMainModule>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math'; pi()"
                )[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions/math"))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function; missing namespace")
            fun functionMissingNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("function; empty namespace")
            fun functionEmptyNamespace() {
                val ctx = parse<XQueryMainModule>("declare default function namespace ''; <br/>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(2))

                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`(""))

                // predefined static context
                assertThat(element[1].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[1].namespacePrefix, `is`(nullValue()))
                assertThat(element[1].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("prefixed namespace declaration")
            fun prefixed() {
                val ctx = parse<XPathFunctionCall>("<a xmlns:b='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default namespace declaration")
            fun default() {
                val ctx = parse<XPathFunctionCall>("<a xmlns='http://www.example.com'>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }

            @Test
            @DisplayName("default namespace declaration; empty namespace")
            fun defaultEmptyNamespace() {
                val ctx = parse<XPathFunctionCall>("<a xmlns=''>{test()}</a>")[0]

                val element = ctx.defaultFunctionNamespace().toList()
                assertThat(element.size, `is`(1))

                // predefined static context
                assertThat(element[0].namespaceType, `is`(XPathNamespaceType.DefaultFunction))
                assertThat(element[0].namespacePrefix, `is`(nullValue()))
                assertThat(element[0].namespaceUri!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) In-scope variables")
    internal inner class InScopeVariables {
        @Nested
        @DisplayName("XQuery 3.1 (3.12.2) For Clause")
        internal inner class ForClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (45) ForBinding ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class ForBinding_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    val element = parse<XPathFunctionCall>("for \$x in 2, \$y in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in for \$y in test() return 1 return 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in for \$y in 2 return test() return 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in for \$y in 2 return 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    val element = parse<XPathFunctionCall>("for \$x in 1, \$y in 2 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (45) ForBinding ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class ForBinding_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for \$y in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    val element = parse<XPathFunctionCall>("for \$x in 2 for \$y in 3, \$z in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x in 1 for \$y in for \$z in test() return 1 return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for \$y in 2 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x in 1 for \$y in for \$z in 2 return test() return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x in 1 for \$y in for \$z in 2 return 1 return test()"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for \$y in 2, \$z in 3 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(3))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
            internal inner class PositionalVar {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for \$x at \$a in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    val element = parse<XPathFunctionCall>("for \$x at \$a in 2, \$y at \$b in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("a"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x at \$a in for \$y at \$b in test() return 1 return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x at \$a in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("a"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x at \$a in for \$y at \$b in 2 return test() return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("b"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "for \$x at \$a in for \$y at \$b in 2 return 1 return test()"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("a"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    val element = parse<XPathFunctionCall>("for \$x at \$a in 1, \$y at \$b in 2 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(4))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("a"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[2].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[3].variableName?.localName?.data, `is`("b"))
                    assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.3) Let Clause")
        internal inner class LetClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (49) LetBinding ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class LetBinding_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    val element = parse<XPathFunctionCall>("let \$x := 2, \$y := test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := let \$y := test() return 1 return 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := let \$y := 2 return test() return 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := let \$y := 2 return 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    val element = parse<XPathFunctionCall>("let \$x := 1, \$y := 2 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (49) LetBinding ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class LetBinding_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := 1 let \$y := test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; previous binding in scope")
                fun inExpr_PreviousBindingInScope() {
                    val element = parse<XPathFunctionCall>("let \$x := 1 let \$y := 2, \$z := test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from VarName expression; inner VarName expression of nested FLWOR")
                fun nestedFLWORExpr() {
                    val element = parse<XPathFunctionCall>(
                        "let \$x := 1 let \$y := let \$z := test() return 1 return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("let \$x := 1 let \$y := 2 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of inner nested FLWOR")
                fun innerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "let \$x := 1 let \$y := let \$z := 2 return test() return 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; return expression of outer nested FLWOR")
                fun outerReturnExpr() {
                    val element = parse<XPathFunctionCall>(
                        "let \$x := 1 let \$y := let \$z := 2 return 1 return test()"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression; multiple bindings")
                fun returnExpr_multipleBindings() {
                    val element = parse<XPathFunctionCall>("let \$x := 1 let \$y := 2, \$z := 3 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(3))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4) Window Clause")
        internal inner class WindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (53) WindowStartCondition ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class WindowStartCondition_InitialClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() start \$y at \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y at \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y at \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() start \$y when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() start \$y previous \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y previous \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y previous \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() start \$y next \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y next \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 start \$y next \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (53) WindowStartCondition ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class WindowStartCondition_IntermediateClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() start \$z at \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z at \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() start \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() start \$z previous \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z previous \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() start \$z next \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 start \$z next \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (54) WindowEndCondition ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class WindowEndCondition_InitialClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() end \$y at \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y at \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y at \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() end \$y when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() end \$y previous \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y previous \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y previous \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in test() end \$y next \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(0))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y next \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for sliding window \$x in 1 end \$y next \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (54) WindowEndCondition ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class WindowEndCondition_IntermediateClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
                internal inner class PositionalVar {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() end \$z at \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z at \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() end \$z when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(2))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() end \$z previous \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z previous \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("from VarName expression")
                    fun inExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in test() end \$z next \$w when 1 return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(1))

                        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from when expression")
                    fun whenExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when test() return 2"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(3))

                        assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("from return expression")
                    fun returnExpr() {
                        val element = parse<XPathFunctionCall>(
                            "for \$x in 1 for sliding window \$y in 1 end \$z next \$w when 2 return test()"
                        )[0]
                        val variables = element.inScopeVariables().toList()
                        assertThat(variables.size, `is`(4))

                        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[2].variableName?.localName?.data, `is`("w"))
                        assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[2].variableName?.namespace, `is`(nullValue()))

                        assertThat(variables[3].variableName?.localName?.data, `is`("x"))
                        assertThat(variables[3].variableName?.prefix, `is`(nullValue()))
                        assertThat(variables[3].variableName?.namespace, `is`(nullValue()))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4.1) Tumbling Window Clause")
        internal inner class TumblingWindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class TumblingWindowClause_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for tumbling window \$x in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for tumbling window \$x in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause ; XQuery 3.1 EBNF (43) IntermeiateClause")
            internal inner class TumblingWindowClause_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for tumbling window \$y in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for tumbling window \$y in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.4.2) Sliding Window Clause")
        internal inner class SlidingWindowClause {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause ; XQuery 3.1 EBNF (42) InitialClause")
            internal inner class SlidingWindowClause_InitialClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for sliding window \$x in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for sliding window \$x in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause ; XQuery 3.1 EBNF (43) IntermediateClause")
            internal inner class SlidingWindowClause_IntermediateClause {
                @Test
                @DisplayName("from VarName expression")
                fun inExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for sliding window \$y in test() return 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("from return expression")
                fun returnExpr() {
                    val element = parse<XPathFunctionCall>("for \$x in 1 for sliding window \$y in 1 return test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.6) Count Clause")
        internal inner class CountClause {
            @Test
            @DisplayName("XQuery 3.1 EBNF (59) CountClause")
            fun countClause() {
                val element = parse<XPathFunctionCall>("for \$x in 1 count \$y return test()")[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(2))

                assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.12.7) Group By Clause")
        internal inner class GroupByClause {
            @Test
            @DisplayName("from return expression")
            fun returnExpr() {
                val element = parse<XPathFunctionCall>("for \$x in 1 group by \$y return test()")[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(2))

                assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
            }

            @Test
            @DisplayName("from return expression; multiple group by variables")
            fun multiple() {
                val element = parse<XPathFunctionCall>("for \$x in 1 group by \$y, \$z return test()")[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(3))

                assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                assertThat(variables[1].variableName?.localName?.data, `is`("z"))
                assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[1].variableName?.namespace, `is`(nullValue()))

                assertThat(variables[2].variableName?.localName?.data, `is`("x"))
                assertThat(variables[2].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[2].variableName?.namespace, `is`(nullValue()))
            }

            @Test
            @DisplayName("from group specification expression")
            fun valueExpr() {
                val element = parse<XPathFunctionCall>("for \$x in 1 group by \$y := test() return 1")[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(1))

                assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
            }

            @Test
            @DisplayName("from group by expression; previous group specification in scope")
            fun valueExpr_PreviousSpecInScope() {
                val element = parse<XPathFunctionCall>("for \$x in 1 group by \$y := 2, \$z := test() return 1")[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(2))

                assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.16) Quantified Expressions")
        internal inner class QuantifiedExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr")
            internal inner class QuantifiedExpr {
                @Test
                @DisplayName("single binding; 'in' Expr")
                fun testQuantifiedExpr_SingleBinding_InExpr() {
                    val element = parse<XPathFunctionCall>("some \$x in test() satisfies 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("single binding; 'satisfies' Expr")
                fun testQuantifiedExpr_SingleBinding_SatisfiesExpr() {
                    val element = parse<XPathFunctionCall>("some \$x in 1 satisfies test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple bindings; first 'in' Expr")
                fun testQuantifiedExpr_MultipleBindings_FirstInExpr() {
                    val element = parse<XPathFunctionCall>("some \$x in test(), \$y in 1 satisfies 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("multiple bindings; last 'in' Expr")
                fun testQuantifiedExpr_MultipleBindings_LastInExpr() {
                    val element = parse<XPathFunctionCall>("some \$x in 1, \$y in test() satisfies 2")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple bindings; 'satisfies' Expr")
                fun testQuantifiedExpr_MultipleBindings_SatisfiesExpr() {
                    val element = parse<XPathFunctionCall>("some \$x in 1, \$y in 2 satisfies test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
        internal inner class Typeswitch {
            @Test
            @DisplayName("case clause; first clause")
            fun caseClause() {
                val element = parse<XPathFunctionCall>(
                    "typeswitch (2) case \$x as xs:string return test() case \$y as xs:int return 2 default \$z return 3"
                )[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(1))

                assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
            }

            @Test
            @DisplayName("case clause; non-first clause")
            fun caseClause_NotFirst() {
                val element = parse<XPathFunctionCall>(
                    "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return test() default \$z return 3"
                )[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(1))

                // Only variable `y` is in scope.
                assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
            }

            @Test
            @DisplayName("default case clause")
            fun defaultCaseClause() {
                val element = parse<XPathFunctionCall>(
                    "typeswitch (2) case \$x as xs:string return 1 case \$y as xs:int return 2 default \$z return test()"
                )[0]
                val variables = element.inScopeVariables().toList()
                assertThat(variables.size, `is`(1))

                assertThat(variables[0].variableName?.localName?.data, `is`("z"))
                assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (4.16) Variable Declaration")
        internal inner class VariableDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
            internal inner class VarDecl {
                @Test
                @DisplayName("no variable declarations; in function body (prolog)")
                fun noVarDecls_InProlog() {
                    val element = parse<XPathFunctionCall>("declare function f() { test() }; 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("no variable declarations; in query body")
                fun noVarDecls_QueryBodyExpr() {
                    val element = parse<XPathFunctionCall>("declare function f() {}; test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("single variable declaration; in function body (prolog)")
                fun singleVarDecl_InProlog() {
                    val element = parse<XPathFunctionCall>(
                        "declare function f() { test() }; declare variable \$x := 1; 2"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("single variable declaration; in query body")
                fun singleVarDecl_QueryBodyExpr() {
                    val element = parse<XPathFunctionCall>(
                        "declare function f() {}; declare variable \$x := 1; test()"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple variable declarations; in function body (prolog)")
                fun multipleVarDecls_InProlog() {
                    val element = parse<XPathFunctionCall>(
                        "declare function f() { test() }; declare variable \$x := 1; declare variable \$y := 2; 3"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple variable declarations; in query body")
                fun multipleVarDecls_QueryBodyExpr() {
                    val element = parse<XPathFunctionCall>(
                        "declare function f() {}; declare variable \$x := 1; declare variable \$y := 2; test()"
                    )[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 (4.18) Function Declaration")
        internal inner class FunctionDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (33) ParamList")
            internal inner class ParamList {
                @Test
                @DisplayName("no parameters")
                fun noParameters() {
                    val element = parse<XPathFunctionCall>("declare function f() { test() }; 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("single parameter")
                fun singleParameter() {
                    val element = parse<XPathFunctionCall>("declare function f(\$x) { test() }; 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple parameters")
                fun multipleParameters() {
                    val element = parse<XPathFunctionCall>("declare function f(\$x, \$y) { test() }; 1")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("outside function body")
                fun outsideFunctionBody() {
                    val element = parse<XPathFunctionCall>("declare function f(\$x) {}; test()")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (34) Param")
            internal inner class Param {
                @Test
                @DisplayName("in FunctionBody; no parameters")
                fun testInlineFunctionExpr_FunctionBody_NoParameters() {
                    val element = parse<XPathFunctionCall>("function () { test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("in FunctionBody; single parameter")
                fun testInlineFunctionExpr_FunctionBody_SingleParameter() {
                    val element = parse<XPathFunctionCall>("function (\$x) { test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("in FunctionBody; multiple parameters")
                fun testInlineFunctionExpr_FunctionBody_MultipleParameters() {
                    val element = parse<XPathFunctionCall>("function (\$x, \$y) { test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("outside FunctionBody")
                fun testInlineFunctionExpr_OutsideFunctionBody() {
                    val element = parse<XPathFunctionCall>("function (\$x) {}(test())")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }
            }
        }

        @Nested
        @DisplayName("XQuery Scripting Extension 1.0 (5.2) Block Expressions")
        internal inner class BlockExpressions {
            @Nested
            @DisplayName("XQuery Scripting Extension 1.0 EBNF (156) BlockVarDecl")
            internal inner class BlockVarDecl {
                @Test
                @DisplayName("no declarations")
                fun noDeclarations() {
                    val element = parse<XPathFunctionCall>("block { test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("single declaration; from value expression")
                fun singleVarDecl_ValueExpr() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := test(); 1 }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("single declaration; from block expression")
                fun singleVarDecl_BlockExpr() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := 1; test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple entries in a declaration; from value expression of first variable")
                fun multipleVarDeclEntries_ValueExpr_FirstDecl() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := test(), \$y := 1; 2 }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("multiple entries in a declaration; from value expression of last variable")
                fun multipleVarDeclEntries_ValueExpr_LastDecl() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := 1, \$y := test(); 2 }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple entries in a declaration; from block expression")
                fun multipleVarDeclEntries_BlockExpr() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := 1, \$y := 2; test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple declarations; from value expression of first variable")
                fun multipleVarDecl_ValueExpr_FirstDecl() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := test(); declare \$y := 1; 2 }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(0))
                }

                @Test
                @DisplayName("multiple declarations; from value expression of last variable")
                fun multipleVarDecl_ValueExpr_LastDecl() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := 1; declare \$y := test(); 2 }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(1))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
                }

                @Test
                @DisplayName("multiple declarations; from block expression")
                fun multipleVarDecl_BlockExpr() {
                    val element = parse<XPathFunctionCall>("block { declare \$x := 1; declare \$y := 2; test() }")[0]
                    val variables = element.inScopeVariables().toList()
                    assertThat(variables.size, `is`(2))

                    assertThat(variables[0].variableName?.localName?.data, `is`("x"))
                    assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

                    assertThat(variables[1].variableName?.localName?.data, `is`("y"))
                    assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
                    assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.1.1) Statically known function signatures")
    internal inner class StaticallyKnownFunctionSignatures {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        string()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        transform-to-json(), (: imported function :)
                        array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        test()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { test:g() };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        json:transform-to-json(), (: imported function :)
                        json:array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        e:test()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("fn:concat()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].arity, `is`(Range(2, Int.MAX_VALUE)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].arity, `is`(Range(1, 1)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].arity, `is`(Range(0, 0)))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        string#0
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        func#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        transform-to-json#0, (: imported function :)
                        array#0 (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        test#0
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { test:g#0 };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        a:sessions#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        json:transform-to-json#0, (: imported function :)
                        json:array#0 (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        a:sessions#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        e:test#0
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("fn:concat#3")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].arity, `is`(Range(2, Int.MAX_VALUE)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].arity, `is`(Range(1, 1)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].arity, `is`(Range(0, 0)))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("default function namespace")
                fun defaultFunctionNamespace() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://www.w3.org/2001/XMLSchema";
                        () => string()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("xs:string"))
                }

                @Test
                @DisplayName("default namespace to imported module")
                fun defaultNamespaceToImportedModule() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.com/test";
                        import module namespace t = "http://example.com/test" at "/namespaces/ModuleDecl.xq";
                        () => func()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:func"))
                }

                @Test
                @DisplayName("default namespace to imported module with built-in functions")
                fun defaultNamespaceToImportedModuleWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://marklogic.com/xdmp/json";
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        () => transform-to-json(), (: imported function :)
                        () => array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    val qname = parse<XPathEQName>(
                        """
                        declare default function namespace "http://example.co.uk/prolog";
                        declare function test() { () };
                        () => test()
                        """
                    )[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("() => fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }

                @Test
                @DisplayName("module declaration")
                fun moduleDeclaration() {
                    val qname = parse<XPathEQName>(
                        """
                        module namespace test = "http://www.example.com/test";
                        declare function test:f() { () => test:g() };
                        declare function test:g() { 2 };
                        """
                    )
                    assertThat(qname.size, `is`(4))

                    val decls = qname[2].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("test:g"))
                    assertThat(decls[0].functionName!!.element, sameInstance(qname[3]))
                }

                @Test
                @DisplayName("module import")
                fun moduleImport() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace a = "http://basex.org/modules/admin";
                        () => a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("module import with built-in functions")
                fun moduleImportWithBuiltinFunctions() {
                    settings.implementationVersion = "marklogic/v8.0"
                    settings.XQueryVersion = "1.0-ml"

                    val qname = parse<XPathEQName>(
                        """
                        import module namespace json = "http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                        () => json:transform-to-json(), (: imported function :)
                        () => json:array() (: built-in function :)
                        """
                    ).drop(1)

                    var decls = qname[0].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    assertThat(decls[1].arity, `is`(Range(2, 2)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:transform-to-json"))

                    decls = qname[1].staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("json:array"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("json:array"))
                }

                @Test
                @DisplayName("namespace")
                fun namespace() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace a = "http://basex.org/modules/admin";
                        () => a:sessions()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("admin:sessions"))
                }

                @Test
                @DisplayName("main module prolog")
                fun mainModuleProlog() {
                    settings.implementationVersion = "w3c/1ed"
                    settings.XQueryVersion = "1.0"

                    val qname = parse<XPathEQName>(
                        """
                        declare namespace e = "http://example.co.uk/prolog";
                        declare function e:test() { () };
                        () => e:test()
                        """
                    )[1]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("e:test"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("() => fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(Range(0, 0)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("() => fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(Range(1, 1)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(Range(0, 0)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }

                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val qname = parse<XPathEQName>("() => fn:concat()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(3))

                    assertThat(decls[0].arity, `is`(Range(2, Int.MAX_VALUE)))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[1].arity, `is`(Range(1, 1)))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:concat"))

                    assertThat(decls[2].arity, `is`(Range(0, 0)))
                    assertThat(decls[2].functionName!!.element!!.text, `is`("fn:concat"))
                }
            }
        }
    }
}

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
package uk.co.reecedunn.intellij.plugin.xslt.tests.psi

import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xslt.ast.marklogic.MarkLogicImportModule
import uk.co.reecedunn.intellij.plugin.xslt.ast.marklogic.MarkLogicTry
import uk.co.reecedunn.intellij.plugin.xslt.ast.saxon.*
import uk.co.reecedunn.intellij.plugin.xslt.ast.xml.XsltDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.*
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XSLT")
private class PluginPsiTest : ParserTestCase() {
    companion object {
        private const val SAXON_NAMESPACE = "http://saxon.sf.net/"
        private const val SAXON6_NAMESPACE = "http://icl.com/saxon"
        private const val XDMP_NAMESPACE = "http://marklogic.com/xdmp"
    }

    @Nested
    @DisplayName("MarkLogic")
    internal inner class MarkLogic {
        @Nested
        @DisplayName("xdmp:import-module")
        internal inner class ImportModule {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:xdmp="http://marklogic.com/xdmp" version="2.0" extension-element-prefixes="xdmp">
                        <xdmp:import-module namespace="urn:lorem:ipsum" href="lorem-ipsum.xqy"/>
                    </xsl:stylesheet>
                """
                val psi = parse<MarkLogicImportModule>(xml, XDMP_NAMESPACE, "import-module")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("xdmp:try")
        internal inner class Try {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:xdmp="http://marklogic.com/xdmp" version="2.0" extension-element-prefixes="xdmp">
                        <xsl:template match="lorem">
                            <xdmp:try/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<MarkLogicTry>(xml, XDMP_NAMESPACE, "try")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }
    }

    @Nested
    @DisplayName("Saxon")
    internal inner class Saxon {
        @Nested
        @DisplayName("saxon:array")
        internal inner class Array {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:array/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonArray>(xml, SAXON_NAMESPACE, "array")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:array-member")
        internal inner class ArrayMember {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:array-member select="(1, 2, 3)"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonArrayMember>(xml, SAXON_NAMESPACE, "array-member")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:assign")
        internal inner class Assign {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:assign name="lorem-ipsum">Ipsum</saxon:assign>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonAssign>(xml, SAXON_NAMESPACE, "assign")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:change")
        internal inner class Change {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:change select="ipsum" to="dolor"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonChange>(xml, SAXON_NAMESPACE, "change")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:deep-update")
        internal inner class DeepUpdate {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:deep-update root="${'$'}m" select="?*[?lorem = 'ipsum']" action="()"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDeepUpdate>(xml, SAXON_NAMESPACE, "deep-update")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:delete")
        internal inner class Delete {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:delete select="ipsum"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDelete>(xml, SAXON_NAMESPACE, "delete")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:do")
        internal inner class Do {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:do action="${'$'}file?close()"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDo>(xml, SAXON_NAMESPACE, "do")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:doctype")
        internal inner class Doctype {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:doctype/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDoctype>(xml, SAXON_NAMESPACE, "doctype")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:entity-ref")
        internal inner class EntityRef {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:entity-ref name="nbsp"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonEntityRef>(xml, SAXON_NAMESPACE, "entity-ref")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:for-each-member")
        internal inner class ForEachMember {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:for-each-member select="array { 1, 2, 3 }" bind-to="x"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonForEachMember>(xml, SAXON_NAMESPACE, "for-each-member")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:import-query")
        internal inner class ImportQuery {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:import-query namespace="urn:lorem:ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonImportQuery>(xml, SAXON_NAMESPACE, "import-query")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:insert")
        internal inner class Insert {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:insert select="ipsum" position="after"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonInsert>(xml, SAXON_NAMESPACE, "insert")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:item-type")
        internal inner class ItemType {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:item-type name="lorem-ipsum" as="xs:string"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonItemType>(xml, SAXON_NAMESPACE, "item-type")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon6:output")
        internal inner class Output {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://icl.com/saxon" version="3.0" extension-element-prefixes="saxon">
                        <saxon:output/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltResultDocument>(xml, SAXON6_NAMESPACE, "output")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:rename")
        internal inner class Rename {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:rename select="ipsum" to="'dolor'"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonRename>(xml, SAXON_NAMESPACE, "rename")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:replace")
        internal inner class Replace {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:replace select="ipsum"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonReplace>(xml, SAXON_NAMESPACE, "replace")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:tabulate-maps")
        internal inner class TabulateMaps {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:tabulate-maps root="${'$'}lorem" select="?ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonTabulateMaps>(xml, SAXON_NAMESPACE, "tabulate-maps")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:type-alias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:type-alias name="lorem-ipsum" as="xs:string"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonItemType>(xml, SAXON_NAMESPACE, "type-alias")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:update")
        internal inner class Update {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonUpdate>(xml, SAXON_NAMESPACE, "update")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:while")
        internal inner class While {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:while test="${'$'}x &lt; 10"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonWhile>(xml, SAXON_NAMESPACE, "while")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }
    }
}

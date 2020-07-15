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
import uk.co.reecedunn.intellij.plugin.xslt.ast.xml.XsltDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.*
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XSLT 3.0 - IntelliJ Program Structure Interface (PSI)")
private class XsltPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XSLT 3.0 Direct XML Elements")
    internal inner class DirectXmlElements {
        @Test
        @DisplayName("hierarchy")
        fun hierarchy() {
            @Language("XML") val xml = """
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                    <xsl:template match="lorem">
                        <lorem-ipsum/>
                    </xsl:template>
                </xsl:stylesheet>
            """
            val psi = parse<XsltDirElemConstructor>(xml, "", "lorem-ipsum")[0]

            assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
            assertThat(psi.children.size, `is`(0))
            assertThat(psi.prevSibling, `is`(nullValue()))
            assertThat(psi.nextSibling, `is`(nullValue()))

            val parent = psi.parent!!
            assertThat(parent.children.size, `is`(1))
            assertThat(parent.children[0], `is`(sameInstance(psi)))
        }
    }

    @Nested
    @DisplayName("XSLT 3.0 (3) Stylesheet Structure")
    internal inner class StylesheetStructure {
        @Nested
        @DisplayName("XSLT 3.0 (3.7) xsl:stylesheet")
        internal inner class Stylesheet {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                    </xsl:stylesheet>
                """
                val psi = parse<XsltStylesheet>(xml, XSLT.NAMESPACE, "stylesheet")[0]

                assertThat(psi.parent, `is`(nullValue()))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (3.7) xsl:transform")
        internal inner class Transform {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                    </xsl:transform>
                """
                val psi = parse<XsltStylesheet>(xml, XSLT.NAMESPACE, "transform")[0]

                assertThat(psi.parent, `is`(nullValue()))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (3.11.2) xsl:include")
        internal inner class Include {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:include href="test.xsl"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltInclude>(xml, XSLT.NAMESPACE, "include")[0]

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
        @DisplayName("XSLT 3.0 (3.11.3) xsl:import")
        internal inner class Import {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:import href="test.xsl"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltImport>(xml, XSLT.NAMESPACE, "import")[0]

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
        @DisplayName("XSLT 3.0 (3.15) xsl:import-schema")
        internal inner class ImportSchema {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:import-schema schema-location="test.xsd"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltImportSchema>(xml, XSLT.NAMESPACE, "import-schema")[0]

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

    @Nested
    @DisplayName("XSLT 3.0 (4) Data Model")
    internal inner class DataModel {
        @Nested
        @DisplayName("XSLT 3.0 (4.4.2) xsl:strip-space")
        internal inner class StripSpace {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:strip-space/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltStripSpace>(xml, XSLT.NAMESPACE, "strip-space")[0]

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
        @DisplayName("XSLT 3.0 (4.4.2) xsl:preserve-space")
        internal inner class PreserveSpace {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:preserve-space/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltPreserveSpace>(xml, XSLT.NAMESPACE, "preserve-space")[0]

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

    @Nested
    @DisplayName("XSLT 3.0 (5) Features of the XSLT Language")
    internal inner class FeaturesOfTheXsltLanguage {
        @Nested
        @DisplayName("XSLT 3.0 (5.4) xsl:decimal-format")
        internal inner class DecimalFormat {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:decimal-format name="lorem-ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltDecimalFormat>(xml, XSLT.NAMESPACE, "decimal-format")[0]

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

    @Nested
    @DisplayName("XSLT 3.0 (6) Template Rules")
    internal inner class TemplateRules {
        @Nested
        @DisplayName("XSLT 3.0 (6.1) xsl:template")
        internal inner class Template {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="test"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltTemplate>(xml, XSLT.NAMESPACE, "template")[0]

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
        @DisplayName("XSLT 3.0 (6.3) xsl:apply-templates")
        internal inner class ApplyTemplates {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:apply-templates select="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltApplyTemplates>(xml, XSLT.NAMESPACE, "apply-templates")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (6.8) xsl:apply-imports")
        internal inner class ApplyImports {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:apply-imports/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltApplyImports>(xml, XSLT.NAMESPACE, "apply-imports")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (6.8) xsl:next-match")
        internal inner class NextMatch {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="ipsum">
                            <xsl:next-match/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltNextMatch>(xml, XSLT.NAMESPACE, "next-match")[0]

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
    @DisplayName("XSLT 3.0 (7) Repetition")
    internal inner class Repetition {
        @Nested
        @DisplayName("XSLT 3.0 (7.1) xsl:for-each")
        internal inner class ForEach {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:for-each select="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltForEach>(xml, XSLT.NAMESPACE, "for-each")[0]

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
    @DisplayName("XSLT 3.0 (8) Conditional Processing")
    internal inner class ConditionalProcessing {
        @Nested
        @DisplayName("XSLT 3.0 (8.1) xsl:if")
        internal inner class If {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:if test="true"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltIf>(xml, XSLT.NAMESPACE, "if")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (8.2) xsl:choose")
        internal inner class Choose {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:choose/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltChoose>(xml, XSLT.NAMESPACE, "choose")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (8.2) xsl:when")
        internal inner class When {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:choose>
                                <xsl:when test="ipsum"/>
                            </xsl:choose>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltWhen>(xml, XSLT.NAMESPACE, "when")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltChoose::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (8.2) xsl:otherwise")
        internal inner class Otherwise {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:choose>
                                <xsl:otherwise/>
                            </xsl:choose>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltOtherwise>(xml, XSLT.NAMESPACE, "otherwise")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltChoose::class.java)))
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
    @DisplayName("XSLT 3.0 (9) Variables and Parameters")
    internal inner class VariablesAndParameters {
        @Nested
        @DisplayName("XSLT 3.0 (9.1) xsl:variable")
        internal inner class Variable {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:variable name="lorem" select="ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltVariable>(xml, XSLT.NAMESPACE, "variable")[0]

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
        @DisplayName("XSLT 3.0 (9.2) xsl:param")
        internal inner class Param {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:param name="lorem" select="ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltParam>(xml, XSLT.NAMESPACE, "param")[0]

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
        @DisplayName("XSLT 3.0 (9.10) xsl:with-param")
        internal inner class WithParam {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:apply-templates>
                                <xsl:with-param name="ipsum" select="dolor"/>
                            </xsl:apply-templates>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltWithParam>(xml, XSLT.NAMESPACE, "with-param")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltApplyTemplates::class.java)))
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
    @DisplayName("XSLT 3.0 (10) Callable Components")
    internal inner class CallableComponents {
        @Nested
        @DisplayName("XSLT 3.0 (10.1) xsl:call-template")
        internal inner class CallTemplate {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:call-template name="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltCallTemplate>(xml, XSLT.NAMESPACE, "call-template")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (10.2) xsl:attribute-set")
        internal inner class AttributeSet {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:attribute-set name="test"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltAttributeSet>(xml, XSLT.NAMESPACE, "attribute-set")[0]

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
        @DisplayName("XSLT 3.0 (10.3) xsl:function")
        internal inner class Function {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:function name="lorem"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltFunction>(xml, XSLT.NAMESPACE, "function")[0]

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

    @Nested
    @DisplayName("XSLT 3.0 (11) Creating Nodes and Sequences")
    internal inner class CreatingNodesAndSequences {
        @Nested
        @DisplayName("XSLT 3.0 (11.1) xsl:namespace-alias")
        internal inner class NamespaceAlias {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:axsl="urn:xslt:namespace-alias" version="1.0">
                        <xsl:namespace-alias stylesheet-prefix="axsl" result-prefix="xsl"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltNamespaceAlias>(xml, XSLT.NAMESPACE, "namespace-alias")[0]

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
        @DisplayName("XSLT 3.0 (11.2) xsl:element")
        internal inner class Element {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:element name="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltElement>(xml, XSLT.NAMESPACE, "element")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.3) xsl:attribute")
        internal inner class Attribute {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:attribute name="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltAttribute>(xml, XSLT.NAMESPACE, "attribute")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.4.2) xsl:text")
        internal inner class Text {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:text>ipsum</xsl:text>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltText>(xml, XSLT.NAMESPACE, "text")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.4.3) xsl:value-of")
        internal inner class ValueOf {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:value-of select="@ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltValueOf>(xml, XSLT.NAMESPACE, "value-of")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.5) xsl:document")
        internal inner class Document {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:document/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltDocument>(xml, XSLT.NAMESPACE, "document")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                    assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.6) xsl:processing-instruction")
        internal inner class ProcessingInstruction {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:processing-instruction name="ipsum">dolor</xsl:processing-instruction>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltProcessingInstruction>(xml, XSLT.NAMESPACE, "processing-instruction")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.7) xsl:namespace")
        internal inner class Namespace {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:namespace name="xs" select="'http://www.w3.org/2001/XMLSchema'"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltNamespace>(xml, XSLT.NAMESPACE, "namespace")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.8) xsl:comment")
        internal inner class Comment {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:comment>ipsum</xsl:comment>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltComment>(xml, XSLT.NAMESPACE, "comment")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.9.1) xsl:copy")
        internal inner class Copy {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:copy/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltCopy>(xml, XSLT.NAMESPACE, "copy")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.9.2) xsl:copy-of")
        internal inner class CopyOf {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:copy-of select="@ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltCopyOf>(xml, XSLT.NAMESPACE, "copy-of")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (11.10) xsl:sequence")
        internal inner class Sequence {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:sequence select="(1, 2, 3)"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltSequence>(xml, XSLT.NAMESPACE, "sequence")[0]

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
    @DisplayName("XSLT 3.0 (12) Numbering")
    internal inner class Numbering {
        @Nested
        @DisplayName("XSLT 3.0 (12) xsl:number")
        internal inner class Number {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:number value="1234"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltNumber>(xml, XSLT.NAMESPACE, "number")[0]

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
    @DisplayName("XSLT 3.0 (13) Sorting")
    internal inner class Sorting {
        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:sort")
        internal inner class Sort {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:apply-templates>
                                <xsl:sort/>
                            </xsl:apply-templates>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltSort>(xml, XSLT.NAMESPACE, "sort")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltApplyTemplates::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (13.2) xsl:perform-sort")
        internal inner class PerformSort {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:perform-sort select="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltPerformSort>(xml, XSLT.NAMESPACE, "perform-sort")[0]

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
    @DisplayName("XSLT 3.0 (14) Grouping")
    internal inner class Grouping {
        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:for-each-group")
        internal inner class ForEachGroup {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:for-each-group select="ipsum"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltForEachGroup>(xml, XSLT.NAMESPACE, "for-each-group")[0]

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
    @DisplayName("XSLT 3.0 (17) Regular Expressions")
    internal inner class RegularExpressions {
        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:analyze-string")
        internal inner class AnalyzeString {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:analyze-string select="ipsum" regex="dolor"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltAnalyzeString>(xml, XSLT.NAMESPACE, "analyze-string")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:matching-substring")
        internal inner class MatchingSubstring {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:analyze-string select="ipsum" regex="dolor">
                                <xsl:matching-substring/>
                            </xsl:analyze-string>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltMatchingSubstring>(xml, XSLT.NAMESPACE, "matching-substring")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltAnalyzeString::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:non-matching-substring")
        internal inner class NonMatchingSubstring {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:analyze-string select="ipsum" regex="dolor">
                                <xsl:non-matching-substring/>
                            </xsl:analyze-string>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltNonMatchingSubstring>(xml, XSLT.NAMESPACE, "non-matching-substring")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltAnalyzeString::class.java)))
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
    @DisplayName("XSLT 3.0 (20) Additional Functions")
    internal inner class AdditionalFunctions {
        @Nested
        @DisplayName("XSLT 3.0 (13.1) xsl:key")
        internal inner class Key {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:key name="lorem" use="ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltKey>(xml, XSLT.NAMESPACE, "key")[0]

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

    @Nested
    @DisplayName("XSLT 3.0 (23) Diagnostics")
    internal inner class Diagnostics {
        @Nested
        @DisplayName("XSLT 3.0 (23.1) xsl:message")
        internal inner class Message {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:message>Lorem ipsum</xsl:message>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltMessage>(xml, XSLT.NAMESPACE, "message")[0]

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
    @DisplayName("XSLT 3.0 (24) Extensibility and Fallback")
    internal inner class ExtensibilityAndFallback {
        @Nested
        @DisplayName("XSLT 3.0 (24.2.3) xsl:fallback")
        internal inner class Fallback {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:template match="lorem">
                            <xsl:unknown>
                                <xsl:fallback/>
                            </xsl:unknown>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltFallback>(xml, XSLT.NAMESPACE, "fallback")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltDirElemConstructor::class.java)))
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
    @DisplayName("XSLT 3.0 (25) Transformation Results")
    internal inner class TransformationResults {
        @Nested
        @DisplayName("XSLT 3.0 (25.1) xsl:result-document")
        internal inner class ResultDocument {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:template match="lorem">
                            <xsl:result-document/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltResultDocument>(xml, XSLT.NAMESPACE, "result-document")[0]

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
    @DisplayName("XSLT 3.0 (26) Serialization")
    internal inner class Serialization {
        @Nested
        @DisplayName("XSLT 3.0 (26) xsl:output")
        internal inner class Output {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                        <xsl:output method="xml"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltOutput>(xml, XSLT.NAMESPACE, "output")[0]

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
        @DisplayName("XSLT 3.0 (26.1) xsl:character-map")
        internal inner class CharacterMap {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:character-map name="lorem-ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltCharacterMap>(xml, XSLT.NAMESPACE, "character-map")[0]

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
        @DisplayName("XSLT 3.0 (26.1) xsl:output-character")
        internal inner class OutputCharacter {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                        <xsl:character-map name="lorem-ipsum">
                            <xsl:output-character character="<" string="&lt;"/>
                        </xsl:character-map>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltOutputCharacter>(xml, XSLT.NAMESPACE, "output-character")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltCharacterMap::class.java)))
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

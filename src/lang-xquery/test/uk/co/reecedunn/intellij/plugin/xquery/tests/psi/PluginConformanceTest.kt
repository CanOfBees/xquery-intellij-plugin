/*
 * Copyright (C) 2017-2020 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Implementation Conformance Checks")
private class PluginConformanceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
    fun testAttributeDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest.xq")

        val attributeDeclTestPsi = file.walkTree().filterIsInstance<PluginAttributeDeclTest>().first()
        val conformance = attributeDeclTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_ATTRIBUTE_DECL))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (30) BinaryConstructor")
    fun testBinaryConstructor() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

        val binaryKindTestPsi = file.descendants().filterIsInstance<PluginBinaryConstructor>().first()
        val conformance = binaryKindTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_BINARY))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
    fun testBinaryTest() {
        val file = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq")

        val binaryKindTestPsi = file.walkTree().filterIsInstance<PluginBinaryTest>().first()
        val conformance = binaryKindTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_BINARY))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
    internal inner class CatchClause {
        @Test
        @DisplayName("catch clause")
        fun testCatchClause() {
            val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")

            val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
            val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
            val versioned = catchClausePsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_CATCH))
        }

        @Test
        @DisplayName("enclosed expression")
        fun testEnclosedExpr_CatchClause() {
            val file = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")

            val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
            val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
            val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
            val versioned = enclosedExprPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XQueryElementType.EXPR))
        }

        @Test
        @DisplayName("missing enclosed expression")
        fun testEnclosedExpr_CatchClause_NoExpr() {
            val file = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq")

            val tryCatchExprPsi = file.descendants().filterIsInstance<XQueryTryCatchExpr>().first()
            val catchClausePsi = tryCatchExprPsi.children().filterIsInstance<XQueryCatchClause>().first()
            val enclosedExprPsi = catchClausePsi.children().filterIsInstance<XPathEnclosedExpr>().first()
            val versioned = enclosedExprPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(2))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.REC_3_1_20170321))
            assertThat(versioned.requiresConformance[1], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.BLOCK_OPEN))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Test
        @DisplayName("function declaration")
        fun testCompatibilityAnnotation_FunctionDecl() {
            val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_PRIVATE))
        }

        @Test
        @DisplayName("variable declaration")
        fun testCompatibilityAnnotation_VarDecl() {
            val file = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_PRIVATE))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
    fun testComplexTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest.xq")

        val complexTypeTestPsi = file.walkTree().filterIsInstance<PluginComplexTypeTest>().first()
        val conformance = complexTypeTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_COMPLEX_TYPE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
    fun testElementDeclTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ElementDeclTest.xq")

        val elementDeclTestPsi = file.walkTree().filterIsInstance<PluginElementDeclTest>().first()
        val conformance = elementDeclTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_ELEMENT_DECL))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    internal inner class ForwardAxis {
        @Test
        @DisplayName("namespace::")
        fun testForwardAxis_Namespace() {
            val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq")

            val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
            val versioned = forwardAxisPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_NAMESPACE))
        }

        @Test
        @DisplayName("property::")
        fun testForwardAxis_Property() {
            val file = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq")

            val forwardAxisPsi = file.descendants().filterIsInstance<XPathForwardAxis>().first()
            val versioned = forwardAxisPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_PROPERTY))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    fun testNamedArrayNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.xq")

        val arrayTestPsi = file.walkTree().filterIsInstance<PluginNamedArrayNodeTest>().first()
        val conformance = arrayTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_ARRAY_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    fun testNamedBooleanNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.xq")

        val booleanTestPsi = file.walkTree().filterIsInstance<PluginNamedBooleanNodeTest>().first()
        val conformance = booleanTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_BOOLEAN_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
    fun testNamedKindTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedKindTest.xq")

        val namedKindTestPsi = file.walkTree().filterIsInstance<PluginNamedKindTest>().first()
        val versioned = namedKindTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryElementType.STRING_LITERAL))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    fun testNamedMapNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedMapNodeTest.xq")

        val objectTestPsi = file.walkTree().filterIsInstance<PluginNamedMapNodeTest>().first()
        val conformance = objectTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_OBJECT_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    fun testNamedNullNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest.xq")

        val nullTestPsi = file.walkTree().filterIsInstance<PluginNamedNullNodeTest>().first()
        val conformance = nullTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_NULL_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    fun testNamedNumberNodeTest() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.xq")

        val numberTestPsi = file.walkTree().filterIsInstance<PluginNamedNumberNodeTest>().first()
        val conformance = numberTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_NUMBER_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (54) NumberConstructor")
    fun testNumberConstructor() {
        val file = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")

        val numberConstructorPsi = file.descendants().filterIsInstance<PluginNumberConstructor>().first()
        val conformance = numberConstructorPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_NUMBER_NODE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
    fun testSchemaComponentTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest.xq")

        val schemaComponentTestPsi = file.walkTree().filterIsInstance<PluginSchemaComponentTest>().first()
        val conformance = schemaComponentTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_COMPONENT))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
    fun testSchemaFacetTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaFacetTest.xq")

        val schemaFacetTestPsi = file.walkTree().filterIsInstance<PluginSchemaFacetTest>().first()
        val conformance = schemaFacetTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_FACET))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
    fun testSchemaParticleTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest.xq")

        val schemaParticleTestPsi = file.walkTree().filterIsInstance<PluginSchemaParticleTest>().first()
        val conformance = schemaParticleTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_PARTICLE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
    fun testSchemaRootTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

        val schemaRootTestPsi = file.walkTree().filterIsInstance<PluginSchemaRootTest>().first()
        val conformance = schemaRootTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_ROOT))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
    fun testSchemaTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest.xq")

        val schemaTypeTestPsi = file.walkTree().filterIsInstance<PluginSchemaTypeTest>().first()
        val conformance = schemaTypeTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_TYPE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
    fun testSimpleTypeTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest.xq")

        val simpleTypeTestPsi = file.walkTree().filterIsInstance<PluginSimpleTypeTest>().first()
        val conformance = simpleTypeTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SIMPLE_TYPE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
    fun testStylesheetImport() {
        val file = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq")

        val stylesheetImportPsi = file.descendants().filterIsInstance<PluginStylesheetImport>().first()
        val conformance = stylesheetImportPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_IMPORT))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
    fun testTextTest_KeyName() {
        val file = parseResource("tests/parser/marklogic-8.0/NamedTextTest.xq")

        val textTestPsi = file.walkTree().filterIsInstance<XPathTextTest>().first()
        val versioned = textTestPsi as VersionConformance

        assertThat(versioned.requiresConformance.size, `is`(1))
        assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_8_0))

        assertThat(versioned.conformanceElement, `is`(notNullValue()))
        assertThat(versioned.conformanceElement.elementType, `is`(XQueryElementType.STRING_LITERAL))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (35) TransactionSeparator")
    internal inner class TransactionSeparator {
        @Test
        @DisplayName("single transaction; no semicolon")
        fun testTransactions_Single_NoSemicolon() {
            val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
            val transactionSeparatorPsi =
                applyExpr.children().filterIsInstance<PluginTransactionSeparator>().firstOrNull()

            assertThat(transactionSeparatorPsi, `is`(nullValue()))
        }

        @Test
        @DisplayName("single transaction; with semicolon")
        fun testTransactions_Single_Semicolon() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
            val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().first()
            val conformance = transactionSeparatorPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }

        @Test
        @DisplayName("multiple transactions; semicolon at end; first transaction")
        fun testTransactions_Multiple_First() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
            val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().first()
            val conformance = transactionSeparatorPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(3))
            assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
            assertThat(conformance.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(conformance.requiresConformance[2], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }

        @Test
        @DisplayName("multiple transactions; semicolon at end; last transaction")
        fun testTransactions_Multiple_Last() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
            val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().last()
            val conformance = transactionSeparatorPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }

        @Test
        @DisplayName("multiple transactions; no semicolon at end; first transaction")
        fun testTransactions_Multiple_NoSemicolonAtEnd_Last() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
            val transactionSeparatorPsi = applyExpr.children().filterIsInstance<PluginTransactionSeparator>().last()
            val conformance = transactionSeparatorPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.TRANSACTION_SEPARATOR))
        }

        @Test
        @DisplayName("multiple transactions; prolog in other transaction")
        fun testTransactions_Multiple_WithProlog() {
            val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

            val transactionSeparatorPsi = file.children().filterIsInstance<PluginTransactionSeparator>().first()
            val conformance = transactionSeparatorPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(2))
            assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
            assertThat(conformance.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) TupleType")
    internal inner class TupleType {
        @Test
        @DisplayName("tuple type")
        fun tupleType() {
            val file = parseResource("tests/parser/saxon-9.8/TupleType.xq")

            val tupleTypePsi = file.walkTree().filterIsInstance<PluginTupleType>().first()
            val conformance = tupleTypePsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_TUPLE))
        }

        @Test
        @DisplayName("extensible tuple type")
        fun extensible() {
            val file = parseResource("tests/parser/saxon-9.9/TupleType_Extensible.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleType>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.STAR))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (24) TupleField")
    internal inner class TupleField {
        @Test
        @DisplayName("tuple field")
        fun tupleField() {
            val file = parseResource("tests/parser/saxon-9.8/TupleField.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.NCNAME))
        }

        @Test
        @DisplayName("as SequenceType")
        fun asType() {
            val file = parseResource("tests/parser/saxon-10.0/TupleField.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_AS))
        }

        @Test
        @DisplayName("optional tuple field")
        fun optional() {
            val file = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.OPTIONAL))
        }

        @Test
        @DisplayName("optional tuple field; compact whitespace")
        fun optional_CompactWhitespace() {
            val file = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_CompactWhitespace.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            // "?:" with compact whitespace
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ELVIS))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
    fun testTypeDecl() {
        val file = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")

        val typeDeclPsi = file.descendants().filterIsInstance<PluginTypeDecl>().first()
        val conformance = typeDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_TYPE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
    fun testUnionType() {
        val file = parseResource("tests/parser/xpath-ng/proposal-6/UnionType.xq")

        val unionTypePsi = file.walkTree().filterIsInstance<PluginUnionType>().first()
        val conformance = unionTypePsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_UNION))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (27) ValidateExpr")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("validate as")
        fun validateAs() {
            val file = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq")

            val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
            val versioned = validateExprPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_AS))
        }

        @Test
        @DisplayName("validate full")
        fun validateFull() {
            val file = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateFull.xq")

            val validateExprPsi = file.descendants().filterIsInstance<XQueryValidateExpr>().first()
            val versioned = validateExprPsi as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(MarkLogic.VERSION_6_0))

            assertThat(versioned.conformanceElement, `is`(notNullValue()))
            assertThat(versioned.conformanceElement.elementType, `is`(XQueryTokenType.K_FULL))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; working draft syntax")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(3))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.WD_1_0_20030502))
            assertThat(versioned.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(versioned.requiresConformance[2], `is`(until(EXistDB.VERSION_4_0)))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("or only")
        fun or() {
            val file = parseResource("tests/parser/xquery-1.0/OrExpr.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathElementType.NODE_TEST))
        }

        @Test
        @DisplayName("orElse only")
        fun orElse() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_SingleOrElse.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }

        @Test
        @DisplayName("orElse first")
        fun orElseFirst() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseFirst.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }

        @Test
        @DisplayName("orElse last")
        fun orElseLast() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseLast.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("and only")
        fun and() {
            val file = parseResource("tests/parser/xquery-1.0/AndExpr.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathElementType.NODE_TEST))
        }

        @Test
        @DisplayName("andAlso only")
        fun andAlso() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_SingleAndAlso.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }

        @Test
        @DisplayName("andAlso first")
        fun andAlsoFirst() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoFirst.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }

        @Test
        @DisplayName("andAlso last")
        fun andAlsoLast() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoLast.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (81) ContextItemFunctionExpr")
    internal inner class ContextItemFunctionExpr {
        @Test
        @DisplayName("simple inline function expression")
        fun simpleInlineFunctionExpr() {
            val file = parseResource("tests/parser/xpath-ng/proposal-5/SimpleInlineFunctionExpr.xq")
            val versioned = file.walkTree().filterIsInstance<PluginContextItemFunctionExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_FN))
        }

        @Test
        @DisplayName("context item function expression")
        fun contextItemFunctionExpr() {
            val file = parseResource("tests/parser/xpath-ng/proposal-5/ContextItemFunctionExpr.xq")
            val versioned = file.walkTree().filterIsInstance<PluginContextItemFunctionExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.CONTEXT_FUNCTION))
        }

        @Test
        @DisplayName("context item function expression; space between dot and brace")
        fun contextItemFunctionExpr_spaceBetweenDotAndBrace() {
            val file = parseResource("tests/parser/xpath-ng/proposal-5/ContextItemFunctionExpr_SpaceBetweenDotAndBrace.xq")
            val versioned = file.walkTree().filterIsInstance<PluginContextItemFunctionExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.DOT))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    fun testSequenceTypeUnion() {
        val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion.xq")
        val conformance = file.walkTree().filterIsInstance<XQuerySequenceTypeUnion>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.UNION))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    inner class SequenceTypeList {
        @Test
        @DisplayName("sequence type list")
        fun sequenceTypeList() {
            val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.COMMA))
        }

        @Test
        @DisplayName("in typed function test")
        fun inTypedFunctionTest() {
            val file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.ANY_ITEM_TYPE))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    fun paramList() {
        val file = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped.xq")
        val conformance = file.walkTree().filterIsInstance<XPathParamList>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_4))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ELLIPSIS))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (103) SchemaWildcardTest")
    fun testSchemaWildcardTest() {
        val file = parseResource("tests/parser/marklogic-7.0/SchemaWildcardTest.xq")

        val schemaWildcardTestPsi = file.walkTree().filterIsInstance<PluginSchemaWildcardTest>().first()
        val conformance = schemaWildcardTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SCHEMA_WILDCARD))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (104) ModelGroupTest")
    fun testModelGroupTest() {
        val file = parseResource("tests/parser/marklogic-7.0/ModelGroupTest.xq")

        val modelGroupTestPsi = file.walkTree().filterIsInstance<PluginModelGroupTest>().first()
        val conformance = modelGroupTestPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_7_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_MODEL_GROUP))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (105) UsingDecl")
    fun testUsingDecl() {
        val file = parseResource("tests/parser/marklogic-6.0/UsingDecl.xq")

        val usingDeclPsi = file.walkTree().filterIsInstance<PluginUsingDecl>().first()
        val conformance = usingDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(MarkLogic.VERSION_4_0))
        assertThat(conformance.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_USING))
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (199) ElementTest ; XQuery IntelliJ Plugin EBNF (111) ElementNameOrWildcard")
    fun elementTest() {
        val conformance = parse<XPathElementTest>("() instance of element(*:test)")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.WILDCARD))
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (195) AttributeTest ; XQuery IntelliJ Plugin EBNF (112) AttribNameOrWildcard")
    fun attributeTest() {
        val conformance = parse<XPathAttributeTest>("() instance of attribute(*:test)")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.WILDCARD))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (114) OtherwiseExpr")
    fun otherwiseExpr() {
        val conformance = parse<PluginOtherwiseExpr>("a otherwise b")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_OTHERWISE))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
    fun typeAlias() {
        val conformance = parse<PluginTypeAlias>("a instance of ~b")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.TYPE_ALIAS))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (117) LambdaFunctionExpr")
    internal inner class LambdaFunctionExpr {
        @Test
        @DisplayName("lambda function expression")
        fun lambdaFunctionExpr() {
            val file = parseResource("tests/parser/saxon-10.0/LambdaFunctionExpr.xq")
            val versioned = file.walkTree().filterIsInstance<PluginLambdaFunctionExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.LAMBDA_FUNCTION))
        }

        @Test
        @DisplayName("space between underscore and brace")
        fun spaceBetweenUnderscoreAndBrace() {
            val file = parseResource("tests/parser/saxon-10.0/LambdaFunctionExpr_SpaceBetweenUnderscoreAndBrace.xq")
            val versioned = file.walkTree().filterIsInstance<PluginLambdaFunctionExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K__))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (118) ParamRef")
    fun paramRef() {
        val conformance = parse<PluginParamRef>("$ 1234")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.INTEGER_LITERAL))
    }
}

/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class SaxonParserTest : ParserTestCase() {
    // region Saxon 9.8 :: UnionType

    @Test
    fun testUnionType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingFirstType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingNextType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingNextType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingNextType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_Multiple() {
        // This is testing handling of whitespace before parsing the next comma.
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_InTypedMapTest() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: TupleType

    @Test
    fun testTupleType() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleType_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: TupleType :: TupleField

    @Test
    fun testTupleField() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_Multiple() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MultipleWithOccurrenceIndicator() {
        // This is testing handling of whitespace before parsing the next comma.
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MissingColon() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingColon.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MissingSequenceType() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}

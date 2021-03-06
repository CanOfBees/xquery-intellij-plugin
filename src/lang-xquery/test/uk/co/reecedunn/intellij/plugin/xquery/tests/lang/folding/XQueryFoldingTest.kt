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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.folding

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.core.lang.foldable.FoldingBuilderImpl
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Code Folding - XQuery FoldingBuilder")
private class XQueryFoldingTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XQueryModule
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (9) BoundarySpaceDecl")
    fun boundarySpaceDecl() {
        val file = parseResource("tests/folding/BoundarySpaceDecl.xq")
        val builder = FoldingBuilderImpl()

        val descriptors = builder.buildFoldRegions(file, file.document!!, false)
        assertThat(descriptors, `is`(notNullValue()))
        assertThat(descriptors.size, `is`(0))

        assertThat(builder.getPlaceholderText(file.node), `is`(nullValue()))
        assertThat(builder.isCollapsedByDefault(file.node), `is`(false))
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (35) FunctionBody")
    internal inner class FunctionBody {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/FunctionBody.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/FunctionBody_MultiLine.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.FUNCTION_BODY))
            assertThat(descriptors[0].range.startOffset, `is`(27))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirElemConstructor.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("single line; self-closing")
        fun singleLine_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor_SelfClosing.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete")
        fun incomplete() {
            val file = parseResource("tests/folding/DirElemConstructor_Incomplete.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after")
        fun incompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor_IncompleteNamespace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("incomplete open tag with query content after inside a direct element")
        fun inner_IncompleteNamespace() {
            val file = parseResource("tests/folding/DirElemConstructor_Inner_IncompleteNamespace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(2))
            assertThat(descriptors[0].range.endOffset, `is`(9))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLine.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(4))
            assertThat(descriptors[0].range.endOffset, `is`(21))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; self closing")
        fun multipleLines_SelfClosing() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLine_SelfClosing.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines with attributes")
        fun multipleLinesWithAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLineWithAttributes.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines with attributes; space after attribute list")
        fun multipleLinesWithAttributesAndSpace() {
            val file = parseResource("tests/folding/DirElemConstructor_MultiLineWithAttributesAndSpace.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(20))
            assertThat(descriptors[0].range.endOffset, `is`(39))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr only")
        fun enclosedExprOnly() {
            val file = parseResource("tests/folding/DirElemConstructor_EnclosedExprOnly.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.ENCLOSED_EXPR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(10))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("EnclosedExpr only, with attributes across multiple lines")
        fun enclosedExprOnly_multipleLineAttributes() {
            val file = parseResource("tests/folding/DirElemConstructor_EnclosedExprOnly_MultiLineAttributes.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(2))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(3))
            assertThat(descriptors[0].range.endOffset, `is`(32))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("..."))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))

            assertThat(descriptors[1].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[1].dependencies, `is`(notNullValue()))
            assertThat(descriptors[1].dependencies.size, `is`(0))
            assertThat(descriptors[1].group, `is`(nullValue()))
            assertThat(descriptors[1].element.elementType, `is`(XQueryElementType.ENCLOSED_EXPR))
            assertThat(descriptors[1].range.startOffset, `is`(22))
            assertThat(descriptors[1].range.endOffset, `is`(29))

            assertThat(builder.getPlaceholderText(descriptors[1].element), `is`("{...}"))
            assertThat(builder.isCollapsedByDefault(descriptors[1].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (149) DirCommentConstructor")
    internal inner class DirCommentConstructor {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/DirCommentConstructor.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/DirCommentConstructor_Empty.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(8))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--...-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/DirCommentConstructor_MultiLine.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(37))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--Lorem ipsum.-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/DirCommentConstructor_MultiLine_Incomplete.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(17))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("<!--Lorem ipsum.-->"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (231) Comment")
    internal inner class Comment {
        @Test
        @DisplayName("single line")
        fun singleLine() {
            val file = parseResource("tests/folding/Comment.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(0))
        }

        @Test
        @DisplayName("multiple lines; empty text")
        fun multipleLines_EmptyText() {
            val file = parseResource("tests/folding/Comment_Empty.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(5))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:...:)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines")
        fun multipleLines() {
            val file = parseResource("tests/folding/Comment_MultiLine.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(34))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; xqdoc")
        fun multipleLines_XQDoc() {
            val file = parseResource("tests/folding/Comment_MultiLine_XQDoc.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(35))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete")
        fun multipleLines_Incomplete() {
            val file = parseResource("tests/folding/Comment_MultiLine_Incomplete.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(15))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(: Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }

        @Test
        @DisplayName("multiple lines; incomplete; xqdoc")
        fun multipleLines_Incomplete_XQDoc() {
            val file = parseResource("tests/folding/Comment_MultiLine_Incomplete_XQDoc.xq")
            val builder = FoldingBuilderImpl()

            val descriptors = builder.buildFoldRegions(file, file.document!!, false)
            assertThat(descriptors, `is`(notNullValue()))
            assertThat(descriptors.size, `is`(1))

            assertThat(descriptors[0].canBeRemovedWhenCollapsed(), `is`(false))
            assertThat(descriptors[0].dependencies, `is`(notNullValue()))
            assertThat(descriptors[0].dependencies.size, `is`(0))
            assertThat(descriptors[0].group, `is`(nullValue()))
            assertThat(descriptors[0].element.elementType, `is`(XQueryElementType.COMMENT))
            assertThat(descriptors[0].range.startOffset, `is`(0))
            assertThat(descriptors[0].range.endOffset, `is`(16))

            assertThat(builder.getPlaceholderText(descriptors[0].element), `is`("(:~ Lorem ipsum. :)"))
            assertThat(builder.isCollapsedByDefault(descriptors[0].element), `is`(false))
        }
    }
}

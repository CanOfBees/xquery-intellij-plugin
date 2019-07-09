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
package uk.co.reecedunn.intellij.plugin.xquery.lexer

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType

@Suppress("Reformat")
object XQueryTokenType {
    // region Multi-Token Symbols

    val XML_COMMENT = IElementType("XQUERY_XML_COMMENT_TOKEN", XQuery) // XQuery 1.0
    val XML_COMMENT_START_TAG = IElementType("XQUERY_XML_COMMENT_START_TAG_TOKEN", XQuery) // XQuery 1.0
    val XML_COMMENT_END_TAG = IElementType("XQUERY_XML_COMMENT_END_TAG_TOKEN", XQuery) // XQuery 1.0

    val PROCESSING_INSTRUCTION_BEGIN = IElementType("XQUERY_PROCESSING_INSTRUCTION_BEGIN_TOKEN", XQuery) // XQuery 1.0
    val PROCESSING_INSTRUCTION_END = IElementType("XQUERY_PROCESSING_INSTRUCTION_END_TOKEN", XQuery) // XQuery 1.0
    val PROCESSING_INSTRUCTION_CONTENTS = IElementType("XQUERY_PROCESSING_INSTRUCTION_CONTENTS_TOKEN", XQuery) // XQuery 1.0

    val DIRELEM_MAYBE_OPEN_XML_TAG = IElementType("XQUERY_DIRELEM_MAYBE_OPEN_XML_TAG_TOKEN", XQuery) // XQuery 1.0
    val DIRELEM_OPEN_XML_TAG = IElementType("XQUERY_DIRELEM_OPEN_XML_TAG_TOKEN", XQuery) // XQuery 1.0

    val OPEN_XML_TAG = IElementType("XQUERY_OPEN_XML_TAG_TOKEN", XQuery) // XQuery 1.0
    val END_XML_TAG = IElementType("XQUERY_END_XML_TAG_TOKEN", XQuery) // XQuery 1.0
    val CLOSE_XML_TAG = IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery) // XQuery 1.0
    val SELF_CLOSING_XML_TAG = IElementType("XQUERY_SELF_CLOSING_XML_TAG_TOKEN", XQuery) // XQuery 1.0
    val XML_ELEMENT_CONTENTS = IElementType("XQUERY_XML_ELEMENT_CONTENTS_TOKEN", XQuery) // XQuery 1.0
    val XML_EQUAL = IElementType("XQUERY_XML_EQUAL_TOKEN", XQuery) // XQuery 1.0
    val XML_WHITE_SPACE = IElementType("XQUERY_XML_WHITE_SPACE_TOKEN", XQuery) // XQuery 1.0
    val XML_TAG_NCNAME: IElementType = INCNameType("XQUERY_XML_TAG_NCNAME_TOKEN", XQuery) // XQuery 1.0
    val XML_TAG_QNAME_SEPARATOR = IElementType("XQUERY_XML_TAG_QNAME_SEPARATOR_TOKEN", XQuery) // XQuery 1.0
    val XML_ATTRIBUTE_NCNAME: IElementType = INCNameType("XQUERY_XML_ATTRIBUTE_NCNAME_TOKEN", XQuery) // XQuery 1.0
    val XML_ATTRIBUTE_QNAME_SEPARATOR = IElementType("XQUERY_XML_ATTRIBUTE_QNAME_SEPARATOR_TOKEN", XQuery) // XQuery 1.0

    val XML_ATTRIBUTE_VALUE_START = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_START_TOKEN", XQuery) // XQuery 1.0
    val XML_ATTRIBUTE_VALUE_CONTENTS = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQuery) // XQuery 1.0
    val XML_ATTRIBUTE_VALUE_END = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_END_TOKEN", XQuery) // XQuery 1.0
    val XML_ESCAPED_CHARACTER = IElementType("XQUERY_XML_ESCAPED_CHARACTER_TOKEN", XQuery) // XQuery 1.0

    val XML_CHARACTER_REFERENCE = IElementType("XQUERY_XML_CHARACTER_REFERENCE_TOKEN", XQuery) // XQuery 1.0
    val XML_PREDEFINED_ENTITY_REFERENCE = IElementType("XQUERY_XML_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery) // XQuery 1.0
    val XML_PARTIAL_ENTITY_REFERENCE = IElementType("XQUERY_XML_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery) // XQuery 1.0
    val XML_EMPTY_ENTITY_REFERENCE = IElementType("XQUERY_XML_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery) // XQuery 1.0

    val CDATA_SECTION = IElementType("XQUERY_CDATA_SECTION_TOKEN", XQuery) // XQuery 1.0
    val CDATA_SECTION_START_TAG = IElementType("XQUERY_CDATA_SECTION_START_TAG_TOKEN", XQuery) // XQuery 1.0
    val CDATA_SECTION_END_TAG = IElementType("XQUERY_CDATA_SECTION_END_TAG_TOKEN", XQuery) // XQuery 1.0

    val STRING_CONSTRUCTOR_START = IElementType("XQUERY_STRING_CONSTRUCTOR_START_TOKEN", XQuery) // XQuery 3.1
    val STRING_CONSTRUCTOR_CONTENTS = IElementType("XQUERY_STRING_CONSTRUCTOR_CONTENTS_TOKEN", XQuery) // XQuery 3.1
    val STRING_CONSTRUCTOR_END = IElementType("XQUERY_STRING_CONSTRUCTOR_END_TOKEN", XQuery) // XQuery 3.1
    val STRING_INTERPOLATION_OPEN = IElementType("XQUERY_STRING_INTERPOLATION_OPEN_TOKEN", XQuery) // XQuery 3.1
    val STRING_INTERPOLATION_CLOSE = IElementType("XQUERY_STRING_INTERPOLATION_CLOSE_TOKEN", XQuery) // XQuery 3.1

    // endregion
    // region Symbols

    val ANNOTATION_INDICATOR = IElementType("XQUERY_ANNOTATION_INDICATOR_TOKEN", XQuery) // XQuery 3.0
    val SEPARATOR = IElementType("XQUERY_SEPARATOR_TOKEN", XQuery) // XQuery 1.0

    // endregion
    // region Keywords

    val K_AFTER = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AFTER", XQuery) // Update Facility 1.0
    val K_ALLOWING = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ALLOWING", XQuery) // XQuery 3.0
    val K_ASCENDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASCENDING", XQuery) // XQuery 1.0
    val K_ASSIGNABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASSIGNABLE", XQuery) // Scripting Extension 1.0
    val K_ATTRIBUTE_DECL = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ATTRIBUTE_DECL_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_BASE_URI = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BASE_URI", XQuery) // XQuery 1.0
    val K_BEFORE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BEFORE", XQuery) // Update Facility 1.0
    val K_BINARY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BINARY", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC60_RESERVED_FUNCTION_NAME) // MarkLogic 6.0
    val K_BLOCK = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BLOCK", XQuery) // Scripting Extension 1.0
    val K_BOUNDARY_SPACE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOUNDARY_SPACE", XQuery) // XQuery 1.0
    val K_BY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BY", XQuery) // XQuery 1.0
    val K_CATCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CATCH", XQuery) // XQuery 3.0
    val K_COLLATION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COLLATION", XQuery) // XQuery 1.0
    val K_COMPLEX_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COMPLEX_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_CONSTRUCTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONSTRUCTION", XQuery) // XQuery 1.0
    val K_CONTEXT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONTEXT", XQuery) // XQuery 3.0
    val K_COPY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY", XQuery) // Update Facility 1.0
    val K_COPY_NAMESPACES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY_NAMESPACES", XQuery) // XQuery 1.0
    val K_COUNT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COUNT", XQuery) // XQuery 3.0
    val K_DECIMAL_FORMAT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_FORMAT", XQuery) // XQuery 3.0
    val K_DECIMAL_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_SEPARATOR", XQuery) // XQuery 3.0
    val K_DECLARE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECLARE", XQuery) // XQuery 1.0
    val K_DELETE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DELETE", XQuery) // Update Facility 1.0
    val K_DESCENDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDING", XQuery) // XQuery 1.0
    val K_DIGIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIGIT", XQuery) // XQuery 3.0
    val K_DOCUMENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT", XQuery) // XQuery 1.0
    val K_ELEMENT_DECL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELEMENT_DECL_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_ENCODING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ENCODING", XQuery) // XQuery 1.0
    val K_EXIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXIT", XQuery) // Scripting Extension 1.0
    val K_EXPONENT_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXPONENT_SEPARATOR", XQuery) // XQuery 3.1
    val K_EXTERNAL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXTERNAL", XQuery) // XQuery 1.0
    val K_FIRST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FIRST", XQuery) // Update Facility 1.0
    val K_FN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FN", XQuery) // Saxon 9.8
    val K_FT_OPTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FT_OPTION", XQuery) // Full Text 1.0
    val K_FULL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FULL", XQuery) // MarkLogic 6.0
    val K_FUZZY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FUZZY", XQuery) // BaseX 6.1
    val K_GREATEST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GREATEST", XQuery) // XQuery 1.0
    val K_GROUP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUP", XQuery) // XQuery 3.0
    val K_GROUPING_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUPING_SEPARATOR", XQuery) // XQuery 3.0
    val K_IMPORT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IMPORT", XQuery) // XQuery 1.0
    val K_INFINITY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INFINITY", XQuery) // XQuery 3.0
    val K_INHERIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INHERIT", XQuery) // XQuery 1.0
    val K_INSERT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSERT", XQuery) // Update Facility 1.0
    val K_INTO = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTO", XQuery) // Update Facility 1.0
    val K_INVOKE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INVOKE", XQuery) // Update Facility 3.0
    val K_LAST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAST", XQuery) // Update Facility 1.0
    val K_LAX = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAX", XQuery) // XQuery 1.0
    val K_MINUS_SIGN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MINUS_SIGN", XQuery) // XQuery 3.0
    val K_MODIFY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODIFY", XQuery) // Update Facility 1.0
    val K_MODULE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODULE", XQuery) // XQuery 1.0
    val K_NAN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAN", XQuery) // XQuery 3.0
    val K_NEXT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NEXT", XQuery) // XQuery 3.0
    val K_NO_INHERIT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NO_INHERIT", XQuery) // XQuery 1.0
    val K_NO_PRESERVE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NO_PRESERVE", XQuery) // XQuery 1.0
    val K_NODES = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NODES", XQuery) // Update Facility 1.0
    val K_NON_DETERMINISTIC = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NON_DETERMINISTIC", XQuery) // BaseX 8.4
    val K_ONLY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ONLY", XQuery) // XQuery 3.0
    val K_ORDER = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDER", XQuery) // XQuery 1.0
    val K_ORDERING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERING", XQuery) // XQuery 1.0
    val K_PATTERN_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PATTERN_SEPARATOR", XQuery) // XQuery 3.0
    val K_PER_MILLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PER_MILLE", XQuery) // XQuery 3.0
    val K_PERCENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PERCENT", XQuery) // XQuery 3.0
    val K_PRESERVE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRESERVE", XQuery) // XQuery 1.0
    val K_PRIVATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRIVATE", XQuery) // MarkLogic 6.0
    val K_PREVIOUS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PREVIOUS", XQuery) // XQuery 3.0
    val K_PUBLIC = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PUBLIC", XQuery) // XQuery 3.0 (§4.15 -- Annotations)
    val K_RENAME = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RENAME", XQuery) // Update Facility 1.0
    val K_REPLACE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REPLACE", XQuery) // Update Facility 1.0
    val K_RETURNING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURNING", XQuery) // Scripting Extension 1.0
    val K_REVALIDATION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REVALIDATION", XQuery) // Update Facility 1.0
    val K_SCHEMA = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA", XQuery) // XQuery 1.0
    val K_SCHEMA_COMPONENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_COMPONENT_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_FACET = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_FACET_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_SCHEMA_PARTICLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_PARTICLE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_ROOT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ROOT_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SEQUENTIAL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SEQUENTIAL", XQuery) // Scripting Extension 1.0
    val K_SIMPLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SIMPLE", XQuery) // Scripting Extension 1.0
    val K_SIMPLE_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SIMPLE_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SKIP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SKIP", XQuery) // Update Facility 1.0
    val K_SLIDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SLIDING", XQuery) // XQuery 3.0
    val K_STABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STABLE", XQuery) // XQuery 1.0
    val K_STRICT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRICT", XQuery) // XQuery 1.0
    val K_STRIP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRIP", XQuery) // XQuery 1.0
    val K_STYLESHEET = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STYLESHEET", XQuery) // MarkLogic 6.0
    val K_SWITCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SWITCH", XQuery, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XQuery 3.0
    val K_TRANSFORM = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRANSFORM", XQuery) // Update Facility 3.0
    val K_TRY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRY", XQuery) // XQuery 3.0
    val K_TUMBLING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TUMBLING", XQuery) // XQuery 3.0
    val K_TUPLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TUPLE", XQuery) // Saxon 9.8
    val K_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPE", XQuery) // XQuery 3.0
    val K_TYPESWITCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPESWITCH", XQuery, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XQuery 1.0
    val K_UNASSIGNABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNASSIGNABLE", XQuery) // Scripting Extension 1.0
    val K_UNORDERED = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNORDERED", XQuery) // XQuery 1.0
    val K_UPDATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATE", XQuery) // BaseX 7.8
    val K_UPDATING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATING", XQuery) // Update Facility 1.0
    val K_VALIDATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALIDATE", XQuery) // XQuery 1.0
    val K_VALUE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALUE", XQuery) // Update Facility 1.0
    val K_VARIABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VARIABLE", XQuery) // XQuery 1.0
    val K_VERSION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VERSION", XQuery) // XQuery 1.0
    val K_WHEN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHEN", XQuery) // XQuery 3.0
    val K_WHERE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHERE", XQuery) // XQuery 1.0
    val K_WHILE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHILE", XQuery, IKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME) // Scripting Extension 1.0
    val K_WITH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WITH", XQuery) // Update Facility 1.0
    val K_XQUERY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_XQUERY", XQuery) // XQuery 1.0
    val K_ZERO_DIGIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ZERO_DIGIT", XQuery) // XQuery 3.0

    // endregion
    // region Terminal Symbols

    val CHARACTER_REFERENCE = IElementType("XQUERY_CHARACTER_REFERENCE_TOKEN", XQuery) // XQuery 1.0
    val PREDEFINED_ENTITY_REFERENCE = IElementType("XQUERY_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery) // XQuery 1.0

    // endregion
    // region Error Reporting and Recovery

    val EMPTY_ENTITY_REFERENCE = IElementType("XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery)
    val ENTITY_REFERENCE_NOT_IN_STRING = IElementType("XQUERY_ENTITY_REFERENCE_NOT_IN_STRING_TOKEN", XQuery)
    val INVALID = IElementType("XQUERY_INVALID_TOKEN", XQuery)
    val PARTIAL_ENTITY_REFERENCE = IElementType("XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery)

    // endregion
    // region Token Sets

    val BOUNDARY_SPACE_MODE_TOKENS = TokenSet.create(K_PRESERVE, K_STRIP)

    val COMPATIBILITY_ANNOTATION_TOKENS = TokenSet.create(
        K_ASSIGNABLE,
        K_PRIVATE,
        K_SEQUENTIAL,
        K_SIMPLE,
        K_UNASSIGNABLE,
        K_UPDATING
    )

    val CONSTRUCTION_MODE_TOKENS = TokenSet.create(K_PRESERVE, K_STRIP)

    val DEFAULT_ELEMENT_OR_FUNCTION_TOKENS = TokenSet.create(
        XPathTokenType.K_ELEMENT,
        XPathTokenType.K_FUNCTION
    )

    val DF_PROPERTY_NAME = TokenSet.create(
        K_DECIMAL_SEPARATOR,
        K_GROUPING_SEPARATOR,
        K_INFINITY,
        K_MINUS_SIGN,
        K_NAN,
        K_PERCENT,
        K_PER_MILLE,
        K_ZERO_DIGIT,
        K_DIGIT,
        K_PATTERN_SEPARATOR,
        K_EXPONENT_SEPARATOR
    )

    val EMPTY_ORDERING_MODE_TOKENS = TokenSet.create(K_GREATEST, XPathTokenType.K_LEAST)

    val FTMATCH_OPTION_START_TOKENS = TokenSet.create(
        XPathTokenType.K_CASE,
        XPathTokenType.K_DIACRITICS,
        K_FUZZY, // BaseX 6.1 XQuery Extension
        XPathTokenType.K_LANGUAGE,
        XPathTokenType.K_LOWERCASE,
        XPathTokenType.K_NO,
        XPathTokenType.K_OPTION,
        XPathTokenType.K_STEMMING,
        XPathTokenType.K_STOP,
        XPathTokenType.K_THESAURUS,
        XPathTokenType.K_UPPERCASE,
        XPathTokenType.K_WILDCARDS
    )

    val INHERIT_MODE_TOKENS = TokenSet.create(K_INHERIT, K_NO_INHERIT)

    val INSERT_LOCATION_TOKENS = TokenSet.create(K_INTO, K_BEFORE, K_AFTER)

    val INSERT_DELETE_NODE_TOKENS = TokenSet.create(XPathTokenType.K_NODE, K_NODES)

    val INSERT_POSITION_TOKENS = TokenSet.create(K_FIRST, K_LAST)

    val ORDERING_MODE_TOKENS = TokenSet.create(XPathTokenType.K_ORDERED, K_UNORDERED)

    val ORDER_MODIFIER_TOKENS = TokenSet.create(K_ASCENDING, K_DESCENDING)

    val PRESERVE_MODE_TOKENS = TokenSet.create(K_PRESERVE, K_NO_PRESERVE)

    val REVALIDATION_MODE_TOKENS = TokenSet.create(K_LAX, K_SKIP, K_STRICT)

    val VALIDATE_EXPR_TOKENS = TokenSet.create(XPathTokenType.K_AS, K_TYPE)

    val VALIDATION_MODE_TOKENS = TokenSet.create(K_LAX, K_STRICT, K_FULL)

    val STRING_LITERAL_TOKENS = TokenSet.create(
        XPathTokenType.STRING_LITERAL_CONTENTS,
        STRING_CONSTRUCTOR_CONTENTS,
        XML_ATTRIBUTE_VALUE_CONTENTS,
        XML_ELEMENT_CONTENTS
    )

    val COMMENT_TOKENS = TokenSet.create(
        XQDocTokenType.CONTENTS,
        XPathTokenType.COMMENT,
        XML_COMMENT
    )

    val LITERAL_TOKENS = TokenSet.create(
        XPathTokenType.INTEGER_LITERAL,
        XPathTokenType.DECIMAL_LITERAL,
        XPathTokenType.DOUBLE_LITERAL,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
    )

    // endregion
}

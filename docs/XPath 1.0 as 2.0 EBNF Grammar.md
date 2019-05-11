---
layout: page
---

# XPath 1.0 as 2.0 EBNF Grammar

This document includes material copied from or derived from the XPath
specifications. Copyright © 1999-2017 W3C® (MIT, ERCIM, Keio, Beihang).

## Abstract
This document describes the XPath 1.0 EBNF grammar using the XPath 2.0
EBNF symbols. This is to make it easier to implement an XPath 1.0 parser
as a subset of an XPath 2.0 parser with minor modifications to account
for precedence changes.

## Table of Contents
- [A XPath Grammar](#a-xpath-grammar)
  - [A.1 EBNF](#a1-ebnf)
    - [A.1.1 Extra-grammatical Constraints](#a11-extra-grammatical-constraints)
    - [A.1.2 Grammar Notes](#a12-grammar-notes)
  - [A.2 Terminal Symbols](#a2-terminal-symbols)
  - [A.3 Reserved Function Names](#a3-reserved-function-names)
- [B References](#b-references)
  - [B.1 W3C References](#b1-w3c-references)
- [C Change Log](#c-change-log)
- [D Differences Between XPath 1.0 and XPath 2.0](#d-differences-between-xpath-10-and-xpath-20)

## A XPath Grammar

### A.1 EBNF

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[1\]   | `XPath`                           | ::= | `Expr`                              |                      |
| \[2\]   | `Expr`                            | ::= | `ExprSingle`                        |                      |
| \[3\]   | `ExprSingle`                      | ::= | `OrExpr`                            |                      |
| \[4\]   | `OrExpr`                          | ::= | `AndExpr ( "or" AndExpr )*`         |                      |
| \[5\]   | `AndExpr`                         | ::= | `EqualityExpr ( "and" EqualityExpr )*` |                   |
| \[6\]   | `EqualityExpr`                    | ::= | `RelationalExpr ( ("=" \| "!=") RelationalExpr )*` | /* gn: comparison */ |
| \[7\]   | `RelationalExpr`                  | ::= | `AdditiveExpr ( ("<" \| ">" \| "<=" \| ">=") AdditiveExpr )*` | /* gn: comparison */ |
| \[8\]   | `AdditiveExpr`                    | ::= | `MultiplicativeExpr ( ("+" \| "-") MultiplicativeExpr )*` | |
| \[9\]   | `MultiplicativeExpr`              | ::= | `UnaryExpr ( ( "*" \| "div" \| "mod") UnaryExpr )*` |      |
| \[10\]  | `UnaryExpr`                       | ::= | `"-"* UnionExpr`                    |                      |
| \[11\]  | `UnionExpr`                       | ::= | `PathExpr ( "|" PathExpr )*`        |                      |
| \[12\]  | `PathExpr`                        | ::= | `("/" RelativePathExpr?) \| ("//" RelativePathExpr) \| RelativePathExpr` | /* xgs: leading-lone-slash */ |
| \[13\]  | `RelativePathExpr`                | ::= | `StepExpr (("/" \| "//") StepExpr)*` |                     |
| \[14\]  | `StepExpr`                        | ::= | `FilterExpr \| AxisStep`            | /* xgc: filter-expr */ |
| \[15\]  | `AxisStep`                        | ::= | `(ReverseStep \| ForwardStep) PredicateList` |             |
| \[16\]  | `ForwardStep`                     | ::= | `(ForwardAxis NodeTest) \| AbbrevForwardStep` |            |
| \[17\]  | `ForwardAxis`                     | ::= | `("child" "::") \| ("descendant" "::") \| ("attribute" "::") \| ("self" "::") \| ("descendant-or-self" "::") \| ("following-sibling" "::") \| ("following" "::") \| ("namespace" "::")` | |
| \[18\]  | `AbbrevForwardStep`               | ::= | `'@'? NodeTest`                     |                      |
| \[19\]  | `ReverseStep`                     | ::= | `(ReverseAxis NodeTest) \| AbbrevReverseStep` |            |
| \[20\]  | `ReverseAxis`                     | ::= | `("parent" "::") \| ("ancestor" "::") \| ("preceding-sibling" "::") \| ("preceding" "::") \| ("ancestor-or-self" "::")` | |
| \[21\]  | `AbbrevReverseStep`               | ::= | ".."                                | /* xgc: predicate */ |
| \[22\]  | `NodeTest`                        | ::= | `KindTest \| NameTest`              |                      |
| \[23\]  | `NameTest`                        | ::= | `QName \| Wildcard`                 |                      |
| \[24\]  | `Wildcard`                        | ::= | `"*" \| (NCName ":" "*")`           | /* ws: explicit */   |
| \[25\]  | `FilterExpr`                      | ::= | `PrimaryExpr PredicateList`         |                      |
| \[26\]  | `PredicateList`                   | ::= | `Predicate*`                        |                      |
| \[27\]  | `Predicate`                       | ::= | `"[" Expr "]"`                      |                      |
| \[28\]  | `PrimaryExpr`                     | ::= | `Literal \| VarRef \| ParenthesizedExpr \| ContextItemExpr \| FunctionCall` | |
| \[39\]  | `Literal`                         | ::= | `NumericLiteral \| StringLiteral`   |                      |
| \[30\]  | `NumericLiteral`                  | ::= | `IntegerLiteral \| DoubleLiteral`   |                      |
| \[31\]  | `VarRef`                          | ::= | `"$" VarName`                       |                      |
| \[32\]  | `VarName`                         | ::= | `QName`                             |                      |
| \[33\]  | `ParenthesizedExpr`               | ::= | `"(" Expr ")"`                      |                      |
| \[34\]  | `ContextItemExpr`                 | ::= | `"."`                               | /* xgc: predicate */ |
| \[35\]  | `FunctionCall`                    | ::= | `QName "(" ( ExprSingle ( "," ExprSingle )* )? ')'` | /\* xgs: reserved-function-names \*/ /\* gn: parens \*/ |
| \[36\]  | `KindTest`                        | ::= | `PITest \| CommentTest \| TextTest \| AnyKindTest` |       |
| \[37\]  | `AnyKindTest`                     | ::= | `"node" "(" ")"`                    |                      |
| \[38\]  | `TextTest`                        | ::= | `"text" "(" ")"`                    |                      |
| \[39\]  | `CommentTest`                     | ::= | `"comment" "(" ")"`                 |                      |
| \[40\]  | `PITest`                          | ::= | `"processing-instruction" "(" StringLiteral? ")"` |        |

### A.1.1 Extra-grammatical Constraints
This section contains constraints on the EBNF productions in addition to those
defined by XPath 2.0, which are required to parse legal sentences. The notes
below are referenced from the right side of the production, with the notation:
`/* xgc: <id> */`.

__constraint: filter-expr__
> XPath 1.0 only supports `FilterExpr` nodes at the start of a `PathExpr`,
> including leading `/` and `//` tokens. These are allowed anywhere in a `PathExpr`
> in XPath 2.0.

__constraint: predicate__
> XPath 1.0 does not support an `AbbrevReverseStep` or `ContextItemExpr` followed
> by a `Predicate`.

### A.1.2 Grammar Notes
This section contains general notes on the EBNF productions in addition to those
defined by XPath 2.0, which may be helpful in understanding how to interpret and
implement the EBNF. These notes are not normative. The notes below are referenced
from the right side of the production, with the notation: `/* gn: <id> */`.

__grammar-note: comparison__
> These XPath 1.0 symbols are retained here as the XPath 2.0 replacement
> (`ComparisonExpr`) only allows a single `GeneralComp` without an `or` or `and`
> expression.

### A.2 Terminal Symbols

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[41\]  | `StringLiteral`                   | ::= | `('"' \[^"\]* '"') \| ("'" \[^'\]* "'")` | /* ws: explicit */ |
| \[42\]  | `IntegerLiteral`                  | ::= | `Digits`                            |                      |
| \[43\]  | `DecimalLiteral`                  | ::= | `("." Digits) \| (Digits "." \[0-9\]*)` | /* ws: explicit */ |
| \[44\]  | `S`                               | ::= | \[[http://www.w3.org/TR/REC-xml#NT-S]()\]<sup><em>XML</em></sup> | /* xgc: xml-version */ |
| \[45\]  | `QName`                           | ::= | \[[http://www.w3.org/TR/REC-xml-names/#NT-QName]()\]<sup><em>Names</em></sup> | /* xgc: xml-version */ |
| \[46\]  | `NCName`                          | ::= | \[[http://www.w3.org/TR/REC-xml-names/#NT-NCName]()\]<sup><em>Names</em></sup> | /* xgc: xml-version */ |

The following symbols are used only in the definition of terminal symbols; they
are not terminal symbols in the grammar of A.1 EBNF.

| Ref     | Symbol                            |     | Expression                          | Options              |
|---------|-----------------------------------|-----|-------------------------------------|----------------------|
| \[47\]  | `Digits`                          | ::= | `[0-9]+`                            |                      |

### A.3 Reserved Function Names

The following names are not allowed as function names in an unprefixed form
because expression syntax takes precedence.

*  `comment`
*  `node`
*  `processing-instruction`
*  `text`

## B References

### B.1 W3C References
__Core Specifications__
*  W3C. *XML Path Language (XPath) 1.0*. W3C Recommendation 16 November 1999.
   See [https://www.w3.org/TR/1999/REC-xpath-19991116/]().
*  W3C. *XML Path Language (XPath) 2.0*. W3C Recommendation 14 December 2010.
   See [https://www.w3.org/TR/2010/REC-xpath20-20101214/]().

## C Change Log
This section documents the changes from the XPath 1.0 to XPath 2.0 EBNF
grammar.

__Path Expressions__
1. Renamed `Expr` to `ExprSingle`.
1. Added the `XPath` and `Expr` symbols from XPath 2.0.
1. Inlined the `AbbreviatedAbsoluteLocationPath` and `AbsoluteLocationPath` symbols into `LocationPath`.
1. Inlined the `AbbreviatedRelativeLocationPath` symbol into `RelativeLocationPath`.
1. Moved `Predicate*` from `Step` into a `PredicateList` symbol.
1. Moved `FilterExpr` into `Step`, adding a `filter-expr` extra-grammatical constraint.
1. Inlined the `LocationPath` symbol into `PathExpr`.
1. Renamed `RelativeLocationPath` to `RelativePathExpr`.
1. Renamed `Step` to `StepExpr`.

__Axis Steps__
1. Split `AxisName` into `ForwardAxis` and `ReverseAxis`, and combine the keywords with the `::` token.
1. Moved `NodeTest` from `Step` to `AxisSpecifier`.
1. Renamed `AbbreviatedAxisSpecifier` to `AbbrevForwardStep`.
1. Added the `ForwardStep` symbol from XPath 2.0.
1. Moved `ReverseAxis NodeTest` into a `ReverseStep` symbol.
1. Moved `PredicateList` into `AxisSpecifier`.
1. Renamed `AxisSpecifier` to `AxisStep`.
1. Used a `predicate` extra-grammatical constraint to prevent `AbbrevReverseStep`
   symbols followed by a `Predicate` after moving `..` from `AbbreviatedStep` to
   `AbbrevReverseStep`.

__Node Tests__
1. Move the `node` `NodeType` into an `AnyKindTest` symbol.
1. Move the `text` `NodeType` into a `TextTest` symbol.
1. Move the `comment` `NodeType` into a `CommentTest` symbol.
1. Move the `processing-instruction` `NodeType` into a `PITest` symbol.
1. Move the `processing-instruction` with a `StringLiteral` from `NodeTest` into the `PITest` symbol.
1. Move `AnyKindTest`, `TextTest`, `CommentTest`, and `PITest` into a `KindTest` symbol.
1. Split out the wildcard syntax from `NameTest` into a `Wildcard` symbol.

__Filter Expressions__
1. Moved `Predicate*` from `FilterExpr` into a `PredicateList` symbol.
1. Inlined the `PredicateExpr` symbol into `Predicate`.

__Primary Expressions__
1. Renamed `VariableReference` to `VarRef`.
1. Added the `Literal` and `VarName` symbols from XPath 2.0.
1. Moved the parenthesized primary expression into a `ParenthesizedExpr` symbol.
1. Inlined the `Argument` symbol into `FunctionCall`.
1. Used a `predicate` extra-grammatical constraint to prevent `ContextItemExpr`
   symbols followed by a `Predicate` after moving `.` from `AbbreviatedStep` to
   `ContextItemExpr`.

__Terminal Symbols__
1. Renamed `Literal` to `StringLiteral`.
1. Replaced `Number` with `NumericLiteral`, `IntegerLiteral`, and `DecimalLiteral`
   from XPath 2.0.
1. Replaced the `ExprWhitespace` symbol with a link to the `S` symbol from the
   *XML* specification.
1. Added links to the `NCName` and `QName` symbols from the *Namespaces in XML*
   specification.
1. Removed the `ExprToken`, `Operator`, and `OperatorName` symbols that are not
   referenced elsewhere in the XPath 1.0 grammar.
1. Inlined the `MultiplyOperator` symbol into `MultiplicativeExpr`.
1. Replaced the `FunctionName` symbol with a reserved function names section.

### D Differences Between XPath 1.0 and XPath 2.0
The following is the list of features added to XPath 2.0 that are not present
in XPath 1.0:

1. Allow multiple comma separated expressions in `Expr`.
1. Support `ForExpr`, `QuantifiedExpr`, and `IfExpr` in single expressions.
1. Support `ValueComp` and `NodeComp` comparisons.
1. Support range expressions (`RangeExpr`).
1. Support `idiv` in multiplicative expressions (`MultiplicativeExpr`).
1. Support `IntersectExceptExpr`, `InstanceofExpr`, `TreatExpr`, `CastableExpr`,
   and `CastExpr`.
1. Support `+` in unary expressions (`UnaryExpr`).
1. Support `FilterExpr` anywhere in a path expression, not just at the start.
1. Support predicates on `..` steps; use `parent::*` in XPath 1.0.
1. Added support for `DoubleLiteral` in `NumericLiteral`.
1. Made the `Expr` in `ParenthesizedExpr` optional.
1. Added `DocumentTest`, `ElementTest`, `AttributeTest`, `SchemaElementTest`,
   and `SchemaAttributeTest`.
1. Added support for `NCName` in `PITest`.
1. Added support for `*:NCName` wildcards.

The following are differences to XPath 1.0 that change how the expression is
interpreted:

1. Only allow one `GeneralComp` without an `or`/`and` expression.
1. Place `UnionExpr` after `MultiplicativeExpr` instead of `UnaryExpr`.

The following keywords have been added to the *Reserved Function Names* list:

* `attribute`
* `document-node`
* `element`
* `empty-sequence`
* `if`
* `item`
* `schema-attribute`
* `schema-element`
* `typeswitch`

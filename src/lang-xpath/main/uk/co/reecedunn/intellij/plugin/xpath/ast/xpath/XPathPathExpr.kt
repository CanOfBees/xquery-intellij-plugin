/*
 * Copyright (C) 2016, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.ast.xpath

import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression

/**
 * An XPath 2.0 and XQuery 1.0 `PathExpr` node in the XQuery AST.
 *
 * In XQuery 1.0 this is a `ValueExpr`. In XQuery 3.0 this is a
 * child of `SimpleMapExpr`. The XQuery 3.0 layout is used here.
 *
 * The `RelativePathExpr` node is not added to the AST. It is
 * combined with `PathExpr`.
 */
interface XPathPathExpr : XPathRelativePathExpr, XpmExpression

/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
/*
 * XPath and XQuery Type System Part 2: Simple and Complex Types
 *
 * Reference: https://www.w3.org/TR/xpath-datamodel-31
 * Reference: https://www.w3.org/TR/2012/REC-xmlschema11-1-20120405
 */
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.xdm.model.*

val XsAnyType = XmlSchemaType(QName("http://www.w3.org/2001/XMLSchema", "anyType"), null)

val XsUntyped = XdmComplexType(QName("http://www.w3.org/2001/XMLSchema", "untyped"))

val XsAnySimpleType = XdmSimpleType(QName("http://www.w3.org/2001/XMLSchema", "anySimpleType"), XsAnyType)

val XsAnyAtomicType = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "anyAtomicType"), XsAnySimpleType)

val XsNumeric = XdmUnionType(QName("http://www.w3.org/2001/XMLSchema", "numeric"))

val XsIDREFS = XdmListType(QName("http://www.w3.org/2001/XMLSchema", "IDREFS"))

val XsNMTOKENS = XdmListType(QName("http://www.w3.org/2001/XMLSchema", "NMTOKENS"))

val XsENTITIES = XdmListType(QName("http://www.w3.org/2001/XMLSchema", "ENTITIES"))

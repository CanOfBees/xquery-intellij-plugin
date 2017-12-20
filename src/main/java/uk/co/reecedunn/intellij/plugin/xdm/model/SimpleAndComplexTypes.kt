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
package uk.co.reecedunn.intellij.plugin.xdm.model

import uk.co.reecedunn.intellij.plugin.xdm.XsAnySimpleType
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyType

open class XmlSchemaType(val typeName: QName, val baseType: XmlSchemaType?)

open class XdmComplexType(typeName: QName): XmlSchemaType(typeName, XsAnyType)

open class XdmSimpleType(typeName: QName, baseType: XmlSchemaType): XmlSchemaType(typeName, baseType)

open class XdmAtomicType(typeName: QName, baseType: XmlSchemaType): XdmSimpleType(typeName, baseType)

open class XdmListType(typeName: QName): XdmSimpleType(typeName, XsAnySimpleType)

open class XdmUnionType(typeName: QName): XdmSimpleType(typeName, XsAnySimpleType)

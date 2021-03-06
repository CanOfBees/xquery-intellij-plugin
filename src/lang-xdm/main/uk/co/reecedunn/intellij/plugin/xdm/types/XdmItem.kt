/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.types

import com.intellij.psi.PsiElement

// region XQuery and XPath 3.1 Data Model (2.7.4) : item()

interface XdmItem

val XdmItem.element: PsiElement?
    get() = when (this) {
        is XdmElementRef -> element
        is PsiElement -> this
        else -> null
    }

// endregion

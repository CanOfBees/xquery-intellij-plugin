/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package com.intellij.compat.extensions

import com.intellij.compat.serviceContainer.BaseKeyedLazyInstance
import org.picocontainer.PicoContainer

// instantiate is deprecated in IntelliJ 2019.3.
// AbstractExtensionPointBean is deprecated in IntelliJ 2020.2.
@Suppress("unused")
fun <T> BaseKeyedLazyInstance<T>.instantiateBean(className: String, container: PicoContainer): T {
    return this.instantiateClass(className, container)
}

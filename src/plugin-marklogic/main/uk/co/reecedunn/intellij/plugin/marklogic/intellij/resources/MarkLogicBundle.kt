/*
 * Copyright (C) 2016, 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources

import com.intellij.BundleBase
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.SoftReference
import java.util.*

object MarkLogicBundle {
    @NonNls
    private const val PATH_TO_BUNDLE = "messages.MarkLogicBundle"

    private var sBundle: SoftReference<ResourceBundle>? = null
    private val bundle: ResourceBundle
        get() {
            var value = com.intellij.reference.SoftReference.dereference(sBundle)
            if (value == null) {
                // This is called if the bundle has not been loaded, or has been garbage collected.
                value = ResourceBundle.getBundle(PATH_TO_BUNDLE)
                sBundle = SoftReference(value)
            }
            return value!!
        }

    fun message(@PropertyKey(resourceBundle = PATH_TO_BUNDLE) key: String, vararg params: Any): String {
        return BundleBase.message(bundle, key, *params)
    }
}

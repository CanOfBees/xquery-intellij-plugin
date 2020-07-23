/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.intellij.javaee

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider

class MarkLogicResourceProvider : StandardResourceProvider {
    companion object {
        private const val XDMP_NAMESPACE = "http://marklogic.com/xdmp"
    }

    override fun registerResources(registrar: ResourceRegistrar?) {
        registrar!!.addStdResource(XDMP_NAMESPACE, "/schemas/xdmp.xsd", MarkLogicResourceProvider::class.java)
    }
}

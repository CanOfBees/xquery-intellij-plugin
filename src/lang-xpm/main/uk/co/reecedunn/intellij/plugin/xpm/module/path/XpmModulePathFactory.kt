/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module.path

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue

interface XpmModulePathFactory {
    companion object {
        val EP_NAME: ExtensionPointName<XpmModulePathFactoryBean> = ExtensionPointName.create(
            "uk.co.reecedunn.intellij.modulePathFactory"
        )
    }

    fun create(project: Project, uri: XsAnyUriValue): XpmModulePath?
}

fun XsAnyUriValue.paths(project: Project): Sequence<XpmModulePath> {
    return XpmModulePathFactory.EP_NAME.extensionList.asSequence().mapNotNull {
        it.getInstance().create(project, this)
    }
}

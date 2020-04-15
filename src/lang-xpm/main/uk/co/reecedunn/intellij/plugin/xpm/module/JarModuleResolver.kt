/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.JarModuleLoader

abstract class JarModuleResolver : ImportPathResolver {
    abstract val classLoader: ClassLoader

    abstract val modules: Map<String, String>

    private val filesystem by lazy { JarModuleLoader(classLoader) }

    override fun match(path: String): Boolean = modules.containsKey(path)

    override fun resolve(path: String): VirtualFile? = modules[path]?.let { filesystem.findFileByPath(it) }
}

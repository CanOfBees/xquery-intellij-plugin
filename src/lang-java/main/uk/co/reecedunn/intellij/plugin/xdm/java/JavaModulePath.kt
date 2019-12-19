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
package uk.co.reecedunn.intellij.plugin.xdm.java

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePathFactory

/**
 * BaseX, eXist-db, and Saxon allow declaring a namespace to a Java class.
 */
class JavaModulePath internal constructor(
    val project: Project,
    val classPath: String,
    val voidThis: Boolean
) : XdmModulePath {
    override fun resolve(): PsiElement? = JavaTypePath.getInstance(project).findClass(classPath)

    companion object : XdmModulePathFactory {
        private val NOT_JAVA_PATH: Regex = "[/:]".toRegex()

        override fun create(project: Project, uri: XsAnyUriValue): JavaModulePath? {
            return when (uri.context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    val path = uri.data
                    when {
                        path.startsWith("java:") -> {
                            if (path.endsWith("?void=this")) // Saxon 9.7
                                JavaModulePath(project, path.substring(5, path.length - 10), true)
                            else // BaseX, eXist-db, Saxon
                                JavaModulePath(project, path.substring(5), false)
                        }
                        path.contains(NOT_JAVA_PATH) || path.isEmpty() -> null
                        else -> JavaModulePath(project, path, false) // BaseX
                    }
                }
                else -> null
            }
        }
    }
}

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
package uk.co.reecedunn.intellij.plugin.expath.pkg

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.ZipFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriPsiElement
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory

data class EXPathPackage internal constructor(
    private val filesystem: VirtualFileSystem,
    private val root: VirtualFile,
    private val descriptor: XmlDocument
) : XpmModuleLoader {
    // region EXPath package descriptor

    val name: XsAnyUriValue? by lazy {
        descriptor.root.attribute("name")?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE)
        }
    }

    val abbrev: String? by lazy { descriptor.root.attribute("abbrev") }

    val version: String? by lazy { descriptor.root.attribute("version") }

    val spec: String? by lazy { descriptor.root.attribute("spec") }

    val title: String? by lazy { descriptor.root.children("pkg:title").firstOrNull()?.text() }

    val home: XsAnyUriValue? by lazy {
        descriptor.root.children("pkg:home").firstOrNull()?.text()?.let {
            XsAnyUri(it, XdmUriContext.Package, XdmModuleType.NONE)
        }
    }

    val dependencies: List<EXPathPackageDependency> by lazy {
        descriptor.root.children("pkg:dependency").map { EXPathPackageDependency(it) }.toList()
    }

    val components: List<EXPathPackageComponent> by lazy {
        descriptor.root.children().mapNotNull {
            when (it.element.localName) {
                "dtd" -> EXPathPackageDtd(it)
                "nvdl" -> EXPathPackageImport(it, XdmModuleType.NVDL)
                "resource" -> EXPathPackageResource(it)
                "rnc" -> EXPathPackageImport(it, XdmModuleType.RelaxNGCompact)
                "rng" -> EXPathPackageImport(it, XdmModuleType.RelaxNG)
                "schematron" -> EXPathPackageImport(it, XdmModuleType.Schematron)
                "xproc" -> EXPathPackageImport(it, XdmModuleType.XProc)
                "xquery" -> EXPathPackageImport(it, XdmModuleType.XQuery)
                "xsd" -> EXPathPackageImport(it, XdmModuleType.XMLSchema)
                "xslt" -> EXPathPackageImport(it, XdmModuleType.XSLT)
                else -> null
            }
        }.toList()
    }

    fun load(component: EXPathPackageComponent): VirtualFile? {
        val path = component.file?.let { "/$it" } ?: return null
        return load(path, root)
    }

    private fun load(path: String, file: VirtualFile): VirtualFile? {
        if (file.path.endsWith(path)) return file
        return file.children.asSequence().map { load(path, it) }.filterNotNull().firstOrNull()
    }

    // endregion
    // region XpmModuleLoader

    override fun resolve(path: XpmModulePath, context: VirtualFile?): PsiElement? = when (path) {
        is XsAnyUriPsiElement -> {
            val component = components.find {
                path.moduleTypes.contains(it.moduleType) && when (it) {
                    is EXPathPackageImport -> path.data == it.importUri?.data
                    else -> false
                }
            }
            component?.let { load(it)?.toPsiFile(path.project) }
        }
        else -> null
    }

    override fun context(path: XpmModulePath, context: VirtualFile?): XpmStaticContext? {
        return resolve(path, context) as? XpmStaticContext
    }

    override fun relativePathTo(file: VirtualFile, project: Project): String? = null

    // endregion
    // region XpmModuleLoaderFactory

    companion object : XpmModuleLoaderFactory {
        private val NAMESPACES = mapOf("pkg" to "http://expath.org/ns/pkg")

        private const val DESCRIPTOR_FILE = "expath-pkg.xml"

        @Suppress("MemberVisibilityCanBePrivate")
        fun create(pkg: VirtualFile): EXPathPackage = when (pkg.isDirectory) {
            true -> {
                val descriptor = pkg.findFileByRelativePath(DESCRIPTOR_FILE)?.let { XmlDocument.parse(it, NAMESPACES) }
                    ?: throw EXPathPackageMissingDescriptor()
                EXPathPackage(pkg.fileSystem, pkg, descriptor)
            }
            else -> create(ZipFileSystem(pkg))
        }

        fun create(pkg: ByteArray): EXPathPackage = create(ZipFileSystem(pkg))

        private fun create(pkg: ZipFileSystem): EXPathPackage {
            val descriptor = pkg.findFileByPath(DESCRIPTOR_FILE)?.let { XmlDocument.parse(it, NAMESPACES) }
                ?: throw EXPathPackageMissingDescriptor()
            return EXPathPackage(pkg, pkg.findFileByPath("")!!, descriptor)
        }

        override fun loader(context: String?): XpmModuleLoader? {
            val file = context?.let { LocalFileSystem.getInstance().findFileByPath(context) } ?: return null
            return create(file)
        }
    }

    // endregion
}

class EXPathPackageMissingDescriptor : Exception("Cannot find expath-pkg.xml in the EXPath package.")

/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.openapi.roots.ExcludeFolder
import com.intellij.openapi.roots.ModuleRootModel
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType
import com.intellij.openapi.roots.ContentEntry

@Suppress("NonExtendableApiUsage")
class MockContentEntry(private val file: VirtualFile) : ContentEntry {
    override fun setExcludePatterns(patterns: MutableList<String>): Unit = TODO()

    override fun getUrl(): String = TODO()

    override fun getExcludeFolders(): Array<ExcludeFolder> = TODO()

    override fun addExcludePattern(pattern: String): Unit = TODO()

    override fun getFile(): VirtualFile? = TODO()

    override fun removeExcludeFolder(excludeFolder: ExcludeFolder): Unit = TODO()

    override fun removeExcludeFolder(url: String): Boolean = TODO()

    override fun getExcludeFolderFiles(): Array<VirtualFile> = TODO()

    override fun getSourceFolderFiles(): Array<VirtualFile> = TODO()

    override fun clearExcludeFolders(): Unit = TODO()

    override fun removeExcludePattern(pattern: String): Unit = TODO()

    override fun getExcludePatterns(): MutableList<String> = TODO()

    override fun addSourceFolder(file: VirtualFile, isTestSource: Boolean): SourceFolder = TODO()

    override fun addSourceFolder(file: VirtualFile, isTestSource: Boolean, packagePrefix: String): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(
        file: VirtualFile,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(file: VirtualFile, type: JpsModuleSourceRootType<P>): SourceFolder {
        TODO()
    }

    override fun addSourceFolder(url: String, isTestSource: Boolean): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(url: String, type: JpsModuleSourceRootType<P>): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder = TODO()

    override fun clearSourceFolders(): Unit = TODO()

    override fun getSourceFolders(): Array<SourceFolder> = arrayOf(MockSourceFolder(file, JavaSourceRootType.SOURCE))

    override fun getSourceFolders(rootType: JpsModuleSourceRootType<*>): MutableList<SourceFolder> = TODO()

    override fun getSourceFolders(rootTypes: MutableSet<out JpsModuleSourceRootType<*>>): MutableList<SourceFolder> {
        TODO()
    }

    override fun getExcludeFolderUrls(): MutableList<String> = TODO()

    override fun addExcludeFolder(file: VirtualFile): ExcludeFolder = TODO()

    override fun addExcludeFolder(url: String): ExcludeFolder = TODO()

    override fun isSynthetic(): Boolean = TODO()

    override fun removeSourceFolder(sourceFolder: SourceFolder): Unit = TODO()

    override fun getRootModel(): ModuleRootModel = TODO()
}

/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.vfs

import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import java.io.FileNotFoundException
import java.io.IOException

@DisplayName("IntelliJ - Base Platform - Files - Virtual File System - ResourceVirtualFile")
class ResourceVirtualFileTest {
    private fun createFile(path: String): VirtualFile {
        return ResourceVirtualFile.create(this::class.java.classLoader, path, TestVirtualFileSystem)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("resource file; valid path")
    fun testFileSystem_CreatingFile() {
        val file = createFile("vfs-test/vfs/test.xq")
        file.charset = CharsetToolkit.UTF8_CHARSET

        assertThat(file.name, `is`("test.xq"))
        assertThat(file.path, anyOf(endsWith("/vfs-test/vfs/test.xq"), endsWith("\\vfs-test\\vfs\\test.xq")))
        assertThat(file.isWritable, `is`(false))
        assertThat(file.isDirectory, `is`(false))
        assertThat(file.isValid, `is`(true))
        assertThat(file.length, `is`(28L))
        assertThat(file.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(file.decode(), `is`("xquery version \"3.0\"; true()"))
        assertThat(file.contentsToByteArray(), `is`("xquery version \"3.0\"; true()".toByteArray()))
        assertThat(file.modificationStamp, `is`(0L))

        val parent = file.parent!!
        assertThat(parent.name, `is`("vfs"))
        assertThat(parent.path, anyOf(endsWith("/vfs-test/vfs"), endsWith("\\vfs-test\\vfs")))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.isValid, `is`(true))
        assertThat(parent.length, `is`(0L))
        assertThat(parent.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(parent.modificationStamp, `is`(0L))

        assertThat(parent.parent, `is`(notNullValue()))

        val children = file.children
        assertThat(children, `is`(nullValue()))
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("resource file; invalid path")
    fun testFileSystem_InvalidFilePath() {
        val file = createFile("vfs-test/vfs/test.xqy")
        assertThat(file.name, `is`("test.xqy"))
        assertThat(file.path, `is`(""))
        assertThat(file.isWritable, `is`(false))
        assertThat(file.isDirectory, `is`(false))
        assertThat(file.isValid, `is`(false))
        assertThat(file.length, `is`(0L))
        assertThrows<FileNotFoundException> { file.inputStream }
        assertThat(file.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(file.modificationStamp, `is`(0L))

        val parent = file.parent!!
        assertThat(parent.name, `is`("vfs"))
        assertThat(parent.path, anyOf(endsWith("/vfs-test/vfs"), endsWith("\\vfs-test\\vfs")))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.isValid, `is`(true))
        assertThat(parent.length, `is`(0L))
        assertThat(parent.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(parent.modificationStamp, `is`(0L))

        assertThat(parent.parent, `is`(notNullValue()))

        val children = file.children
        assertThat(children, `is`(nullValue()))
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("resource directory")
    fun testFileSystem_Directory() {
        val file = createFile("vfs-test/vfs")
        assertThat(file.name, `is`("vfs"))
        assertThat(file.path, anyOf(endsWith("/vfs-test/vfs"), endsWith("\\vfs-test\\vfs")))
        assertThat(file.isWritable, `is`(false))
        assertThat(file.isDirectory, `is`(true))
        assertThat(file.isValid, `is`(true))
        assertThat(file.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { file.inputStream }
        assertThat(file.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(file.modificationStamp, `is`(0L))

        val parent = file.parent!!
        assertThat(parent.name, `is`("vfs-test"))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.isValid, `is`(true))
        assertThat(parent.length, `is`(0L))
        assertThat(parent.fileSystem, instanceOf(TestVirtualFileSystem::class.java))
        assertThat(parent.modificationStamp, `is`(0L))

        assertThat(parent.parent, `is`(nullValue()))

        val children = file.children!!
        assertThat(children.size, `is`(1))
        assertThat(children[0].name, `is`("test.xq"))
        assertThat(children[0].path, anyOf(endsWith("/vfs-test/vfs/test.xq"), endsWith("\\vfs-test\\vfs\\test.xq")))
        assertThat(children[0].length, `is`(28L))
        assertThat(children[0].fileSystem, instanceOf(TestVirtualFileSystem::class.java))
    }
}

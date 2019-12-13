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
package uk.co.reecedunn.intellij.plugin.xdm.tests.java

import com.intellij.mock.MockProjectEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.*
import uk.co.reecedunn.compat.testFramework.PlatformLiteFixture
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.java.JavaModulePath
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("Modules - Java Paths")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class JavaModulePathTest : PlatformLiteFixture() {
    private var project: Project? = null

    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()
        project = MockProjectEx(testRootDisposable)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        val uri = XsAnyUri("", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        val uri = XsAnyUri("http://www.example.com/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("HTTPS scheme URL")
    fun httpsScheme() {
        val uri = XsAnyUri("https://www.example.com/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        val uri = XsAnyUri("file:///C:/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("URN scheme")
    fun urnScheme() {
        val uri = XsAnyUri("urn:lorem:ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("Java scheme")
    fun javaScheme() {
        val uri = XsAnyUri("java:java.lang.String", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)!!
        assertThat(path.classPath, `is`("java.lang.String"))
    }

    @Nested
    internal inner class RelativePath {
        @Test
        @DisplayName("relative path")
        fun relativePath() {
            val uri = XsAnyUri("lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xq' extension")
        fun xq() {
            val uri = XsAnyUri("test.xq", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqy' extension")
        fun xqy() {
            val uri = XsAnyUri("test.xqy", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xquery' extension")
        fun xquery() {
            val uri = XsAnyUri("test.xquery", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqu' extension")
        fun xqu() {
            val uri = XsAnyUri("test.xqu", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xql' extension")
        fun xql() {
            val uri = XsAnyUri("test.xql", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqm' extension")
        fun xqm() {
            val uri = XsAnyUri("test.xqm", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqws' extension")
        fun xqws() {
            val uri = XsAnyUri("test.xqws", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Stylesheet file, 'xsl' extension")
        fun xsl() {
            val uri = XsAnyUri("test.xsl", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Stylesheet file, 'xslt' extension")
        fun xslt() {
            val uri = XsAnyUri("test.xslt", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Schema file, 'xsd' extension")
        fun xsd() {
            val uri = XsAnyUri("test.xsd", XdmUriContext.TargetNamespace, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        val uri = XsAnyUri("java.lang.String", XdmUriContext.TargetNamespace, null as PsiElement?)
        val path = JavaModulePath.create(project!!, uri)!!
        assertThat(path.classPath, `is`("java.lang.String"))
    }
}

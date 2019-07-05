/*
 * Copyright (C) 2016-2019 Reece H. Dunn
 * Copyright 2000-2016 JetBrains s.r.o.
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
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.ide.startup.impl.StartupManagerImpl
import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.mock.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.PomModel
import com.intellij.pom.core.impl.PomModelImpl
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.*
import com.intellij.psi.impl.PsiCachedValuesFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistryImpl
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.CachedValuesManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.CachedValuesManagerImpl
import com.intellij.util.messages.MessageBus
import org.jetbrains.annotations.NonNls
import org.picocontainer.PicoContainer
import org.picocontainer.PicoInitializationException
import org.picocontainer.PicoIntrospectionException
import org.picocontainer.defaults.AbstractComponentAdapter
import uk.co.reecedunn.compat.testFramework.PlatformLiteFixture
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

// NOTE: The IntelliJ ParsingTextCase implementation does not make it easy to
// customise the mock implementation, making it difficult to implement some tests.
abstract class ParsingTestCase<File : PsiFile>(
    private var mFileExt: String?,
    vararg definitions: ParserDefinition
) : PlatformLiteFixture() {
    constructor(fileExt: String?, language: Language) : this(fileExt) {
        this.language = language
    }

    private var mFileFactory: PsiFileFactory? = null

    var language: Language? = null
        private set

    private val mDefinitions: Array<out ParserDefinition> = definitions

    override fun setUp() {
        super.setUp()

        // IntelliJ ParsingTestCase setUp
        initApplication()
        val component =
            PlatformLiteFixture.getApplication().picoContainer.getComponentAdapter(ProgressManager::class.java.name)
        if (component == null) {
            PlatformLiteFixture.getApplication().picoContainer.registerComponent(object :
                AbstractComponentAdapter(ProgressManager::class.java.name, Any::class.java) {

                @Throws(PicoInitializationException::class, PicoIntrospectionException::class)
                override fun getComponentInstance(container: PicoContainer): Any {
                    return ProgressManagerImpl()
                }

                @Throws(PicoIntrospectionException::class)
                override fun verify(container: PicoContainer) {
                }
            })
        }
        Extensions.registerAreaClass("IDEA_PROJECT", null)
        myProject = MockProjectEx(testRootDisposable)
        val psiManager = MockPsiManager(myProject)
        mFileFactory = PsiFileFactoryImpl(psiManager)
        val appContainer = PlatformLiteFixture.getApplication().picoContainer
        PlatformLiteFixture.registerComponentInstance(
            appContainer, MessageBus::class.java, PlatformLiteFixture.getApplication().messageBus
        )
        val editorFactory = MockEditorFactory()
        PlatformLiteFixture.registerComponentInstance(appContainer, EditorFactory::class.java, editorFactory)
        PlatformLiteFixture.registerComponentInstance(
            appContainer, FileDocumentManager::class.java,
            MockFileDocumentManagerImpl(
                { editorFactory.createDocument(it) }, FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY
            )
        )
        PlatformLiteFixture.registerComponentInstance(
            appContainer, PsiDocumentManager::class.java, MockPsiDocumentManagerEx()
        )

        registerApplicationService(PsiBuilderFactory::class.java, PsiBuilderFactoryImpl())
        registerApplicationService(DefaultASTFactory::class.java, DefaultASTFactoryImpl())
        registerApplicationService(ReferenceProvidersRegistry::class.java, ReferenceProvidersRegistryImpl())
        myProject.registerService(
            CachedValuesManager::class.java, CachedValuesManagerImpl(myProject, PsiCachedValuesFactory(psiManager))
        )
        myProject.registerService(PsiManager::class.java, psiManager)
        myProject.registerService(PsiFileFactory::class.java, mFileFactory!!)
        myProject.registerService(StartupManager::class.java, StartupManagerImpl(myProject))
        registerExtensionPoint(FileTypeFactory.FILE_TYPE_FACTORY_EP, FileTypeFactory::class.java)
        registerExtensionPoint("com.intellij.lang.MetaLanguage", "EP_NAME")

        for (definition in mDefinitions) {
            addExplicitExtension(LanguageParserDefinitions.INSTANCE, definition.fileNodeType.language, definition)
        }

        if (mDefinitions.isNotEmpty()) {
            language = mDefinitions[0].fileNodeType.language
            addExplicitExtension(LanguageParserDefinitions.INSTANCE, language!!, mDefinitions[0])
        }

        if (language != null) {
            PlatformLiteFixture.registerComponentInstance(
                PlatformLiteFixture.getApplication().picoContainer,
                FileTypeManager::class.java,
                MockFileTypeManager(MockLanguageFileType(language!!, mFileExt))
            )
        }

        // That's for reparse routines
        val pomModel = PomModelImpl(myProject)
        myProject.registerService(PomModel::class.java, pomModel)
        TreeAspect(pomModel)
    }

    // region IntelliJ ParsingTestCase Methods

    protected fun <T> addExplicitExtension(instance: LanguageExtension<T>, language: Language, `object`: T) {
        instance.addExplicitExtension(language, `object`)
        Disposer.register(myProject, com.intellij.openapi.Disposable {
            instance.removeExplicitExtension(language, `object`)
        })
    }

    override fun <T> registerExtensionPoint(extensionPointName: ExtensionPointName<T>, aClass: Class<T>) {
        super.registerExtensionPoint(extensionPointName, aClass)
        Disposer.register(myProject, com.intellij.openapi.Disposable {
            Extensions.getRootArea().unregisterExtensionPoint(extensionPointName.name)
        })
    }

    private fun registerExtensionPoint(epClassName: String, epField: String) {
        try {
            val epClass = Class.forName(epClassName)
            val epname = epClass.getField(epField)
            val register = ParsingTestCase::class.java.getDeclaredMethod(
                "registerExtensionPoint", ExtensionPointName::class.java, Class::class.java
            )
            register.invoke(this, epname.get(null), epClass)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun registerApplicationService(className: String) {
        try {
            val aClass = Class.forName(className) as Class<Any>
            val `object` = aClass.newInstance()
            registerApplicationService(aClass, `object`)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
        }

    }

    fun createVirtualFile(@NonNls name: String, text: String): VirtualFile {
        val file = LightVirtualFile(name, language!!, text)
        file.charset = CharsetToolkit.UTF8_CHARSET
        return file
    }

    fun getFileViewProvider(project: Project, file: VirtualFile, physical: Boolean): FileViewProvider {
        val manager = PsiManager.getInstance(project)
        val factory = LanguageFileViewProviders.INSTANCE.forLanguage(language!!)
        var viewProvider: FileViewProvider? = factory?.createFileViewProvider(file, language, manager, physical)
        if (viewProvider == null) viewProvider = SingleRootFileViewProvider(manager, file, physical)
        return viewProvider
    }

    fun prettyPrintASTNode(file: File): String = file.toPsiTreeString()

    @Suppress("UNCHECKED_CAST")
    fun parseText(text: String): File {
        return createVirtualFile("testcase.xqy", text).toPsiFile(myProject)!!
    }

    protected inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery).walkTree().filterIsInstance<T>().toList()
    }

    fun getDocument(file: PsiFile): Document {
        return PsiDocumentManager.getInstance(myProject).getDocument(file)!!
    }

    fun completion(text: String, completionPoint: String = "completion-point"): PsiElement {
        return parse<LeafPsiElement>(text).find { it.text == completionPoint }!!
    }
}

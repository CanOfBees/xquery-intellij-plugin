/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryPluginBundle
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

class XQST0031 : Inspection("xqst/XQST0031.md", XQST0031::class.java.classLoader) {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val descriptors = SmartList<ProblemDescriptor>()
        var mainVersion: Specification? = null
        var isFirstVersion = true
        file.XQueryVersions.forEach(fun(version) {
            if (isFirstVersion) {
                mainVersion = version.getVersionOrDefault(file.project)
            }

            if (version.version == null && version.declaration == null)
                return

            if (version.version == null) {
                // Unrecognised XQuery version string.
                val description = XQueryPluginBundle.message("inspection.XQST0031.unsupported-version.message")
                descriptors.add(
                    manager.createProblemDescriptor(
                        version.declaration as PsiElement,
                        description,
                        null as LocalQuickFix?,
                        ProblemHighlightType.GENERIC_ERROR,
                        isOnTheFly
                    )
                )
                return
            }

            val xqueryVersion =
                XQuerySpec.versionForXQuery(settings.product, settings.productVersion, version.version!!.versionId)
            if (xqueryVersion == null) {
                // The XQuery version is not supported by the implementation.
                val description = XQueryPluginBundle.message("inspection.XQST0031.unsupported-version.message")
                descriptors.add(
                    manager.createProblemDescriptor(
                        version.declaration as PsiElement,
                        description,
                        null as LocalQuickFix?,
                        ProblemHighlightType.GENERIC_ERROR,
                        isOnTheFly
                    )
                )
                return
            }

            if (!isFirstVersion && mainVersion != xqueryVersion) {
                val description =
                    XQueryPluginBundle.message("inspection.XQST0031.unsupported-version.different-version-for-transaction")
                descriptors.add(
                    manager.createProblemDescriptor(
                        version.declaration as PsiElement,
                        description,
                        null as LocalQuickFix?,
                        ProblemHighlightType.GENERIC_ERROR,
                        isOnTheFly
                    )
                )
            }

            isFirstVersion = false
        })
        return descriptors.toTypedArray()
    }
}

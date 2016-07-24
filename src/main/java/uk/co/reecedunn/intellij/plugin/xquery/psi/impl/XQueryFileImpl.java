/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryStringLiteral;
import uk.co.reecedunn.intellij.plugin.xquery.ast.XQueryVersionDecl;
import uk.co.reecedunn.intellij.plugin.xquery.filetypes.XQueryFileType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;

public class XQueryFileImpl extends PsiFileBase implements XQueryFile {
    public XQueryFileImpl(@NotNull FileViewProvider provider) {
        super(provider, XQuery.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return XQueryFileType.INSTANCE;
    }

    public XQueryVersion getXQueryVersion() {
        XQueryVersionDecl versionDecl = findChildByClass(XQueryVersionDecl.class);
        if (versionDecl != null) {
            XQueryStringLiteral version = versionDecl.getVersion();
            if (version != null) {
                XQueryVersion xqueryVersion = XQueryVersion.parse(version.getSimpleContents());
                if (xqueryVersion != null) {
                    return xqueryVersion;
                }
            }
        }

        XQueryProjectSettings settings = XQueryProjectSettings.getInstance(getProject());
        return settings.getXQueryVersion();
    }
}

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
package uk.co.reecedunn.intellij.plugin.core.tests.psi;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class MockPsiManager extends com.intellij.mock.MockPsiManager {
    public MockPsiManager(@NotNull Project project) {
        super(project);
    }

    private String streamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtil.copyCompletely(new InputStreamReader(stream), writer);
        return writer.toString();
    }

    @Override
    public PsiFile findFile(@NotNull VirtualFile file) {
        try {
            Language language = LanguageUtil.getLanguageForPsi(getProject(), file);
            if (language == null) {
                return null;
            }

            String content = streamToString(file.getInputStream());
            return PsiFileFactory.getInstance(getProject()).createFileFromText(file.getName(), language, content, true, false, false, file);
        } catch (IOException e) {
            return null;
        }
    }
}

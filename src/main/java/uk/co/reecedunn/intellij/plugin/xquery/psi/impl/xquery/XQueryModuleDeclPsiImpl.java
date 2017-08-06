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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.core.functional.Option;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog;
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver;

import static uk.co.reecedunn.intellij.plugin.core.functional.PsiTreeWalker.children;

public class XQueryModuleDeclPsiImpl extends ASTWrapperPsiElement implements XQueryModuleDecl, XQueryNamespaceResolver, XQueryPrologResolver {
    public XQueryModuleDeclPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Option<XQueryNamespace> getNamespace() {
        return children(this).findFirst(XQueryNCName.class).flatMap((name) ->
            Option.of(name.getLocalName()).flatMap((localName) -> {
                PsiElement element = findChildByType(XQueryElementType.URI_LITERAL);
                return Option.some(new XQueryNamespace(localName, element, this));
            })
        );
    }

    @Override
    public Option<XQueryNamespace> resolveNamespace(CharSequence prefix) {
        return getNamespace().filter((ns) -> ns.getPrefix().getText().equals(prefix));
    }

    @Override
    public XQueryProlog resolveProlog() {
        return children(this).findFirst(XQueryProlog.class).getOrElse(null);
    }
}

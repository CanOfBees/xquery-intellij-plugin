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
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryForwardAxis;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryLanguageType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVersionedConstruct;

public class XQueryForwardAxisPsiImpl extends ASTWrapperPsiElement implements XQueryForwardAxis, XQueryVersionedConstruct {
    private TokenSet MARKLOGIC_AXIS = TokenSet.create(XQueryTokenType.K_NAMESPACE, XQueryTokenType.K_PROPERTY);

    public XQueryForwardAxisPsiImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public XQueryVersion getLanguageTypeVersion(XQueryLanguageType type) {
        if (type == XQueryLanguageType.XQUERY) {
            final ASTNode node = getNode().findChildByType(MARKLOGIC_AXIS);
            return node == null ? XQueryVersion.VERSION_1_0 : null;
        } else if (type == XQueryLanguageType.MARKLOGIC_EXTENSION) {
            final ASTNode node = getNode().findChildByType(MARKLOGIC_AXIS);
            return node == null ? null : XQueryVersion.VERSION_6_0;
        }
        return null;
    }

    @Override
    public PsiElement getLanguageTypeElement(XQueryLanguageType type) {
        if (type == XQueryLanguageType.MARKLOGIC_EXTENSION) {
            final ASTNode node = getNode().findChildByType(MARKLOGIC_AXIS);
            return node == null ? null : node.getPsi();
        }
        return null;
    }
}

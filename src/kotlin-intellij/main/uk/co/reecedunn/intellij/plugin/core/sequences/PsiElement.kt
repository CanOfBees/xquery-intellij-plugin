/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.sequences

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import java.util.*

private class PsiElementTreeIterator(node: PsiElement?) : Iterator<PsiElement> {
    private val stack: Stack<PsiElement> = Stack()

    init {
        stack.push(node)
    }

    override fun hasNext(): Boolean = !stack.isEmpty()

    override fun next(): PsiElement {
        val current = stack.pop()

        // An element (file) with a directory as a parent may be part of a
        // resource JAR file. This can cause the file to have a different
        // object location to the child in the parent directory, which will
        // result in nextSibling logging a "Cannot find element among its
        // parent' children." error.
        if (current?.parent !is PsiDirectory) {
            val right = current?.nextSibling
            if (right != null) {
                stack.push(right)
            }
        }

        val left = current?.firstChild
        if (left != null) {
            stack.push(left)
        }

        return current
    }
}

private class PsiElementIterator(
    private var node: PsiElement?,
    private var walk: (PsiElement) -> PsiElement?
) : Iterator<PsiElement> {
    override fun hasNext(): Boolean = node != null

    override fun next(): PsiElement {
        val ret = node!!
        node = walk(ret)
        return ret
    }
}

class PsiElementReversibleSequence(
    private var node: PsiElement,
    private var gen: (PsiElement) -> Iterator<PsiElement>,
    private var rgen: (PsiElement) -> Iterator<PsiElement>
) : Sequence<PsiElement> {
    override fun iterator(): Iterator<PsiElement> = gen(node)

    fun reversed(): PsiElementReversibleSequence {
        return PsiElementReversibleSequence(node, rgen, gen)
    }
}

fun PsiElement.ancestors(): Sequence<PsiElement> {
    return PsiElementIterator(parent, PsiElement::getParent).asSequence()
}

fun PsiElement.ancestorsAndSelf(): Sequence<PsiElement> {
    return PsiElementIterator(this, PsiElement::getParent).asSequence()
}

fun PsiElement.descendants(): Sequence<PsiElement> {
    return PsiElementIterator(firstChild, PsiElement::getFirstChild).asSequence()
}

fun PsiElement.children(): PsiElementReversibleSequence {
    return PsiElementReversibleSequence(this,
        { e ->
            PsiElementIterator(
                e.firstChild,
                PsiElement::getNextSibling
            )
        },
        { e ->
            PsiElementIterator(
                e.lastChild,
                PsiElement::getPrevSibling
            )
        })
}

fun PsiElement.siblings(): PsiElementReversibleSequence {
    return PsiElementReversibleSequence(this,
        { e -> PsiElementIterator(e.nextSibling, PsiElement::getNextSibling) },
        { e -> PsiElementIterator(e.prevSibling, PsiElement::getPrevSibling) }
    )
}

fun PsiElement.walkTree(): PsiElementReversibleSequence {
    return PsiElementReversibleSequence(this,
        { element -> PsiElementTreeIterator(element) },
        { element ->
            PsiElementIterator(element) { e ->
                val parent = e.parent
                // An element (file) with a directory as a parent may be part of a
                // resource JAR file. This can cause the file to have a different
                // object location to the child in the parent directory, which will
                // result in prevSibling logging a "Cannot find element among its
                // parent' children." error.
                (if (parent is PsiDirectory) null else e.prevSibling) ?: parent
            }
        })
}

fun Sequence<PsiElement>.filterIsElementType(elementType: IElementType): Sequence<PsiElement> {
    return filter { it.elementType === elementType }
}

fun Sequence<PsiElement>.filterIsElementType(tokens: TokenSet): Sequence<PsiElement> {
    return filter { tokens.contains(it.elementType) }
}

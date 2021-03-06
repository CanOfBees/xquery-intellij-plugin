/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.processor.query.ProcessTerminatedException
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans.XPathException
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans.toXPathException
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.debugger.SaxonStackFrame
import java.lang.reflect.InvocationTargetException

private const val ERR_NS = "http://www.w3.org/2005/xqt-errors"

internal fun <T> check(
    queryFile: VirtualFile?,
    classLoader: ClassLoader,
    listener: SaxonErrorListener?,
    f: () -> T
): T {
    return try {
        f()
    } catch (e: InvocationTargetException) {
        throw if (listener?.fatalError != null)
            listener.fatalError!!
        else {
            val target = e.targetException
            when {
                target is ProcessTerminatedException -> target
                target.cause is ProcessTerminatedException -> target.cause!!
                else -> target.run { toXPathException(classLoader)?.toSaxonQueryError(queryFile) ?: this }
            }
        }
    }
}

internal fun <T> check(queryFile: VirtualFile?, classLoader: ClassLoader, f: () -> T): T =
    check(queryFile, classLoader, null, f)

internal fun XPathException.toSaxonQueryError(queryFile: VirtualFile?): QueryError {
    val qname = getErrorCodeQName()
    val ns = qname?.getNamespaceURI()
    val prefix = qname?.getPrefix().nullize()
    val localname = qname?.getLocalName()?.nullize() ?: "FOER0000"
    return QueryError(
        standardCode = if (ns == ERR_NS || prefix == null) localname else "$prefix:$localname",
        vendorCode = null,
        description = message,
        value = listOf(),
        frames = queryFile?.let { listOf(SaxonStackFrame.create(locator!!, null, null, it)) } ?: listOf()
    )
}

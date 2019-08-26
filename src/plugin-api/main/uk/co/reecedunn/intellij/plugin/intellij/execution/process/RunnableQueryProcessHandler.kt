/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.process

import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery

class RunnableQueryProcessHandler(private val query: RunnableQuery) : QueryProcessHandlerBase() {
    override fun startNotify() {
        super.startNotify()
        try {
            notifyBeginResults()
            pooled_thread { query.run() }.execute { results ->
                try {
                    results.results.forEach { result -> notifyResult(result) }
                    notifyEndResults()
                    notifyResultTime(QueryResultTime.Elapsed, results.elapsed)
                } catch (e: Throwable) {
                    notifyEndResults()
                    notifyException(e)
                } finally {
                    notifyProcessDetached()
                }
            }.onException { e ->
                notifyEndResults()
                notifyException(e)
                notifyProcessDetached()
            }
        } catch (e: Throwable) {
            notifyEndResults()
            notifyException(e)
            notifyProcessDetached()
        }
    }
}

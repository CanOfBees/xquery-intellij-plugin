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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.profile

import org.hamcrest.CoreMatchers.`is`
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.marklogic.profile.toMarkLogicProfileReport
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDecimal
import uk.co.reecedunn.intellij.plugin.xpath.model.XsInteger

@Suppress("XmlPathReference")
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Profiler - MarkLogicProfile")
class MarkLogicProfileTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        @Language("xml")
        val profile = """
            <prof:report xsi:schemaLocation="http://marklogic.com/xdmp/profile profile.xsd" xmlns:prof="http://marklogic.com/xdmp/profile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <prof:metadata>
                    <prof:overall-elapsed>PT0.0000564S</prof:overall-elapsed>
                    <prof:created>2019-01-03T09:44:37.9608193Z</prof:created>
                    <prof:server-version>9.0-5</prof:server-version>
                </prof:metadata>
                <prof:histogram/>
            </prof:report>
        """

        val p = profile.toMarkLogicProfileReport(DatabaseModule("test.xqy"))
        assertThat(p.elapsed.months, `is`(XsInteger.ZERO))
        assertThat(p.elapsed.seconds, `is`(XsDecimal("0.0000564".toBigDecimal())))
        assertThat(p.created, `is`("2019-01-03T09:44:37.9608193Z"))
        assertThat(p.version, `is`("9.0-5"))

        val results = p.results.toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].id, `is`(""))
        assertThat(results[0].context, `is`(""))
        assertThat(results[0].frame.module, `is`(DatabaseModule("test.xqy")))
        assertThat(results[0].frame.lineNumber, `is`(1))
        assertThat(results[0].frame.columnNumber, `is`(1))
        assertThat(results[0].count, `is`(1))
        assertThat(results[0].selfTime.months, `is`(XsInteger.ZERO))
        assertThat(results[0].selfTime.seconds, `is`(XsDecimal.ZERO))
        assertThat(results[0].totalTime.months, `is`(XsInteger.ZERO))
        assertThat(results[0].totalTime.seconds, `is`(XsDecimal("0.0000564".toBigDecimal())))
    }

    @Test
    @DisplayName("results; no uri")
    fun results_noUri() {
        @Language("xml")
        val profile = """
            <prof:report xsi:schemaLocation="http://marklogic.com/xdmp/profile profile.xsd" xmlns:prof="http://marklogic.com/xdmp/profile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <prof:metadata>
                    <prof:overall-elapsed>PT0.0000435S</prof:overall-elapsed>
                    <prof:created>2019-01-03T10:50:34.2913686Z</prof:created>
                    <prof:server-version>9.0-5</prof:server-version>
                </prof:metadata>
                <prof:histogram>
                    <prof:expression>
                        <prof:expr-id>7399381704112208326</prof:expr-id>
                        <prof:expr-source>for ${'$'}x in 1 to 10 return ${'$'}x</prof:expr-source>
                        <prof:uri/>
                        <prof:line>1</prof:line>
                        <prof:column>0</prof:column>
                        <prof:count>1</prof:count>
                        <prof:shallow-time>PT0.0000051S</prof:shallow-time>
                        <prof:deep-time>PT0.0000064S</prof:deep-time>
                    </prof:expression>
                    <prof:expression>
                        <prof:expr-id>16683152708792260640</prof:expr-id>
                        <prof:expr-source>1 to 10</prof:expr-source>
                        <prof:uri/>
                        <prof:line>1</prof:line>
                        <prof:column>12</prof:column>
                        <prof:count>2</prof:count>
                        <prof:shallow-time>PT0.0000013S</prof:shallow-time>
                        <prof:deep-time>PT0.0000014S</prof:deep-time>
                    </prof:expression>
                </prof:histogram>
            </prof:report>
        """

        val p = profile.toMarkLogicProfileReport(DatabaseModule("test.xqy"))
        assertThat(p.elapsed.months, `is`(XsInteger.ZERO))
        assertThat(p.elapsed.seconds, `is`(XsDecimal("0.0000435".toBigDecimal())))
        assertThat(p.created, `is`("2019-01-03T10:50:34.2913686Z"))
        assertThat(p.version, `is`("9.0-5"))

        val results = p.results.toList()
        assertThat(results.size, `is`(3))

        assertThat(results[2].id, `is`("16683152708792260640"))
        assertThat(results[2].context, `is`("1 to 10"))
        assertThat(results[2].frame.module, `is`(DatabaseModule("test.xqy")))
        assertThat(results[2].frame.lineNumber, `is`(1))
        assertThat(results[2].frame.columnNumber, `is`(13))
        assertThat(results[2].count, `is`(2))
        assertThat(results[2].selfTime.months, `is`(XsInteger.ZERO))
        assertThat(results[2].selfTime.seconds, `is`(XsDecimal("0.0000013".toBigDecimal())))
        assertThat(results[2].totalTime.months, `is`(XsInteger.ZERO))
        assertThat(results[2].totalTime.seconds, `is`(XsDecimal("0.0000014".toBigDecimal())))
    }
}

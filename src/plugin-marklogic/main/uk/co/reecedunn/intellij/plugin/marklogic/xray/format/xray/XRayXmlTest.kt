/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray

import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTest
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestAssertion
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.toXsDuration

class XRayXmlTest(private val test: XmlElement) : XRayTest {
    override val name: String by lazy { test.attribute("name")!! }

    override val result: TestResult by lazy {
        test.attribute("result")!!.let { result -> TestResult.value(result) }
    }

    override val duration: XsDurationValue? by lazy { test.attribute("time")?.toXsDuration() }

    private val assertionsList by lazy {
        test.children("xray:assert").map { XRayXmlTestAssert(it) }.toList()
    }

    override val assertions: Sequence<XRayTestAssertion>
        get() = assertionsList.asSequence()

    override val error: Throwable? by lazy { test.child("error:error")?.toMarkLogicQueryError(null) }
}

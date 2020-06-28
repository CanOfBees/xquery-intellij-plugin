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
package uk.co.reecedunn.intellij.plugin.core.text

import java.text.NumberFormat

object Units {
    private fun numberFormat(min: Int, max: Int): NumberFormat {
        val nf = NumberFormat.getInstance()
        nf.minimumFractionDigits = min
        nf.maximumFractionDigits = max
        return nf
    }

    @Suppress("unused")
    object Precision {
        val milli: NumberFormat = numberFormat(0, 3)
        val micro: NumberFormat = numberFormat(3, 6)
        val nano: NumberFormat = numberFormat(6, 9)

        val milliWithZeros: NumberFormat = numberFormat(3, 3)
        val microWithZeros: NumberFormat = numberFormat(6, 6)
        val nanoWithZeros: NumberFormat = numberFormat(9, 9)
    }
}

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
/**
 * IntelliJ defines a DSL for creating UIs, but currently has several disadvantages:
 * 1.  It is not supported on IntelliJ 2019.1 or earlier;
 * 2.  It is classified as being in active development, with potential breaking changes between releases;
 * 3.  It is not necessarily comparable to the swing API in terms of available functionality and flexibility.
 *
 * As such, a swing compatible DSL is defined in this file.
 */
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.Container
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.LayoutManager
import javax.swing.JPanel

// region panel

fun panel(layout: LayoutManager, init: JPanel.() -> Unit): JPanel {
    val panel = JPanel(layout)
    panel.init()
    return panel
}

fun panel(init: JPanel.() -> Unit): JPanel = panel(GridBagLayout(), init)

// endregion
// region grid

fun grid(x: Int, y: Int): GridBagConstraints {
    val constraints = GridBagConstraints()
    constraints.gridx = x
    constraints.gridy = y
    return constraints
}

// endregion
// region label

fun Container.label(text: String, constraints: Any? = null): JBLabel {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.NONE
        constraints.insets = JBUI.insets(0, 0, 4, 8)
    }

    val label = JBLabel(text)
    add(label, constraints)
    return label
}

// endregion
// region textFieldWithBrowseButton

fun Container.textFieldWithBrowseButton(
    constraints: Any?,
    init: TextFieldWithBrowseButton.() -> Unit
): TextFieldWithBrowseButton {
    if (constraints is GridBagConstraints) {
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = JBUI.insetsBottom(4)
    }

    val field = TextFieldWithBrowseButton()
    field.init()
    add(field, constraints)
    return field
}

fun Container.textFieldWithBrowseButton(init: TextFieldWithBrowseButton.() -> Unit): TextFieldWithBrowseButton {
    return textFieldWithBrowseButton(null, init)
}

// endregion

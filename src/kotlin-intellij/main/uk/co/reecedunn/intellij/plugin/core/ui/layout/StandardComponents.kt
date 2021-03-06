/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.*
import java.awt.*
import java.awt.event.ActionListener
import javax.swing.*

// region button group

data class ButtonGroupBuilder(val container: Container, val group: ButtonGroup = ButtonGroup())

fun Container.buttonGroup(init: ButtonGroupBuilder.() -> Unit): ButtonGroup {
    val builder = ButtonGroupBuilder(this)
    builder.init()
    return builder.group
}

// endregion
// region check box

fun Container.checkBox(constraints: Any?, text: String? = null, init: JCheckBox.() -> Unit = {}): JCheckBox {
    if (constraints is GridBagConstraints) {
        constraints.anchor = GridBagConstraints.WEST
    }

    val checkbox = JBCheckBox(text)
    checkbox.init()
    add(checkbox, constraints)
    return checkbox
}

// endregion
// region combo box

fun <T> Container.comboBox(constraints: Any?, init: ComboBox<T>.() -> Unit = {}): ComboBox<T> {
    @Suppress("UNCHECKED_CAST") val combobox = when (constraints) {
        is ComboBoxModel<*> -> ComboBox(constraints as ComboBoxModel<T>)
        else -> ComboBox()
    }
    combobox.init()
    if (constraints !is ComboBoxModel<*>) {
        add(combobox, constraints)
    }
    return combobox
}

// endregion
// region component with browse button

fun <T : JComponent> Container.componentWithBrowseButton(
    constraints: Any?,
    browseActionListener: ActionListener,
    component: () -> T
): ComponentWithBrowseButton<T> {
    val ui = ComponentWithBrowseButton(component(), browseActionListener)
    add(ui, constraints)
    return ui
}

// endregion
// region label

fun Container.label(text: String?, constraints: Any? = null, init: JLabel.() -> Unit = {}): JBLabel {
    if (constraints is GridBagConstraints) {
        constraints.anchor = GridBagConstraints.WEST
    }

    val label = if (text == null) JBLabel() else JBLabel(text)
    label.init()
    add(label, constraints)
    return label
}

fun Container.label(constraints: Any? = null, init: JLabel.() -> Unit = {}): JBLabel = label(null, constraints, init)

// endregion
// region password field

fun Container.passwordField(
    constraints: Any?,
    init: JPasswordField.() -> Unit = {}
): JPasswordField {
    val field = JBPasswordField()
    field.init()
    add(field, constraints)
    return field
}

// endregion
// region radio button

fun ButtonGroupBuilder.radio(
    constraints: Any?,
    text: String? = null,
    init: JRadioButton.() -> Unit = {}
): JRadioButton {
    if (constraints is GridBagConstraints) {
        constraints.anchor = GridBagConstraints.WEST
    }

    val radio = JBRadioButton(text)
    radio.init()
    container.add(radio, constraints)
    group.add(radio)
    return radio
}

// endregion
// region text field

fun Container.textField(
    constraints: Any?,
    init: JTextField.() -> Unit = {}
): JTextField {
    val field = JBTextField()
    field.init()
    add(field, constraints)
    return field
}

// endregion
// region text field with browse button

fun Container.textFieldWithBrowseButton(
    constraints: Any?,
    init: TextFieldWithBrowseButton.() -> Unit = {}
): TextFieldWithBrowseButton {
    val field = TextFieldWithBrowseButton()
    field.init()
    add(field, constraints)
    return field
}

// endregion

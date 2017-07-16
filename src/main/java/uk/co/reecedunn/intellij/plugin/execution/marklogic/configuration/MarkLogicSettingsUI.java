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
package uk.co.reecedunn.intellij.plugin.execution.marklogic.configuration;

import javax.swing.*;

public class MarkLogicSettingsUI {
    private JPanel mPanel;
    private JTextField mHostName;
    private JTextField mPort;
    private JTextField mUserName;
    private JPasswordField mPassword;

    public JPanel getPanel() {
        return mPanel;
    }

    private void createUIComponents() {
        mHostName = new JTextField();
        mPort = new JTextField();
        mUserName = new JTextField();
        mPassword = new JPasswordField();
    }

    public void reset(MarkLogicRunConfiguration configuration) {
        mHostName.setText(configuration.getServerHost());
        mPort.setText(Integer.toString(configuration.getServerPort()));
        mUserName.setText(configuration.getUserName());
        mPassword.setText(configuration.getPassword());
    }

    public void apply(MarkLogicRunConfiguration configuration) {
        configuration.setServerHost(mHostName.getText());
        configuration.setServerPort(Integer.parseInt(mPort.getText()));
        configuration.setUserName(mUserName.getText());
        configuration.setPassword(String.valueOf(mPassword.getPassword()));
    }
}

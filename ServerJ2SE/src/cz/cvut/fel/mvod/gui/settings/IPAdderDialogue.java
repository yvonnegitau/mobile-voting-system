/*
Copyright 2011 Radovan Murin

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package cz.cvut.fel.mvod.gui.settings;

import cz.cvut.fel.mvod.common.networkAddressRange;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;

/**
 * A dialog that enables the adding of IPs to the rules.
 * @author Radovan Murin
 */
public class IPAdderDialogue extends JDialog {

    JTextField IP;
    JLabel IPLabel;
    JTextField mask;
    JLabel maskLabel;
    String[] options;
    JComboBox combo;
    JButton okBTN;
    JButton cancelBTN;
    IPAdderDialogue instance;
/**
 * The constructor of the dialogue.
 */
    public IPAdderDialogue() {
        super();
        IP = new JTextField(16);
        IPLabel = new JLabel(GlobalSettingsAndNotifier.singleton.messages.getString("IPAddressLabel"));
        mask = new JTextField(16);
        maskLabel = new JLabel(GlobalSettingsAndNotifier.singleton.messages.getString("maskLabel"));
        options = new String[]{GlobalSettingsAndNotifier.singleton.messages.getString("optionEnableAll"), GlobalSettingsAndNotifier.singleton.messages.getString("optionRestrictSSL"), GlobalSettingsAndNotifier.singleton.messages.getString("optionDenyAll")};
        combo = new JComboBox(options);
        okBTN = new JButton(GlobalSettingsAndNotifier.singleton.messages.getString("addLabel"));
        cancelBTN = new JButton(GlobalSettingsAndNotifier.singleton.messages.getString("closeLabel"));
        instance = this;
        cancelBTN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                instance.dispose();
            }
        });
        okBTN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                String[] IPs = IP.getText().split("\\.");

                int[] IPArray = new int[IPs.length];
                for (int i = 0; i < IPArray.length; i++) {
                    try {
                        IPArray[i] = Integer.parseInt(IPs[i]);
                    } catch (NumberFormatException ex) {
                        instance.showError(ex.toString());
                    }

                }

                String[] masks = mask.getText().split("\\.");
                int[] maskArray = new int[masks.length];
                for (int i = 0; i < maskArray.length; i++) {
                    try{
                    maskArray[i]= Integer.parseInt(masks[i]);
                    }catch(NumberFormatException ex) {
                        instance.showError(ex.toString());
                    }
                }
                String action = null;
                switch(combo.getSelectedIndex()) {
                    case 0:action=networkAddressRange.ALLOW_ANY;break;
                    case 1:action=networkAddressRange.ALLOW_SSL;break;
                    case 2:action=networkAddressRange.DENY_ACCESS;
                }

                try{
                    networkAddressRange nar = new networkAddressRange(IPArray, maskArray, action);
                    GlobalSettingsAndNotifier.singleton.permited.add(nar);
                    GlobalSettingsAndNotifier.singleton.notifyListeners();

                }catch (Exception ex) {
                    instance.showError(ex.toString());
                }
            }
        });
        setSize(200, 200);
        setLayout(new FlowLayout());
        add(IPLabel);
        add(IP);
        add(maskLabel);
        add(mask);
        add(combo);
        add(okBTN);
        add(cancelBTN);


    }
/**
 * Displays an error.
 * @param toString the given error.
 */
    private void showError(String toString) {
        JOptionPane.showMessageDialog(instance,
                toString,
                GlobalSettingsAndNotifier.singleton.messages.getString("errorLabel"),
                JOptionPane.ERROR_MESSAGE);
    }
}

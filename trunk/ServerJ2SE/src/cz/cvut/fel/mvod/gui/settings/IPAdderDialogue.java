/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Murko
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

    public IPAdderDialogue() {
        super();
        IP = new JTextField(16);
        IPLabel = new JLabel("IP adresa");
        mask = new JTextField(16);
        maskLabel = new JLabel("Maska siete");
        options = new String[]{"Povoliť všetky spojenia", "Povoliť SSL spojenie", "Zakázané spojenie"};
        combo = new JComboBox(options);
        okBTN = new JButton("Potvrdiť");
        cancelBTN = new JButton("Zatvoriť okno");
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

    private void showError(String toString) {
        JOptionPane.showMessageDialog(instance,
                toString,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

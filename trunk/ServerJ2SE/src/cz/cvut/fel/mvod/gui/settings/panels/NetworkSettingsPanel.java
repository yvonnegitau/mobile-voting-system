/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.gui.settings.panels;

import cz.cvut.fel.mvod.common.ObjectReadWriter;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.global.Notifiable;
import cz.cvut.fel.mvod.gui.Showable;
import cz.cvut.fel.mvod.gui.settings.IPAdderDialogue;
import cz.cvut.fel.mvod.gui.settings.NetworkSettingsTable;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author Murko
 */
public class NetworkSettingsPanel extends JPanel implements Showable, Notifiable {

    JButton okBTN;
    JButton clearBTN;
    JCheckBox implicit;
    JLabel implicitLabel;
    NetworkSettingsTable nST;
    JButton addBTN;
    JTable tejbl;
    NetworkSettingsPanel instance;

    public NetworkSettingsPanel() {
        super();
        instance = this;
        //setTitle("Sieťové nastavenia");
        GlobalSettingsAndNotifier.singleton.addListener(this);
        okBTN = new JButton("Potvrdiť");
        clearBTN = new JButton("Zmazať všetko");
        nST = new NetworkSettingsTable();
        implicit = new JCheckBox("Povoliť spojenie z adries tu nezahrnutých");
        try {
            implicit.setSelected(GlobalSettingsAndNotifier.singleton.getSetting("IMPLICIT_ALLOW").equalsIgnoreCase("true") ? true : false);
        } catch (NullPointerException ex) {
            GlobalSettingsAndNotifier.singleton.modifySettings("IMPLICIT_ALLOW", "false", false);

        }
        addBTN = new JButton("Pridať pravidlo");



        tejbl = new JTable(nST);

        tejbl.addMouseListener(new PopClickListener());

        tejbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn column = null;
        for (int i = 0; i < 3; i++) {
            column = tejbl.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(75);
            }
            if (i == 1) {
                column.setPreferredWidth(25); //third column is bigger
            }
            if (i == 2) {
                column.setPreferredWidth(150);
            }
        }
        setSize(275, 125 + nST.getRowCount() * 20);
        okBTN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                GlobalSettingsAndNotifier.singleton.modifySettings("IMPLICIT_ALLOW", implicit.isSelected() ? "true" : "false", false);
               // instance.dispose();
            }
        });
        clearBTN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                GlobalSettingsAndNotifier.singleton.permited.clear();
                instance.notifyOfChange();
            }
        });

        addBTN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                IPAdderDialogue adder = new IPAdderDialogue();
                adder.setVisible(true);

            }
        });

        FlowLayout fl = new FlowLayout();
        setLayout(fl);
        add(tejbl);
        add(implicit);

        add(okBTN);
        add(clearBTN);
        add(addBTN);



    }

    @Override
    public void notifyOfChange() {
        nST = new NetworkSettingsTable();
        tejbl.setModel(nST);
        setSize(275, 125 + nST.getRowCount() * 20);
    }

    /**
     * stackover
     */
    class PopClickListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPop(e);
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPop(e);
            }
        }

        private void doPop(MouseEvent e) {
            PopUpDemo menu = new PopUpDemo();
            menu.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("DELETING");
                    GlobalSettingsAndNotifier.singleton.permited.remove(tejbl.getSelectedRow());
                    GlobalSettingsAndNotifier.singleton.notifyListeners();

                }
            });
            menu.show(e.getComponent(), e.getX(), e.getY());

        }
    }

    class PopUpDemo extends JPopupMenu {

        JMenuItem delItem;
        JMenuItem saveItem;

        public PopUpDemo() {
            delItem = new JMenuItem("Odstrániť");
            saveItem = new JMenuItem("Uložiť pravidlá do súboru");
            delItem.setEnabled(true);
            delItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("DELETING");
                    GlobalSettingsAndNotifier.singleton.permited.remove(tejbl.getSelectedRow());
                    GlobalSettingsAndNotifier.singleton.notifyListeners();
                }
            });
            saveItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    File file = null;
                    final JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(instance);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fc.getSelectedFile();
                    }
                    try {
                        ObjectReadWriter.saveIPTables(file);
                    } catch (IOException ex) {
                        showError(ex.toString(), instance);
                    }
                }
            });

            add(delItem);
            add(saveItem);
        }
    }
    private static void showError(String toString, JPanel instance) {
        JOptionPane.showMessageDialog(instance,
                toString,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

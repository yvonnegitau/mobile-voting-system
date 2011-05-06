package cz.cvut.fel.mvod.prologueServer;
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

import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 * A table with registrant information in it
 * @author Radovan Murin
 */
public class RegistrantTable extends AbstractTableModel {

    String[] columnNames = {GlobalSettingsAndNotifier.singleton.messages.getString("nameFormInput"),
        GlobalSettingsAndNotifier.singleton.messages.getString("surnameFormInput"),
        GlobalSettingsAndNotifier.singleton.messages.getString("usernameFormInput"),
       GlobalSettingsAndNotifier.singleton.messages.getString("IDLabel"),
        ""};
    Object[][] data = null;
    HashMap<String, List<Voter>> regs;
    HashMap<Object[],byte[]> passwords = new HashMap<Object[], byte[]>();
/**
 * The class constructor
 * @param regs the registrants
 */
    public RegistrantTable(HashMap<String, List<Voter>> regs) {
        super();
        this.regs = regs;
        Set<String> keys = regs.keySet();
        data = presentData(null);

    }

    public Object[][] presentData(String identifier) {
        Object[][] data = null;
        int i = 0;
        if (identifier == null) {
            Set<String> keys = regs.keySet();
            data = new Object[getMultiMapSize(regs)][columnNames.length];
            Iterator kI = keys.iterator();
            while (kI.hasNext()) {
                String key = (String) kI.next();
                List<Voter> LV = regs.get(key);
                Iterator<Voter> IL = LV.iterator();
                while (IL.hasNext()) {

                    Voter v = IL.next();
                    data[i][0] = v.getFirstName();
                    data[i][1] = v.getLastName();
                    data[i][2] = v.getUserName();
                    data[i][3] = key;
                    data[i][4] = false;
                    passwords.put(data[i], v.getPassword());
                    i++;
                }
            }
        } else {

            List<Voter> LV = regs.get(identifier);
            data = new Object[LV.size()][columnNames.length];
            Iterator<Voter> IL = LV.iterator();
            while (IL.hasNext()) {
                Voter v = IL.next();
                data[i][0] = v.getFirstName();
                data[i][1] = v.getLastName();
                data[i][2] = v.getUserName();
                data[i][3] = identifier;
                data[i][4] = false;
                i++;
            }
        }
        return data;

    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object dta = null;
        try {
            dta = data[rowIndex][columnIndex];
        } catch (Exception ex) {
        }

        return dta;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public Voter getVoterAt(int row){
        Voter v = null;
        try{
            v = new Voter((String)data[row][0], (String)data[row][1], passwords.get(data[row]), (String)data[row][2]);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    private int getMultiMapSize(HashMap<String, List<Voter>> regs) {
        Set<String> keys = regs.keySet();
        Iterator<String> KI = keys.iterator();
        int i = 0;
        while (KI.hasNext()) {
            List<Voter> LV = regs.get(KI.next());
            i += LV.size();
        }
        return i;
    }
}

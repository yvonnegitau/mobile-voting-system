package cz.cvut.fel.mvod.prologueServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import cz.cvut.fel.mvod.common.Voter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Murko
 */
public class RegistrantTable extends AbstractTableModel {

    String[] columnNames = {"Meno",
        "Priezvisko",
        "Login",
        "ID",
        "Checker"};
    Object[][] data = null;
    HashMap<String, List<Voter>> regs;
    HashMap<Object[],byte[]> passwords = new HashMap<Object[], byte[]>();

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

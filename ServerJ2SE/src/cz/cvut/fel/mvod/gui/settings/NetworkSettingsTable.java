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
import cz.cvut.fel.mvod.gui.ErrorDialog;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Radovan Murin
 */
public class NetworkSettingsTable extends AbstractTableModel {

    String[] columnNames = {GlobalSettingsAndNotifier.singleton.messages.getString("IPAddressLabel"),
        GlobalSettingsAndNotifier.singleton.messages.getString("maskLabel"),
        GlobalSettingsAndNotifier.singleton.messages.getString("ruleLabel")};
    networkAddressRange[] data = null;
  
  //  JTableHeader head;

    public NetworkSettingsTable() {
        super();
        Iterator<networkAddressRange> iNAR = GlobalSettingsAndNotifier.singleton.permited.iterator();
       
        data = new networkAddressRange[GlobalSettingsAndNotifier.singleton.permited.size()];
        int i = 0;
        while(iNAR.hasNext()){
            data[i++] = iNAR.next();
            
        }
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return data[rowIndex].getNetworkForHumans();
        }
        if(columnIndex==1) {
            return data[rowIndex].getShortMask();
        }
        if(columnIndex==2){
            String action = data[rowIndex].getAction();
            
              if(action.equals(networkAddressRange.ALLOW_ANY)){
                  return GlobalSettingsAndNotifier.singleton.messages.getString("optionEnableAll");
              }
              if(action.equals(networkAddressRange.ALLOW_SSL)){
                  return GlobalSettingsAndNotifier.singleton.messages.getString("optionRestrictSSL");
              }
              if(action.equals(networkAddressRange.DENY_ACCESS)) {
                  return GlobalSettingsAndNotifier.singleton.messages.getString("optionDenyAll");
              }
            
        }
        return "null";
    }
    
    public void removeRow(int rowIndex) {
        GlobalSettingsAndNotifier.singleton.permited.remove(rowIndex);
        GlobalSettingsAndNotifier.singleton.notifyListeners();
        
    }
}

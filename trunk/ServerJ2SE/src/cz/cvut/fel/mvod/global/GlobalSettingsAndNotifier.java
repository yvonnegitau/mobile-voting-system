/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.global;

import cz.cvut.fel.mvod.common.networkAddressRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murko
 */
public class GlobalSettingsAndNotifier {
    public static GlobalSettingsAndNotifier singleton  = new GlobalSettingsAndNotifier();

    private ArrayList<Notifiable> listeners;
    private HashMap<String,String> settings;
    public ArrayList<networkAddressRange> permited;



    private GlobalSettingsAndNotifier() {
        listeners = new ArrayList<Notifiable>();
        settings = new HashMap<String, String>();
        permited =new  ArrayList<networkAddressRange>();
         int[] add = new int[] {192,168,2,1};
         int[] mask = new int[] {255,255,255,0};
        try {
            permited.add(new networkAddressRange(add, mask));
        } catch (Exception ex) {
            Logger.getLogger(GlobalSettingsAndNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
 

        
    }

    public void addListener(Notifiable n ){
        listeners.add(n);
    }

    public void modifySettings(String name,String value) {
        settings.put(name, value);
        Iterator<Notifiable> iN = listeners.iterator();
        while(iN.hasNext()) {
            iN.next().notifyOfChange();
        }
    }

    public String getSetting(String name) {
        String out = null;
        out = settings.get(name);
        return out;
    }


    public void modifySettings(String name, String value, boolean flagNotify) {
         if(!flagNotify){
            settings.put(name, value);
        } else {
            modifySettings(name, value);
        }
    }



}

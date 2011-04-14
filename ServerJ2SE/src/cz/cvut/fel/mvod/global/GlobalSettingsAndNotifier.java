/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Murko
 */
public class GlobalSettingsAndNotifier {
    public static GlobalSettingsAndNotifier singleton  = new GlobalSettingsAndNotifier();

    private ArrayList<Notifiable> listeners;
    private HashMap<String,String> settings;



    private GlobalSettingsAndNotifier() {
        listeners = new ArrayList<Notifiable>();
        settings = new HashMap<String, String>();
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.global;

import cz.cvut.fel.mvod.common.networkAddressRange;
import cz.cvut.fel.mvod.prologueServer.PrologueServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murko
 */
public final class GlobalSettingsAndNotifier {
    public static GlobalSettingsAndNotifier singleton  = new GlobalSettingsAndNotifier();

    private ArrayList<Notifiable> listeners;
    private HashMap<String,String> settings;
    public ArrayList<networkAddressRange> permited;



    private GlobalSettingsAndNotifier() {
        listeners = new ArrayList<Notifiable>();
        settings = new HashMap<String, String>();
        modifySettings("PROLOGUE_PORT", "10443");
        modifySettings("allowBeacon", "true");
        modifySettings("HTTP_PORT", "10666");
        modifySettings("SSL_PORT", "11109");
         modifySettings("IMPLICIT_ALLOW", "false");
         modifySettings("SERVER_NAME", "Default Value");
         modifySettings("prologueState", PrologueServer.STATE_INACTIVE+"");
         modifySettings("Prologue_certpath", "null");
         modifySettings("NET_ORIGIN","NO_RESTRICTIONS");

        


        permited =new  ArrayList<networkAddressRange>();
         int[] add = new int[] {0,0,0,0};
         int[] mask = new int[] {0,0,0,0};
        try {
            permited.add(new networkAddressRange(add, mask,networkAddressRange.ALLOW_ANY));
        } catch (Exception ex) {
            Logger.getLogger(GlobalSettingsAndNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
 

        
    }

    public void addListener(Notifiable n ){
        listeners.add(n);
    }

    public void modifySettings(String name,String value) {
        settings.put(name.toUpperCase(), value);
        notifyListeners();
    }

    public void notifyListeners() {
        Iterator<Notifiable> iN = listeners.iterator();
        while(iN.hasNext()) {
            iN.next().notifyOfChange();
        }
        
    }

    public String getSetting(String name) {
        String n = name.toUpperCase();
        String out = null;
        out = settings.get(n);
        if(out==null) out = " Null ";
        return out;
    }


    public void modifySettings(String name, String value, boolean flagNotify) {
        System.out.println("SETTING "+name +" modified to "+value);
         if(!flagNotify){
            settings.put(name.toUpperCase(), value);
        } else {
            modifySettings(name.toUpperCase(), value);
        }
    }



}

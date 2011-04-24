/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.global;

import cz.cvut.fel.mvod.common.networkAddressRange;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author Murko
 */
public class savedSettings implements Serializable {
    Locale locale;
    public HashMap<String, String> settings;
    public ArrayList<networkAddressRange> permited;

    public savedSettings(Locale locale, HashMap<String, String> settings, ArrayList<networkAddressRange> permited) {
        this.locale = locale;
        this.settings = settings;
        this.permited = permited;
    }

    public Locale getLocale() {
        return locale;
    }

    public ArrayList<networkAddressRange> getPermited() {
        return permited;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    
}

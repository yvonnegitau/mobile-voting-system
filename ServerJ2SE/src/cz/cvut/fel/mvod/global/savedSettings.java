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
package cz.cvut.fel.mvod.global;

import cz.cvut.fel.mvod.common.networkAddressRange;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class represents a savable form of the GlobalSettingsAndNotifier
 * @author Radovan Murin
 */
public class savedSettings implements Serializable {
    Locale locale;
    public HashMap<String, String> settings;
    public ArrayList<networkAddressRange> permited;
/**
 * The class constructor
 * @param locale the locale in use
 * @param settings teh array list of settings
 * @param permited the IP tables
 */
    public savedSettings(Locale locale, HashMap<String, String> settings, ArrayList<networkAddressRange> permited) {
        this.locale = locale;
        this.settings = settings;
        this.permited = permited;
    }
/**
 * Returns the locale
 * @return  the locale in the instance
 */
    public Locale getLocale() {
        return locale;
    }
/**
 * returns the array list of IP tables
 * @return the arrayList of networkAddressRanges
 */
    public ArrayList<networkAddressRange> getPermited() {
        return permited;
    }
/**
 * returns the HashMap of settings
 * @return
 */
    public HashMap<String, String> getSettings() {
        return settings;
    }

    
}

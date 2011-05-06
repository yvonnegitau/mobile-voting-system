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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * A class of a combo box with available languages
 * @author Radovan Murin
 */
public class LanguageChoicesCBM extends DefaultComboBoxModel {
    HashMap<Locale, String> locales;
    public LanguageChoicesCBM() {
        super();
        locales =new HashMap<Locale, String>();
        locales.put(new Locale("en", "EN"),"English");
        locales.put(new Locale("cs", "CZ"),"Czech");
        locales.put(new Locale("sk", "SR"),"Slovak");
        locales.put(new Locale("fr", "FR"),"French");
        Iterator<String> is = locales.values().iterator();
        while(is.hasNext()) addElement(is.next());
    }
    /**
     * returns the language position.
     * @param loc the language that it's position si desired to know.
     * @return The position in the combo box.
     */
    public int getLangPos(Locale loc){
        String lang = locales.get(loc);
        System.out.println("Looking up "+loc.getLanguage());
        int i = 0;
        Iterator<String> is = locales.values().iterator();
        while(is.hasNext()) {
            if(is.next().equals(lang)) {
                System.out.println("FOUND");
                return i;
            }
            i++;
        }
        return i;
    }
/**
 * returns the locale that is currently selected
 * @return
 */
    public Locale getSelected(){
        String sel = (String) getSelectedItem();
        Iterator<Locale> il = locales.keySet().iterator();
        while(il.hasNext()){
            Locale next = il.next();
            if(locales.get(next).equals(sel)) return next;

        }
        return null;

    }

   




}

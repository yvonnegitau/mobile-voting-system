/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.gui.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Murko
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

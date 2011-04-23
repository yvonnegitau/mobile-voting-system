package cz.cvut.fel.mvod.prologueServer;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Murko
 */
public class ProvidingHandler extends registeringHandler {

    @Override
    public String parsePost(String body){
        return GlobalSettingsAndNotifier.singleton.messages.getString("regsAreClosedTXT");
    }

}

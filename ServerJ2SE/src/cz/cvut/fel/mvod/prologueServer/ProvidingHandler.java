package cz.cvut.fel.mvod.prologueServer;

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
        return "Registrations are closed, please contact the administrator for more help";
    }

}

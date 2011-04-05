/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.net;
/** 
 *
 * @author Murko
 */
public class BeaconXMLGenerator {
        String beaconMessage = "0000";
    public BeaconXMLGenerator(String friendlyName,int listenPort) {
            beaconMessage= "<serverinfo><friendlyname>"+friendlyName+"</friendlyname><port>"+listenPort+"</port></serverinfo>\n";
    }

    public String getBeaconMSG() {
        return beaconMessage;

    }



}

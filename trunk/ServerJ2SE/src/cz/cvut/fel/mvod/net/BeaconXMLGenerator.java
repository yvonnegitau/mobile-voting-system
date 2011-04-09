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
    int serverID = -1;

    public BeaconXMLGenerator(String friendlyName, int listenPort) {
        serverID = (int) Math.floor(Math.random() * 1000);
        beaconMessage = "<serverinfo id='" + serverID + "'><friendlyname>" + friendlyName + "</friendlyname><port>" + listenPort + "</port></serverinfo>\n";

    }

    public String getBeaconMSG() {
        return beaconMessage;

    }
}

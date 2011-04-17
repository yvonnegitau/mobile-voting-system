/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;

/** 
 *
 * @author Murko
 */
public class InfoXMLGenerator {

    String beaconMessage = "0000";
    int serverID = -1;

    public InfoXMLGenerator(String friendlyName, int listenPort) {
        serverID = (int) Math.floor(Math.random() * 1000);
        beaconMessage = "<serverinfo id='" + serverID + "'><friendlyname>" + friendlyName + "</friendlyname><port>" + listenPort + "</port></serverinfo>\n";

    }

    public String getBeaconMSG() {
        return beaconMessage;
    }

    public static String getListenPortMSG() {
        String msg = "<listenports>";
        String http = GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT");
        String ssl = GlobalSettingsAndNotifier.singleton.getSetting("SSL_PORT");
        if (http!=null && Integer.parseInt(http) > 0) {
            msg = msg.concat("<port secure=\"false\">" + http + "</port>");
        }
        if (ssl!=null && Integer.parseInt(ssl) > 0) {
            msg = msg.concat("<port secure=\"true\">" + ssl + "</port>");
        }
        msg = msg.concat("</listenports>");
        return msg;
    }
}

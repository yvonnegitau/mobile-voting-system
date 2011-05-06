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
package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;

/** 
 * Generates the XML messages concerning server information.
 * @author RadovanMurin
 */
public class InfoXMLGenerator {

    String beaconMessage = "0000";
    int serverID = -1;
/**
 * The class constructor
 * @param friendlyName the friendly name of the server as it is to be displayed on the device.
 * @param listenPort the HTTP listening port.
 */
    public InfoXMLGenerator(String friendlyName, int listenPort) {
        serverID = (int) Math.floor(Math.random() * 1000);
        beaconMessage = "<serverinfo id='" + serverID + "'><friendlyname>" + friendlyName + "</friendlyname><port>" + listenPort + "</port></serverinfo>\n";

    }
/**
 * Returns the XML message
 * @return
 */
    public String getBeaconMSG() {
        return beaconMessage;
    }
/**
 * Generates and returns an XML string that contains a list of ports the server is listenin on
 * along with identifiers whether it is a secured port or not.
 * @return the XML String
 */
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

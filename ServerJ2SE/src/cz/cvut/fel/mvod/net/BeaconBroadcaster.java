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
import cz.cvut.fel.mvod.global.Notifiable;
import cz.cvut.fel.mvod.gui.ErrorDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Timer;

/**
 * A UDP beacon broadcaster. The broadcaster advertises the location and the presence of an active server on the
 * local network.
 * @author Radovan Murin
 */
public class BeaconBroadcaster extends Thread implements Notifiable {

    private static int BROADCAST_PORT = 50666;
    private DatagramSocket s = null;
    private ArrayList<DatagramPacket> beaconPacket = new ArrayList<DatagramPacket>();
    private Inet4Address address = null;
    private ArrayList<Inet4Address> broadcasts = new ArrayList<Inet4Address>();
    private int frequency = 2000;
    private ActionListener sender = null;
    private int ID;
    private InfoXMLGenerator payload;
   // private String state = GlobalSettingsAndNotifier.singleton.getSetting("allowBeacon");
/**
 * Constructior of the broadcaster
 * @param FriendlyName the friendly name of the server as is should appear on the devices
 * @param listenPort the HTTP listening port
 * @throws UnknownHostException
 */
    public BeaconBroadcaster(String FriendlyName, int listenPort) throws UnknownHostException {
        payload = new InfoXMLGenerator(FriendlyName, listenPort);
        GlobalSettingsAndNotifier.singleton.addListener(this);
        try {
            s = new DatagramSocket();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        address = (Inet4Address) s.getLocalAddress();
       
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                List<InterfaceAddress> ia = ni.getInterfaceAddresses();
                for (InterfaceAddress a : ia) {
                    if (a.getAddress().getAddress().length == 4) {
                        broadcasts.add((Inet4Address) a.getBroadcast());
                    }
                }
            }
        } catch (SocketException ex) {
             ErrorDialog.main(new String[]{GlobalSettingsAndNotifier.singleton.messages.getString("portBoundErr")+'\n'+"BeaconBroadcaster: "+BROADCAST_PORT+" \n"+ex.toString()});
        }
        final String buf = payload.getBeaconMSG();
        for (int i = 0; i < broadcasts.size(); i++) {
            DatagramPacket bp = new DatagramPacket(buf.getBytes(), buf.getBytes().length, broadcasts.get(i), BROADCAST_PORT);
            
            beaconPacket.add(bp);
        }
        sender = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < beaconPacket.size(); i++) {
                    try {
                       if(GlobalSettingsAndNotifier.singleton.getSetting("allowBeacon").equals("true")) s.send(beaconPacket.get(i));
                        
                          

                    } catch (IOException ex) {
                        System.out.println("Beacon failed to send.");
                    }
                }

            }
        };
    }

    @Override
    public void run() {
        new Timer(frequency, sender).start();
        while (true) {
        }
    }

    @Override
    public void notifyOfChange() {
        payload = new InfoXMLGenerator(GlobalSettingsAndNotifier.singleton.getSetting("SERVER_NAME"),Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT")));
       final String buf = payload.getBeaconMSG();
        for (int i = 0; i < broadcasts.size(); i++) {
            DatagramPacket bp = new DatagramPacket(buf.getBytes(), buf.getBytes().length, broadcasts.get(i), BROADCAST_PORT);

            beaconPacket.add(bp);
        }
    }
}

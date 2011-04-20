/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.global.Notifiable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author Murko
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
            System.out.println(ex.toString());
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
                        
                          //System.out.println("BEACONING");

                    } catch (IOException ex) {
                        System.out.println(ex.toString());
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
       // String b = GlobalSettingsAndNotifier.singleton.getSetting("allowBeacon");
        //state = b;
    }
}

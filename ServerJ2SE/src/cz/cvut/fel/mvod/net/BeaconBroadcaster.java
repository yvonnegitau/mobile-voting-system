/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.net;

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
public class BeaconBroadcaster extends Thread {

    private static int BROADCAST_PORT = 50666;
    private DatagramSocket s = null;
    private ArrayList<DatagramPacket> beaconPacket = new ArrayList<DatagramPacket>();
    private Inet4Address address = null;
    private ArrayList<Inet4Address> broadcasts = new ArrayList<Inet4Address>();
    private int frequency = 2000;
    private ActionListener sender = null;
    private int ID;
    private BeaconXMLGenerator payload;

    public BeaconBroadcaster(String FriendlyName,int listenPort) throws UnknownHostException {
        payload = new BeaconXMLGenerator(FriendlyName, listenPort);
        try {
            s = new DatagramSocket();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        address = (Inet4Address) s.getLocalAddress();
        System.out.println(address.toString());
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
            System.out.println(new String(bp.getData()));
            beaconPacket.add(bp);
        }
        sender = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < beaconPacket.size(); i++) {
                    try {
                        s.send(beaconPacket.get(i));
                        Inet4Address deb = (Inet4Address) InetAddress.getByName("147.32.89.127");
                        byte[] buffer = new byte[644];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, deb, 9000);
                        s.send(packet);
                        System.out.println("SENT to " + beaconPacket.get(i).getAddress());
                    } catch (IOException ex) {
                        System.out.println(ex.toString());
                    }
                }
                System.out.println("*************************************");
            }
        };
    }

    @Override
    public void run() {
        new Timer(frequency, sender).start();
        while (true) {
        }
    }
}

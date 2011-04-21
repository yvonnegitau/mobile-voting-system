package cvut.fel.mobilevoting.murinrad.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.views.ServerListView;

import android.util.Log;

public class BeaconListener extends Thread {
	private ServerListView observer;
	private int BROADCAST_LISTEN_PORT = 50666;
	private DatagramSocket datagramSocket = null;
	ArrayList<Integer> recievedIDs = new ArrayList<Integer>();

	public BeaconListener(final ServerListView observer) {
		this.observer = observer;

		try {
			// Inet4Address n = new Inet
			datagramSocket = new DatagramSocket(BROADCAST_LISTEN_PORT);
			datagramSocket.setBroadcast(true);
			this.start();
		} catch (SocketException e1) {
			Log.e("Android Mobile Voting",
					"Beaconing port error: " + e1.toString());
		}

		// byte[] buffer = new byte[128];
		// DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

	}

	@Override
	public void run() {
		while (true) {

			byte[] buffer = new byte[128];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				/*Log.d("Android mobile voting",
						"trying to recieve the beacon on add "
								+ datagramSocket.getLocalAddress() + " "
								+ datagramSocket.getLocalPort());*/
				datagramSocket.receive(packet);
			//	Log.d("Android mobile voting", "Packet recieved");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.w("Android Mobile Voting",
						"Beacon Recieval Error " + e.toString());
			}
			String a = new String(packet.getData());
			//Log.d("Android mobile voting", a);
			try {
				final ServerData s = XMLParser.XMLParser.parseBeacon(a, packet
						.getAddress().getHostAddress());
				Integer id = s.getId();
				s.setId(-2); // Temporary server ID stored in ID while parsin,
								// if not corrented, the database will have
								// problems
				if (!recievedIDs.contains(id)) {
					observer.handler.post(new Runnable() {

						@Override
						public void run() {
							
							observer.printServers();
							observer.addServer(s);

						}

					});
					recievedIDs.add(id);
				}
				// observer.addServer(s);
				// observer.
			} catch (ParserConfigurationException e) {
				Log.e("Android mobile voting", e.toString());
			} catch (SAXException e) {
				Log.e("Android mobile voting", e.toString());
			} catch (IOException e) {
				Log.e("Android mobile voting", e.toString());
			}
		}

	}
	
	public void resetFilter() {
		recievedIDs.clear();
	}

}

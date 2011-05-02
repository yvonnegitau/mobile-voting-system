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
package cvut.fel.mobilevoting.murinrad.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.views.ServerListView;

/**
 * A listener class that listens for UDP beacons from a server the beacons are
 * received ad UDP port 50666 SAMPLE BEACON: <serverinfo id='45'><friendlyname>TestServer</friendlyname><port> 
 * 10666
 * "</port></serverinfo>
 * 
 * @author Radovan Murin
 * 
 */
public class BeaconListener extends Thread {
	private ServerListView observer;
	private int BROADCAST_LISTEN_PORT = 50666;
	private DatagramSocket datagramSocket = null;
	ArrayList<Integer> recievedIDs = new ArrayList<Integer>();

	/**
	 * The class constructor
	 * 
	 * @param observer
	 *            observer os the ServerListView that displays the discovered
	 *            Servers
	 */
	public BeaconListener(final ServerListView observer) {
		this.observer = observer;

		try {

			datagramSocket = new DatagramSocket(BROADCAST_LISTEN_PORT);
			datagramSocket.setBroadcast(true);
			this.start();
		} catch (SocketException e1) {
			Log.e("Android Mobile Voting",
					"Beaconing port error: " + e1.toString());
		}

	}

	@Override
	public void run() {
		while (true) {

			byte[] buffer = new byte[128];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				datagramSocket.receive(packet);

			} catch (IOException e) {

				Log.w("Android Mobile Voting",
						"Beacon Recieval Error " + e.toString());
			}
			String a = new String(packet.getData());

			try {
				final ServerData s = XMLParser.XMLParser.parseBeacon(a, packet
						.getAddress().getHostAddress());
				Integer id = s.getId();
				s.setId(-2); // Temporary server ID stored in ID while parsing,
								// if not corrected, the database will have
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
			} catch (ParserConfigurationException e) {
				Log.e("Android mobile voting", e.toString());
			} catch (SAXException e) {
				Log.e("Android mobile voting", e.toString());
			} catch (IOException e) {
				Log.e("Android mobile voting", e.toString());
			}
		}

	}

	/**
	 * Resets the filter of received servers
	 */
	public void resetFilter() {
		recievedIDs.clear();
	}

}

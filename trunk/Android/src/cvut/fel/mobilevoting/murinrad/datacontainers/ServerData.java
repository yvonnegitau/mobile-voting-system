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
package cvut.fel.mobilevoting.murinrad.datacontainers;

import java.io.Serializable;

/**
 * This class contains the Server Connection Information for one particular
 * server
 * 
 * @author Radovan Murin
 * 
 */
public class ServerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9184520037096671888L;
	private String login = null;
	private String password = null;
	private int id = -1;
	private String address = null;
	private int port = -1;
	private String friendlyName = null;
	boolean changed = false;
/**
 * The constructor of the class
 * @param login login credential
 * @param password password - unencrypted
 * @param id the id in the database, if not in database then -1
 * @param address the IP address or domain name
 * @param port the port of a HTTP listening server
 * @param FN a friendly name for easy referencing in the GUI
 */
	public ServerData(String login, String password, int id,
			String address, int port, String FN) {
		setLogin(login);
		setFriendlyName(FN);
		setPassword(password);
		setId(id);
		setAddress(address);
		setPort(port);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
		changed = true;
	}

	public String getPassword() {
		return password;
	}
	public String getEncryptedPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		changed = true;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		changed = true;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
		changed = true;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
		changed = true;
	}
	
	@Override
	/**
	 * @inherit
	 */
	public String toString() {
		String s = "Server ID = "+getId()+"IP = "+getAddress()+"Port = "+getPort();
		return s;
	}
}

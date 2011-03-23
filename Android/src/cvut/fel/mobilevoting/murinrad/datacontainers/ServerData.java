package cvut.fel.mobilevoting.murinrad.datacontainers;

import java.io.Serializable;
import java.net.Inet4Address;

import android.util.Log;

import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;

/**
 * This class contains the Server Connection Information for one particular
 * server
 * 
 * @author Murko
 * 
 */
public class ServerData implements Serializable {
	private String login = null;
	private String password = null;
	private int id = -1;
	private String address = null;
	private int port = -1;
	private String friendlyName = null;
	boolean changed = false;

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
}

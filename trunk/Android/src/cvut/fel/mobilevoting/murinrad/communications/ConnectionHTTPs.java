package cvut.fel.mobilevoting.murinrad.communications;

import org.apache.http.conn.scheme.SchemeRegistry;

import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;

public class ConnectionHTTPs extends ConnectionHTTP {

	public ConnectionHTTPs(ServerData server, QuestionsView parent) {
		super(server, parent);
		this.server = server;
		this.parent = parent;
		SchemeRegistry sr = new SchemeRegistry();
		//Scheme https = new Scheme
		
		
	}

}

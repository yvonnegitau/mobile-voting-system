package cvut.fel.mobilevoting.murinrad.communications;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.SocketFactory;

import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;

import cvut.fel.mobilevoting.murinad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.QuestionsView;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.string;


import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ConnectionHTTP extends Thread implements Runnable, ConnectionInterface {
	DefaultHttpClientConnection connection;
	BufferedReader in = null;
	Socket MyClient;
	Context context;
	boolean connected = false;
	boolean hasBody = false;
	int bodySize = 0;
	QuestionsView parent;
	ServerData server;

	/* (non-Javadoc)
	 * @see cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#run()
	 */

	@Override
	public void run() {
		Looper.prepare();
		String s = "";
		char c;
		ArrayList<String> headers = null;
		boolean messageEnd = false;
		while (true) {
			headers = new ArrayList<String>();
			messageEnd = false;
			if (!connected)
				break;
			s = "";
			try {
				while (true) {
					if (headers.isEmpty()) {

					} else {
						if (headers.get(headers.size() - 1).equals("\r\n"))
							break;
					}

					while (!s.contains("\r\n") && connected) {
						c = (char) in.read();
						s += c;
						Log.v("char read 1", c + "");

					}
					// if (!(s == ""))
					headers.add(s);
					Log.i("Adding to haeders", s);
					if (s.contains("Content-length: ")) {
						// hasBody = true;
						bodySize = Integer.parseInt(s
								.replace("Content-length: ", "")
								.replace("\r\n", "").replace(" ", ""));
					}
					s = "";
				}
				if (bodySize > 0) {
					for (int i = 0; i < bodySize; i++) {
						if (!connected)
							break;
						c = (char) in.read();
						Log.v("char read" + i, c + "");
						if (c > 128) {
							i++;
							Log.v("char read" + i, c + "Was in UTF");
						}
						s += c;

					}
					Log.i("Adding to headers", s);
					headers.add(s);

				}

				// */*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/* AREA FOR MESSAGE
				// PROCESSING
				if (!headers.isEmpty()) {
					parseHeader(headers.get(0));
					for (int i = 0; i < headers.size(); i++) {
						Log.e("Android Mobile Voting", "Header number " + i
								+ ": " + headers.get(i));

					}
					if (bodySize > 0)
						Log.i("Header sent for parsing",
								headers.get(headers.size() - 1));
					XMLParser.XMLParser.parseString(
							headers.get(headers.size() - 1), parent);
				}

				Looper.loop();
				// */*/*/*/*/*/*/*//*/*/*/*/*/*/*/*/*/*/*/
				s = "";
				bodySize = 0;

			} catch (Exception ex) {
				Log.e("Android Mobile Voting Run Loop", ex.toString());
			}

		}

	}

	public ConnectionHTTP(ServerData server, QuestionsView parent) {
		try {
			this.connection = new DefaultHttpClientConnection();
			this.parent = parent;
			this.server = server;
			BasicHttpParams params = new BasicHttpParams();
			params.setParameter("test", "in order");
			MyClient = new Socket();
			// MyClient.setSoTimeout(2000);
			MyClient.connect(
					new InetSocketAddress(server.getAddress(), server.getPort()),
					2000);
			// MyClient = new Socket(server.getAddress(), server.getPort());
			connection.bind(MyClient, params);
			in = new BufferedReader(new InputStreamReader(
					MyClient.getInputStream()));
			// parent.showToast("Login Success");
			HttpRequest request = new BasicHttpRequest("GET", "/");
			BasicHeader head = new BasicHeader("ID", server.getLogin());
			BasicHeader h2 = new BasicHeader("Password", server.getPassword());
			request.addHeader(head);
			request.addHeader(h2);

			connection.sendRequestHeader(request);
			connection.flush();
			this.connected = true;
		} catch (Exception ex) {
			Log.e("Android Mobile Voting", ex.toString());
			parent.showToast(ex.toString());
			parent.finish();
			
		}
		
	}

	/* (non-Javadoc)
	 * @see cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#sendReq()
	 */
	@Override
	public void sendReq() throws Exception {
		HttpRequest request = new BasicHttpRequest("GET", "localhost");
		connection.sendRequestHeader(request);
		connection.flush();
	}

	/**
	 * Parses the 1st header
	 * 
	 * @param data
	 * @return
	 */
	private boolean parseHeader(String data) {
		if (data.contains("400")) {

			parent.mHandler.post(new Runnable() {

				@Override
				public void run() {
					parent.showToast(parent.getString(R.string.ErrorMsg400));

				}

			});
			Log.w("Android Mobile Voting", "400 Error");
			parent.finish();
			return false;
		}
		if (data.contains("401")) {
			// Unauthorized
			// parent.showToast(parent.getString(R.string.ErrorMsg401));
			parent.mHandler.post(new Runnable() {

				@Override
				public void run() {
					parent.showToast(parent.getString(R.string.ErrorMsg401));

				}

			});
			Log.w("Android Mobile Voting", "401 Error");
			parent.finish();
			return false;
		}
		if (data.contains("403")) {
			// Need credentials
			// parent.showToast(parent.getString(R.string.ErrorMsg403));

			parent.mHandler.post(new Runnable() {

				@Override
				public void run() {
					parent.showToast(parent.getString(R.string.ErrorMsg403));

				}

			});
			Log.w("Android Mobile Voting", "403 Error");
			parent.finish();
			return false;
		}

		if (data.contains("404")) {
			// Nothing to display
			parent.mHandler.post(new Runnable() {

				@Override
				public void run() {
					parent.showToast(parent.getString(R.string.ErrorMsg404));

				}

			});
			// parent.showToast(parent.getString(R.string.ErrorMsg404));
			Log.w("Android Mobile Voting", "404 Error");
			parent.finish();
			return false;
		}

		if (data.contains("200")) {
			Log.w("Android Mobile Voting", "200 OK");
			return true;
		}
		return false;

	}

	/* (non-Javadoc)
	 * @see cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#closeConnection()
	 */
	@Override
	public void closeConnection() {
		if (connected) {
			try {
				connection.shutdown();
				parent.showToast("Disconnected from "
						+ server.getFriendlyName());
			} catch (Exception ex) {
				Log.e("Android Mobile Voting", ex.toString());
			}
			connected = false;

		}
	}

	/* (non-Javadoc)
	 * @see cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#post(int, int)
	 */
	@Override
	public void postAnswers(ArrayList<QuestionData> answers) {
		Log.i("Android Mobile Voting", "in posting method");
		String xml = XMLMaker.XMLMaker.buildAnswer(answers);
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/");
		BasicHeader head = new BasicHeader("ID", server.getLogin());
		BasicHeader h2 = new BasicHeader("Password", server.getPassword());
		BasicHeader cl = new BasicHeader("Content-length", xml.getBytes().length+"");
		BasicHttpEntity entity = new BasicHttpEntity();
		InputStream in = null;
		Log.i("Android Mobile Voting", "loading entity");
		try {
			in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Log.i("Android Mobile Voting", "setting content");
		entity.setContent(in);
		request.setEntity(entity);
		request.addHeader(head);
		request.addHeader(h2);
		request.addHeader(cl);

		try {
			connection.sendRequestHeader(request);
			connection.sendRequestEntity(request);
			connection.flush();
			Log.e("Android Mobile Voting", "sending");
		} catch (HttpException e) {
			Log.e("Android Mobile Voting",e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Android Mobile Voting",e.toString());
		}

	}
}

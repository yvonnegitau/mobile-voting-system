package cvut.fel.mobilevoting.murinrad.communications;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.UnsupportedEncodingException;

import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


import org.apache.http.*;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import org.apache.http.params.BasicHttpParams;

import org.apache.http.protocol.HttpContext;
import org.xml.sax.SAXException;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;

import android.content.Context;

import android.util.Log;


public class ConnectionHTTP extends Thread implements Runnable,
		ConnectionInterface {
	DefaultHttpClient connection;
	Socket MyClient;
	Context context;
	boolean connected = false;
	boolean hasBody = false;
	int bodySize = 0;
	QuestionsView parent;
	ServerData server;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#run()
	 */

	@Override
	public void run() {
	}

	public ConnectionHTTP(ServerData server, QuestionsView parent) {
		try {
			Log.d("Android mobile voting", "Creating Connection");
			SchemeRegistry sr = new SchemeRegistry();
			Log.d("Android mobile voting", "Creating schreg");
			Scheme http = new Scheme("http", new PlainSocketFactory(),
					server.getPort());
			Log.d("Android mobile voting", "scheme");
			sr.register(http);
			Log.d("Android mobile voting", "sch registered");
			BasicHttpParams params = new BasicHttpParams();
			params.setParameter("test", "in order");
			SingleClientConnManager sccm = new SingleClientConnManager(params,
					sr);
			Log.d("Android mobile voting", "Connection Manager Created");
			this.connection = new DefaultHttpClient(sccm, params);
			this.connection.addResponseInterceptor(new MyInterceptor());
			Log.d("Android mobile voting", "bound");
			// this.connection.addResponseInterceptor(new )
			this.parent = parent;
			this.server = server;

			postAndRecieve("GET", "/", null, null);
			Log.d("Android mobile voting", "request sent");
			this.connected = true;
		} catch (Exception ex) {
			Log.e("Android Mobile Voting", ex.toString());
			parent.showToast(ex.toString());
			parent.finish();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#sendReq
	 * ()
	 */

	/**
	 * Parses the responce code
	 * 
	 * @param data
	 * @return
	 */
	private boolean parseResponceCode(String data) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#
	 * closeConnection()
	 */
	@Override
	public void closeConnection() {
		if (connected) {
			try {
				// connection.shutdown();
				parent.showToast("Disconnected from "
						+ server.getFriendlyName());
			} catch (Exception ex) {
				Log.e("Android Mobile Voting", ex.toString());
			}
			connected = false;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cvut.fel.mobilevoting.murinrad.communications.ConnectionInterface#post
	 * (int, int)
	 */
	public void postAnswers(ArrayList<QuestionData> answers) {
		Log.i("Android Mobile Voting", "in posting method");
		String xml = XMLMaker.XMLMaker.buildAnswer(answers);
		postAndRecieve("POST", "/", null, xml);
	}

	public void postAndRecieve(String method, String URL,
			ArrayList<BasicHeader> headers, String body) {
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
				method, URL);
		BasicHeader head = new BasicHeader("ID", server.getLogin());
		BasicHeader h2 = new BasicHeader("Password", server.getPassword());
		BasicHeader cl = null;
		if (body != null) {
			cl = new BasicHeader("Content-length", body.getBytes().length + "");
			Log.d("Android mobile voting", "Calculating payload size");
		}

		BasicHttpEntity entity = new BasicHttpEntity();
		if (body != null) {
			InputStream in = null;
			Log.i("Android Mobile Voting", "loading entity");
			try {
				in = new ByteArrayInputStream(body.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			entity.setContent(in);
			request.setEntity(entity);
		}
		request.addHeader(head);
		request.addHeader(h2);
		if (cl != null)
			Log.d("Android mobile voting", "Added payload size");
			request.addHeader(cl);

		if (headers != null) {
		}

		try {
			HttpHost g = new HttpHost(server.getAddress(), server.getPort());
			// connection.
			connection.execute(g, request);
			/*
			 * connection.sendRequestHeader(request);
			 * connection.sendRequestEntity(request); connection.flush();
			 */
			// recieveResponce();
			Log.e("Android Mobile Voting", "sending");
		} catch (IOException e) {
			Log.e("Android Mobile VotingS&RError", e.toString());
		}
	}

	/*private void recieveResponce() throws HttpException, IOException {
		
		 * HttpResponse res = connection.receiveResponseHeader();
		 * parseResponceCode(res.getStatusLine().getStatusCode()+""); Header[]
		 * cl = res.getHeaders("Content-length"); if(cl!=null &&
		 * !cl[0].getValue().equals("0")){
		 * 
		 * //connection.receiveResponseEntity(new Basi)
		 * 
		 * }
		 

	}*/

	public class MyInterceptor implements HttpResponseInterceptor {

		@Override
		public void process(HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			parseResponceCode(response.getStatusLine().getStatusCode() + "");
			Log.d("Android mobiel voting", "IM INT THE INTERCEPTOR");
			Header[] cl = response.getHeaders("Content-length");
			Log.d("Android mobiel voting", "content length");
			if (cl != null && !cl[0].getValue().equals("0")) {
				Log.d("Android mobiel voting", "Goodies!");
				HttpEntity hE = response.getEntity();
				Log.d("Android mobiel voting", "Whats innit?");
				byte[] buffer = new byte[Integer.parseInt(cl[0].getValue())];
				Log.d("Android mobiel voting", "Delicious cake?");
				hE.getContent().read(buffer);
				Log.d("Android mobiel voting", "You shouldnt have");
				try {
					XMLParser.XMLParser.parseQuestionXML(new String(buffer)	,parent);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// connection.receiveResponseEntity(new Basi)

			}

		}

	}
}

package cvut.fel.mobilevoting.murinrad.communications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.UnsupportedEncodingException;

import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateEncodingException;

import org.apache.http.*;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BasicHttpEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import org.apache.http.params.BasicHttpParams;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.crypto.Base64;
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
	int port = 0;
	QuestionsView parent;
	ServerData server;
	SchemeRegistry schemeRegistry = null;
	String usingScheme = "http";
	ConnectionHTTP instance;

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
			this.parent = parent;
			this.server = server;
			port = server.getPort();
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			schemeRegistry = new SchemeRegistry();

			Scheme http = new Scheme("http", new PlainSocketFactory(), port);

			schemeRegistry.register(http);

			HttpPost post = new HttpPost(server.getAddress());
			SingleClientConnManager cm = new SingleClientConnManager(
					post.getParams(), schemeRegistry);

			connection = new DefaultHttpClient(cm, params);
			this.connection.addResponseInterceptor(new MyInterceptor(this));
			postAndRecieve("OPTIONS", "/", null, null, true);
			instance = this;

		} catch (IllegalStateException ex) {

		} catch (Exception ex) {
			Log.e("Android Mobile Voting", ex.toString());
			parent.showToast(ex.toString());
			parent.finish();
		}

	}

	public void InitializeSecure(int sslPort) {
		if (sslPort != -1) {
			port = sslPort;
			try {
				KeyStore trusted = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trusted.load(null, null);
				SSLSocketFactory sslf = null;
				try {
					sslf = new SSLSocketFactory(trusted);

					sslf.connectSocket(sslf.createSocket(),
							server.getAddress(), sslPort, null, 0,
							new BasicHttpParams()).close();
					postAndRecieve("GET", "/", null, null, true);
				} catch (SSLException ex) {
					SSLSession ssls = null;
					SSLSocket s = null;
					try {
						sslf = new MySSLSocketFactory(trusted);
						sslf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
						s = (SSLSocket) sslf.connectSocket(sslf.createSocket(),
								server.getAddress(), sslPort, null, 0,
								new BasicHttpParams());
					} catch (SSLException e) {
						Log.w("Android mobile voing CERT CHECK", e.toString());
					}
					s.startHandshake();
					ssls = s.getSession();
					final javax.security.cert.X509Certificate[] x = ssls
							.getPeerCertificateChain();

					for (int i = 0; i < x.length; i++) {

						parent.mHandler.post(new Runnable() {

							@Override
							public void run() {

								try {
									parent.askForTrust(getThumbPrint(x[0]),
											instance);
								} catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (CertificateEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (final Exception ex) {
									parent.mHandler.post(new Runnable() {

										@Override
										public void run() {
											parent.showToast(ex.toString());

										}

									});
									Log.w("Android Mobile Voting", "400 Error");
									parent.finish();
								}

							}
						});

					}

				}
				Scheme https = new Scheme("https", sslf, sslPort);
				schemeRegistry.register(https);
				usingScheme = "https";
				port = sslPort;
			} catch (final Exception ex) {
				parent.mHandler.post(new Runnable() {

					@Override
					public void run() {
						parent.showToast(ex.toString());

					}

				});
				// Log.w("Android Mobile Voting", "400 Error");
				parent.finish();

			}
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
	public boolean parseResponceCode(String data) {
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
			parent.showToast(parent.getString(R.string.ErrorMsg404));
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
		parent.finish();
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
		postAndRecieve("POST", "/", null, xml, true);
	}

	public void postAndRecieve(String method, String URL,
			ArrayList<BasicHeader> headers, String body, boolean authenticate) {
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
				method, URL);

		BasicHeader head = null;
		BasicHeader h2 = null;
		if (authenticate) {
			head = new BasicHeader("ID", server.getLogin());
			h2 = new BasicHeader("Password", server.getPassword());
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

		if (headers != null) {
		}

		try {
			HttpHost g = new HttpHost(server.getAddress(), port, usingScheme);
			connection.execute(g, request);

			Log.e("Android Mobile Voting", "sending");
		} catch (final IOException e) {
			parent.mHandler.post(new Runnable() {

				@Override
				public void run() {
					parent.showToast(e.toString());

				}

			});
			Log.w("Android Mobile Voting", "400 Error");
			parent.finish();
			// Log.e("Android Mobile VotingError", e.toString());
		}
	}

	@Override
	public QuestionsView getParent() {

		return parent;
	}

	/**
	 * 
	 * 
	 * http://stackoverflow.com/questions/1270703/how-to-retrieve-compute-an-
	 * x509-certificates-thumbprint-in-java
	 * 
	 * @param cert
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateEncodingException
	 */
	public static String getThumbPrint(javax.security.cert.X509Certificate cert)
			throws NoSuchAlgorithmException, CertificateEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] der = null;
		der = cert.getEncoded();
		md.update(der);
		byte[] digest = md.digest();
		return hexify(digest);

	}

	public static String hexify(byte bytes[]) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer buf = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}

		return buf.toString();
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {

					Log.w("Android mobile voting", "CERTIFICATE ERROR1");
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					Log.w("Android mobile voting", "CERTIFICATE ERROR1");
				}

				public X509Certificate[] getAcceptedIssuers() {
					Log.w("Android mobile voting", "CERTIFICATE ERROR1");
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public void permitException() {
		postAndRecieve("GET", "/", null, null, true);

	}

}

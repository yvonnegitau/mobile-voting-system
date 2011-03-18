/*
 * © 2010, Jakub Valenta
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Jakub Valenta
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors “as is” and any
 * express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the foundation or contributors be liable for any direct, indirect,
 * incidental, special, exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits; or business
 * interruption) however caused and on any theory of liability, whether in contract, strict
 * liability, or tort (including negligence or otherwise) arising in any way out of the use
 * of this software, even if advised of the possibility of such damage.
 */
package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.common.Question;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cz.cvut.fel.mvod.common.Vote;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Implementace odlehčeného HTTP serveru. Zpracovává pouze požadavky
 * GET a POST.
 * @author jakub
 */
class Server {

	private static final int DEFAULT_PORT = 10666;
	private static final int CLIENT_COUNT = 100;
	private static final Server instance = new Server();
	
	private int port;
	private int client_count;
	private HttpServer server;
	private DataProvider provider;
	private boolean connected;

	public static Server getInstance() {
		return instance;
	}

	private Server() {
		port = DEFAULT_PORT;
		client_count = CLIENT_COUNT;
		connected = false;
		this.provider = NetworkAccessManager.getDataProvider();
	}

	/**
	 * Otevře síťové spojení.
	 * @throws IOException pokud selže otevření socketu
	 */
	public void connect() throws IOException {
		if(!connected) {
			server = HttpServer.create(new InetSocketAddress(port), client_count);
//			server = HttpsServer.create(new InetSocketAddress(port), client_count);
//			try {
//				SSLContext sslContext = SSLContext.getInstance("TLS");
//				KeyStore ks = KeyStore.getInstance("JKS");
//				ks.load(new FileInputStream("mvod.ks"), "qwertz".toCharArray());
//				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//				kmf.init(ks, "qwertz".toCharArray());
//				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//				tmf.init(ks);
//				final SSLEngine engine = sslContext.createSSLEngine();
//				server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
//					@Override
//					public void configure(HttpsParameters params) {
//						params.setCipherSuites(engine.getEnabledCipherSuites());
//						params.setProtocols(engine.getEnabledProtocols());
//						 // get the remote address if needed
//						InetSocketAddress remote = params.getClientAddress();
//
//						SSLContext c = getSSLContext();
//
//						// get the default parameters
//						SSLParameters sslparams = c.getDefaultSSLParameters();
//						if (remote.equals (...) ) {
//							// modify the default set for client x
//						}
//
//						params.setSSLParameters(sslparams);
//						// statement above could throw IAE if any params invalid.
//						// eg. if app has a UI and parameters supplied by a user.
//
//						}
//				});
//			} catch(NoSuchAlgorithmException ex) {
//				assert false;
//			} catch(Exception ex) {
//				assert false;
//			}
			server.createContext("/", new Handler());
			server.start();
			connected = true;
		}
	}

	/**
	 * Zavře socket.
	 */
	public void stop() {
		if(connected) {
			server.stop(0);
			connected = false;
		}
	}

	/**
	 * Obsluha HTTP požadavků.
	 * @author jakub
	 */
	class Handler implements HttpHandler {

		private static final String GET = "GET";
		private static final String POST = "POST";
		private static final String USER_NAME = "ID";
		private static final String PASSWORD = "Password";
		private static final String QUESTION = "Question";
		private static final int BAD_REQUEST = 400;
		private static final int UNAUTHORIZED = 401;
		private static final int FORBIDDEN = 403;
		private static final int NOT_FOUND = 404;
		private static final int OK = 200;

		/**
		 * Zpracuje přijatý požadavek.
		 * @param request
		 * @throws IOException 
		 */
		public void handle(HttpExchange request) throws IOException {
			try {
				String method = request.getRequestMethod();
				String userName = checkHeaders(request);
				if(userName == null)
				{
					return;
				}
				if(method.equalsIgnoreCase(GET)) {
					List<Question> questions = provider.getQuestions(userName);
					if(questions != null) {
						sendMessage(request, XMLSerializer.serializeQuestions(
								questions, provider.isPasswordNeeded(userName)));
					} else {
						sendResponse(request, NOT_FOUND);
						return;
					}
				} else if(method.equalsIgnoreCase(POST)) {
					InputStream in = request.getRequestBody();
					ByteArrayOutputStream data = new ByteArrayOutputStream();
					byte[] buffer = new byte[10];
					int length;
					while((length = in.read(buffer)) > 0) {
						data.write(buffer, 0, length);
					}
					List<Vote> votes =  XMLSerializer.parseVote(new ByteArrayInputStream(data.toByteArray()));
					provider.setResponses(userName, votes);
					sendResponse(request, OK);
				} else {
					sendResponse(request, BAD_REQUEST);
				}
			} finally {
				request.close();
			}
		}

		/**
		 * Odešle odpověď se zadaným kódem.
		 * @param request zpracovávaný požadavek
		 * @param code stavový kód
		 * @throws IOException 
		 */
		private void sendResponse(HttpExchange request, int code) throws IOException {
			request.sendResponseHeaders(code, -1);
		}

		/**
		 * Odešle odpověď se zadanou zprávou.
		 * @param request zpracovávaný požadavek
		 * @param message odesílaná zpráva
		 * @throws IOException
		 */
		private void sendMessage(HttpExchange request, byte[] message)	throws IOException {
			request.sendResponseHeaders(OK, message.length);
			OutputStream out = request.getResponseBody();
			out.write(message);
		}

		/**
		 * Otestuje zprávnost přijatého požadavku
		 * @param request zpracovávaný požadavek
		 * @return načtené uživatelské jméno
		 * @throws IOException
		 */
		private String checkHeaders(HttpExchange request) throws IOException {
//FIXME házet výjimky místo vracení null, přejemenovat/rozdělit (dělá i něco jiného než je název metody)
			Headers headers = request.getRequestHeaders();
			if(!headers.containsKey(USER_NAME)) {
				sendResponse(request, FORBIDDEN);
				return null;
			}
			String userName = headers.getFirst(USER_NAME);
			String password = headers.getFirst(PASSWORD);
			if(password == null || !provider.checkPassword(userName, password)) {
				sendResponse(request, UNAUTHORIZED);
				return null;
			}
			return userName;
		}
	}
}

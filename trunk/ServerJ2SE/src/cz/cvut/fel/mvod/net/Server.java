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
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.networkAddressRange;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.gui.settings.CertManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.*;
import javax.swing.JOptionPane;

/**
 * Implementace odlehčeného HTTP serveru. Zpracovává pouze požadavky
 * GET a POST.
 * @author jakub
 */
public class Server {

    private static final int CLIENT_COUNT = 100;
    private static final Server instance = new Server();
    private static char[] passphrase = null;

    public static void setCertPass(String passphrase) {
        Server.passphrase = passphrase.toCharArray();
    }
    private int client_count;
    private HttpServer server;
    HttpsServer secureServer;
    private DataProvider provider;
    private boolean connected;

    public static Server getInstance() {
        return instance;
    }

    private Server() {


        client_count = CLIENT_COUNT;
        connected = false;
        this.provider = NetworkAccessManager.getDataProvider();

    }

    /**
     * Otevře síťové spojení.
     * @throws IOException pokud selže otevření socketu
     */
    public void connect() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException, CertificateException, Exception {
        if (!connected) {
            server = HttpServer.create(new InetSocketAddress(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT"))), client_count);
            BeaconBroadcaster b = new BeaconBroadcaster(GlobalSettingsAndNotifier.singleton.getSetting("Server_NAME"), Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT")));
            b.start();
            server.createContext("/", new Handler());
            server.start();
            secureServer = HttpsServer.create(new InetSocketAddress(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("SSL_PORT"))), -1);
            secureServer.createContext("/", new Handler());
            secureServer.setExecutor(null);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            if (GlobalSettingsAndNotifier.singleton.getSetting("Voting_useEmbedded").equalsIgnoreCase("FALSE")) {
                while (true) {

                    try {
                        if (passphrase == null) {
                            CertManager.changeCert(CertManager.VOTING);
                        }
                        System.out.println("USING EXTERNAL CERT");
                        ks.load(new FileInputStream(GlobalSettingsAndNotifier.singleton.getSetting("Prologue_certpath")), passphrase);
                        break;
                    } catch (Exception ex) {
                        passphrase = JOptionPane.showInputDialog(null, GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallLabel"), GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallTitle"), JOptionPane.WARNING_MESSAGE).toCharArray();
                    }
                }

            } else {
                passphrase = "12345".toCharArray();
                try {
                    ks.load(new FileInputStream("server.p12"), passphrase);
                } catch (Exception ex) {
                    JOptionPane.showConfirmDialog(null, GlobalSettingsAndNotifier.singleton.messages.getString("certFail"), GlobalSettingsAndNotifier.singleton.messages.getString("errorLabel"), JOptionPane.ERROR_MESSAGE);
                    throw new Exception("CertFail");
                }
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passphrase);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            secureServer.setHttpsConfigurator(new HttpsConfigurator(ssl) {

                @Override
                public void configure(HttpsParameters params) {
                    InetSocketAddress remote = params.getClientAddress();
                    SSLContext c = getSSLContext();
                    SSLParameters sslparams = c.getDefaultSSLParameters();
                    params.setSSLParameters(sslparams);

                }
            });
            secureServer.start();
            connected = true;
        }
    }

    /**
     * Zavře socket.
     */
    public void stop() {
        if (connected) {
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
        private static final String OPTIONS = "OPTIONS";
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
        @Override
        public void handle(HttpExchange request) throws IOException {
            try {
                System.out.println("PROTOCOL = " + request.getProtocol());
                /*  if (!checkOrigin(request.getRemoteAddress(), true)) {
                System.out.println("BAD REQ");
                sendResponse(request, FORBIDDEN);

                }*/
                String method = request.getRequestMethod();
                System.out.println(method);
                if (method.equalsIgnoreCase(OPTIONS)) {
                    System.out.println("PTIONS RECU");
                    //ByteArrayOutputStream data = new ByteArrayOutputStream();
                    byte[] buffer = InfoXMLGenerator.getListenPortMSG().getBytes();
                    sendMessage(request, buffer);
                    request.close();
                    return;
                }
                System.out.println("Going to check username");
                String userName = checkHeaders(request);
                if (userName == null) {
                    System.out.println("USERNAME BAD");
                    sendResponse(request, UNAUTHORIZED);
                    request.close();

                    return;
                }
                System.out.println("Username checked");

                if (method.equalsIgnoreCase(GET)) {
                    List<Question> questions = provider.getQuestions(userName);
                    if (questions != null) {
                        sendMessage(request, XMLSerializer.serializeQuestions(
                                questions, provider.isPasswordNeeded(userName)));
                    } else {
                        sendResponse(request, NOT_FOUND);
                        return;
                    }
                } else if (method.equalsIgnoreCase(POST)) {
                    InputStream in = request.getRequestBody();
                    ByteArrayOutputStream data = new ByteArrayOutputStream();
                    byte[] buffer = new byte[10];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        data.write(buffer, 0, length);
                    }
                    List<Vote> votes = XMLSerializer.parseVote(new ByteArrayInputStream(data.toByteArray()));
                    provider.setResponses(userName, votes);
                    sendResponse(request, OK);
                } else {
                    System.out.println("BAD REQ");
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
            System.out.println("Sending " + code);
            request.sendResponseHeaders(code, -1);
        }

        /**
         * Odešle odpověď se zadanou zprávou.
         * @param request zpracovávaný požadavek
         * @param message odesílaná zpráva
         * @throws IOException
         */
        private void sendMessage(HttpExchange request, byte[] message) throws IOException {
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
            String userName = null;
            System.out.println("Checking");
            if (!headers.containsKey(USER_NAME)) {
                sendResponse(request, FORBIDDEN);
                System.out.println("problem with no u name");
                return null;
            }
            System.out.println("here");
            userName = headers.getFirst(USER_NAME);
            System.out.println("bleh");
            String password = headers.getFirst(PASSWORD);
            System.out.println("ooh");
            try {
                if (password == null || !provider.checkPassword(userName, password)) {
                    System.out.println("Bad names");
                    sendResponse(request, UNAUTHORIZED);
                    System.out.println("dasdasd");
                    return null;
                }
            } catch (Exception ex) {
                sendResponse(request, FORBIDDEN);
                ex.printStackTrace();
                return null;
            }
            System.out.println("sadsa");
            if (userName == null) {
                sendResponse(request, FORBIDDEN);
            }
            System.out.println("returned");
            return userName;
        }

        private boolean checkOrigin(InetSocketAddress remoteAddress, boolean isSecured) {
            try {
                if (GlobalSettingsAndNotifier.singleton.getSetting("RESTRICT_SECURE").equals("true") && !isSecured) {
                    return false;
                }
                String mode = GlobalSettingsAndNotifier.singleton.getSetting("NET_ORIGIN");
                if (mode.equals("NO_RESTRICTIONS")) {
                    return true;
                }
                String add = remoteAddress.getAddress().toString().replace("/", "");
                String[] parts = null;
                //add = "147.2.5.4";
                if (add.contains("-")) {
                    parts = add.split("-");
                }
                if (add.contains("\\.")) {
                    parts = add.split("\\.");
                }
                System.out.println(parts);
                System.out.println(parts.length);
                int[] remote = new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])};
                if (mode.equals("RESTRICT_LAN")) {
                    return networkAddressRange.isOnLAN(remote);
                }
                Iterator<networkAddressRange> inar = GlobalSettingsAndNotifier.singleton.permited.iterator();
                while (inar.hasNext()) {
                    networkAddressRange n = inar.next();
                    switch (n.isAllowed(remote, isSecured)) {
                        case 1:
                            return true;
                        case -1:
                            return false;

                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

            if (GlobalSettingsAndNotifier.singleton.getSetting("IMPLICIT_ALLOW").equalsIgnoreCase("true")) {

                return true;
            }

            return false;
        }
    }
}

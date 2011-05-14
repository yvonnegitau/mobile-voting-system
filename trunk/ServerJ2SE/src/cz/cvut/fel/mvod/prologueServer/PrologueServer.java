package cz.cvut.fel.mvod.prologueServer;
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

import com.sun.net.httpserver.*;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.global.Notifiable;
import cz.cvut.fel.mvod.gui.settings.CertManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import javax.swing.JOptionPane;

/**
 *  The class that represents a server that prodes voter registration and connection information
 * @author Radovan Murin
 */
public class PrologueServer implements Notifiable {

    /**
     * The server is providing information and accepting new regiestrations
     */
    public static final int STATE_REGISTERING = 1;
    /**
     * The server is only providing information to connect.
     */
    public static final int STATE_PROVIDING = 2;
    /**
     * Server offline
     */
    public static final int STATE_INACTIVE = 3;
    static char[] passphrase;
    HttpsServer s;
    SSLContext sslContext;
    HttpsServer server;

    /**
     * The constructor of the server, any exception except IOException are likely caused by a bad certificate.
     * @throws IOException the server is likely to have the port blocked.
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    public PrologueServer() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        getMyPublicIP();
        GlobalSettingsAndNotifier.singleton.addListener(this);

        server = HttpsServer.create(new InetSocketAddress(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("PROLOGUE_PORT"))), -1);
        server.createContext("/", new registeringHandler());
        server.setExecutor(null);

        KeyStore ks = KeyStore.getInstance("PKCS12");
        if (GlobalSettingsAndNotifier.singleton.getSetting("Prologue_USEDEFAULTCERT").equalsIgnoreCase("FALSE")) {




            while (true) {

                try {
                    if (passphrase == null) {
                        CertManager.changeCert(CertManager.VOTING);
                    }
                    //passphrase = "qwerty".toCharArray();
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
                return;
            }


        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(ssl) {

            @Override
            public void configure(HttpsParameters params) {
                InetSocketAddress remote = params.getClientAddress();
                SSLContext c = getSSLContext();
                SSLParameters sslparams = c.getDefaultSSLParameters();
                params.setSSLParameters(sslparams);

            }
        });
        server.start();
        GlobalSettingsAndNotifier.singleton.modifySettings("prologueState", STATE_REGISTERING + "", true);
        // GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", getMyPublicIP());




    }

    /**
     * Prevents new user registration.
     */
    private void stopRegistration() {

        server.removeContext("/");
        server.createContext("/", new ProvidingHandler());

    }

    /**
     * Stops the server.
     */
    private void stopServer() {
        GlobalSettingsAndNotifier.singleton.modifySettings("prologueState", STATE_INACTIVE + "", false);
        server.stop(1);




    }

    /**
     * Changes the server state
     * @param state the state to change the server state into
     */
    private void changeState(int state) {
        //if(state == )
        switch (state) {
            case STATE_REGISTERING:
                break;
            case STATE_PROVIDING:
                stopRegistration();
                break;
            case STATE_INACTIVE:
                stopServer();
                break;
            default:
                break;
        }
        GlobalSettingsAndNotifier.singleton.modifySettings("prologueState", state + "", false);

    }

    /**
     * returns the state the serer is currently in
     * @return
     */
    public int getState() {
        return Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("prologueState"));
    }

    @Override
    public void notifyOfChange() {
        changeState(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("prologueState")));
    }

    /**
     *
     * Snippet from http://www.daniweb.com/software-development/java/threads/62812
     * Detects the public IP and places it in the settings.
     * @return
     */
    public void getMyPublicIP() {
        try {
            URL autoIP = new URL("http://www.whatismyip.com/automation/n09230945.asp");
            BufferedReader in = new BufferedReader(new InputStreamReader(autoIP.openStream()));
            String ip_address = (in.readLine()).trim();

            GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", ip_address);

        } catch (Exception e) {
            GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", "ERROR");
            e.printStackTrace();

        }
    }
/**
 * Returns an Arraylist of addresses. The array does not contain the IP that is used on the internet nor localhost.
 * @return
 */
    public static ArrayList<InetAddress> getMyLocalIP() {

        Enumeration<NetworkInterface> interfaces = null;
        ArrayList<InetAddress> result = new ArrayList<InetAddress>();
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            Logger.getLogger(PrologueServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> iAddr = ni.getInetAddresses();
            while (iAddr.hasMoreElements()) {
                InetAddress add = iAddr.nextElement();
                try {
                    if (add.getHostAddress().contains(".") && !add.getHostAddress().equals(InetAddress.getByName("localhost").toString().split("/")[1]) && !add.getHostAddress().contains(GlobalSettingsAndNotifier.singleton.getSetting("PUBLIC_IP"))) {
                        result.add(add);
                    }
                } catch (UnknownHostException ex) {
                    Logger.getLogger(PrologueServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return result;

    }

    /**
     * Sets the password to the certificate file.
     * @param pass
     */
    public static void setCertPass(String pass) {
        passphrase = pass.toCharArray();
    }
}

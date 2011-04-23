package cz.cvut.fel.mvod.prologueServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.*;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.global.Notifiable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.*;
import java.security.cert.*;
import javax.net.ssl.*;

/**
 *
 * @author Murko
 */
public class PrologueServer implements Notifiable {

    public static final int STATE_REGISTERING = 1;
    public static final int STATE_PROVIDING = 2;
    public static final int STATE_INACTIVE = 3;
    HttpsServer s;
    SSLContext sslContext;
    HttpsServer server;

    public PrologueServer() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        getMyPublicIP();
        GlobalSettingsAndNotifier.singleton.addListener(this);

        server = HttpsServer.create(new InetSocketAddress(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("PROLOGUE_PORT"))), -1);
        server.createContext("/", new registeringHandler());
        server.setExecutor(null);

        char[] passphrase = "passphrase".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("testkeys"), passphrase);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(ssl) {

            public void configure(HttpsParameters params) {
                InetSocketAddress remote = params.getClientAddress();
                SSLContext c = getSSLContext();
                SSLParameters sslparams = c.getDefaultSSLParameters();
                params.setSSLParameters(sslparams);

            }
        });
        server.start();
        GlobalSettingsAndNotifier.singleton.modifySettings("prologueState", STATE_REGISTERING + "",true);
       // GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", getMyPublicIP());




    }

    private void stopRegistration() {

        server.removeContext("/");
        server.createContext("/", new ProvidingHandler());

    }

    private void stopServer() {

        server.stop(1);
        



    }

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
        GlobalSettingsAndNotifier.singleton.modifySettings("prologueState", state + "",false);

    }

    public int getState() {
        return Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("prologueState"));
    }

    @Override
    public void notifyOfChange() {
        changeState(Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("prologueState")));
    }

/**
 * Snippet from http://www.daniweb.com/software-development/java/threads/62812
 * @return
 */
    public void getMyPublicIP(){
        try {
            URL autoIP = new URL("http://www.whatismyip.com/automation/n09230945.asp");
            BufferedReader in = new BufferedReader( new InputStreamReader(autoIP.openStream()));
            String ip_address = (in.readLine()).trim();

            GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", ip_address);

         }catch (Exception e){
             GlobalSettingsAndNotifier.singleton.modifySettings("PUBLIC_IP", "ERROR");
	    	e.printStackTrace();

            	    }
    }
    
}

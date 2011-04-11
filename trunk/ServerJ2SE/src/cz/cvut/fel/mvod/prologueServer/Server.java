/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.prologueServer;

//import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Murko
 */
public class Server {

    HttpsServer s;
    SSLContext sslContext;
   // public static Server httpsServer = new Server();

    public Server() {
       
      
try{
        HttpsServer server = HttpsServer.create(new InetSocketAddress(10443), -1);
        server.createContext("/", new customHandler());
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
                params.setSSLParameters (sslparams);
               
            }

            
         
        });

        server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

}
}

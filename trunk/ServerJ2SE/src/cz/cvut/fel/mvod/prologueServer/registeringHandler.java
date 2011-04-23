package cz.cvut.fel.mvod.prologueServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmlpull.v1.XmlPullParserException;
import java.util.HashMap;

/**
 *
 * @author Murko
 */
public class registeringHandler implements HttpHandler {

    XMLFactory wpb = new XMLFactory();

    public void handle(HttpExchange he) throws IOException {
        if (he.getRequestMethod().equalsIgnoreCase("GET")) {
            InputStream is = he.getRequestBody();
            String URI = he.getRequestURI().toString();
            System.out.println(URI);
            System.out.println(he.getRequestHeaders().values());
            String responce = "";
            try {
                if (URI.equals("/")) {
                    responce = generateMainWebPage();
                } else if (URI.equals("/registration")) {
                    responce = generateRegWebPage();
                } else {
                    responce = GlobalSettingsAndNotifier.singleton.messages.getString("404Error");
                }
            } catch (XmlPullParserException ex) {
                Logger.getLogger(registeringHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStream s = he.getResponseBody();
            is.close();
            he.sendResponseHeaders(200, responce.getBytes().length);
            s.write(responce.getBytes());
            s.close();
        } else if (he.getRequestMethod().equalsIgnoreCase("POST")) {

            InputStream is = he.getRequestBody();
            OutputStream out = he.getResponseBody();
            String responce = "OK";
            String msg = null;
            int bodyL = Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"));

            byte[] mesidz = new byte[bodyL];
            is.read(mesidz);
            msg = new String(mesidz);
            responce = parsePost(msg);
            he.sendResponseHeaders(200, responce.getBytes().length);
            out.write(responce.getBytes());
            out.close();






            //System.out.println("Message stands " + new String(mesidz));
        } else {
            String responce = "<h2> hohoho</h2>";
            OutputStream s = he.getResponseBody();
            s.write(responce.getBytes());
            s.flush();
            he.sendResponseHeaders(200, responce.getBytes().length);
        }

    }

    private boolean passCheck(String p1, String p2) {
        return p1.equals(p2);
    }

    protected String generateMainWebPage() throws XmlPullParserException, IOException {
        wpb = new XMLFactory();
        return wpb.makeIntroPage("147.32.89.127", 10666);
    }

    protected String generateRegWebPage() {
        FileOperator fr = new FileOperator();
        return fr.getWholeTextFile("regpage.html");
    }

    protected String parsePost(String body) {
        String[] tokens = body.split("&");


        HashMap<String, String> pairs = new HashMap<String, String>();
        try {
            for (int i = 0; i < tokens.length; i++) {

                String[] pair = tokens[i].split("=");
                pairs.put(pair[0], pair[1]);
            }
        } catch (Exception ex) {
            return "<p>"+GlobalSettingsAndNotifier.singleton.messages.getString("fillAllMSG")+"</p>";


        }
        if (!passCheck(pairs.get("pass1"), pairs.get("pass2"))) {
            return "<p>"+GlobalSettingsAndNotifier.singleton.messages.getString("passMismatchErr")+"</p>";
        }
        wpb = new XMLFactory();
        if (!wpb.addRegistrationEntry(pairs)) {
            return "<p>"+GlobalSettingsAndNotifier.singleton.messages.getString("usernameExistsErr")+"</p>";
        }
        return "OK";

    }

}

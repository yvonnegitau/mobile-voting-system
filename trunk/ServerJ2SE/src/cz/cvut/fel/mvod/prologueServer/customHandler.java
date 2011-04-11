/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.prologueServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmlpull.v1.XmlPullParserException;
import java.util.HashMap;

/**
 *
 * @author Murko
 */
public class customHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        if (he.getRequestMethod().equalsIgnoreCase("GET")) {
            InputStream is = he.getRequestBody();
            String URI = he.getRequestURI().toString();
            System.out.println(URI);
            System.out.println(he.getRequestHeaders().values());
            String responce = "";
            try {
                if (URI.equals("/")) {
                    XMLFactory wpb = new XMLFactory();
                    responce = wpb.makeIntroPage("147.32.89.127", 10666);
                } else if (URI.equals("/registration")) {
                    FileOperator fr = new FileOperator();
                    responce = fr.getWholeTextFile("regpage.html");
                } else {
                    responce = "chevron seven, will not lock";
                }
            } catch (XmlPullParserException ex) {
                Logger.getLogger(customHandler.class.getName()).log(Level.SEVERE, null, ex);
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
            String[] tokens = msg.split("&");


            HashMap<String, String> pairs = new HashMap<String, String>();
            try {
                for (int i = 0; i < tokens.length; i++) {
                  
                    String[] pair = tokens[i].split("=");
                    pairs.put(pair[0], pair[1]);
                }
            } catch (Exception ex) {
                responce = "<p>Prosim vyplnte vsetky udaje</p>";
                 he.sendResponseHeaders(200, responce.getBytes().length);
                out.write(responce.getBytes());
                out.close();
               
            }
            if(!passCheck(pairs.get("pass1"), pairs.get("pass2"))) {
                responce = "<p>Hesla sa nezhoduju, prosim zadajte rovnake hesla</p>";
            }
             XMLFactory wpb = new XMLFactory();
             if(!wpb.addRegistrationEntry(pairs)) {
                 responce = "<p>Username in use, please use another</p>";
             }
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
}

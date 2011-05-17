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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.global.Notifiable;
import cz.cvut.fel.mvod.gui.ErrorDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmlpull.v1.XmlPullParserException;
import java.util.HashMap;
import javax.swing.Timer;

/**
 * An HTTP handler that handles incoming traffic to the prologue server. It handles new user creating and information web page output.
 * 
 * @author Radovan Murin
 */
public class registeringHandler implements HttpHandler, Notifiable {

    boolean showedWI = false;
    boolean showedWR = false;
    XMLFactory wpb = new XMLFactory();
    webPageLocalizer introPage;
    webPageLocalizer regPage;
    HashMap<String, WebPageFetcher> webPages;
    String CSS = "";

    /**
     *
     * The constructor for this class, initializes the base stuff
     */
    public registeringHandler() {
        loadPages();
        int delay = 30000;
        ActionListener taskPerformer = new ActionListener() {

            @Override
            /**
             * Page refresher so that the server doesnt need to be restarted after a change.
             * Every 30 secs.
             * */
            public void actionPerformed(ActionEvent evt) {
                loadPages();
            }
        };
        new Timer(delay, taskPerformer).start();
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        if (he.getRequestMethod().equalsIgnoreCase("GET")) {
            InputStream is = he.getRequestBody();
            String URI = he.getRequestURI().toString();
            String msg = null;
            String aLang = he.getRequestHeaders().getFirst("Accept-language");
            String[] langs = aLang.split(",");
            String responce = "";
            Headers heads = he.getResponseHeaders();


            if (URI.equals("/")) {

                heads.add("Content-Type", "text/html");
                responce = introPage.getWP(langs);
                if (!introPage.isLoaded()) {
                    try {
                        responce = generateMainWebPage();
                    } catch (XmlPullParserException ex) {
                        Logger.getLogger(registeringHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (URI.contains("favicon")) {
                responce = "null";
            } else if (URI.contains("css")) {
                heads.add("Content-Type", "text/css");
                responce = CSS;

            } else if (URI.equals("/registration")) {
                heads.add("Content-Type", "text/html");
                responce = regPage.getWP(langs);
                if (!regPage.isLoaded()) {
                    responce = generateRegWebPage();
                }

            } else {
                String file = URI.replaceAll(".*/", "");

                String dir = URI.replaceAll(file, "");
         

                responce = GlobalSettingsAndNotifier.singleton.messages.getString("404Error");
                if (dir.charAt(0) == '/') {
                    dir = dir.replaceFirst("/", "");
                }
               
                WebPageFetcher pages = webPages.get(dir);
                String[] tst = {"en"};
                /**
                 * Tests if the pages are loaded, or loaded with errors and attempts a load
                 */
                if (pages == null || pages.sitesLoc.values().iterator().next().getWP(tst).contains("error")) {
                    pages = new WebPageFetcher("summary.sum", dir);

                    webPages.put(dir, pages);
                }
                if (file.equals("")) {
                    file = "index.html";
                }


                responce = pages.fetch(file, langs);



            }
            if (responce.equals("")) {
                heads.add("Content-Type", "text/html");
                responce = GlobalSettingsAndNotifier.singleton.messages.getString("404Error");
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
        } else {
            String responce = "<h2> hohoho</h2>";
            OutputStream s = he.getResponseBody();
            s.write(responce.getBytes());
            s.flush();
            he.sendResponseHeaders(200, responce.getBytes().length);
        }

    }

    /**
     * Verifies that the inputted passwords match
     * @param p1 password1
     * @param p2 password2
     * @return true if the passwords match
     */
    private boolean passCheck(String p1, String p2) {
        return p1.equals(p2);
    }

    /**
     * @deprecated
     * Returns the fallback introduction web page
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected String generateMainWebPage() throws XmlPullParserException, IOException {
       
        wpb = new XMLFactory();
        return wpb.makeIntroPage(GlobalSettingsAndNotifier.singleton.getSetting("PUBLIC_IP"), Integer.parseInt(GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT")));
    }

    /**
     * @deprecated
     * Returns the registration web page
     * @return the string representation of the web page.
     */
    protected String generateRegWebPage() {
       
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \" http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">	<head>		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />		<title>Mobile Voting Server - Radovan Murin</title>	</head>	<h1>Voter Registration</h1>	<body>		<form name=\"input\" action=\"index.html\" method=\"post\">			Name:			<input type=\"text\" name=\"name\" />			<br />			Surname:			<input type=\"text\" name=\"surname\" />			<br />			Username :			<input type=\"text\" name=\"username\" />			<br />			Identification :			<input type=\"text\" name=\"ID\" />			<br />			Password :			<input type=\"text\" name=\"pass1\" />			<br />			Password again :			<input type=\"text\" name=\"pass2\" />			<br />			<input type=\"submit\" value=\"Submit\" />			<br />		</form>	</body>/html>";
    }

    /**
     * Parses the body of the post method.
     * @param body the body that needs parsing
     * @return the message that is to applear to the end user, informing the succes/failure of the request.
     */
    protected String parsePost(String body) {
        System.out.println(body);
        String[] tokens = body.split("&");


        HashMap<String, String> pairs = new HashMap<String, String>();
        try {
            for (int i = 0; i < tokens.length; i++) {

                String[] pair = tokens[i].split("=");
                pairs.put(pair[0], pair[1]);
            }
        } catch (Exception ex) {
            return "<p>" + GlobalSettingsAndNotifier.singleton.messages.getString("fillAllMSG") + "</p>";


        }
        if (!passCheck(pairs.get("pass1"), pairs.get("pass2"))) {
            return "<p>" + GlobalSettingsAndNotifier.singleton.messages.getString("passMismatchErr") + "</p>";
        }
        wpb = new XMLFactory();
        if (!wpb.addRegistrationEntry(pairs)) {
            return "<p>" + GlobalSettingsAndNotifier.singleton.messages.getString("usernameExistsErr") + "</p>";
        }
        return "OK";

    }

    /**
     * Loads the base pages and resets the cached other pages
     */
    private void loadPages() {
        introPage = new webPageLocalizer("index", "webpages");
        introPage.getWP(new String[]{"en"});
        if (!introPage.loadSuccess) {
             if (!showedWI) {
            new ErrorDialog(null, true, GlobalSettingsAndNotifier.singleton.messages.getString("mainPageErr")).setVisible(true);
            showedWI = true;
        }
            try {
                introPage.webPage.clear();
                introPage.webPage.put("en", generateMainWebPage());
            } catch (XmlPullParserException ex) {
                Logger.getLogger(registeringHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(registeringHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        regPage = new webPageLocalizer("regpage", "webpages");
        regPage.getWP(new String[]{"en"});
        if (!regPage.loadSuccess) {
            if (!showedWR) {
                new ErrorDialog(null, true, GlobalSettingsAndNotifier.singleton.messages.getString("regPageErr")).setVisible(true);
                showedWR=true;
            }
            regPage.webPage.clear();
            
            regPage.webPage.put("en", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \" http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">	<head>		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />		<title>Mobile Voting Server - Radovan Murin</title>	</head>	<h1>Voter Registration</h1>	<body>		<form name=\"input\" action=\"index.html\" method=\"post\">			Name:			<input type=\"text\" name=\"name\" />			<br />			Surname:			<input type=\"text\" name=\"surname\" />			<br />			Username :			<input type=\"text\" name=\"username\" />			<br />			Identification :			<input type=\"text\" name=\"ID\" />			<br />			Password :			<input type=\"text\" name=\"pass1\" />			<br />			Password again :			<input type=\"text\" name=\"pass2\" />			<br />			<input type=\"submit\" value=\"Submit\" />			<br />		</form>	</body>/html>");
        }
        webPages = new HashMap<String, WebPageFetcher>();
        FileOperator fo = new FileOperator();
        CSS = fo.getWholeTextFile("style.css");

    }

    @Override
    public void notifyOfChange() {
    }
}

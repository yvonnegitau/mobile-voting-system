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
import cz.cvut.fel.mvod.crypto.Base64;
import cz.cvut.fel.mvod.crypto.CryptoUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * A class that creates XMLs handles creates XMLs
 * @author Radovan Murin
 */
public class XMLFactory {

    private final static String NAMESPACE = "http://www.w3.org/1999/xhtml";

    public XMLFactory() {
    }
    /**
     * Adds a registrant into persistance
     * @param values the values of the registrant
     * @return true if succesful
     */
    public boolean addRegistrationEntry(HashMap<String, String> values) {
        try {
            File file = new File("registrations.xml");
            if(file.createNewFile()) {
                OutputStream f0;
            byte buf[] = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><registrations></registrations>".getBytes();
            f0 = new FileOutputStream("registrations.xml");
            for (int i = 0; i < buf.length; i++) {
                f0.write(buf[i]);
            }
            f0.flush();
            f0.close();
                
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            Element root = doc.getDocumentElement();
            if (root == null) {
                return false;
            }


            NodeList persons = root.getElementsByTagName("person");
            for (int i = 0; i < persons.getLength(); i++) {
                Node p = persons.item(i);
                NodeList childs = p.getChildNodes();
                for (int o = 0; o < childs.getLength(); o++) {
                    if (childs.item(o).getNodeName().equals("username")) {
                        if (childs.item(o).getTextContent().equals(values.get("username"))) {
                            return false;
                        }
                    }
                }
            }


            Element person = doc.createElement("person");
            Element username = doc.createElement("username");
            username.setTextContent(values.get("username"));
            Element name = doc.createElement("name");
            name.setTextContent(values.get("name"));
            Element surname = doc.createElement("surname");
            surname.setTextContent(values.get("surname"));
            Element ID = doc.createElement("id");
            ID.setTextContent(values.get("ID"));
            Element pass = doc.createElement("pass");
            pass.setTextContent(new String(Base64.encode(CryptoUtils.passwordDigest(values.get("pass1"), values.get("username")),Base64.DEFAULT)));
            person.appendChild(username);
            person.appendChild(name);
            person.appendChild(surname);
            person.appendChild(ID);
            person.appendChild(pass);
            root.appendChild(person);



            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();
            OutputStream f0;
            byte buf[] = xmlString.getBytes();
            f0 = new FileOutputStream("registrations.xml");
            for (int i = 0; i < buf.length; i++) {
                f0.write(buf[i]);
            }
            f0.close();
            buf = null;




        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }
/**
 * Generates an introductory web page
 * WARNING, this method is now to be used only as fallback when the "index.html" is not available
 * @param IP the external IP of the server
 * @param port the port of the server
 * @return the String containing the web page
 * @throws XmlPullParserException
 * @throws IOException
 */
    public String makeIntroPage(String IP, int port) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        XmlSerializer serializer = factory.newSerializer();
        String xml = new String();
        StringWriter os = new StringWriter();
        serializer.setOutput(os);
        serializer.setPrefix("", NAMESPACE);
        serializer.startTag(NAMESPACE, "html");
        serializer.startTag(NAMESPACE, "head");
        serializer.startTag(NAMESPACE, "meta");
        serializer.attribute(null, "http-equiv", "Content-Type");
        serializer.attribute(null, "content", "text/html; charset=UTF-8");
        serializer.endTag(NAMESPACE, "meta");
        serializer.startTag(NAMESPACE, "title");
        serializer.text("Mobile Voting Server - Radovan Murin");
        serializer.endTag(NAMESPACE, "title");
        serializer.endTag(NAMESPACE, "head");

        serializer.startTag(NAMESPACE, "body");
        serializer.startTag(NAMESPACE, "h1");
        serializer.text("Vitajte v systéme pre mobilné hlasovanie");
        serializer.endTag(NAMESPACE, "h1");

        serializer.startTag(NAMESPACE, "p");
        serializer.text("Na tejto stránke získate všetky potrebné informácie pre registráciu a pripojenie sa k hlasoveciemu zariadeniu.");
        serializer.endTag(NAMESPACE, "p");

        serializer.startTag(NAMESPACE, "h2");
        serializer.text("Registrácia voliča ");
        serializer.endTag(NAMESPACE, "h2");


        serializer.startTag(NAMESPACE, "p");
        serializer.text("Pre registráciu, prosim nasledujte");
        serializer.startTag(NAMESPACE, "a");
        serializer.attribute(null, "href", "registration");
        serializer.text(" tento odkaz.");

        serializer.endTag(NAMESPACE, "a");
        serializer.endTag(NAMESPACE, "p");


        serializer.startTag(NAMESPACE, "h2");
        serializer.text("Pripojenie prostredíctvom internetu");
        serializer.endTag(NAMESPACE, "h2");


        serializer.startTag(NAMESPACE, "p");
        serializer.text("Ak sa pripájate prostredníctvom internetu, to znamená, že sa nenachádzate v sieti hlasovacej centrály, použite nasledujúce adresy pre pripojenie");
        serializer.endTag(NAMESPACE, "p");

        serializer.startTag(NAMESPACE, "table");
        serializer.attribute(null, "border", 1 + "");
        serializer.startTag(NAMESPACE, "thead");

        serializer.startTag(NAMESPACE, "tr");

        serializer.startTag(NAMESPACE, "th");
        serializer.text("IP");
        serializer.endTag(NAMESPACE, "th");

        serializer.startTag(NAMESPACE, "th");
        serializer.text("Port");
        serializer.endTag(NAMESPACE, "th");

        serializer.endTag(NAMESPACE, "tr");


        serializer.endTag(NAMESPACE, "thead");



        serializer.startTag(NAMESPACE, "tbody");
        serializer.startTag(NAMESPACE, "tr");
        serializer.startTag(NAMESPACE, "td");
        serializer.text(IP);
        serializer.endTag(NAMESPACE, "td");
        serializer.startTag(NAMESPACE, "td");
        serializer.text(port + "");
        serializer.endTag(NAMESPACE, "td");

        serializer.endTag(NAMESPACE, "tr");
        serializer.endTag(NAMESPACE, "tbody");


        serializer.endTag(NAMESPACE, "table");

        serializer.startTag(NAMESPACE, "h2");
        serializer.text("Pripojenie prostredíctvom miestnej siete");
        serializer.endTag(NAMESPACE, "h2");

        serializer.startTag(NAMESPACE, "p");
        serializer.text("Týmto pripojením sa rozumie, ak sa hlasovacie zariadenie nachádza v rovnakej miestnej sieti."
                + "Tieto nastavenia nastavujte ručne len v prípade, že Vaše zariadenie nepodporuje automaticku konfiguráciu a hlasovacia základňa"
                + " sa nezobrazuje v ponuke zariadenia.");
        serializer.endTag(NAMESPACE, "p");

        serializer.startTag(NAMESPACE, "h4");
        serializer.text("Nastavenie pre lokálnu komunikáciu");
        serializer.endTag(NAMESPACE, "h4");


        serializer.startTag(NAMESPACE, "table");
        serializer.attribute(null, "border", 1 + "");
        serializer.startTag(NAMESPACE, "thead");

        serializer.startTag(NAMESPACE, "tr");

        serializer.startTag(NAMESPACE, "th");
        serializer.text("IP");
        serializer.endTag(NAMESPACE, "th");

        serializer.startTag(NAMESPACE, "th");
        serializer.text("Port");
        serializer.endTag(NAMESPACE, "th");

        serializer.endTag(NAMESPACE, "tr");


        serializer.endTag(NAMESPACE, "thead");



        serializer.startTag(NAMESPACE, "tbody");
        serializer.startTag(NAMESPACE, "tr");
        serializer.startTag(NAMESPACE, "td");
        serializer.text("192.168.2.1");
        serializer.endTag(NAMESPACE, "td");
        serializer.startTag(NAMESPACE, "td");
        serializer.text(port + "");
        serializer.endTag(NAMESPACE, "td");

        serializer.endTag(NAMESPACE, "tr");
        serializer.endTag(NAMESPACE, "tbody");


        serializer.endTag(NAMESPACE, "table");


        serializer.endTag(NAMESPACE, "body");
        serializer.endTag(NAMESPACE, "html");
        serializer.endDocument();

        System.out.println("********************************");

        return os.toString();
    }
}

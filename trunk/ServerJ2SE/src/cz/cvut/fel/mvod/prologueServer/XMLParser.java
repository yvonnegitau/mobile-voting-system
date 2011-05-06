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


import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.crypto.Base64;
import cz.cvut.fel.mvod.prologueServer.FileOperator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class used for loading and parsings XMLs
 * @author Radovan Murin
 */
public class XMLParser {
/**
 * Reads the registrations file and outputs the registrants
 * @return a Hashmap of registrants
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws UnsupportedEncodingException
 * @throws IOException
 */
    public HashMap<String, List<Voter>> getRegistrants() throws ParserConfigurationException, SAXException, UnsupportedEncodingException, IOException {
        FileOperator fo = new FileOperator();
        String XML = fo.getWholeTextFile("registrations.xml");
        HashMap<String, List<Voter>> registrants = new HashMap<String, List<Voter>>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        InputStream in = new ByteArrayInputStream(XML.getBytes("UTF-8"));
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        doc.getDocumentElement().normalize();
        NodeList people = doc.getElementsByTagName("person");
        for(int i=0;i<people.getLength();i++) {
            Element person = (Element) people.item(i);
            String name = person.getElementsByTagName("name").item(0).getTextContent();
            String surname = person.getElementsByTagName("surname").item(0).getTextContent();
            String login = person.getElementsByTagName("username").item(0).getTextContent();
            String identifier = person.getElementsByTagName("id").item(0).getTextContent();
            byte[] password = Base64.decode(person.getElementsByTagName("pass").item(0).getTextContent(),Base64.DEFAULT);
            Voter v = new Voter(name, surname, password, login);
            List<Voter> lv = registrants.get(identifier);
            if (lv == null) lv = new ArrayList<Voter>() {};
            lv.add(v);
            registrants.put(identifier, lv);
            System.out.println("Identifikator " + identifier);
        }
        return registrants;
    }
}

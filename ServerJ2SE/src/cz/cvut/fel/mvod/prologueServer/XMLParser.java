package cz.cvut.fel.mvod.prologueServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Murko
 */
public class XMLParser {

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

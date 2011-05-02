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
package cvut.fel.mobilevoting.murinrad.communications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;
/**
 * An XML parser that parses the XMLs that can be recieved in the communications
 * @author Radovan Murin
 *
 */
public class XMLParser {
	/**
	 * The singleton instance of this class, does all the work
	 */
	public static final XMLParser XMLParser = new XMLParser();

	private XMLParser() {
	}
/**
 * parses a server connection XML
 * @param XML the xml
 * @param surface a surface to display errors
 * @param server the server that does the connection
 * @throws SAXException
 * @throws IOException
 * @throws ParserConfigurationException
 */
	protected void parseServerXML(String XML, final QuestionsView surface,
			Connection server) throws SAXException, IOException,
			ParserConfigurationException {
		Document doc = preprocess(XML);
		Element root = doc.getDocumentElement();
		if (root.getNodeName().equals("listenports")) {
			NodeList nl = root.getElementsByTagName("port");
			int p = -1;
			for (int i = 0; i < nl.getLength(); i++) {
				Element port = (Element) nl.item(i);
				if (port.getAttribute("secure").equals("true"))
					p = Integer.parseInt(getNodeValue(port));
				Log.d("Android mobile voting port is ", p+"");
			}
			server.InitializeSecure(p);

		} else if (root.getNodeName().equals("voting")) {
			Log.d("parsing", "parsing");
			parseQuestionXML(XML, surface);
		}

	}

	/**
	 * Parses the XML string into question objects and passes them to the View
	 * that will display them
	 * 
	 * @param XML
	 * @param surface
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	protected void parseQuestionXML(String XML, final QuestionsView surface)
			throws SAXException, IOException, ParserConfigurationException {
		final ArrayList<QuestionData> questions = new ArrayList<QuestionData>();
		Document doc = preprocess(XML);
		Log.i("Android mobile Voting", "BEGINING PARSING QUESTION");
		/******************************************************************/
		NodeList questionList = doc.getElementsByTagName("question");
		Log.i("Android Mobile Voting",
				"Question count = " + questionList.getLength());
		for (int i = 0; i < questionList.getLength(); i++) {
			Element node = (Element) questionList.item(i);
			int id = Integer.parseInt(node.getAttribute("id"));
			int min = Integer.parseInt(node.getAttribute("min"));
			int max = Integer.parseInt(node.getAttribute("max"));
			Log.i("Android Mobile Voting", "Question " + i + " id= " + id);
			Node details = null;
			if (node.getElementsByTagName("details") != null) {
				details = (Node) node.getElementsByTagName("details").item(0);
			}
			Node txt = (Node) node.getElementsByTagName("text").item(0);

			Log.i("Android Mobile Voting", "Number of elements named text = "
					+ node.getElementsByTagName("text").getLength());
			String dText = "No Details";
			String qText = getNodeValue(txt);
			if (details != null)
				dText = getNodeValue(details);
			// Log.i("Android Mobile Voting", "Question " + i + " text= " +
			// qText);
			NodeList aListXML = node.getElementsByTagName("alternative");
			ArrayList<String> aList = new ArrayList<String>();
			for (int a = 0; a < aListXML.getLength(); a++) {
				Node alternative = (Node) aListXML.item(a);
				aList.add(getNodeValue(alternative));
			}
			QuestionData q = new QuestionData(id, qText, dText, aList, min, max);
			questions.add(q);
		}
		// surface.drawQuestions(questions);
		surface.mHandler.post(new Runnable() {

			@Override
			public void run() {
				surface.drawQuestions(questions);

			}
		});

	}
/**
 * processes the XML for parsing
 * @param XML
 * @return
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws IOException
 */
	private Document preprocess(String XML)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream in = new ByteArrayInputStream(XML.getBytes("UTF-8"));
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		doc.getDocumentElement().normalize();
		return doc;

	}
/**
 * Parses the beacon that the server periodicaly sends to the clients by UDP
 * @param xml
 * @param IP
 * @return returns a server that needs editing before connecting
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws IOException
 */
	protected ServerData parseBeacon(String xml, String IP)
			throws ParserConfigurationException, SAXException, IOException {
		Document doc = preprocess(xml);
	//	Log.i("Android mobile Voting", "BEGINING PARSING Beacon");
		/******************************************************************/

		NodeList serverNode = doc.getElementsByTagName("serverinfo");
		Element serverElement = (Element) serverNode.item(0);
		int id = Integer.parseInt(serverElement.getAttribute("id"));
		// int id = Integer.parseInt(node.getAttribute("id"));
		Node FN = (Node) serverElement.getElementsByTagName("friendlyname")
				.item(0);
		String FNtxt = "";
		FNtxt = getNodeValue(FN);
		Node PN = serverElement.getElementsByTagName("port").item(0);
		int port = -1;
		port = Integer.parseInt(getNodeValue(PN));
		ServerData s = new ServerData("temporary", "null", id, IP, port, FNtxt);
		//Log.d("Android mobile Voting", s.toString());
		return s;

	}

	/**
	 * SNIPPET FROM http://www.coderanch.com/how-to/java/GetNodeValue
	 * 
	 * @param node
	 * @return
	 */
	private static String getNodeValue(Node node) {
		StringBuffer buf = new StringBuffer();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node textChild = children.item(i);
			if (textChild.getNodeType() != Node.TEXT_NODE) {
				System.err.println("Mixed content! Skipping child element "
						+ textChild.getNodeName());
				continue;
			}
			buf.append(textChild.getNodeValue());
		}
		return buf.toString();
	}

}

package cvut.fel.mobilevoting.murinrad;

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


import android.os.Handler;
import android.util.Log;

public class XMLParser {
	public static final XMLParser XMLParser = new XMLParser();

	private XMLParser() {
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
	protected void parseString(String XML, final QuestionsView surface)
			throws SAXException, IOException, ParserConfigurationException {
		final ArrayList<QuestionData> questions = new ArrayList<QuestionData>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream in = new ByteArrayInputStream(XML.getBytes("UTF-8"));
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		doc.getDocumentElement().normalize();
		Log.i("Android mobile Voting", "BEGINING PARSING");
		/******************************************************************/
		NodeList questionList = doc.getElementsByTagName("question");
		Log.i("Android Mobile Voting",
				"Question count = " + questionList.getLength());
		for (int i = 0; i < questionList.getLength(); i++) {
			Element node = (Element) questionList.item(i);
			int id = Integer.parseInt(node.getAttribute("id"));
			Log.i("Android Mobile Voting", "Question " + i + " id= " + id);
			Node txt = (Node) node.getElementsByTagName("text").item(0);
			Log.i("Android Mobile Voting", "Number of elements named text = "
					+ node.getElementsByTagName("text").getLength());

			String qText = getNodeValue(txt);
			//Log.i("Android Mobile Voting", "Question " + i + " text= " + qText);
			NodeList aListXML = node.getElementsByTagName("alternative");
			ArrayList<String> aList = new ArrayList<String>();
			for (int a = 0; a < aListXML.getLength(); a++) {
				Node alternative = (Node) aListXML.item(a);
				aList.add(getNodeValue(alternative));
			}
			QuestionData q = new QuestionData(id, qText, aList);
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

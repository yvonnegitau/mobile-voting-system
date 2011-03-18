/*
 * © 2010, Jakub Valenta
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Jakub Valenta
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors “as is” and any
 * express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the foundation or contributors be liable for any direct, indirect,
 * incidental, special, exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits; or business
 * interruption) however caused and on any theory of liability, whether in contract, strict
 * liability, or tort (including negligence or otherwise) arising in any way out of the use
 * of this software, even if advised of the possibility of such damage.
 */

package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.persistence.DAOException;
import cz.cvut.fel.mvod.persistence.DAOFacade;
import cz.cvut.fel.mvod.persistence.DAOFacadeImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Knihovní třída poskytující funkce pro serializaci a deserializaci XML zpráv.
 * @author jakub
 */
class XMLSerializer {

	private static final String ROOT = "voting";
	private static final String QUESTION = "question";
	private static final String QUESTION_TEXT = "text";
	private static final String QUESTION_ID = "id";
	private static final String ALTERNATIVE = "alternative";
	private static final String MAX_ALTERNATIVES = "max";
	private static final String MIN_ALTERNATIVES = "min";
	private static final String PASSWORD = "password";
	private static final String NEEDED = "true";
	private static final String NOT_NEEDED = "false";


	/**
	 * Serializuje otázky do formátu XML.
	 * Struktura XML je popsaná v souboru voting.dtd.
	 * @param questions otázky
	 * @param passwordNeeded zda se má vynutit zadání hesla
	 * @return serializované otázky
	 */
	public static byte[] serializeQuestions(List<Question> questions, boolean passwordNeeded) {
		DocumentBuilder xml = null;
		try {
			xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException ex) {
			//nemelo by nikdy nastat
			assert false;
		}
		Document doc = xml.newDocument();
		Element root = doc.createElement(ROOT);
		root.setAttribute(PASSWORD, passwordNeeded ? NEEDED : NOT_NEEDED);
		doc.appendChild(root);
		for(Question question: questions) {
			Element questionElement = doc.createElement(QUESTION);
			questionElement.setAttribute(QUESTION_ID, "" + question.getId());
			questionElement.setAttribute(MAX_ALTERNATIVES, question.getMaxSelect() + "");
			questionElement.setAttribute(MIN_ALTERNATIVES, question.getMinSelect() + "");
			Element text = doc.createElement(QUESTION_TEXT);
			text.appendChild(doc.createTextNode(question.getText()));
			questionElement.appendChild(text);
			for(Alternative alternative: question.getAlternatives()) {
				Element alternativeElement = doc.createElement(ALTERNATIVE);
				alternativeElement.appendChild(doc.createTextNode(alternative.getText()));
				questionElement.appendChild(alternativeElement);
			}
			root.appendChild(questionElement);
		}
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch(TransformerConfigurationException ex) {
			assert false;
			//nemelo by nikdy nastat
		}
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			transformer.transform(source, new StreamResult(out));
		} catch(TransformerException ex) {
			//ok
			assert false;
		}
		return out.toByteArray();
	}

	/**
	 * Deserializuje přijaté hlasy z XML.
	 * Struktura XML je popsaná v souboru vote.dtd.
	 * @param in vstupní proud XML souboru
	 * @return přijaté hlasy
	 */
	public static List<Vote> parseVote(InputStream in) throws IOException {
		class VoteHandler extends DefaultHandler{

			private static final String VOTE = "vote";

			private Vote currentVote;
			private List<Vote> votes = new ArrayList<Vote>();
			private List<Alternative> checked;
			private DAOFacade dao = DAOFacadeImpl.getInstance();
			private String tag = null;

			public VoteHandler() {
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if(qName.equals(QUESTION)) {
					newVote();
					int id = Integer.parseInt(attributes.getValue(QUESTION_ID));
					Question q;
					try {
						q = dao.getQuestion(id);
					} catch(DAOException ex) {
						throw new SAXException(ex);
					}
					if(q == null) {
						throw new SAXException("No such question.");
					}
					currentVote.setQuestion(q);
					currentVote.setChecked(checked);
					tag = null;
				} else if(qName.equals(ALTERNATIVE)) {
					tag = ALTERNATIVE;
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				//ok comparing references
				if(tag == ALTERNATIVE) {
					StringBuffer buffer = new StringBuffer();
					buffer.append(ch, start, length);
					int index = Integer.parseInt(buffer.toString().trim());
					checked.add(currentVote.getQuestion().getAlternative(index));
					tag = null;
				}
			}

			private void newVote() {
				currentVote = new Vote();
				checked = new ArrayList<Alternative>();
				votes.add(currentVote);
			}

			public List<Vote> getVotes() {
				return votes;
			}
		}
		VoteHandler handler = new VoteHandler();
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(in, handler);
		} catch(SAXException ex) {
			//ok
			assert false;
		} catch(ParserConfigurationException ex) {
			//ok
			assert false;
		}
		return handler.getVotes();
	}

}

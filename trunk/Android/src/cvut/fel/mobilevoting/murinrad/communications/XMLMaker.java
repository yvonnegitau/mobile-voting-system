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

import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;

import android.util.Log;
import android.util.Xml;
/**
 * An XML serializer
 * Creates XML in string form for various usages
 * @author Radovan Murin
 *
 */
public class XMLMaker {

	public static final XMLMaker XMLMaker = new XMLMaker();

	private XMLMaker() {

	}
/**
 * Serializes the answers to an XML defined in the thesis
 * @param questions an ArrayList of QuestionData classes
 * @return the XML in string form
 */
	public String buildAnswer(ArrayList<QuestionData> questions) {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "vote");
			for (int i = 0; i < questions.size(); i++) {
				int[] answers = questions.get(i).getAnswerField();
				serializer.startTag("", "question");
				serializer.attribute("", "id", questions.get(i).getId() + "");
				for (int o = 0; o < answers.length; o++) {
					Log.e("Android Mobile Voting","answer["+o+"] is "+ answers[o]);
					if (answers[o] != -1 && answers[o]!=0) {
						serializer.startTag("", "alternative");
						serializer.text(o + "");
						serializer.endTag("", "alternative");
					}
				}
				serializer.endTag("", "question");
			}
			serializer.endTag("", "vote");
			serializer.endDocument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Log.i("XML Output", writer.toString());
		return writer.toString();
	}

}

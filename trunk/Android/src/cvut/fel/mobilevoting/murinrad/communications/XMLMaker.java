package cvut.fel.mobilevoting.murinrad.communications;

import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;

import android.util.Log;
import android.util.Xml;

public class XMLMaker {

	public static final XMLMaker XMLMaker = new XMLMaker();

	private XMLMaker() {

	}

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

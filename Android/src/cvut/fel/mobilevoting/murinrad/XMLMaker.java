package cvut.fel.mobilevoting.murinrad;

import java.io.StringWriter;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class XMLMaker {

	public static final XMLMaker XMLMaker = new XMLMaker();

	private XMLMaker() {

	}

	public String buildAnswer(int answerNo,int qNo) {
		/*XMLBuilder builder = XMLBuilder.create("vote")
			.e("question")
			.a("id", qNo+"")
			.e("alternative")
				.t(answerNo+"");*/
		
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "vote");
	        serializer.startTag("", "question");
	        serializer.attribute("", "id", qNo+"");
	        
	            serializer.startTag("", "alternative");
	            serializer.text(answerNo+"");
	            serializer.endTag("", "alternative");
	            serializer.endTag("", "question");
	            serializer.endTag("", "vote");
	        serializer.endDocument();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 
	Log.i("XML Output",writer.toString());
	return writer.toString();
	}
	

}

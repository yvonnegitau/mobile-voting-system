package cvut.fel.mobilevoting.murinrad.communications;

import java.util.ArrayList;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;

import junit.framework.TestCase;

public class XMLMakerTest extends TestCase {

	public void testBuildAnswer() {
		ArrayList<QuestionData> are = new ArrayList<QuestionData>();
		ArrayList<String> we = new ArrayList<String>();
		we.add("human");
		we.add("or");
		QuestionData dancers = new QuestionData(123, "TESTCASE", "TESTDET", we, 1, 2);
		dancers.setAnswer(1);
		are.add(dancers);
		String samstown = XMLMaker.XMLMaker.buildAnswer(are);
		assertTrue(samstown.contains("human"));
	}

}

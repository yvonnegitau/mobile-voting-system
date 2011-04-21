package cvut.fel.mobilevoting.murinrad.communications;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicHeader;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;

public interface ConnectionInterface {

	public abstract void run();

	public abstract void postAndRecieve(String method, String URL,
			ArrayList<BasicHeader> headers, String body,boolean authenticate) throws IOException;

	public abstract void closeConnection();

	//public abstract void post(QuestionData answers);

	void postAnswers(ArrayList<QuestionData> answers);

	public abstract boolean parseResponceCode(String string);

	public abstract QuestionsView getParent();

}
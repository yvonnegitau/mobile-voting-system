package cvut.fel.mobilevoting.murinrad.communications;

import java.util.ArrayList;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;

public interface ConnectionInterface {

	public abstract void run();

	public abstract void sendReq() throws Exception;

	public abstract void closeConnection();

	//public abstract void post(QuestionData answers);

	void postAnswers(ArrayList<QuestionData> answers);

}
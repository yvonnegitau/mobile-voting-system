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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicHeader;

import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;
/**
 * An interface that connection managers must obey to be used in this application
 * @author Radovan Murin
 *
 */
public interface ConnectionInterface {

	public abstract void run();

	/**
	 * Posts a request
	 * 
	 * @param method
	 *            the method of the request
	 * @param URL
	 *            the URL
	 * @param headers
	 *            headers to be included, can be NULL
	 * @param body
	 *            the request body, can be NULL
	 * @param authenticate
	 *            if true, the username and password will be sent in the message
	 *            headers
	 * @throws IOException
	 */
	public abstract void postAndRecieve(String method, String URL,
			ArrayList<BasicHeader> headers, String body, boolean authenticate)
			throws IOException;

	/**
	 * Closes the connection and stops the thread
	 */
	public abstract void closeConnection();

	// public abstract void post(QuestionData answers);
	/**
	 * Posts answers to the Server
	 * 
	 * @param answers
	 *            the QuestionData that is to be sent
	 */
	void postAnswers(ArrayList<QuestionData> answers);

	/**
	 * Parses the responce code
	 * 
	 * @param data
	 *            the responce data recieved in the responce header
	 * @return returns true if 200, false if anything else - server error
	 */
	public abstract boolean parseResponceCode(String string);

	/**
	 * Returns the parent view of the connection Interface
	 * 
	 * @return true if 200, else false
	 */
	public abstract QuestionsView getParent();

}
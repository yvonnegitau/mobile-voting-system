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
package cvut.fel.mobilevoting.murinrad.datacontainers;

import java.util.ArrayList;
/**
 * A question data class
 * it contains the question text, detail, answers, selected answers and everything answer related
 * @author Radovan Murin
 *
 */
public class QuestionData {
	int id, max, min;
	private int answer = -1;
	private int[] answerField;
	String details = "Details field";
	
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	String text = "";
	ArrayList<String> answers = null;
/**
 * Constructor for the class
 * @param id the ID of the class, used by the server
 * @param text the question text
 * @param details the question details
 * @param answers possible answers
 * @param min minimum questions selected to be a valid response
 * @param max maximum question selected to be a valid response
 */
	public QuestionData(int id, String text,String details, ArrayList<String> answers,int min,int max) {
		this.id = id;
		this.text = text;
		this.min = min;
		this.max = max;
		if(details!=null) this.details = details;
		max = 1;
		answerField = new int[answers.size()];
		for(int i=0;i<max;i++) {
			answerField[i] = -1;
		}
		this.answers = answers;

	}

	public int getId() {
		return id;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public String getText() {
		return text;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}
	
	public void setAnswers(int[] answers) {
		this.answerField = answers;
	}
	
	public int[] getAnswerField() {
		return answerField;
	}
	/**
	 * Sets the answer
	 * @param pos answer position
	 * @param val value
	 */
public void setAnswer(int pos,int val) {
		answerField[pos] = val;
	}

}

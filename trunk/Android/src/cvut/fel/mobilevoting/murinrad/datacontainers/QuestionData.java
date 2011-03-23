package cvut.fel.mobilevoting.murinrad.datacontainers;

import java.util.ArrayList;
import java.util.Collection;

public class QuestionData {
	int id, max, min;
	private int answer = -1;
	private int[] answerField;
	String details = "";

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	String text = null;
	ArrayList<String> answers = null;

	public QuestionData(int id, String text,String details, ArrayList<String> answers,int min,int max) {
		this.id = id;
		this.text = text;
		this.min = min;
		this.max = max;
		this.details = details;
		max = 1;
		answerField = new int[max];
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

}

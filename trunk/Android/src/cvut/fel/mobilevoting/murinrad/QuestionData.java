package cvut.fel.mobilevoting.murinrad;

import java.util.ArrayList;
import java.util.Collection;

public class QuestionData {
	int id, max, min;
	private int answer = -1;

	String text = null;
	ArrayList<String> answers = null;

	public QuestionData(int id, String text, ArrayList<String> answers) {
		this.id = id;
		this.text = text;
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

}

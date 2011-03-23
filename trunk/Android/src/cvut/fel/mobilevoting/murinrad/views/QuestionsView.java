package cvut.fel.mobilevoting.murinrad.views;

import java.util.ArrayList;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.communications.ConnectionHTTP;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.gui.QuestionButton;
import cvut.fel.mobilevoting.murinrad.gui.QuestionButtonLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionsView extends Activity {
	ServerData server;
	ConnectionHTTP con;
	LinearLayout layout;
	public Handler mHandler;
	ArrayList<QuestionButtonLayout> buttons = new ArrayList<QuestionButtonLayout>();
	boolean showingCheckers = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		server = (ServerData) getIntent().getSerializableExtra("ServerData");
		mHandler = new Handler();
		this.setTitle(getString(R.string.connectedTo) + " : "
				+ server.getFriendlyName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		con = new ConnectionHTTP(server, this);
		con.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		con.closeConnection();
		con = null;
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void drawQuestions(ArrayList<QuestionData> questions) {
		Log.v("Android Mobile Voting", "questions size : "
				+ questions.get(0).getText());
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < questions.size(); i++) {
			QuestionButtonLayout question = new QuestionButtonLayout(this,
					questions.get(i), this);
			buttons.add(question);
			layout.addView(question, i);
		}
		setContentView(layout);
		Log.i("Android mobile voting", "Im here");

	}

	public void sendToServer(ArrayList<QuestionData> answers) {
		con.postAnswers(answers);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inf = getMenuInflater();
		//if (showingCheckers) {
			inf.inflate(R.menu.questionlistmenualt, menu);
		//} else {
			inf.inflate(R.menu.questionlistmenu, menu);
		//}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sendAnswers:
			if (!showingCheckers)
				showPickers(true);
			else
				showPickers(false);
			break;
		case R.id.cancelSending:
			showPickers(false);
			break;
		case R.id.sentAllBTN:
			sendChecked();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendChecked() {
		ArrayList<QuestionData> q = new ArrayList<QuestionData>();
		for (int i = 0;i<buttons.size();i++) {
			if(buttons.get(i).isChecked()){
				q.add(buttons.get(i).extractQData());
			}
		}
		sendToServer(q);
		
	}

	private void showPickers(boolean b) {
		for (int i = 0; i < buttons.size(); i++) {
			if (b)
				buttons.get(i).showCheckers();
			if (!b)
				buttons.get(i).hideCheckers();

		}
		// invalidate();
		showingCheckers = b;

	}

}

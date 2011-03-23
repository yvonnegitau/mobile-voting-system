package cvut.fel.mobilevoting.murinrad.views;



import java.util.ArrayList;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.communications.ConnectionHTTP;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.gui.QuestionButton;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		server = (ServerData) getIntent().getSerializableExtra("ServerData");
		mHandler = new Handler();
		this.setTitle(getString(R.string.connectedTo) + " : "
				+ server.getFriendlyName());
	}
	@Override
	protected void onResume(){
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
		Log.v("Android Mobile Voting","questions size : "+questions.get(0).getText());
	    layout = new LinearLayout(this);
	    layout.setOrientation(LinearLayout.VERTICAL);
		for(int i=0;i<questions.size();i++) {
			QuestionButton question = new QuestionButton(this, questions.get(i), this);
			layout.addView(question,i);
		}
		setContentView(layout);
		Log.i("Android mobile voting", "Im here");
		
	}
	public void sendToServer(ArrayList<QuestionData> answers) {
		con.postAnswers(answers);
		
		
	}
	
	

}

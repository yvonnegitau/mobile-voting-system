package cvut.fel.mobilevoting.murinrad.views;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.communications.Connection;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.gui.NoSSLDialog;
import cvut.fel.mobilevoting.murinrad.gui.QuestionButton;
import cvut.fel.mobilevoting.murinrad.gui.QuestionButtonLayout;
import cvut.fel.mobilevoting.murinrad.gui.SecurityExceptionDialogue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	Connection con;
	LinearLayout layout;
	public Handler mHandler;
	ArrayList<QuestionButtonLayout> buttons;
	boolean showingCheckers = false;
	// ConnectionProgressDialog cpg;
	ArrayList<String> statuses;
	Iterator<String> statIterate;
	ProgressDialog curentP;
	boolean showStatus = true;
	QuestionsView instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statuses = new ArrayList<String>();
		instance = this;
		statuses.add(getString(R.string.conStatDial));
		statuses.add(getString(R.string.conStatDialHTTPS));

		statuses.add(getString(R.string.conStatDialSucc));
		statIterate = statuses.iterator();
		server = (ServerData) getIntent().getSerializableExtra("ServerData");
		mHandler = new Handler();
		this.setTitle(getString(R.string.connectedTo) + " : "
				+ server.getFriendlyName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		con = new Connection(server, this);
		// con.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// con.closeConnection();
		// con = null;
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void drawQuestions(ArrayList<QuestionData> questions) {
		showNextProgres();
		buttons = new ArrayList<QuestionButtonLayout>();
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
		// if (showingCheckers) {
		inf.inflate(R.menu.questionlistmenualt, menu);
		// } else {
		inf.inflate(R.menu.questionlistmenu, menu);
		// }
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
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).isChecked()) {
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

	public void askForTrust(String thumbPrint, Connection instance) {
		Dialog d = new SecurityExceptionDialogue(this, thumbPrint, instance);
		d.show();

	}

	/**
	 * 
	 * @param state
	 */
	public void showNextProgres() {
		if (curentP == null) {
			curentP = ProgressDialog.show(this, "",
					getString(R.string.conStatDial),true,true);
		} else {
			curentP.dismiss();
		}

	}

	public void showNoSSLDialog(Connection instance) {
		Dialog d = new NoSSLDialog(this, instance);
		d.show();
		
	}
	
	public void showConnectionError() {
		if (curentP != null) {
			curentP.dismiss();
			curentP = null;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.networkErrorDialogueText))
		       .setCancelable(false)
		       .setPositiveButton(getString(R.string.connectAgain), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		        	   con.forceInit();
		                
		           }
		       })
		       .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   instance.finish();
		                
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		
	}

}

package cvut.fel.mobilevoting.murinrad.gui;

import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionButton extends DefaultButton {
	private QuestionData qData;
	final QuestionsView parent;
	private boolean checked = false;
	int picked = 0;

	public QuestionButton(final Context context, final QuestionData qData,
			final QuestionsView parent) {
		super(context, qData.getText());
		this.qData = qData;
		this.parent = parent;
		// setBackgroundColor(Color.RED);
	}

	void showChoices() {
		final CharSequence[] cs = qData.getAnswers().toArray(
				new CharSequence[qData.getAnswers().size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.answerPick));
		builder.setMultiChoiceItems(cs, null,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton, boolean isChecked) {
						Log.e("Android Mobile Voting", whichButton + "");
						if (isChecked) {
							if(picked+1<=qData.getMax()){
							qData.setAnswer(whichButton, 1);
							picked++;
						} else {
							Toast.makeText(getContext(), parent.getString(R.string.TooMuchAnswers), Toast.LENGTH_LONG).show(); 
						}
						}
						if (!isChecked) {
							if(picked-1>=qData.getMin()){
							qData.setAnswer(whichButton, -1);
							picked--;
							} else {
								Toast.makeText(getContext(), parent.getString(R.string.notEnoughAnswers), Toast.LENGTH_LONG).show();
									
							}
						}
						
					}
					
				}).setPositiveButton(R.string.Confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				
				});
		/*
		 * builder.setItems(cs, new DialogInterface.OnClickListener() { public
		 * void onClick(DialogInterface dialog, int item) {
		 * Toast.makeText(context.getApplicationContext(),
		 * qData.getAnswers().get(item), Toast.LENGTH_SHORT) .show();
		 * qData.setAnswer(item); } });
		 */
		AlertDialog alert = builder.create();
		alert.show();

	}

	void prepForSending() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(parent.getString(R.string.confirmSendDialog))
				.setCancelable(true)
				.setPositiveButton(parent.getString(R.string.YES),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									passToSend();
								} catch (Exception ex) {

								}
							}
						})
				.setNegativeButton(R.string.NO,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

	}

	void passToSend() throws ParserConfigurationException,
			FactoryConfigurationError {
		ArrayList<QuestionData> list = new ArrayList<QuestionData>();
		list.add(qData);
		parent.sendToServer(list);

	}

	@Override
	public void onClickAction() {
		showChoices();

	}

	@Override
	public void onLongClickAction() {
		//prepForSending();
		showDetails();	
	}

	private void showDetails() {
		final Dialog d = new Dialog(parent);
		d.setContentView(R.layout.questiondetailsdialog);
		d.setTitle(parent.getString(R.string.QDDTitle));
		d.show();
		TextView tv = (TextView) d.findViewById(R.id.QDDText);
		Button btn = (Button) d.findViewById(R.id.QDDDismiss);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				d.dismiss();
				
			}
		});
		tv.setText(qData.getDetails());
		
		
		
	}

	public boolean getChecked() {
		return checked;
	}

	public void flipChecked() {
		checked = !checked;
	}

	public QuestionData extractQData() {
		return qData;
	}

}

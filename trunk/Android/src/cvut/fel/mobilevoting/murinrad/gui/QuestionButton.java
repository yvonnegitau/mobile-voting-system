package cvut.fel.mobilevoting.murinrad.gui;

import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;
/**
 * A button that represents a question
 * @author Radovan Murin
 *
 */
public class QuestionButton extends DefaultButton {
	private QuestionData qData;
	final QuestionsView parent;
	private boolean checked = false;
	int picked = 0;
	AlertDialog alert;
	QuestionButton instance;
/**
 * The constructor of the class
 * @param context application context
 * @param qData Question data to be represented
 * @param parent a view that this button is embedded in
 */
	public QuestionButton(final Context context, final QuestionData qData,
			final QuestionsView parent) {
		super(context, qData.getText());
		this.qData = qData;
		this.parent = parent;
		instance = this;
		
		
		// setBackgroundColor(Color.RED);
	}
/**
 * Shows the answer options
 */
	void showChoices() {
		final CharSequence[] cs = qData.getAnswers().toArray(
				new CharSequence[qData.getAnswers().size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle(context.getString(R.string.answerPick));
		//builder.set
		builder.setMultiChoiceItems(cs, null,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton, boolean isChecked) {
						if (isChecked) {
							if(picked+1<=qData.getMax()){
							qData.setAnswer(whichButton, 1);
							alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
							picked++;
						} else {
							alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
							Toast.makeText(getContext(), parent.getString(R.string.TooMuchAnswers), Toast.LENGTH_LONG).show(); 
						}
						}
						if (!isChecked) {
							if(picked-1>=qData.getMin()){
							qData.setAnswer(whichButton, -1);
							alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
							picked--;
							} else {
								//long [] ids = alert.getListView().getCheckItemIds();
								alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
								Log.e("Android Mobile Voting", whichButton + " is null" );
								Toast.makeText(getContext(), parent.getString(R.string.notEnoughAnswers), Toast.LENGTH_LONG).show();
									
							}
						}	
						
						if(picked<=qData.getMax() && picked>=qData.getMin()) {
							alert.setCancelable(false);
							
							
						} else {
							alert.setCancelable(true);
						}
						
					}
					
				}).setPositiveButton(R.string.Confirm,	
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							
					}
				
				});
		alert = builder.create();
		
		alert.show();

	}
/**
 * Prepares the answers for sending, displays a dialogue
 */
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
/**
 * Sends the answers to the Connection class attached to the parent
 * @throws ParserConfigurationException
 * @throws FactoryConfigurationError
 */
	void passToSend() throws ParserConfigurationException,
			FactoryConfigurationError {
		ArrayList<QuestionData> list = new ArrayList<QuestionData>();
		if(picked<=qData.getMax() && picked>=qData.getMin()) {
			list.add(qData);
		parent.sendToServer(list);
			
		} else {
			alert.setCancelable(true);
		}
		

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
/**
 * displays the question details
 */
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

	@SuppressWarnings("unused")
	private boolean getChecked() {
		return checked;
	}

	@SuppressWarnings("unused")
	private void flipChecked() {
		checked = !checked;
	}
/**
 * Extracts the euqstion data from this button
 * @return the question data
 */
	public QuestionData extractQData() {
		return qData;
	}

}

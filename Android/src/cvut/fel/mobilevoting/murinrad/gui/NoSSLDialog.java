package cvut.fel.mobilevoting.murinrad.gui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.communications.Connection;

/**
 * A dialog that contains a warning about the SSL layer
 * 
 * @author Radovan Murin
 * 
 */
public class NoSSLDialog extends Dialog {
	CheckBox check;
	Button confirmer;

	/**
	 * Constructs the dialogue
	 * 
	 * @param context
	 *            the application context
	 * @param caller
	 *            the connection that is having this problem
	 */
	public NoSSLDialog(Context context, final Connection caller) {
		super(context);
		setTitle(context.getString(R.string.noSSLWindTitle));
		setContentView(R.layout.nossldialog);
		confirmer = (Button) findViewById(R.id.noSSLDialogBTN);
		loadChecker();
		confirmer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (check.isChecked())
						caller.retrieveQuestions();
					if (!check.isChecked())
						caller.closeConnection();
				} catch (NullPointerException ex) {
					Log.e("Android mobile voting", ex.toString());

				}
				dismiss();

			}
		});
	}

	private void loadChecker() {
		check = (CheckBox) findViewById(R.id.noSSLDialogCheck);
	}

}

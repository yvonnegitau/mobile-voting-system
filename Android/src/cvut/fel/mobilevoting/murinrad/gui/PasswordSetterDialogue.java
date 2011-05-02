package cvut.fel.mobilevoting.murinrad.gui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
/**
 * Dialogue that sets the main password
 * @author Radovan Murin
 *
 */
public class PasswordSetterDialogue extends Dialog {
	/**
 * The constructor for the dialog
 * @param context the context the application is in right now
 */
	public PasswordSetterDialogue(final Context context) {
		super(context);
		setContentView(R.layout.passwordadddialog);
		setTitle(context.getString(R.string.passwordDialogTitle));
		final TextView oldPass = (TextView) findViewById(R.id.oldPass);
		final TextView newPass1 = (TextView) findViewById(R.id.newPass1);
		final TextView newPass2 = (TextView) findViewById(R.id.newPass2);
		Button okBTN = (Button) findViewById(R.id.passwordDialogOK);
		Button cancelBTN = (Button) findViewById(R.id.passwordDialogCancel);
		if (Cryptography.crypto.verifyPass("") == -1) {

			TextView oldPassLabel = (TextView) findViewById(R.id.oldPassLabel);
			oldPassLabel.setVisibility(TextView.GONE);
			oldPass.setVisibility(TextView.GONE);
		}
		okBTN.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!verifyLength((newPass1.getText().toString()))) {
					Toast.makeText(context, context.getString(R.string.passwordReq), Toast.LENGTH_LONG).show();
					return;
				}
				if (Cryptography.crypto
						.verifyPass(oldPass.getText().toString()) == 0) {
					Toast.makeText(context,
							context.getString(R.string.wrongPass),
							Toast.LENGTH_LONG).show();
				} else if (newPass1.getText().toString()
						.equals(newPass2.getText().toString())) {
					Cryptography.crypto.ChangeCryptoKey(newPass1.getText()
							.toString());
					Toast.makeText(context,
							context.getString(R.string.passwordChangeSuccess),
							Toast.LENGTH_LONG).show();
					dismiss();
				}

			}
		});
		cancelBTN.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

	}
/**
 * Verifies that the password length is correct
 * @param s
 * @return
 */
	private boolean verifyLength(String s) {
		if (s.length() == 8)
			return true;
		return false;
	}

}

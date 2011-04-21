package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class PasswordSetterDialogue extends Dialog {
	private final Context context;

	public PasswordSetterDialogue(final Context context) {
		super(context);
		this.context = context;
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

	private boolean verifyLength(String s) {
		if (s.length() == 8)
			return true;
		return false;
	}

}

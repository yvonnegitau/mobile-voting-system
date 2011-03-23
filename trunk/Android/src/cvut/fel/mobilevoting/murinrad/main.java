package cvut.fel.mobilevoting.murinrad;

import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
import cvut.fel.mobilevoting.murinrad.gui.PasswordSetterDialogue;
import cvut.fel.mobilevoting.murinrad.storage.PreferencesStorage;
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

public class main extends Activity {
	public static Activity c;
	private static final String shadowFile = "shadow";
	private String pHash;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// SharedPreferences settings = getSharedPreferences(shadowFile, 0);
		// String temp = Cryptography.md5("Ahoj");
		// Toast.makeText(c, temp, Toast.LENGTH_LONG).show();
		// SharedPreferences.Editor editor = settings.edit();
		// editor.putString("pHash", temp);
		// editor.commit();
		// pHash = settings.getString("pHash", "");
		c = this;
		// pHash = Cryptography.md5("Ahoj");
		setContentView(R.layout.main);
		Button okButton = null;

		final EditText passwordField = (EditText) findViewById(R.string.passwordField);
		
		okButton = (Button) findViewById(R.string.loginConfirmBTN);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String test = passwordField.getText().toString();

				if (passCheck(test)) {
					Intent intent = new Intent();
					intent.setClassName("cvut.fel.mobilevoting.murinrad",
							"cvut.fel.mobilevoting.murinrad.views.ServerListView");
					startActivity(intent);

				} else {
					Toast.makeText(c, R.string.wrongPass, Toast.LENGTH_LONG)
							.show();
				}

			}
		});
		//PreferencesStorage.store.addEntry(PreferencesStorage.PASSWORD_HASH, "");
		passCheck("");
	}

	void showNewPassword() {
		PasswordSetterDialogue d = new PasswordSetterDialogue(this);
		d.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	/**
	 * Checks the master password
	 * 
	 * @param pass
	 *            string value of the password
	 * @return
	 */
	private boolean passCheck(String pass) {
		int outcome = Cryptography.crypto.verifyPass(pass);
		switch (outcome) {
		case -1:
			showNewPassword();
		case 0:
			return false;
		case 1:
			Cryptography.crypto.init(pass);
			return true;
		default:
			return false;
		}
	}
}
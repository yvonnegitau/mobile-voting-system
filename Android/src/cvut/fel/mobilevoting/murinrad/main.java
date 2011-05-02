/*
  Copyright 2011 Radovan Murin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package cvut.fel.mobilevoting.murinrad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
import cvut.fel.mobilevoting.murinrad.gui.PasswordSetterDialogue;
/**
 * The main entry class for this application
 * @author Radovan Murin
 *
 */
public class main extends Activity {
	/**
	 * the reference to this activity for displaying purposes
	 */
	public static Activity c;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = this;
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
/**
 * Displays the new password dialogue
 */
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
	 * @param pass string value of the password
	 * @return false if the pass is incorrect, true if correct
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
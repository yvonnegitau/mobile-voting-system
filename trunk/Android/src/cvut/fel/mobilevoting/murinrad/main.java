package cvut.fel.mobilevoting.murinrad;

import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class main extends Activity {
	Activity c;
	private static final String shadowFile = "shadow";
	private String pHash;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences settings = getSharedPreferences(shadowFile, 0);
		String temp = Cryptography.md5("Ahoj");
		//Toast.makeText(c, temp, Toast.LENGTH_LONG).show();
		 SharedPreferences.Editor editor = settings.edit();
		editor.putString("pHash",temp);
		editor.commit();
		pHash = settings.getString("pHash", "");
		c = this;
		//pHash = Cryptography.md5("Ahoj");
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
							"cvut.fel.mobilevoting.murinrad.ServerList");
					startActivity(intent);

				} else {
					Toast.makeText(c, R.string.wrongPass, Toast.LENGTH_LONG)
							.show();
				}

			}
		});
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
		if(pHash==null) return true;
		String p = Cryptography.md5(pass);
		Log.i("Android mobile voting", pHash);
		Log.i("Android Mobile Voting", p);
		if(p.equals(pHash))	return true;
		return false;
	}
}
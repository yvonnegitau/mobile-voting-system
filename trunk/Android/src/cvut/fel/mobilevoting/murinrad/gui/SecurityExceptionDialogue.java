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
package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.communications.Connection;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
/**
 * A dialogue that shows a no certificate error
 * @author Radovan Murin
 *
 */
public class SecurityExceptionDialogue extends Dialog {
	CheckBox check;
	Button confirmer;
	public SecurityExceptionDialogue(Context context,String fingerprint,final Connection caller) {
		super(context);
		setTitle(context.getString(R.string.CertWindowTitle));
		setContentView(R.layout.certificatedialog);
		confirmer = (Button)findViewById(R.id.CertificateWindOK);
		loadChecker();
		TextView hash = (TextView) findViewById(R.id.CertificateWindowFingerprint);	
		hash.setText(fingerprint);
		confirmer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
				if(check.isChecked()) caller.retrieveQuestions();
				if(!check.isChecked()) caller.closeConnection();}
				catch(NullPointerException ex){
					Log.e("Android mobile voting", ex.toString());
					
				}
				dismiss();
				
			}
		});
	}
	
	private void loadChecker(){
		check = (CheckBox) findViewById(R.id.CertificateWindCheckbox);
	}
	
	

}

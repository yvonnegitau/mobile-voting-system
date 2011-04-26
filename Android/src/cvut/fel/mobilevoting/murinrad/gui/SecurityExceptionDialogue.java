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
				if(check.isChecked()) caller.permitException();
				if(!check.isChecked()) caller.closeConnection();}
				catch(NullPointerException ex){
					Log.e("Android mobile voting", ex.toString());
					
				}
				dismiss();
				
			}
		});
	}
	
	void loadChecker(){
		check = (CheckBox) findViewById(R.id.CertificateWindCheckbox);
	}
	
	

}

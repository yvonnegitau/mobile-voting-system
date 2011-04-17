package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.communications.ConnectionHTTP;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SecurityExceptionDialogue extends Dialog {

	public SecurityExceptionDialogue(Context context,String fingerprint,final ConnectionHTTP caller) {
		super(context);
		setTitle(context.getString(R.string.CertWindowTitle));
		setContentView(R.layout.certificatedialog);
		Button confirmer = (Button)findViewById(R.id.CertificateWindOK);
		final CheckBox check = (CheckBox) findViewById(R.id.CertificateWindCheckbox);
		TextView hash = (TextView) findViewById(R.id.CertificateWindowFingerprint);	
		hash.setText(fingerprint);
		confirmer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(check.isChecked()) caller.permitException();
				if(!check.isChecked()) caller.closeConnection();
				dismiss();
				
			}
		});
	}
	
	

}

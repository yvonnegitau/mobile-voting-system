package cvut.fel.mobilevoting.murinrad.gui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.communications.Connection;

public class NoSSLDialog extends Dialog {
	CheckBox check;
	Button confirmer;
	public NoSSLDialog(Context context,final Connection caller) {
		super(context);
		setTitle(context.getString(R.string.noSSLWindTitle));
		setContentView(R.layout.nossldialog);
		confirmer = (Button)findViewById(R.id.noSSLDialogBTN);
		loadChecker();
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
		check = (CheckBox) findViewById(R.id.noSSLDialogCheck);
	}

	

}

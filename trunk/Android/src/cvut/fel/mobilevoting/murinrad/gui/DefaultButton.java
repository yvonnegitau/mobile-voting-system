package cvut.fel.mobilevoting.murinrad.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

public abstract class DefaultButton extends Button {
	Context context;
	public DefaultButton(Context context,String btnTXT) {
		super(context);
		setTextSize(18);
		setText(btnTXT);
		this.context = context;
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickAction();

			}
		});

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onLongClickAction();
				return true;
			}
		});

	}

	public abstract void onClickAction();

	public abstract void onLongClickAction();

}

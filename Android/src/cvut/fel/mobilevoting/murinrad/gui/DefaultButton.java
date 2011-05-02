package cvut.fel.mobilevoting.murinrad.gui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
/**
 * The default button look class
 * @author Radovan Murin
 *
 */
public abstract class DefaultButton extends Button {
	Context context;
	/**
	 * The constructor of the button
	 * @param context the application context
	 * @param btnTXT the text of the button
	 */
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

	protected abstract void onClickAction();

	protected abstract void onLongClickAction();

}

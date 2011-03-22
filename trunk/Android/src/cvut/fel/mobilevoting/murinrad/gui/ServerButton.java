package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.ChangeServerView;
import cvut.fel.mobilevoting.murinrad.QuestionsView;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.ServerList;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.communications.Connection;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


public class ServerButton extends DefaultButton {
	final ServerData server;
	final ServerButton me = this;
	final ServerList parent;
	boolean tst = false;
	Connection con = null;

	public ServerButton(final Context context, final ServerData server,
			ServerList parent) {
		super(context, server.getFriendlyName());
		this.server = server;
		this.parent = parent;
	}

	@Override
	public void onClickAction() {
		connectMe();

	}

	void showChoices() {
		final CharSequence[] items = {
				me.context.getString(R.string.EditServerTag),
				me.context.getString(R.string.deleteServerTag),
				me.context.getString(R.string.serverMenuJustConnect) };

		AlertDialog.Builder builder = new AlertDialog.Builder(me.context);
		builder.setTitle(me.context.getString(R.string.pickChallenge));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					editMe();
					break;
				case 1:
					deleteMe();
					break;
				case 2:
					connectMe();
					break;
				default:
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	void deleteMe() {
		parent.deleteServer(server.getId());

	}

	void connectMe() {
		Intent i = new Intent(me.context,
				cvut.fel.mobilevoting.murinrad.QuestionsView.class);
		i.putExtra("ServerData", me.server);
		me.context.startActivity(i);

	}

	void editMe() {
		Intent i = new Intent(me.context,
				cvut.fel.mobilevoting.murinrad.ChangeServerView.class);
		i.putExtra("id", me.server.getId()); 
		me.context.startActivity(i);
	}

	@Override
	public void onLongClickAction() {
		showChoices();

	}

}

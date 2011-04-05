package cvut.fel.mobilevoting.murinrad.views;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.R.id;
import cvut.fel.mobilevoting.murinrad.R.menu;
import cvut.fel.mobilevoting.murinrad.R.string;
import cvut.fel.mobilevoting.murinrad.communications.BeaconListener;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.gui.PasswordSetterDialogue;
import cvut.fel.mobilevoting.murinrad.gui.ServerButton;
import cvut.fel.mobilevoting.murinrad.storage.DatabaseStorage;

public class ServerListView extends Activity {
	private LinearLayout layout = null;
	private DatabaseStorage storage;
	// private ArrayList<ServerData> servers = new ArrayList<ServerData>();
	private ArrayList<ServerData> servers;
	int helper = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BeaconListener bl = new BeaconListener(this);
		onResume();

	}

	@Override
	public void onResume() {
		super.onResume();
		storage = new DatabaseStorage(this);
		// storage.dropDatabase();
		servers = storage.getServers();
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView header = new TextView(this);
		header.setTextSize(18);
		header.setText(R.string.HeaderServerList);
		layout.addView(header);
		for (int i = 0; i < servers.size(); i++) {
			addServer(servers.get(i));
		}
		setContentView(layout);
		
		//bl.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		storage.closeDB();
	}

	public void deleteServer(int id) {
		storage.delete(id);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.serverlistmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.slmnew:
			newServer();
			return true;
		case R.id.slmexit:
			try {
				finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.slmChangePWD:
			PasswordSetterDialogue d = new PasswordSetterDialogue(this);
			d.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void newServer() {
		Intent i = new Intent(this,
				cvut.fel.mobilevoting.murinrad.views.ChangeServerView.class);
		i.putExtra("id", -1);
		startActivity(i);

	}

	public void addServer(ServerData sd) {
		final ServerButton serverBTN = new ServerButton(this, sd, this);
		layout.addView(serverBTN, 1);

	}

}

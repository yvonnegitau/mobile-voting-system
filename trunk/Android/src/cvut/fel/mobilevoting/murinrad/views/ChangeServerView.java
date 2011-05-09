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
package cvut.fel.mobilevoting.murinrad.views;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.storage.DatabaseStorage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Server data alteration view
 * 
 * @author Murko
 * 
 */
public class ChangeServerView extends Activity {
	ServerData server = null;
	TextView friendlyName;
	TextView ipAdd;
	TextView userName;
	TextView pass;
	TextView pNumber;
	DatabaseStorage storage;
	int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.serverinfo);
			storage = new DatabaseStorage(this);
			friendlyName = (TextView) findViewById(R.id.idFname);
			ipAdd = (TextView) findViewById(R.id.idIPadd);
			userName = (TextView) findViewById(R.id.idUserName);
			pass = (TextView) findViewById(R.id.idPass);
			pNumber = (TextView) findViewById(R.id.idPortNumber);
			id = (Integer) getIntent().getSerializableExtra("id");
			if (id == -2) {
				server = (ServerData) getIntent()
						.getSerializableExtra("server");
				if (server != null)
					server.setId(-1);

			}
			if (id > 0) {
				try {
					server = storage.getServer(id);
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (server != null) {
				friendlyName.setText(server.getFriendlyName());
				ipAdd.setText(server.getAddress());
				pNumber.setText(server.getPort() + "");
				userName.setText(server.getLogin());
				pass.setText(server.getPassword());
			}
		} catch (Exception ex) {
			Log.w("Android Mobile Voting", "View inited out of order");
			Toast.makeText(this, getString(R.string.viewOutOfOrder), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.serverchangemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.connect:
			connect();
			return true;
		case R.id.save:
			try {
				back();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void back() throws Exception {
		save();

	}

	private void save() {
		int port = parsePort(pNumber.getText().toString());
		if (port != -1) {
			ServerData s = new ServerData(userName.getText().toString(), pass
					.getText().toString(), id, ipAdd.getText().toString(),
					port, friendlyName.getText().toString());
			try {
				storage.addServer(s);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
		}

	}

	private void connect() {
		int port = parsePort(pNumber.getText().toString());
		if (port == -1)
			return;
		ServerData s = new ServerData(userName.getText().toString(), pass
				.getText().toString(), -1, ipAdd.getText().toString(), port,
				getString(R.string.temporaryserverTag));
		Intent i = new Intent(this,
				cvut.fel.mobilevoting.murinrad.views.QuestionsView.class);

		i.putExtra("ServerData", s);
		startActivity(i);
	}

	private int parsePort(String port) {
		int i = -1;
		try {
			i = Integer.parseInt(port);
		} catch (NumberFormatException ex) {
			Toast.makeText(this, getString(R.string.badPortFormatError),
					Toast.LENGTH_LONG).show();
		}
		if (i < 1 || i > 65535) {
			Toast.makeText(this, getString(R.string.badPortFormatError),
					Toast.LENGTH_LONG).show();
			return -1;
		}

		return i;

	}

}

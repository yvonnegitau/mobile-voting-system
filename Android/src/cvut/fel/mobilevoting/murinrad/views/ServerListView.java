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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.communications.BeaconListener;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.gui.PasswordSetterDialogue;
import cvut.fel.mobilevoting.murinrad.gui.ServerButton;
import cvut.fel.mobilevoting.murinrad.storage.DatabaseStorage;
/**
 * A server list that shows the servers in persistence and provides a means to manage servers/voting points
 * @author Radovan Murin
 *
 */
public class ServerListView extends Activity {
	private LinearLayout layout = null;
	private DatabaseStorage storage;
	// private ArrayList<ServerData> servers = new ArrayList<ServerData>();
	private ArrayList<ServerData> servers;
	int helper = 0;
	public Handler handler = new Handler();
	BeaconListener bl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// beaconingServers = new ArrayList<ServerData>();
		bl = new BeaconListener(this);
		@SuppressWarnings("unused")
		Thread t = new Thread() {
			@Override
			public void run(){
				mUpdateTimeTask.run();
				
			}
		};
		//t.start();
		
		onResume();

	}
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
		       final long start = 5000;
		       long millis = SystemClock.uptimeMillis() - start;
		       int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 15;

		       if (seconds == 0) {
		    	   printServers(); 
		          Log.d("Android mobile voting","seconds < 10");
		       } 	     
		       handler.postAtTime(this,
		               start + (((minutes * 60) + seconds + 1) * 1000));
		   }
		};

	@Override
	public void onPause() {
		super.onPause();
		//bl.resetFilter();

	}

	
	
	@Override
	public void onRestart() {
		super.onRestart();
		bl.resetFilter();
		
	}
	@Override
	public void onResume() {
		super.onResume();
		printServers();

		// bl.start();
	}
	
	
	public void printServers() {
		bl.resetFilter();
		
		storage = new DatabaseStorage(this);
		servers = storage.getServers();
		// servers.addAll(beaconingServers);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView header = new TextView(this);
		header.setTextSize(18);
		header.setText(R.string.HeaderServerList);
		layout.addView(header);
		for (int i = 0; i < servers.size(); i++) {
			addServer(servers.get(i));
		}
		TextView delimiter = new TextView(this);
		delimiter.setText(getString(R.string.LANServersDelimiter));
		delimiter.setTextSize(15);
		layout.addView(delimiter,layout.getChildCount());
		setContentView(layout);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
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
		layout.addView(serverBTN, layout.getChildCount());

	}

}

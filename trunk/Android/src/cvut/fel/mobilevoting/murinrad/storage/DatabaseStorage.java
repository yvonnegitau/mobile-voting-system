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
package cvut.fel.mobilevoting.murinrad.storage;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.crypto.Cryptography;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;

/**
 * A database class that provides an interface for database operations
 * 
 * @author Radovan Murin
 * 
 */
public class DatabaseStorage {
	public static final String dbName = "ServerStore";
	public static final String dbTableName = "Servers";
	private static Context context;

	// for testing purposes
	/*
	 * private static final String testData = "INSERT INTO " + dbTableName +
	 * "s (IPAddress, portN, uName, pass ,Fname) VALUES (\"147.32.89.127\", \"10666\", \"murinrad\", \"12345\", \"My server\");"
	 * ;
	 */
	private static final String dbCreationSQL = "CREATE TABLE IF NOT EXISTS "
			+ dbTableName
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, IPAddress VARCHAR, portN VARCHAR, uName VARCHAR, pass VARCHAR ,Fname VARCHAR);commit;";
	SQLiteDatabase DB = null;

	public DatabaseStorage(Context context) {

		DB = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		DatabaseStorage.context = context;
		// dropDatabase();
		DB.execSQL(dbCreationSQL);
		DB.close();
		// DB.execSQL(testData);
	}
/**
 * Returns the servers in the database
 * @return an ArrayList of servers In the database
 */
	public ArrayList<ServerData> getServers() {
		DB = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		Cursor serverCursor = null;
		ArrayList<ServerData> servers = new ArrayList<ServerData>();
		try {
			serverCursor = DB.query(dbTableName, null, null, null, null, null,
					"id");
			int IDpos = serverCursor.getColumnIndex("id");
			int IPpos = serverCursor.getColumnIndex("IPAddress");
			int PNpos = serverCursor.getColumnIndex("portN");
			int UNpos = serverCursor.getColumnIndex("uName");
			int PSpos = serverCursor.getColumnIndex("pass");
			int FNpos = serverCursor.getColumnIndex("Fname");

			serverCursor.moveToFirst();
			while (true) {
				ServerData s = new ServerData(serverCursor.getString(UNpos),
						Cryptography.crypto.decrypt(
								serverCursor.getString(PSpos),
								Cryptography.masterKey),
						Integer.parseInt(serverCursor.getString(IDpos)),
						serverCursor.getString(IPpos),
						Integer.parseInt(serverCursor.getString(PNpos)),
						serverCursor.getString(FNpos));
				servers.add(s);
				if (!serverCursor.moveToNext())
					break;

			}
		} catch (Exception ex) {
			Log.w("Android Mobile Voting", ex.toString());
		}
		DB.close();
		return servers;
	}
/**
 * Adds a server to the persistence of edits an existing entry
 * @param s the server to be altered
 * @return true if success, else false
 * @throws InvalidKeyException
 * @throws UnsupportedEncodingException
 * @throws NoSuchAlgorithmException
 * @throws NoSuchPaddingException
 * @throws IllegalBlockSizeException
 * @throws BadPaddingException
 */
	public boolean addServer(ServerData s) throws InvalidKeyException,
			UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		boolean outcome = false;
		String cmd;
		if (s.getId() > -1) {

			cmd = "UPDATE "
					+ dbTableName
					+ " SET IPAddress=\'"
					+ s.getAddress()
					+ "\', portN='"
					+ s.getPort()
					+ "\',uName=\'"
					+ s.getLogin()
					+ "\',pass=\'"
					+ Cryptography.crypto.encrypt(s.getPassword(),
							Cryptography.masterKey) + "\',Fname=\'"
					+ s.getFriendlyName() + "\' WHERE id='" + s.getId() + "'";
			Log.i("Android mobile voting", "Updating idem no" + s.getId());
			outcome = false;

		} else {

			cmd = "INSERT INTO "
					+ dbTableName
					+ " (IPAddress, portN, uName, pass ,Fname) VALUES (\""
					+ s.getAddress()
					+ "\", \""
					+ s.getPort()
					+ "\", \""
					+ s.getLogin()
					+ "\", \""
					+ Cryptography.crypto.encrypt(s.getPassword(),
							Cryptography.masterKey) + "\", \""
					+ s.getFriendlyName() + "\");";
			Log.i("Android mobile voting", "Creating new database entry...");
			outcome = true;
		}
		executeSQL(cmd);
		Toast.makeText(context, context.getString(R.string.databaseSuccess),
				Toast.LENGTH_LONG);

		return outcome;
	}
	/**
	 * deletes a server from the persistence
	 * @param id
	 * @return true always, legacy purposes
	 */
	public boolean delete(int id) {

		String cmd = "DELETE FROM " + dbTableName + " WHERE id=" + id + "";
		executeSQL(cmd);

		return true;
	}
/**
 * Drops the tables and creates new
 */
	public void dropDatabase() {
		
		String cmd = "drop table " + dbTableName;
		executeSQL(cmd);
		executeSQL(dbCreationSQL);

	}
/**
 * executes a sql command
 * @param cmd
 */
	private void executeSQL(String cmd) {

		DB = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		Log.i("Android mobile voting", "Executing command: " + cmd);
		try {
			DB.execSQL("begin");
			DB.execSQL(cmd);
			DB.execSQL("commit");
		} catch (SQLException ex) {
			Log.e("Android Mobile Voting", ex.toString());
		} catch (Exception ex) {
			Log.e("Andoid Mobile Voting", ex.toString());
		}
		Toast.makeText(context, context.getString(R.string.databaseSuccess),
				Toast.LENGTH_LONG).show();
		DB.close();
	}
/**
 * returns the data for the server that the id is inputed
 * @param id
 * @return the server data in the ServerData class envelope
 * @throws InvalidKeyException
 * @throws NumberFormatException
 * @throws NoSuchAlgorithmException
 * @throws NoSuchPaddingException
 * @throws IllegalBlockSizeException
 * @throws BadPaddingException
 */
	public ServerData getServer(int id) throws InvalidKeyException,
			NumberFormatException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		DB = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		ServerData s = null;
		Cursor serverCursor = null;
		try {
			serverCursor = DB.query(dbTableName, null, "id=" + id, null, null,
					null, null);
		} catch (Exception ex) {
			Log.w("Android Mobile Voting", ex.toString());
		}
		int IDpos = serverCursor.getColumnIndex("id");
		int IPpos = serverCursor.getColumnIndex("IPAddress");
		int PNpos = serverCursor.getColumnIndex("portN");
		int UNpos = serverCursor.getColumnIndex("uName");
		int PSpos = serverCursor.getColumnIndex("pass");
		int FNpos = serverCursor.getColumnIndex("Fname");
		serverCursor.moveToFirst();
		s = new ServerData(serverCursor.getString(UNpos),
				Cryptography.crypto.decrypt(serverCursor.getString(PSpos),
						Cryptography.masterKey), Integer.parseInt(serverCursor
						.getString(IDpos)), serverCursor.getString(IPpos),
				Integer.parseInt(serverCursor.getString(PNpos)),
				serverCursor.getString(FNpos));
		DB.close();
		return s;
	}

}

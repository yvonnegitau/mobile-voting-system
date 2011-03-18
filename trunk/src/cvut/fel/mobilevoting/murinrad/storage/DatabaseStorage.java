package cvut.fel.mobilevoting.murinrad.storage;

import java.util.ArrayList;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.ServerData;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseStorage {
	private static final int version = 1;
	public static final String dbName = "ServerStore";
	public static final String dbTableName = "Servers";
	private static Context context;

	private static final String testData = "INSERT INTO "
			+ dbTableName
			+ "s (IPAddress, portN, uName, pass ,Fname) VALUES (\"147.32.89.127\", \"10666\", \"murinrad\", \"12345\", \"My server\");";

	private static final String dbCreationSQL = "CREATE TABLE IF NOT EXISTS "
			+ dbName
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, IPAddress VARCHAR, portN VARCHAR, uName VARCHAR, pass VARCHAR ,Fname VARCHAR);commit;";
	SQLiteDatabase DB = null;

	public DatabaseStorage(Context context) {

		DB = context.openOrCreateDatabase(dbName, context.MODE_PRIVATE, null);
		this.context = context;
		// dropDatabase();
		DB.execSQL(dbCreationSQL);
		// DB.execSQL(testData);
	}

	public ArrayList<ServerData> getServers() {

		ArrayList<ServerData> servers = new ArrayList<ServerData>();

		Cursor serverCursor = DB.query(dbTableName, null, null, null, null,
				null, "id");
		int IDpos = serverCursor.getColumnIndex("id");
		int IPpos = serverCursor.getColumnIndex("IPAddress");
		int PNpos = serverCursor.getColumnIndex("portN");
		int UNpos = serverCursor.getColumnIndex("uName");
		int PSpos = serverCursor.getColumnIndex("pass");
		int FNpos = serverCursor.getColumnIndex("Fname");

		serverCursor.moveToFirst();
		while (true) {
			ServerData s = new ServerData(serverCursor.getString(UNpos),
					serverCursor.getString(PSpos),
					Integer.parseInt(serverCursor.getString(IDpos)),
					serverCursor.getString(IPpos),
					Integer.parseInt(serverCursor.getString(PNpos)),
					serverCursor.getString(FNpos));
			servers.add(s);
			if (!serverCursor.moveToNext())
				break;

		}
		return servers;
	}

	public boolean addServer(ServerData s) {
		boolean outcome = false;
		String cmd;
		if (s.getId() != -1) {

			cmd = "UPDATE " + dbTableName + " SET IPAddress=\'"
					+ s.getAddress() + "\', portN='" + s.getPort()
					+ "\',uName=\'" + s.getLogin() + "\',pass=\'"
					+ s.getPassword() + "\',Fname=\'" + s.getFriendlyName()
					+ "\' WHERE id='" + s.getId() + "'";
			Log.i("Android mobile voting", "Updating idem no" + s.getId());
			outcome = false;

		} else {

			cmd = "INSERT INTO " + dbTableName
					+ " (IPAddress, portN, uName, pass ,Fname) VALUES (\""
					+ s.getAddress() + "\", \"" + s.getPort() + "\", \""
					+ s.getLogin() + "\", \"" + s.getPassword() + "\", \""
					+ s.getPassword() + "\");";
			Log.i("Android mobile voting", "Creating new database entry...");
			outcome = true;
		}
		executeSQL(cmd);
		Toast.makeText(context, context.getString(R.string.databaseSuccess),
				Toast.LENGTH_LONG);
		return outcome;
	}

	public boolean delete(int id) {
		String cmd = "DELETE FROM " + dbTableName + " WHERE id=" + id + "";
		executeSQL(cmd);
		return true;
	}

	private void dropDatabase() {
		String cmd = "drop table " + dbTableName;
		executeSQL(cmd);

	}

	private void executeSQL(String cmd) {
		Log.i("Android mobile voting", "Executing command: "+cmd);
		try {
			DB.execSQL("begin");
			DB.execSQL(cmd);
			DB.execSQL("commit");
		} catch (SQLException ex) {
			Log.e("Android Mobile Voting", ex.toString());
		}
		Toast.makeText(context, context.getString(R.string.databaseSuccess),
				Toast.LENGTH_LONG).show();

	}
	
	public ServerData getServer(int id) {
		ServerData s = null;
		Cursor serverCursor = DB.query(dbTableName, null, "id="+id, null, null, null, null);
		int IDpos = serverCursor.getColumnIndex("id");
		int IPpos = serverCursor.getColumnIndex("IPAddress");
		int PNpos = serverCursor.getColumnIndex("portN");
		int UNpos = serverCursor.getColumnIndex("uName");
		int PSpos = serverCursor.getColumnIndex("pass");
		int FNpos = serverCursor.getColumnIndex("Fname");
		serverCursor.moveToFirst();
		 s = new ServerData(serverCursor.getString(UNpos),
				serverCursor.getString(PSpos),
				Integer.parseInt(serverCursor.getString(IDpos)),
				serverCursor.getString(IPpos),
				Integer.parseInt(serverCursor.getString(PNpos)),
				serverCursor.getString(FNpos));
		return s;
	}
	
	public void closeDB() {
		DB.close();
	}

}

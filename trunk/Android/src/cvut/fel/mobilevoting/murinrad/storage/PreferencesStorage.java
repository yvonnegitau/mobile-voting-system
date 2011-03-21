package cvut.fel.mobilevoting.murinrad.storage;

import cvut.fel.mobilevoting.murinrad.main;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesStorage {
	public static int PASSWORD_HASH = 0;
	private final String shadowFile = "shadow";
	private SharedPreferences shadow = null;
	private Context c = null;
	public static PreferencesStorage store = new PreferencesStorage();

	private PreferencesStorage() {
		shadow = main.c.getSharedPreferences(shadowFile, 0);
	}

	public void addEntry(int key, String value) {
		SharedPreferences.Editor editor = shadow.edit();
		switch (key) {
		case 0:
			editor.putString(resolveKeyID(key), value);
			break;
		default:
			break;
		}
		editor.commit();
	}

	public String getEntry(int key) {
		String entry = shadow.getString(resolveKeyID(key), "");
		return entry;
	}

	private String resolveKeyID(int key) {
		switch (key) {
		case 0:
			return "pHash";
		default:
			return null;
		}

	}

}

package cvut.fel.mobilevoting.murinrad.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cvut.fel.mobilevoting.murinrad.main;
import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;
import cvut.fel.mobilevoting.murinrad.storage.DatabaseStorage;
import cvut.fel.mobilevoting.murinrad.storage.PreferencesStorage;
import android.util.Log;

public class Cryptography {
	public static Cryptography crypto = new Cryptography();
	public static String masterKey = null;
	private DESKeySpec keySpec = null;
	private static SecretKey key = null;
	static boolean  initialized = false;
	private String pHash = null;

	public Cryptography() {
		pHash = PreferencesStorage.store
				.getEntry(PreferencesStorage.PASSWORD_HASH);

	}

	/**
	 * Snippet from http://www.kospol.gr/204/create-md5-hashes-in-android/
	 * Return s the md5 HASH
	 * 
	 * @param
	 * @return
	 */
	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {

		}
		return "";
	}

	/**
	 * Encrypts the given string with the master code
	 * 
	 * @param input
	 * @param sKey
	 *            not used
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encrypt(String input, String sKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		if (initialized) {
			byte[] cleartext = input.getBytes("UTF8");

			Cipher cipher = Cipher.getInstance("DES");

			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] encrypedPwdBytes = cipher.doFinal(cleartext);

			return Base64.encodeToString(encrypedPwdBytes, Base64.DEFAULT);
		}
		return null;
	}

	/**
	 * Decrypts the given string with the master code
	 * 
	 * @param input
	 *            the string to be decrypted
	 * @param sKey
	 *            not used now
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decrypt(String input, String sKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		if (initialized) {
			byte[] in = Base64.decode(input, Base64.DEFAULT);
			Cipher cipher2 = Cipher.getInstance("DES");// cipher is not thread
														// safe
			cipher2.init(Cipher.DECRYPT_MODE, key);
			byte[] plainTextPwdBytes = (cipher2.doFinal(in));

			return new String(plainTextPwdBytes);
		} else
			return null;

	}

	public int verifyPass(String pass) {
		if (pHash.equals(""))
			return -1;
		if (pass.equals(""))
			return 0;
		String p = md5(pass);
		if (p.equals(pHash)) {
			return 1;
		}
		return 0;

	}

	/**
	 * Initializes the instance for use crypting/decrypting
	 */
	public void init(String k) {
		setKey(k);
		try {
			keySpec = new DESKeySpec(k.getBytes("UTF8"));// @Todo pozor
															// heslo !!!
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			key = keyFactory.generateSecret(keySpec);
			initialized = true;
		} catch (Exception ex) {
			Log.e("Android Mobile Voting Crypto init", ex.toString());
		}
	}

	/**
	 * Sets the master key which the instance will use for encryption
	 * 
	 * @param key
	 */
	private void setKey(String key) {
		Cryptography.masterKey = key;
	}

	/**
	 * Changes the master code
	 * 
	 * @param s
	 *            key in open text
	 * @return
	 */
	public boolean ChangeCryptoKey(String s) {
		if (s.length() < 7)
			return false;
		DatabaseStorage db = new DatabaseStorage(main.c.getApplicationContext());
		ArrayList<ServerData> servers = db.getServers();
		Log.i("Android Mobile Voting", "Servers size "+servers.size());
		init(s);
		PreferencesStorage.store.addEntry(PreferencesStorage.PASSWORD_HASH,
				md5(s));
		pHash = md5(s);
		if (!servers.isEmpty()) {
			db.dropDatabase();
			Log.i("Android Mobile Voting", "Wasnt Empty ");
		}

		for (int i = 0; i < servers.size(); i++) {
			try {
				Log.i("Android Mobile Voting", "Adding servername "+servers.get(i).getFriendlyName());
				servers.get(i).setId(-1);
				db.addServer(servers.get(i));
				
			} catch (Exception e) {
				Log.e("Android Mobile Voting", e.toString());
				 db.dropDatabase();
				db.closeDB();
				return false;
			}
		}
		db.closeDB();
		return true;
	}
}

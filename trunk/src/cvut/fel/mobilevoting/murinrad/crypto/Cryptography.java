package cvut.fel.mobilevoting.murinrad.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class Cryptography {
	public static Cryptography crypto = new Cryptography();
	private static Cipher cipher;
	private static IvParameterSpec ivSpec;

	public Cryptography() {

		byte[] iv = "ahoj".getBytes();
		// wrap key data in Key/IV specs to pass to cipher
		ivSpec = new IvParameterSpec(iv);
		// create the cipher with the algorithm you choose
		// see javadoc for Cipher class for more info, e.g.
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Snippet from http://www.kospol.gr/204/create-md5-hashes-in-android/
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
			Log.e("Android Mobile Voting", e.toString());
		}
		return "";
	}

	public String encrypt(String input, String sKey)
			throws ShortBufferException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] in = input.getBytes();
		SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), "DES");

		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] encrypted = new byte[cipher.getOutputSize(in.length)];
		int enc_len = cipher.update(in, 0, in.length, encrypted, 0);
		enc_len += cipher.doFinal(encrypted, enc_len);

		return new String(encrypted);

	}

	public String decrypt(String input, String sKey)
			throws InvalidKeyException, InvalidAlgorithmParameterException,
			ShortBufferException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] in = input.getBytes();
		SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), "DES");
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] decrypted = new byte[cipher.getOutputSize(in.length)];
		int dec_len = cipher.update(in, 0, in.length, decrypted, 0);
		dec_len += cipher.doFinal(decrypted, dec_len);
		return "";

	}
}

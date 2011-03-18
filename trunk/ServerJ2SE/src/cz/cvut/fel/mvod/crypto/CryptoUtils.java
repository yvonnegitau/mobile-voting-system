/*
 * © 2010, Jakub Valenta
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Jakub Valenta
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors “as is” and any
 * express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the foundation or contributors be liable for any direct, indirect,
 * incidental, special, exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits; or business
 * interruption) however caused and on any theory of liability, whether in contract, strict
 * liability, or tort (including negligence or otherwise) arising in any way out of the use
 * of this software, even if advised of the possibility of such damage.
 */

package cz.cvut.fel.mvod.crypto;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Knihovní třída poskytující kryptografické funkce potřebné pro program.
 * @author jakub
 */
public final class CryptoUtils {

	/**
	 * Řetězec používaný pro solení při hashování hesla.
	 */
	private static final String salt = "yV@!4\"=zfk9w&xDkkv[%1d(agt'|v0Oycg_ACDUt";

	/**
	 * Implementace SHA-2 hashovacího algoritmu.
	 * @param data řetězce k vytvoření hashe
	 * @return SHA-2 otisk (32 bajtů)
	 */
	private static byte[] sha2(String... data) {
		byte[] digest = new byte[32];
		StringBuilder buffer = new StringBuilder();
		for(String s: data) {
			buffer.append(s);
		}
		MessageDigest sha256 = null;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			assert false;
		}
		sha256.update(buffer.toString().getBytes());
		try {
			sha256.digest(digest, 0, digest.length);
		} catch (DigestException ex) {
			assert false;
		}
		return digest;
	}

	/**
	 * Spočítá hash hesla. SHA-2(password|userName|salt),
	 * kde | je oprátor zřetězení, a salt je tajný token použitý pro vyšší ochranu.
	 * @param password heslo
	 * @param userName uživatelské jméno
	 * @return 32 bajtový hash hesla
	 */
	public static  byte[] passwordDigest(String password, String userName) {
		return sha2(password, userName, salt);
	}

	private CryptoUtils() {
		//library class
	}
}

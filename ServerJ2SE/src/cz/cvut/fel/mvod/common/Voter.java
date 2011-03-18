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
package cz.cvut.fel.mvod.common;

import java.io.Serializable;

/**
 * Účastník hlasování.
 * @author Petr
 */
public class Voter implements Serializable {

	private static final long serialVersionUID = (long) 1127;

	private String firstName;
	private String lastName;
	private byte[] password;
	private String userName;
	private int id;
	private boolean idSet;

	public Voter() {
		this("", "", null, null);
		idSet = false;
	}

	public Voter(String firstName, String lastName, byte[] password, String userName) {
		this(firstName, lastName, password, userName, -1);
		idSet = false;
	}

	public Voter(String firstName, String lastName, byte[] password, String username, int id) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.userName = username;
		this.id = id;
		idSet = false;
	}

	/**
	 * Vrátí jméno účastníka.
	 * @return jméno 
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Nastaví jméno účastníka.
	 * @param firstName jméno
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Vrátí příjmení účastníka.
	 * @return příjmení
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Nastaví příjmení účastníka.
	 * @param lastName příjmení
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Vrátí hash hesla účastníka.
	 * @return hash hesla
	 */
	public byte[] getPassword() {
		return password;
	}

	/**
	 * Nastaví hash hesla účastníka.
	 * @param password zahashované heslo
	 */
	public void setPassword(byte[] password) {
		this.password = password;
	}

	/**
	 * Vrátí uživatelské jméno účastníka.
	 * @return uživatelské jméno
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Nastaví uživatelské jméno účastníka.
	 * @param userName uživatelské jméno
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Vrátí jednoznačný identifikátor účastníka.
	 * @return jednoznačný identifikátor
	 */
	public int getId() {
		return id;
	}

	/**
	 * Nastaví jednoznačný identifikátor.
	 * Vyvolá výjimku IllegalAccessError pokud je identifikátor nastavený.
	 * @param id jednoznačný identifikátor
	 */
	public void setId(int id) {
		if(!idSet) {
			this.id = id;
			idSet = true;
		} else {
			throw new IllegalAccessError("Field ID is already set.");
		}
	}
}

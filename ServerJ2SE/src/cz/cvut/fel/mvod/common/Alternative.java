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
 * Odpověď na otázku.
 * @author jakub
 */
public class Alternative implements Serializable {

	private static final long serialVersionUID = (long) 123456;

	private int id;
	private String text;
	private boolean isCorrect;
	private boolean idSet;

	public Alternative(int id, String text, boolean isCorrect) {
		this.id = id;
		this.text = text;
		this.isCorrect = isCorrect;
	}
	
	public Alternative() {
		text = "";
		isCorrect = false;
		id = -1;
		idSet = false;
	}

	/**
	 * Zjištění, jestli je odpověď správná.
	 * @return true pokud je odpověď správná
	 */
	public boolean isCorrect() {
		return isCorrect;
	}

	/**
	 * Vrací text odpovědi.
	 * @return text odpovědi
	 */
	public String getText() {
		return text;
	}

	/**
	 * Nastaví text odpovědi.
	 * @param value text odpovědi
	 */
	public void setText(String value) {
		this.text = value;
	}

	/**
	 * Nastaví správnost odpovědi.
	 * @param isCorrect true pokud je odpověď správná
	 */
	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	/**
	 * Nastaví jednoznačný identifikátor instance.
	 * Tuto metodu je možné volat pouze pokud není identifikátor nastavený.
	 * Jinak vyvolá výjimku IllegalAccessError.
	 * @param id jednoznačný identifikátor
	 */
	public void setId(int id) {
		if(idSet) {
			throw new IllegalAccessError();
		}
		idSet = true;
		this.id = id;
	}

	/**
	 * Vrací jednoznačný identifikátor odpovědi.
	 * @return jednoznačný identifikátor odpovědi
	 */
	public int getId() {
		return id;
	}
}

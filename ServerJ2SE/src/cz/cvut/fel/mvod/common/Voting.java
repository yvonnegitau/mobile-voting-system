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
import java.util.ArrayList;
import java.util.List;

/**
 * Hlasování nebo test.
 * @author Petr
 */
public class Voting implements Serializable {

	private static final long serialVersionUID = (long) 9963;

	private boolean secret;
	private boolean test;
	private int minVoters;
	private List<Question> questions;
	private EvaluationType evaluation;
	private int id;
	private boolean idSet;

	public Voting() {
		secret = false;
		test = false;
		minVoters = 0;
		questions = new ArrayList<Question>();
		id = -1;
		idSet = false;
	}

	public Voting(boolean secret, boolean test, List<Question> questions, int id) {
		this.secret = secret;
		this.test = test;
		this.minVoters = 0;
		this.questions = questions;
		this.id = id;
		idSet = true;
	}

	/**
	 * Nastaví jednoznačný identifikátor hlasování.
	 * Pokud je již nastavený vyvolá výjimku IllegalAccessError.
	 * @param id jednoznačný identifikátor
	 */
	public void setId(int id) {
		if( ! idSet) {
			this.id = id;
			idSet = true;
		} else {
			throw new IllegalAccessError("Field ID is already set.");
		}
	}

	/**
	 * Vrátí jednoznačný identifikátor.
	 * @return jednoznačný identifikátor
	 */
	public int getId() {
		return id;
	}

	/**
	 * Vrátí seznam otázek.
	 * @return otázky
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * Vrátí počet otázek.
	 * @return počet otázek
	 */
	public int getQuestionCount() {
		return questions.size();
	}

	/**
	 * Přidá otázku.
	 * @param question nová otázka
	 */
	public void addQuestion(Question question) {
		questions.add(question);
	}

	/**
	 * Odebere otázku.
	 * @param index pořadové číslo otázky
	 * @throws IndexOutOfBoundsException pokud neexistuje otázka na zadaném pořadovém místě.
	 */
	public void removeQuestion(int index) {
		if(index < 0 || index >= questions.size()) {
			throw new IndexOutOfBoundsException();
		}
		questions.remove(index);
	}

	/**
	 * Vrátí otázku se zadaným pořadovým číslem.
	 * @param index pořadové číslo
	 * @return ot8zka
	 * @throws IndexOutOfBoundsException pokud neexistuje otázka na zadaném pořadovém místě.
	 */
	public Question getQuestion(int index) {
		if(index < 0 || index >= questions.size()) {
			throw new IndexOutOfBoundsException();
		}
		return questions.get(index);
	}

	/**
	 * Vrátí jestli je hlasování tajné.
	 * @return je hlasování tajné
	 */
	public boolean isSecret() {
		return secret;
	}

	/**
	 * Nastaví tajné/veřejné hlasování.
	 * @param isSecret je hlasování tajné
	 */
	public void setSecret(boolean isSecret) {
		this.secret = isSecret;
	}

	/**
	 * Vrátí jestli se jedná o test.
	 * @return jedná se o test?
	 */
	public boolean isTest() {
		return test;
	}

	/**
	 * Nastaví jestli je hlasování test.
	 * @param isTest je test?
	 */
	public void setTest(boolean isTest) {
		this.test = isTest;
	}

	/**
	 * Vrátí hranici platnosti hlasování v procentech
	 * @return hranice platnosti hlasování (v rozsahu 0 - 100)
	 */
	public int getMinVoters() {
		return minVoters;
	}

	/**
	 * Nastaví hranici platnosti hlasování.
	 * @param minVoters číslo v rozsahu 0 - 100
	 * @throws NumberFormatException pokud je číslo mimo rozsah 0 - 100
	 */
	public void setMinVoters(int minVoters) {
		if(minVoters < 0 || minVoters > 100) {
			throw new NumberFormatException();
		}
		this.minVoters = minVoters;
	}

	/**
	 * Vrátí způsob vyhodnocení testu.
	 * @return způsob vyhodnocení
	 */
	public EvaluationType getEvaluation() {
		return evaluation;
	}

	/**
	 * Nastaví způsob vyhodnocení testu.
	 * @param evaluation způsob vyhodnocení
	 */
	public void setEvaluation(EvaluationType evaluation) {
		this.evaluation = evaluation;
	}
}

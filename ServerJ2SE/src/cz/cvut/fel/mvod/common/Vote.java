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

import java.util.ArrayList;
import java.util.List;

/**
 * Odeslaný hlas.
 * @author jakub
 */
public class Vote {

	private int id;
	private Voter voter;
	private List<Alternative> checked;
	private Question question;
	private int evaluation;
	private boolean idSet;

	public Vote() {
		idSet = false;
	}

	public Vote(Voter voter, Question question, List<Alternative> checked, int evaluation) {
		this.voter = voter;
		this.question = question;
		if(checked != null) {
			this.checked = checked;
		} else {
			this.checked = new ArrayList<Alternative>();
		}
		this.evaluation = evaluation;
	}

	/**
	 * Vyhodnocení hlasu (kolik bylo získáno bodů).
	 * @return vyhodnocení hlasu
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * Vrátí seznam označených odpovědí.
	 * @return označené odpovědi
	 */
	public List<Alternative> getChecked() {
		return checked;
	}

	/**
	 * Vrátí hlasujícího, který hlas odevzdal.
	 * V případě tajného hlasování je vždy null.
	 * @return autor hlasu
	 */
	public Voter getVoter() {
		return voter;
	}

	/**
	 * Vrátí jednoznačný identifikátor hlasu.
	 * @return jednoznačný identifikátor hlasu
	 */
	public int getId() {
		return id;
	}

	/**
	 * Nastaví jednoznačný identifikátor hlasu.
	 * Pokud je tato metoda zavolaná a identifikátor je nastaven vyvolá
	 * výjimku IllegalAccessError
	 * @param id jednoznažný identifikátor
	 */
	public void setId(int id) {
		if(idSet) {
			throw new IllegalAccessError();
		}
		idSet = true;
		this.id = id;
	}

	/**
	 * Vrátí otázku, ke které se hlas vztahuje.
	 * @return otázka, ke které se hlas vztahuje
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Nastaví otázku, na kterou hlas odpovídá.
	 * @param question otázka, na kterou hlas odpovídá
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * Nastaví vyhodnocení odpovědi (počet získaných bodů).
	 * @param evaluation vyhodnocení odpovědi
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * Nastaví autora hlasu.
	 * @param voter účastník hlasování, který odeslal tento hlas
	 */
	public void setVoter(Voter voter) {
		this.voter = voter;
	}

	/**
	 * Nastaví seznam označených odpovědí.
	 * @param checked označené odpovědi
	 */
	public void setChecked(List<Alternative> checked) {
		this.checked = checked;
	}
}

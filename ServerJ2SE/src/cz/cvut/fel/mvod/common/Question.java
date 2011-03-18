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
 * Testová nebo hlasovací otázkaa.
 * @author Petr
 */
public class Question implements Serializable, Cloneable {

	private static final long serialVersionUID = (long) 1235748;

	private int evaluation;
	private int id;
	private String text;
	private int minPercent;
	private int maxWinners;
	private int maxSelect;
	private int minSelect;
	private List<Alternative> alternatives;
	private boolean idSet;
	private State state;

	public Question() {
		this("", 0, 1, 1, 0, 1, null);
	}

	public Question(String text, int minPercent, int maxWinners,
					int maxSelect, int minSelect, int evaluation, List<Alternative> alternatives) {
		this.text = text;
		this.minPercent = minPercent;
		this.maxWinners = maxWinners;
		this.maxSelect = maxSelect;
		this.minSelect = minSelect;
		this.evaluation = evaluation;
		if(alternatives != null) {
			this.alternatives = alternatives;
		} else {
			this.alternatives = new ArrayList<Alternative>();
		}
		id = -1;
		idSet = false;
		state = State.NOT_SET;
	}

	protected Question(Question question) {
		text = question.getText();
		minPercent = question.getMinPercent();
		maxWinners = question.getMaxWinners();
		maxSelect = question.getMaxSelect();
		minSelect = question.getMinSelect();
		id = question.getId();
		alternatives = new ArrayList<Alternative>();
		evaluation = question.getEvaluation();
		for(int i = 0; i < question.getAlternativesCount(); i ++) {
			alternatives.add(question.getAlternative(i));
		}
	}

	/**
	 * Stav otázky.
	 */
	public static enum State {
		/**
		 * Hlasování o otázce právě probíhá.
		 */
		RUNNING,
		/**
		 * Hlasování o otázce bylo ukončeno.
		 */
		FINISHED, 
		/**
		 * Otázka nebyla dosud zadaná.
		 */
		NOT_SET
	}

	/**
	 *Vrátí počet odpovědí.
	 * @return počet odpovědí
	 */
	public int getAlternativesCount() {
		return alternatives.size();
	}

	/**
	 * Přidá další odpověď.
	 * @param alt odpověď
	 * @throws IllegalStateException pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 */
	public void addAlternative(Alternative alt) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		alternatives.add(alt);
	}

	/**
	 * Nataví odpovědi otázky.
	 * @param alternatives odpovědi
	 * @throws IllegalStateException pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 */
	public void setAlternatives(List<Alternative> alternatives) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		this.alternatives = alternatives;
	}


	/**
	 * Odebere odpověď na otázku.
	 * @param index of alternative to remove
	 * @throws IndexOutOfBoundsException pokud neexistuje otázka se zadaným indexem
	 * @throws IllegalStateException pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 */
	public void removeAlternative(int index) {
		if(index < 0 || index > alternatives.size()) {
			throw new IndexOutOfBoundsException();
		}
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		alternatives.remove(index);
	}


	/**
	 * Vrátí odpověď.
	 * @param index pořadové číslo odpovědi
	 * @return alternative odpověď
	 * @throws IndexOutOfBoundsException pokud neexistuje otdpověď se zadaným pořadovým číslem
	 */
	public Alternative getAlternative(int index) {
		if(index < 0 || index > alternatives.size()) {
			throw new IndexOutOfBoundsException();
		}
		return alternatives.get(index);
	}

	/**
	 * Vrátí text otázky.
	 * @return text otázky
	 */
	public String getText() {
		return text;
	}

	/**
	 * Nastaví text otázky.
	 * @param text text otázky
	 * @throws IllegalStateException pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 */
	public void setText(String text) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		this.text = text;
	}

	/**
	 * Vrátí minimální počet hlasů, který je potřeba, aby mohla být odpověď
	 * označena jako vítězná.
	 * @return číslo v rozsahu 0 - 100
	 */
	public int getMinPercent() {
		return minPercent;
	}

	/**
	 * Nastaví minimální počet hlasů v procentech, který je potřeba získat,
	 * aby mohla být odpověď označena jako vítězná.
	 * @param minPercent číslo v rozsahu 0 - 100
	 * @throws IllegalStateException  pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 * @throws NumberFormatException pokud není parametr minPercent v rozsahu 0 - 100
	 */
	public void setMinPercent(int minPercent) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		if(minPercent < 0 || minPercent > 100) {
			throw new NumberFormatException();
		}
		this.minPercent = minPercent;
	}

	/**
	 * Vrátí maximální počet vítězů hlasování o této otázce.
	 * @return maximální počet vítězů
	 */
	public int getMaxWinners() {
		return maxWinners;
	}

	/**
	 * Nastaví maximální počet vítězů, kteří mohou vyhrát při jednom hlasování.
	 * např. při dvoukolové volbě postupují do druhého kola dva, tak
	 * nastavím maxWinners na 2
	 * @param maxWinners maximální počet vítězů
	 * @throws IllegalStateException  pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 * @throws NumberFormatException pokud je maxWinners menší než 0
	 */
	public void setMaxWinners(int maxWinners) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		if(maxWinners < 0) {
			throw new NumberFormatException();
		}
		this.maxWinners = maxWinners;
	}

	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	/**
	 * Maximální počet odpovědí, které je možné označit.
	 * @param maxSelect maximální počet odpovědí, které je možné označit
	 * @throws IllegalStateException  pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 * @throws NumberFormatException pokud je maxSelect menší než 0
	 */
	public void setMaxSelect(int maxSelect) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		if(maxSelect < 0) {
			throw new NumberFormatException();
		}
		this.maxSelect = maxSelect;
	}

	/**
	 * Minimální počet odpovědí, které je možné označit.
	 * @param minSelect minimální počet odpovědí, které je možné označit
	 * @throws IllegalStateException  pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 * @throws NumberFormatException pokud je minSelect menší než 0
	 */
	public void setMinSelect(int minSelect) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		if(minSelect < 0) {
			throw new NumberFormatException();
		}
		this.minSelect = minSelect;
	}

	/**
	 * Vrátí maximální počet zaškrtnutých odpovědí.
	 * @return maximální počet zaškrtnutých odpovědí.
	 */
	public int getMaxSelect() {
		return maxSelect;
	}

	
	/**
	 * Vrátí minimální počet zaškrtnutých odpovědí.
	 * @return minimální počet zaškrtnutých odpovědí.
	 */
	public int getMinSelect() {
		return minSelect;
	}

	/**
	 * Nastaví jednoznačný identifikátor otázky.
	 * Tuto metodu je možné volat, pouze pokud není jednoznačný identifikátor nastavený.
	 * Jinak vyvolá výjimku IllegalAccessError
	 * @param id jednoznačný identifikátor otázky
	 */
	public void setId(int id) {
		if(!idSet) {
			this.id = id;
			idSet = true;
		} else {
			throw new IllegalAccessError("Field ID is already set.");
		}
	}

	/**
	 * Vrátí jednoznačný identifikátor otázky.
	 * @return jednoznačný identifikátor otázky
	 */
	public int getId() {
		return id;
	}

	/**
	 * Vrátí ohodnocení otázky (počet bodů, které je možné za otázku získat).
	 * @return ohodnocení otázky
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * Nastaví ohodnocení otázky (počet bodů, které je možné za otázku získat).
	 * @param evaluation ohodnocení otázky
	 * @throws IllegalStateException  pokud je otázka již zadaná,
	 * nebo probíhá hlasování {@link Question.State}
	 */
	public void setEvaluation(int evaluation) {
		if(state != State.NOT_SET) {
			throw new IllegalStateException();
		}
		this.evaluation = evaluation;
	}

	/**
	 * Nastaví stav otázky.
	 * @param state stav otázky
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Vrátí stav otázky.
	 * @return stav otázky
	 */
	public State getState() {
		return state;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Question(this);
	}
}

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

package cz.cvut.fel.mvod.evaluation;

import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vyhodnocení otázky hlasování.
 * @author jakub
 */
public class VotingQuestionResult {

	private int votersCount;
	private int votesCount;
	private double votesPercent;
	private List<Alternative> winner;
	private Question question;
	private boolean valid;
	private List<Counter> alternatives;

	public VotingQuestionResult(Question question, double minVotes, int votersCount, List<Vote> votes) {
		this.question = question;
		this.votersCount = votersCount;
		votesCount = votes.size();
		votesPercent = 100.0 * votesCount / votersCount;
		valid = votesPercent >= minVotes && votes.size() > 0;
		Map<Alternative, Counter> counter = new HashMap<Alternative, Counter>();
		alternatives = new ArrayList<Counter>();
		for(Alternative alternative: question.getAlternatives()) {
			Counter c = new Counter(alternative);
			counter.put(alternative, c);
			alternatives.add(c);
		}
		for(Vote vote: votes) {
			for(Alternative alternative: vote.getChecked()) {
				counter.get(alternative).voteCount ++;
			}
		}
		double max = 0;
		winner = new ArrayList<Alternative>();
		for(Counter c: alternatives) {
			c.votePercent = votesCount != 0 ? 100.0 * c.voteCount / votesCount : 0;
			if(max < c.votePercent) {
				max = c.votePercent;
			}
		}
		if(max >= minVotes && max != 0) {
			for(Counter c: alternatives) {
				if(c.votePercent == max) {
					winner.add(c.alternative);
				}
			}
		}
	}

	/**
	 * Vrátí počet hlasů.
	 * @return počet hlasů
	 */
	public int getVotesCount() {
		return votesCount;
	}

	/**
	 * Vrátí účast na hlasování v procentech.
	 * @return účast na hlasování v procentech
	 */
	public double getVotesPercent() {
		return votesPercent;
	}

	/**
	 * Vrátí otázku, která se vztahuje k tomuto hodnocení.
	 * @return otázka
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Vrátí zda bylo hlasování o této otázce platné.
	 * @return bylo hlasování platné
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Vrátí vítěze hlasování (pouze pokud bylo hlasování platné).
	 * @return vítěz hlasování
	 */
	public List<Alternative> getWinner() {
		return winner;
	}

	/**
	 * Vrátí počet účastníků hlasů.
	 * @return počet účastníků hlasování
	 */
	public int getVotersCount() {
		return votersCount;
	}

	/**
	 * Vrátí relativní počet hlasů odpovědi.
	 * @param alternative odpověď
	 * @return relativní počet hlasů
	 */
	public double getAlternativePercent(Alternative alternative) {
		for(Counter c: alternatives) {
			if(c.alternative == alternative) {
				return c.votePercent;
			}
		}
		throw new IllegalArgumentException("No such alternative.");
	}

	/**
	 * Absolutní počet hlasů odpovědi.
	 * @param alternative odpověď
	 * @return absolutní počet hlasů
	 */
	public int getAlternativeVoteCount(Alternative alternative) {
		for(Counter c: alternatives) {
			if(c.alternative == alternative) {
				return c.voteCount;
			}
		}
		throw new IllegalArgumentException("No such alternative.");
	}

}

/**
 * Počítadlo hlasů odpovědi.
 * @author jakub
 */
class Counter {
	/**
	 * Odpověď.
	 */
	public final Alternative alternative;
	/**
	 * Absolutní počet hlasů odpovědi.
	 */
	public int voteCount;
	/**
	 * Relativní počet hlasů odpovědi v procentech.
	 */
	public double votePercent;

	public Counter(Alternative alternative) {
		this.alternative = alternative;
		voteCount = 0;
		votePercent = 0;
	}

}
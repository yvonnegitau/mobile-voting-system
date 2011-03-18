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

import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vyhodnocení hlasování.
 * @author jakub
 */
public class VotingResult {

	private Voting voting;
	private Map<Question, List<Vote>> votesByQuestion;
	private Map<Voter, List<Vote>> votesByVoter;
	private Map<String, Voter> voters;
	private List<VotingQuestionResult> questionResults;
	private List<Question> questions;
	private int validQuestionCount;

	public VotingResult(Voting voting, Collection<Vote> votes, Collection<Voter> voters) {
		if(voting.isTest()) {
			throw new IllegalArgumentException();
		}
		this.voting = voting;
		this.votesByQuestion = new HashMap<Question, List<Vote>>();
		this.votesByVoter = new HashMap<Voter, List<Vote>>();
		this.voters = new HashMap<String, Voter>();
		for(Question question: voting.getQuestions()) {
			this.votesByQuestion.put(question, new ArrayList<Vote>());
		}
		for(Voter voter: voters) {
			this.votesByVoter.put(voter, new ArrayList<Vote>());
		}
		for(Vote vote: votes) {
			List<Vote> questionVotes = this.votesByQuestion.get(vote.getQuestion());
			questionVotes.add(vote);
			if(!voting.isSecret()) {
				List<Vote> voterVotes = this.votesByVoter.get(vote.getVoter());
				voterVotes.add(vote);
			}
		}
		for(Voter voter: voters) {
			this.voters.put(voter.getUserName(), voter);
		}
		this.validQuestionCount = 0;
		questionResults = new ArrayList<VotingQuestionResult>();
		questions = new ArrayList<Question>();
		for(Question q: voting.getQuestions()) {
			if(q.getState() != Question.State.FINISHED) {
				continue;
			}
			VotingQuestionResult result = new VotingQuestionResult(q, voting.getMinVoters(), voters.size(), this.votesByQuestion.get(q));
			if(result.isValid()) {
				validQuestionCount ++;
			}
			questionResults.add(result);
			questions.add(q);
		}
	}

	/**
	 * Seznam vyhodnocení jednotlivých otázek.
	 * @return vyhodnocení otázek
	 */
	public Collection<VotingQuestionResult> getQuestionResults() {
		return questionResults;
	}

	/**
	 * Vrátí seznam účastníků hlasování.
	 * @return účastníci hlasování 
	 */
	public Collection<Voter> getVoters() {
		return votesByVoter.keySet();
	}

	/**
	 * Vrátí seznam otázek.
	 * @return otázky hlasování
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * Vrátí seznam hlasů odeslané účastníkem.
	 * Pokud se jedná o tajné hlasování vrátí vždy null.
	 * @param voter účastník
	 * @return účastníkovi hlasy
	 */
	public List<Vote> getVotes(Voter voter) {
		if(voting.isSecret()) {
			return null;
		}
		return votesByVoter.get(voter);
	}

	/**
	 * Vrátí seznam hlasů jedné otázky.
	 * @param question otázka
	 * @return hlasy odpovídající na otázku
	 */
	public List<Vote> getVotes(Question question) {
		return votesByQuestion.get(question);
	}

	/**
	 * Vrátí hlasování.
	 * @return hlasování
	 */
	public Voting getVoting() {
		return voting;
	}

	/**
	 * Vrátí počet účastníků hlasování.
	 * @return počet účastníků
	 */
	public int getVotersCount() {
		return voters.size();
	}

	/**
	 * Vrátí počet otázek hlasování.
	 * @return počet otázek
	 */
	public int getQuestionCount() {
		return questionResults.size();
	}

	/**
	 * Vrátí počet otázek s platným výsledkem.
	 * @return počet otázek s platným výsledkem
	 */
	public int getValidQuestionCount() {
		return validQuestionCount;
	}
}

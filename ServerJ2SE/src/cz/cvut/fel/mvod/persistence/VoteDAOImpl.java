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
package cz.cvut.fel.mvod.persistence;

import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jakub
 */
class VoteDAOImpl extends DefaultDAOObservable implements VoteDAO, DAOObservable {

	private final Map<Question, List<Vote>> votesByQuestion = new HashMap<Question,  List<Vote>>();
	private final Map<Voter, List<Vote>> votesByVoter = new HashMap<Voter,  List<Vote>>();
	private final List<Vote> votes = new ArrayList<Vote>();

	public void saveVote(Vote vote) throws DAOException {
		synchronized(votesByQuestion) {
			List<Vote> questionVotes = votesByQuestion.get(vote.getQuestion());
			if(questionVotes == null) {
				questionVotes = new ArrayList<Vote>();
				votesByQuestion.put(vote.getQuestion(), questionVotes);
			}
			questionVotes.add(vote);
		}
		synchronized(votesByVoter) {
			List<Vote> voterVotes = votesByVoter.get(vote.getVoter());
			if(voterVotes == null) {
				voterVotes = new ArrayList<Vote>();
				votesByVoter.put(vote.getVoter(), voterVotes);
			}
			voterVotes.add(vote);
		}
		synchronized(votes) {
			votes.add(vote);
		}
		notifyObservers(DAOObserverEvent.NEW_DATA);
	}

	public void deleteVote(Vote vote) throws DAOException {
		synchronized(votesByQuestion) {
			votesByQuestion.get(vote.getQuestion()).remove(vote);
		}
		synchronized(votesByVoter) {
			votesByVoter.get(vote.getVoter()).remove(vote);
		}
		notifyObservers(DAOObserverEvent.DELETE);
	}

	public Collection<Vote> getVotes(Question question) throws DAOException {
		List<Vote> votes;
		synchronized(votesByQuestion) {
			votes = votesByQuestion.get(question);
		}
		if(votes == null) {
			votes = new ArrayList<Vote>();
		}
		return votes;
	}

	public Collection<Vote> getVotes(Voter voter) throws DAOException {
		List<Vote> votes;
		synchronized(votesByVoter) {
			votes = votesByVoter.get(voter);
		}
		if(votes == null) {
			votes = new ArrayList<Vote>();
		}
		return votes;
	}

	@Override
	public Collection<Vote> retrieveVotes() throws DAOException {
		return votes;
	}

}

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
import cz.cvut.fel.mvod.common.Voting;
import cz.cvut.fel.mvod.persistence.derby.DerbyDAOFactory;
import cz.cvut.fel.mvod.persistence.derby.DerbyDAOFactoryImpl;

/**
 *
 * @author jakub
 */
public class DAOFacadeImpl implements DAOFacade {

	private static DAOFacade instance;

	private final VotingDAO votings;
	private final VoterDAO voters;
	private final VoteDAO votes;
	private final QuestionDAO questionsDB;
	private final VotingDAO votingsDB;
	private final VoterDAO votersDB;
	private final VoteDAO votesDB;
	private Voting currentVoting = null;

	private DAOFacadeImpl() throws DAOException {
		votings = DAOFactoryImpl.getInstance().getVotingDAO();
		voters = DAOFactoryImpl.getInstance().getVoterDAO();
		votes = DAOFactoryImpl.getInstance().getVoteDAO();
		DerbyDAOFactory factory = new DerbyDAOFactoryImpl();
		questionsDB = factory.getQuestionDAO();
		votingsDB = factory.getVotingDAO();
		votersDB = factory.getVoterDAO();
		votesDB = factory.geVoteDAO();
	}

	public static synchronized void initInstance() throws DAOException {
		if(instance == null) {
			instance = new DAOFacadeImpl();
		}
	}

	public static DAOFacade getInstance() {
		if(instance == null) {
			throw new IllegalAccessError("Unitialized singleton.");
		}
		return instance;
	}

	/**
	 * {@inheritDoc }
	 */
	public Question getQuestion(int id) throws DAOException {
		for(Voting voting: votings.getVotings()) {
			for(Question q: voting.getQuestions()) {
				if(q.getId() == id) {
					return q;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc }
	 */
	public Voter getVoter(String userName) throws DAOException {
		for(Voter voter: voters.retrieveVoters()) {
			if(voter.getUserName().equals(userName)) {
				return voter;
			}
		}
		return null;
	}

	public Voting getCurrentVoting() {
		return currentVoting;
	}

	public void setCurrentVoting(Voting voting) throws DAOException {
		currentVoting = voting;
		votings.saveVoting(currentVoting, null);
	}

	public void saveCurrentVoting() throws DAOException {
		if(currentVoting != null) {
			synchronized(votersDB) {
				for(Voter voter: voters.retrieveVoters()) {
					if(voter.getId() == -1) {
						Voter v = votersDB.getVoter(voter.getUserName());
						if(v == null) {
							votersDB.saveVoter(voter);
						} else {
							voter.setId(v.getId());
							votersDB.updateVoter(voter);
						}
					} else {
						votersDB.updateVoter(voter);
					}
				}
			}
			synchronized(votingsDB) {
				if(currentVoting.getId() == -1) {
					votingsDB.saveVoting(currentVoting, voters.retrieveVoters());
				}
			}
			synchronized(questionsDB) {
				for(Question question: currentVoting.getQuestions()) {
					if(question.getId() == -1 && (question.getState() == Question.State.RUNNING ||
							question.getState() == Question.State.FINISHED)) {
						questionsDB.saveQuestion(question, currentVoting.getId());
					}
				}
			}
		}
		votings.updateVoting(currentVoting);
	}

	public void notifyVotingChanged() throws DAOException {
		saveCurrentVoting();
	}

	public void saveVote(Vote vote) throws DAOException {
		votes.saveVote(vote);
		synchronized(votesDB) {
			votesDB.saveVote(vote);
		}
	}

	@Override
	public void retrieveVotersFromDatabase() throws DAOException {
		synchronized(votersDB) {
			for(Voter voter: votersDB.retrieveVoters()) {
				voters.saveVoter(voter);
			}
		}
	}

}

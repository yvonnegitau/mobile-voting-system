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

package cz.cvut.fel.mvod.persistence.derby;

import cz.cvut.fel.mvod.persistence.DAOException;
import cz.cvut.fel.mvod.persistence.VotingDAO;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO object for accesing Votings in database.
 * @author jakub
 */
class DerbyVotingDAO extends DerbyDAO implements VotingDAO{

	private static final int ID = 1;
	private static final int DATE = 2;
	private static final int TEST = 3;
	private static final int SECRET = 4;

	private static DerbyVotingDAO instance = null;
	private PreparedStatement saveVoting = null;
	private PreparedStatement deleteVoting = null;
	private PreparedStatement getVoting = null;
	private PreparedStatement getVotings = null;
	private String saveVotingSQL = "INSERT INTO Voting " +
			"(date, test, secret) " +
			"VALUES (?, ?, ?)";
	private String deleteVotingSQL = "DELETE FROM Voting WHERE id = ?";
	private String getVotingSQL = "SELECT * FROM Voting WHERE id = ?";
	private String getVotingsSQL = "SELECT * FROM Voting";

	private DerbyVotingDAO() {
	}

	/**
	 * Returns the only instance of <code>DerbyVotingDAO</code>.
	 * @return <code>DerbyVotingDAO</code> instance
	 */
	public static DerbyVotingDAO getInstance() {
		if(instance == null) {
			instance = new DerbyVotingDAO();
		}
		return instance;
	}

	/**
	 * Save voting to database. Attribute id will be updated.
	 * @param voting to save
	 * @param voters voting participants
	 * @throws DerbyDatabaseException if SQL connection fails
	 */
	public void saveVoting(Voting voting, List<Voter> voters) throws DerbyDatabaseException {
		if(saveVoting == null) {
			saveVoting = prepareStatement(saveVotingSQL);
		}
		Object[] attributes = {
			new Date((new java.util.Date()).getTime()),
			voting.isTest() ? 1 : 0, voting.isSecret() ? 1 : 0
		};
		ResultSet results = execute(saveVoting, attributes);
		try {
			if(results.next()) {
				voting.setId(results.getInt(1));
			}
			for(Voter voter : voters) {
				if(voter.getId() == -1) {
					try {
						DerbyVoterDAO.getInstance().saveVoter(voter);
					} catch (DerbyDatabaseException ex) {
						//ok, voter is already present in database
					}
				}
				DerbyParticipantDAO.getInstance().addVoter(voting, voter);
			}
			for(Question question : voting.getQuestions()) {
				DerbyQuestionDAO.getInstance().saveQuestion(question, voting.getId());
			}
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
	}

	/**
	 * Deletes voting from database. All records connected to this
	 * instance will be deleteted.
	 * @param voting to delete
	 * @throws DerbyDatabaseException if SQL connection fails
	 */
	public void deleteVoting(Voting voting) throws DerbyDatabaseException {
		if(deleteVoting == null) {
			deleteVoting = prepareStatement(deleteVotingSQL);
		}
		DerbyQuestionDAO.getInstance().deleteQuestions(voting);
		DerbyParticipantDAO.getInstance().deleteParticipants(voting);
		execute(deleteVoting, (Object) voting.getId());
	}

	/**
	 * Loads Voting from database.
	 * @param id voting ID
	 * @return
	 * @throws DerbyDatabaseException if SQL connection fails
	 */
	public Voting getVoting(int id) throws DerbyDatabaseException {
		if(getVoting == null) {
			getVoting = prepareStatement(getVotingSQL);
		}
		return parseVoting(executeQuery(getVoting, (Object) id));
	}

	/**
	 * Loads all votings from database.
	 * @return votings stored in database
	 * @throws DerbyDatabaseException if SQL connection fails
	 */
	public List<Voting> getVotings() throws DerbyDatabaseException {
		if(getVotings == null) {
			getVotings = prepareStatement(getVotingsSQL);
		}
		ResultSet results = executeQuery(getVotings);
		List<Voting> votings = new ArrayList<Voting>();
		Voting voting = null;
		while((voting = parseVoting(results)) != null) {
			votings.add(voting);
		}
		return votings;
	}

	private Voting parseVoting(ResultSet results) throws DerbyDatabaseException {
		Voting voting = null;
		try {
			if(results.next()) {
				List<Question> questions = DerbyQuestionDAO.getInstance().getQuestions(results.getInt(1));
				voting = new Voting((results.getInt(SECRET) == 1 ? true : false),
						(results.getInt(TEST) == 1 ? true : false), questions,
						results.getInt(ID));
			}
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
		return voting;
	}

	public void updateVoting(Voting voting) throws DAOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void saveVoting(Voting voting) throws DAOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

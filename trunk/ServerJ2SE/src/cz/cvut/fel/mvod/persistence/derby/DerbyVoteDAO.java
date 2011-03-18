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
import cz.cvut.fel.mvod.persistence.VoteDAO;
import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

/**
 * Data acces object to embedded derby database for Vote class.
 * @author jakub
 */
class DerbyVoteDAO extends DerbyDAO implements VoteDAO {

	private static final int ID = 1;
	private static final int QUESTION_ID = 2;
	private static final int VOTER_ID = 3;
	private static final int EVAL = 4;
	private static final int ALTERNATIVE_ID = 1;
	private static final int VOTE_ID = 2;

	private static DerbyVoteDAO instance = null;
	private PreparedStatement saveVote = null;
	private PreparedStatement deleteVote = null;
	private PreparedStatement saveChecked = null;
	private PreparedStatement deleteChecked = null;
	private String saveVoteSQL = "INSERT INTO Vote " +
			"(question_id, eval, voter_id) VALUES(?, ?, ?)";
	private String deleteVoteSQL = "DELETE FROM Vote " +
			"WHERE id = ?";
	private String saveCheckedSQL = "INSERT INTO Checked" +
			"(alternative_id, vote_id) VALUES(?, ?)";
	private String deleteCheckedSQL = "DELETE FROM Checked " +
			"WHERE vote_id = ?";

	public static DerbyVoteDAO getInstance() {
		if(instance == null) {
			instance = new DerbyVoteDAO();
		}
		return instance;
	}

	private DerbyVoteDAO() {
	}


	/**
	 * Saves vote to database. Vote attribute ID is updated.
	 * @param vote to save
	 * @throws DerbyDatabaseException if sql error occurs
	 */
	public void saveVote(Vote vote) throws DerbyDatabaseException {
		if(saveVote == null) {
			saveVote = prepareStatement(saveVoteSQL);
		}
		if(saveChecked == null) {
			saveChecked = prepareStatement(saveCheckedSQL);
		}
		Object[] attributes = {
			vote.getQuestion().getId(), vote.getEvaluation(),
			(vote.getVoter() == null ? new NullType(Types.INTEGER) : vote.getVoter().getId())
		};
		ResultSet results = execute(saveVote, attributes);
		try {
			if (results.next()) {
				vote.setId(results.getInt(1));
			}
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
		for(Alternative alternative : vote.getChecked()) {
			execute(saveChecked, new Object[] {alternative.getId(), vote.getId()});
		}
	}

	/**
	 * Deletes vote from database.
	 * @param vote to delete
	 * @throws DerbyDatabaseException if SQL error occurs
	 */
	public void deleteVote(Vote vote) throws DerbyDatabaseException {
		if(deleteChecked == null) {
			deleteChecked = prepareStatement(deleteCheckedSQL);
		}
		if(deleteVote == null) {
			deleteVote = prepareStatement(deleteVoteSQL);
		}
		execute(deleteChecked, vote.getId());
		execute(deleteVote, vote.getId());
	}

	public List<Vote> getVotes(Question question) throws DerbyDatabaseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Vote> getVotes(Voter voter) throws DerbyDatabaseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection<Vote> retrieveVotes() throws DAOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

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

import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author jakub
 */
class DerbyParticipantDAO extends DerbyDAO{

	private static DerbyParticipantDAO instance = null;
	private PreparedStatement addVoter = null;
	private PreparedStatement deleteVotingParticipants = null;
	private PreparedStatement getVoters = null;
	private String addVoterSQL = "INSERT INTO Participant " +
			"(voting_id, voter_id) VALUES (?, ?)";
	private String deleteVotingParticipantsSQL = "DELETE FROM Participant WHERE voting_id = ?";
	private String getVotersSQL = "SELECT Voter.* FROM Participant JOIN Voter " +
			"ON (voter_id = id) WHERE voting_id = ?";

	/**
	 * Returns the only instance of <code>DerbyParticipantDAO</code>.
	 * @return <code>DerbyParticipantDAO</code> instance
	 */
	static DerbyParticipantDAO getInstance() {
		if(instance == null) {
			instance = new DerbyParticipantDAO();
		}
		return instance;
	}

	/**
	 * Adds relation between voter a and voting.
	 * @param voting
	 * @param voter participant of voting
	 * @throws DerbyDatabaseException
	 */
	public void addVoter(Voting voting, Voter voter) throws DerbyDatabaseException {
		if(addVoter == null) {
			addVoter = prepareStatement(addVoterSQL);
		}
		Object[] attributes = {
			voting.getId(), voter.getId()
		};
		execute(addVoter, attributes);
	}

	/**
	 * Deletes all relations between voting and voters.
	 * Voting and Voter tables stay untouched.
	 * @param voting
	 * @throws DerbyDatabaseException
	 */
	public void deleteParticipants(Voting voting) throws DerbyDatabaseException {
		if(deleteVotingParticipants == null) {
			deleteVotingParticipants = prepareStatement(deleteVotingParticipantsSQL);
		}
		execute(deleteVotingParticipants, (Object) voting.getId());
	}

	/**
	 * Returns all participants of specified voting.
	 * @param votingID
	 * @return
	 * @throws DerbyDatabaseException
	 */
	public List<Voter> getVoters(int votingID) throws DerbyDatabaseException{
		if(getVoters == null) {
			getVoters = prepareStatement(getVotersSQL);
		}
		ResultSet results = executeQuery(getVoters, (Object)votingID);
		return DerbyVoterDAO.getInstance().parseVoters(results);
	}
}

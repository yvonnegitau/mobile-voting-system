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
import cz.cvut.fel.mvod.persistence.VoterDAO;
import cz.cvut.fel.mvod.common.Voter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Object for acessing Voter instances stored in database.
 * @author jakub
 */
class DerbyVoterDAO extends DerbyDAO implements VoterDAO {

	private static final int ID = 1;
	private static final int USER_NAME = 2;
	private static final int FIRST_NAME = 3;
	private static final int LAST_NAME = 4;
	private static final int PASSWORD = 5;

	private static DerbyVoterDAO instance = null;
	private PreparedStatement saveVoter = null;
	private PreparedStatement updateVoter = null;
	private PreparedStatement deleteVoter = null;
	private PreparedStatement getVoter = null;
	private PreparedStatement getVoterByUserName = null;
	private PreparedStatement retrieveVoters = null;
	private String saveVoterSQL = "INSERT INTO Voter " +
			"(user_name, first_name, last_name, password) VALUES(?, ?, ?, ?)";
	private String updateVoterSQL = "UPDATE Voter " +
			"SET " +
			"	user_name = ?," +
			"	first_name = ?, " +
			"	last_name = ?, " +
			"	password = ?" +
			"WHERE id = ?";
	private String deleteVoterSQL = "DELETE FROM Voter WHERE id = ?";
	private String getVoterSQL = "SELECT * FROM Voter WHERE id = ?";
	private String getVoterByUserNameSQL = "SELECT * FROM Voter WHERE user_name = ?";
	private String retrieveVotersSQL = "SELECT * FROM Voter";

	private DerbyVoterDAO() {
	}

	/**
	 * Returns the only instance of <code>DerbyVoterDAO</code>.
	 * @return <code>DerbyVoterDAO</code> instance
	 */
	public static DerbyVoterDAO getInstance() {
		if(instance == null) {
			instance = new DerbyVoterDAO();
		}
		return instance;
	}

	/**
	 * Saves voter into database.
	 * @param voter to save
	 * @throws DerbyDatabaseException if exists record with equal id
	 * or database connection error occurs
	 */
	public void saveVoter(Voter voter) throws DerbyDatabaseException {
		if(saveVoter == null) {
			saveVoter = prepareStatement(saveVoterSQL);
		}
		Object[] attributes = {
			voter.getUserName(), voter.getFirstName(),
			voter.getLastName(), voter.getPassword()
		};
		ResultSet results = execute(saveVoter, attributes);
		try {
			if(results.next()) {
				voter.setId(results.getInt(1));
			}
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
	}

	/**
	 * Updates voter attributes.
	 * @param voter to update
	 * @throws DerbyDatabaseException if database connection error occurs
	 */
	public void updateVoter(Voter voter) throws DerbyDatabaseException {
		if(updateVoter == null) {
			updateVoter =  DerbySqlConnection.getInstance().prepareStatement(updateVoterSQL);
		}
		Object[] attributes = {
			voter.getUserName(), voter.getFirstName(), voter.getLastName(),
			voter.getPassword(), voter.getId()
		};
		execute(updateVoter, attributes);
	}

	/**
	 * Delete voter from database.
	 * @param voter to delete
	 * @throws DerbyDatabaseException if database connection error occurs
	 */
	public void deleteVoter(Voter voter) throws DerbyDatabaseException {
		if(deleteVoter == null) {
			deleteVoter =  DerbySqlConnection.getInstance().prepareStatement(deleteVoterSQL);
		}
		execute(deleteVoter, (Object)voter.getId());
	}

	/**
	 * Get voter from database.
	 * @param id voter device number
	 * @return voter or null if is not present in database
	 * @throws DerbyDatabaseException if database connection error occurs
	 */
	public Voter getVoter(int id) throws DerbyDatabaseException {
		if(getVoter == null) {
			getVoter =  DerbySqlConnection.getInstance().prepareStatement(getVoterSQL);
		}
		List<Voter> voters = parseVoters(executeQuery(getVoter, id));
		if(voters.isEmpty()) {
			return null;
		}
		return voters.get(0);
	}

	/**
	 * Retrieve all <code>Voter</code> instances stored in database.
	 * @return collection containing all Voter instances stored in database
	 * @throws DerbyDatabaseException if database connection error occurs
	 */
	public List<Voter> retrieveVoters() throws DerbyDatabaseException {
		if(retrieveVoters == null) {
			retrieveVoters = DerbySqlConnection.getInstance().prepareStatement(retrieveVotersSQL);
		}
		ResultSet results = executeQuery(retrieveVoters);
		return parseVoters(results);
	}

	List<Voter> parseVoters(ResultSet results) throws DerbyDatabaseException {
		try {
			List<Voter> voters = new ArrayList<Voter>();
			while(results.next()) {
				voters.add(new Voter(results.getString(FIRST_NAME),
						results.getString(LAST_NAME), results.getBytes(PASSWORD),
						results.getString(USER_NAME), results.getInt(ID)));
			}
			return voters;
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
	}

	@Override
	public Voter getVoter(String userName) throws DAOException {
		if(getVoterByUserName == null) {
			getVoter =  DerbySqlConnection.getInstance().prepareStatement(getVoterByUserNameSQL);
		}
		List<Voter> voters = parseVoters(executeQuery(getVoter, userName));
		if(voters.isEmpty()) {
			return null;
		}
		return voters.get(0);
	}
}

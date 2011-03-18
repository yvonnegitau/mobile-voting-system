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

import cz.cvut.fel.mvod.persistence.AlternativeDAO;
import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.persistence.DAOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO object for accesing Alternatives in database.
 * @author jakub
 */
class DerbyAlternativeDAO extends DerbyDAO implements AlternativeDAO {

	private static DerbyAlternativeDAO instance = null;
	private PreparedStatement saveAlternative = null;
	private PreparedStatement deleteQuestionAlternatives = null;
	private PreparedStatement deleteAlternative = null;
	private PreparedStatement getAlternative = null;
	private PreparedStatement getAlternatives = null;
	private PreparedStatement getQuestionAlternatives = null;
	private String saveAlternativeSQL = "INSERT INTO Alternative " +
			"(question_id, correct, text) " +
			"VALUES (?, ?, ?)";
	private String deleteQuestionAlternativesSQL = "DELETE FROM Alternative WHERE question_id = ?";
	private String deleteAlternativeSQL = "DELETE FROM Alternative WHERE id = ?";
	private String getAlternativeSQL = "SELECT * FROM Alternative WHERE id = ?";
	private String getAlternativesSQL = "SELECT * FROM Alternative";
	private String getQuestionAlternativesSQL = "SELECT * FROM Alternative WHERE question_id = ?";

	private DerbyAlternativeDAO() {
	}

	/**
	 * Returns the only instance of <code>DerbyAlternativeDAO</code>.
	 * @return singleton
	 */
	public static DerbyAlternativeDAO getInstance() {
		if(instance == null) {
			instance = new DerbyAlternativeDAO();
		}
		return instance;
	}

	/**
	 * Saves alternative to database.
	 * @param alternative to save
	 * @param questionId id of question, containing thi alterantive
	 * @throws DerbyDatabaseException
	 */
	public void saveAlternative(Alternative alternative, int questionId)
			throws DerbyDatabaseException {
		if(saveAlternative == null) {
			saveAlternative = prepareStatement(saveAlternativeSQL);
		}
		Object[] attributes = {
			questionId, alternative.isCorrect() ? 1 : 0,
			alternative.getText()
		};
		ResultSet results = execute(saveAlternative, attributes);
		try {
			if(results.next()) {
				alternative.setId(results.getInt(1));
			}
		} catch(SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
	}

	/**
	 * Deletes alterantives which belongs to specified question.
	 * @param questionID question id
	 * @throws DerbyDatabaseException
	 */
	public void deleteAlternatives(int questionID) throws DerbyDatabaseException {
		if(deleteQuestionAlternatives == null) {
			deleteQuestionAlternatives = prepareStatement(deleteQuestionAlternativesSQL);
		}
		execute(deleteQuestionAlternatives, (Object) questionID);
	}

	/**
	 * Deletes alternative by id.
	 * @param id alternative id
	 * @throws DerbyDatabaseException
	 */
	public void deleteAlternative(int id) throws DerbyDatabaseException {
		if(deleteAlternative == null) {
			deleteAlternative = prepareStatement(deleteAlternativeSQL);
		}
		DerbyAlternativeDAO.getInstance().deleteAlternatives(id);
		execute(deleteAlternative, (Object) id);
	}

	/**
	 * Gets alternative by id.
	 * @param id alternative id
	 * @return alternative with specified id
	 * @throws DerbyDatabaseException
	 */
	public Alternative getAlternative(int id) throws DerbyDatabaseException {
		if(getAlternative == null) {
			getAlternative = prepareStatement(getAlternativeSQL);
		}
		List<Alternative> alternatives = parseAlternatives(
				executeQuery(getAlternative, (Object)id));
		if(alternatives.isEmpty()) {
			return null;
		}
		return alternatives.get(0);
	}

	/**
	 * Gets all alternatives which belongs to specified question.
	 * @param questionID question id
	 * @return question alternatives
	 * @throws DerbyDatabaseException
	 */
	public List<Alternative> getAlternatives(int questionID) throws DerbyDatabaseException {
		if(getQuestionAlternatives == null) {
			getQuestionAlternatives = prepareStatement(getQuestionAlternativesSQL);
		}
		ResultSet results = executeQuery(getQuestionAlternatives,
				(Object)questionID);
		return parseAlternatives(results);
	}

	/**
	 * Gets all alternatives.
	 * @return all alternatives
	 * @throws DerbyDatabaseException
	 */
	public List<Alternative> getAlternatives() throws DerbyDatabaseException {
		if(getAlternatives == null) {
			getAlternatives = prepareStatement(getAlternativesSQL);
		}
		ResultSet results = executeQuery(getAlternatives);
		return parseAlternatives(results);
		
	}

	private List<Alternative> parseAlternatives(ResultSet results) throws DerbyDatabaseException {
		try {
			List<Alternative> alternatives = new ArrayList<Alternative>();
			while(results.next()) {
				Alternative alternative = new Alternative();
				alternative.setCorrect(results.getInt(3) == 1 ? true : false);
				alternative.setText(results.getString(4));
				alternative.setId(results.getInt(1));
				alternatives.add(alternative);
			}
			return alternatives;
		} catch (SQLException ex) {
			throw new DerbyDatabaseException(ex);
		}
	}

	public void updateAlternative(Alternative alternative, int questionId) throws DAOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

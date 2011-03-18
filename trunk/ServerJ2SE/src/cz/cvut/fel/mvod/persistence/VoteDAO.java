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
import java.util.Collection;

/**
 * Data access objekt pro přijaté hlasy.
 * @author jakub
 */
public interface VoteDAO {

	/**
	 * Uloží hlas.
	 * @param vote hlas
	 * @throws DAOException pokud operace selže
	 */
	public void saveVote(Vote vote) throws DAOException;

	/**
	 * Smaže hlas.
	 * @param vote hlas
	 * @throws DAOException pokud operace selže
	 */
	public void deleteVote(Vote vote) throws DAOException;
	
	/**
	 * Vrátí všechny hlasy odpovídající na otázku.
	 * @param question otázka
	 * @return hlasy odpovídající na otázku
	 * @throws DAOException pokud operace selže
	 */
	public Collection<Vote> getVotes(Question question) throws DAOException;

	/**
	 * Vrátí všechny hlasy poslané účastníkem.
	 * @param voter účastník
	 * @return účastníkovi hlasy
	 * @throws DAOException pokud operace selže
	 */
	public Collection<Vote> getVotes(Voter voter) throws DAOException;

	/**
	 * Vrátí všechny hlasy.
	 * @return všechny hlasy
	 * @throws DAOException pokud operace selže
	 */
	public Collection<Vote> retrieveVotes() throws DAOException;
}

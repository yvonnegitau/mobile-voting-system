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

import cz.cvut.fel.mvod.common.Alternative;
import java.util.Collection;

/**
 * Data access object pro odpovědi na otázku.
 * @author jakub
 */
public interface AlternativeDAO {

	/**
	 * Smaže otázku.
	 * @param id identifikátor otázky
	 * @throws DAOException pokud operace selže
	 */
	void deleteAlternative(int id) throws DAOException;

	/**
	 * Smaže všechny alternativy otázky.
	 * @param questionID identifikátor otázky
	 * @throws DAOException pokud operace selže
	 */
	void deleteAlternatives(int questionID) throws DAOException;

	/**
	 * Vrátí odpověď.
	 * @param id identifikátor odpovědi
	 * @return odpověď se zadaným identifikátorem
	 * @throws DAOException pokud operace selže
	 */
	Alternative getAlternative(int id) throws DAOException;

	/**
	 * Vrátí všehcny alternativy otázky.
	 * @param questionID identifikátor otázky
	 * @return všechny odpovědi na otázku
	 * @throws DAOException pokud operace selže
	 */
	Collection<Alternative> getAlternatives(int questionID) throws DAOException;

	/**
	 * Uloží odpověď
	 * @param alternative odpověď
	 * @param questionId identifikátor otázky (vlastníka odpovědi)
	 * @throws DAOException pokud operace selže
	 */
	void saveAlternative(Alternative alternative, int questionId) throws DAOException;

	/**
	 * Aktualizuje odpověd.
	 * @param alternative odpověď
	 * @param questionId identifikátor otázky
	 * @throws DAOException pokud operace selže
	 */
	void updateAlternative(Alternative alternative, int questionId) throws DAOException;
}

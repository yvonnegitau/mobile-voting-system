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

import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import cz.cvut.fel.mvod.persistence.derby.DerbyDAOFactoryImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jakub
 */
class VotingDAOImpl extends DefaultDAOObservable implements VotingDAO, DAOObservable {

	private final VotingDAO database = new DerbyDAOFactoryImpl().getVotingDAO();
	private final Map<Integer, Voting> votings = new HashMap<Integer, Voting>();

	public void deleteVoting(Voting voting) throws DAOException {
		synchronized(votings) {
			votings.remove(voting.getId());
		}
		notifyObservers(DAOObserverEvent.DELETE);
	}

	public Voting getVoting(int id) throws DAOException {
		synchronized(votings) {
			return votings.get(id);
		}
	}

	public Collection<Voting> getVotings() throws DAOException {
		return votings.values();
	}

	public void saveVoting(Voting voting, List<Voter> voters) throws DAOException {
		synchronized(votings) {
			if(!votings.containsKey(voting.getId())) {
				votings.put(voting.getId(), voting);
			}
		}
		notifyObservers(DAOObserverEvent.NEW_DATA);
	}

	public void updateVoting(Voting voting) throws DAOException {
		notifyObservers(DAOObserverEvent.UPDATE);
	}

}

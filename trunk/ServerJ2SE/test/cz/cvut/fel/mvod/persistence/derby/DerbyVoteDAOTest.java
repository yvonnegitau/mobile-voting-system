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

import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jakub
 */
public class DerbyVoteDAOTest {

	List<Vote> votes = new ArrayList<Vote>();

    public DerbyVoteDAOTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
		try {
			DerbySqlConnection.getInstance("TEST").connect();
		} catch (DerbyDatabaseException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
		Iterator<Vote> it = votes.iterator();
		while(it.hasNext())
		{
			Vote v = it.next();
			it.remove();
			try {
				DerbyVoteDAO.getInstance().deleteVote(v);
			} catch(DerbyDatabaseException ex) {
			}
		}
		DerbyVotingDAOTest.cleanVotingTable();
		DerbyVoterDAOTest.cleanVoterTable();
    }

	/**
	 * Test of saveVote method, of class DerbyVoteDAO.
	 */
	@Test
	public void testSaveAndDeleteVote() throws Exception {
		System.out.println("saveAndDeleteVote");
		Vote vote1 = getInstance(false);
		Vote vote2 = getInstance(true);
		DerbyVoteDAO instance = DerbyVoteDAO.getInstance();
		instance.saveVote(vote1);
		instance.saveVote(vote2);
		instance.deleteVote(vote1);
		instance.deleteVote(vote2);
	}

	Vote getInstance(boolean secret) throws DerbyDatabaseException {
		Voter voter = null;
		if(!secret) {
			voter = DerbyVoterDAOTest.getVoterInstance("voter" + 1);
			DerbyVoterDAO.getInstance().saveVoter(voter);
		}
		Voting voting = DerbyVotingDAOTest.getInstance();
		DerbyVotingDAO.getInstance().saveVoting(voting, new ArrayList<Voter>());
		Question question = DerbyVotingDAOTest.getQuestionInstance();
		int max = question.getAlternativesCount();
		List<Alternative> checked = new ArrayList<Alternative>();
		for(int i = 0; i < max; i += 2) {
			checked.add(question.getAlternative(i));
		}
		DerbyQuestionDAO.getInstance().saveQuestion(question, voting.getId());
		Vote vote = new Vote(voter, question, checked, 1);
		votes.add(vote);
		return vote;
	}

}
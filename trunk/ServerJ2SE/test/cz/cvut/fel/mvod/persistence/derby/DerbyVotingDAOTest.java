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
import cz.cvut.fel.mvod.common.EvaluationType;
import cz.cvut.fel.mvod.common.Question;
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
import static org.junit.Assert.*;

/**
 *
 * @author jakub
 */
public class DerbyVotingDAOTest {

	static private List<Voting> votings = new ArrayList<Voting>();

    public DerbyVotingDAOTest() {
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
		cleanVotingTable();
    }

	/**
	 * Test of saveVoting and getVoting methods, of class DerbyVotingDAO.
	 */
	@Test
	public void testSaveAndGetVoting() throws Exception {
		System.out.println("saveAndGetVoting");
		Voting voting = getInstance();
		DerbyVotingDAO instance = DerbyVotingDAO.getInstance();
		instance.saveVoting(voting, getVoters());
		Voting result = instance.getVoting(voting.getId());
		assertTrue(comparator(voting, result));
	}

	/**
	 * Test of deleteVoting method, of class DerbyVotingDAO.
	 */
	@Test
	public void testDeleteVoting() throws Exception {
		System.out.println("deleteVoting");
		Voting voting = getInstance();
		DerbyVotingDAO instance = DerbyVotingDAO.getInstance();
		instance.saveVoting(voting, getVoters());
		instance.deleteVoting(voting);
		assertNull(instance.getVoting(voting.getId()));
	}

	/**
	 * Test of getVotings method, of class DerbyVotingDAO.
	 */
	@Test
	public void testGetVotings() throws Exception {
		System.out.println("getVotings");
		DerbyVotingDAO instance = DerbyVotingDAO.getInstance();
		List<Voting> expResult = new ArrayList<Voting>();
		List<Voter> voters = getVoters();
		for(int i = 0; i < 5; i ++) {
			Voting voting = getInstance();
			expResult.add(voting);
			instance.saveVoting(voting, voters);
		}
		List<Voting> result = instance.getVotings();
		assertEquals(expResult.size(), result.size());
		for(int i = 0; i < result.size(); i ++) {
			assertTrue(comparator(expResult.get(i), result.get(i)));
		}
	}

	static Voting getInstance() {
		Voting voting = new Voting();
		voting.setEvaluation(EvaluationType.PARTIAL);
		voting.setMinVoters(5);
		voting.setSecret(true);
		voting.setTest(false);
		for(int i = 0; i < 5; i ++) {
			voting.addQuestion(getQuestionInstance());
		}
		votings.add(voting);
		return voting;
	}

	boolean comparator(Voting v1, Voting v2) {
		boolean flag = v1.getId() == v2.getId() &&
				v1.isSecret() == v2.isSecret() &&
				v1.isTest() == v2.isTest() &&
				v1.getQuestionCount() == v2.getQuestionCount();
		if(flag) {
			for(int i = 0; i < v1.getQuestionCount(); i ++) {
				if(!questionComparator(v1.getQuestions().get(i),
						v2.getQuestions().get(i))) {
					return false;
				}
			}
		}
		return flag;
	}

	static void cleanVotingTable() {
		Iterator<Voting> it = votings.iterator();
		while(it.hasNext()) {
			try {
				DerbyVotingDAO.getInstance().deleteVoting(it.next());
			} catch(DerbyDatabaseException ex) {
			}
			it.remove();
		}
		DerbyVoterDAOTest.cleanVoterTable();
	}

	static Question getQuestionInstance() {
		Question q = new Question("question", 20, 2, 5, 0, 12, null);
		for(int i = 0; i < 5; i ++) {
			q.addAlternative(getAlternativeInstance());
		}
		return q;
	}

	static boolean questionComparator(Question q1, Question q2) {
		boolean flag = q1.getMaxSelect() == q2.getMaxSelect() &&
				q1.getMinSelect() == q2.getMinSelect() &&
				q1.getAlternativesCount() == q2.getAlternativesCount() &&
				q1.getMaxWinners() == q2.getMaxWinners() &&
				q1.getMinPercent() == q2.getMinPercent() &&
				q1.getMinSelect() == q2.getMinSelect() &&
				q1.getMaxSelect() == q2.getMaxSelect() &&
				q1.getEvaluation() == q2.getEvaluation() &&
				q1.getText().equals(q2.getText());
		for(int i = 0; i < q1.getAlternativesCount(); i ++) {
			if(!alternativeComparator(q1.getAlternatives().get(i),
					q2.getAlternatives().get(i))) {
				return false;
			}
		}
		return flag;
	}


	static Alternative getAlternativeInstance() {
		Alternative instance = new Alternative();
		instance.setCorrect(true);
		instance.setText("Test value.");
		return instance;
	}

	static boolean alternativeComparator(Alternative a1, Alternative a2) {
		return a1.getId() == a2.getId() &&
				a1.isCorrect() == a2.isCorrect() &&
				a1.getText().equals(a2.getText());
	}

	List<Voter> getVoters() {
		List<Voter> voters = new ArrayList<Voter>();
		for(int i = 0; i < 10; i ++) {
			voters.add(DerbyVoterDAOTest.getVoterInstance("voter" + i));
		}
		return  voters;
	}

}
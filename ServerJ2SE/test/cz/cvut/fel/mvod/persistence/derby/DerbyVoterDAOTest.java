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
import cz.cvut.fel.mvod.crypto.CryptoUtils;
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
public class DerbyVoterDAOTest {

	private static List<Voter> voters = new ArrayList<Voter>();

    public DerbyVoterDAOTest() {
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
		//DerbySqlConnection.getInstance().disconnect();
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
		cleanVoterTable();
    }

	/**
	 * Test of saveVoter and getVoter methods, of class DerbyVoterDAO.
	 */
	@Test
	public void testSaveAndGetVoter() throws Exception {
		System.out.println("saveAndGetVoter");
		Voter voter = getVoterInstance("voter" + 112);
		DerbyVoterDAO instance = DerbyVoterDAO.getInstance();
		instance.saveVoter(voter);
		Voter other = instance.getVoter(voter.getId());
		assertTrue(comparator(voter, other));
	}

	/**
	 * Test of updateVoter method, of class DerbyVoterDAO.
	 */
	@Test
	public void testUpdateVoter() throws Exception {
		System.out.println("updateVoter");
		Voter voter = getVoterInstance("voter" + 1134);
		DerbyVoterDAO instance = DerbyVoterDAO.getInstance();
		instance.saveVoter(voter);
		voter.setUserName("voter" + 1145);
		voter.setPassword(CryptoUtils.passwordDigest("dasfsdgber", voter.getUserName()));
		instance.updateVoter(voter);
		assertTrue(comparator(voter, instance.getVoter(voter.getId())));
	}

	/**
	 * Test of deleteVoter method, of class DerbyVoterDAO.
	 */
	@Test
	public void testDeleteVoter() throws Exception {
		System.out.println("deleteVoter");
		Voter voter = getVoterInstance("voter" + 113);
		DerbyVoterDAO instance = DerbyVoterDAO.getInstance();
		instance.saveVoter(voter);
		instance.deleteVoter(voter);
		assertNull(instance.getVoter(voter.getId()));
	}

	/**
	 * Test of retrieveVoters method, of class DerbyVoterDAO.
	 */
	@Test
	public void testRetrieveVoters() throws Exception {
		System.out.println("retrieveVoters");
		DerbyVoterDAO instance = DerbyVoterDAO.getInstance();
		List<Voter> expResult = new ArrayList<Voter>();
		for(int i = 0; i < 10; i ++) {
			Voter voter = getVoterInstance("voter" + i);
			expResult.add(voter);
			instance.saveVoter(voter);
		}
		List<Voter> result = instance.retrieveVoters();
		assertEquals(expResult.size(), result.size());
		for(int i = 0; i < expResult.size(); i ++) {
			assertTrue(comparator(expResult.get(i), result.get(i)));
		}
	}

	static Voter getVoterInstance(String userName) {
		Voter voter = new Voter("Pepa", "Zdepa", CryptoUtils.passwordDigest("3924bv", userName), userName);
		voters.add(voter);
		return voter;
	}

	static boolean comparator(Voter v1, Voter v2) {
		assertArrayEquals(v1.getPassword(), v2.getPassword());
		if(v1.getUserName().equals(v2.getUserName()) &&
			v1.getFirstName().equals(v2.getFirstName()) &&
			v1.getLastName().equals(v2.getLastName())) {
				return true;
		}
		return false;
	}

	static void cleanVoterTable() {
		Iterator<Voter> it = voters.iterator();
		while(it.hasNext()) {
			try {
				DerbyVoterDAO.getInstance().deleteVoter(it.next());
			} catch(DerbyDatabaseException ex) {
			}
			it.remove();
		}
	}

}
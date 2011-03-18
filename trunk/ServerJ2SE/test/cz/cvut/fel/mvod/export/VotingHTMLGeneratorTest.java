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

package cz.cvut.fel.mvod.export;
import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.common.Voting;
import cz.cvut.fel.mvod.crypto.CryptoUtils;
import cz.cvut.fel.mvod.evaluation.VotingResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class VotingHTMLGeneratorTest {

	private static File dest;

	public VotingHTMLGeneratorTest() {
	}
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		dest = new File("./html");
		if(dest.exists()) {
			deleteDir(dest);
		}
		assertTrue(dest.mkdir());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
//		deleteDir(dest);
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	@Test
	public void exportTest() throws IOException {
		VotingHTMLGenerator generator = new VotingHTMLGenerator(getInstance(), dest);
		generator.generate();
		assert((new File(dest.getAbsolutePath() + "/index.html")).exists());
		assert((new File(dest.getAbsolutePath() + "/style.css")).exists());
		for(int i = 0; i < 10; i ++) {
			assert((new File(dest.getAbsolutePath() + "/" + i + ".html")).exists());
			assert((new File(dest.getAbsolutePath() + "/user" + i + ".html")).exists());
		}
	}

	private static void deleteDir(File dir) {
		if(dir.exists() ) {
			File[] files = dir.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDir(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		dir.delete();
	}

	private VotingResult getInstance() {
		List<Voter> voters = getVoters();
		Voting voting = getVoting();
		List<Vote> votes = new ArrayList<Vote>();
		for(Question q: voting.getQuestions()) {
			votes.addAll(getVotes(q, voters));
		}
		return new VotingResult(voting, votes, voters);
	}

	private Voting getVoting() {
		Voting v = new Voting(false, false, new ArrayList<Question>(), 0);
		for(int i = 0; i < 10; i ++) {
			Question q = new Question("Otázka č." + i, 0, 1, 3, 0, 0, getAlternatives());
			q.setId(i);
			q.setState(Question.State.FINISHED);
			v.addQuestion(q);
		}
		return v;
	}

	private List<Alternative> getAlternatives() {
		List<Alternative> alts = new ArrayList<Alternative>();
		for(int i = 0; i < 10; i ++) {
			alts.add(new Alternative(i, "Odpověď č. " + i, false));
		}
		return alts;
	}

	private List<Voter> getVoters() {
		List<Voter> voters = new ArrayList<Voter>();
		for(int i = 0; i < 10; i ++)  {
			voters.add(new Voter("Name" + i, "Surname" + i, CryptoUtils.passwordDigest("qwert" + i, "user"  + i), "user"  + i, i));
		}
		return voters;
	}

	private List<Vote> getVotes(Question question, List<Voter> voters) {
		List<Vote> votes = new ArrayList<Vote>();
		if(question.getId() != 4) {
			for(Voter v: voters) {
				List<Alternative> checked = new ArrayList<Alternative>();
				checked.add(question.getAlternative(v.getId() % 3));
				if(question.getId() == 2) {
					checked.add(question.getAlternative(5));
					checked.add(question.getAlternative(6));
				}
				votes.add(new Vote(v, question, checked, 0));
			}
		}
		return votes;
	}

}

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

package cz.cvut.fel.mvod.net;

import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;
import java.io.InputStream;
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
public class XMLSerializerTest {

    public XMLSerializerTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	/**
	 * Test of serializeQuestions method, of class XMLSerializer.
	 */
	@Test
	public void testSerializeQuestions() {
		System.out.println("serializeQuestion");
		List<Alternative> alts1 = new ArrayList<Alternative>();
		alts1.add(new Alternative(1, "10 - 20", false));
		alts1.add(new Alternative(2, "20 - 30", true));
		alts1.add(new Alternative(3, "30 - 40", true));
		alts1.add(new Alternative(4, "vice nez 40", true));
		List<Alternative> alts2 = new ArrayList<Alternative>();
		alts2.add(new Alternative(1, "jablko", false));
		alts2.add(new Alternative(2, "banan", true));
		alts2.add(new Alternative(3, "citron", true));
		alts2.add(new Alternative(4, "jahoda", true));
		List<Question> qs = new ArrayList<Question>();
		qs.add(new Question("Kolik je vam let?", 30, 2, 1, 1, 0, alts1));
		qs.add(new Question("Ktere ovoce mate radi", 30, 1, 4, 2, 12, alts2));
		boolean passwordNeeded = false;
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
			"<voting password=\"false\">" +
			"<question id=\"-1\" max=\"1\" min=\"1\">" +
			"<text>Kolik je vam let?</text>" +
			"<alternative>10 - 20</alternative>" +
			"<alternative>20 - 30</alternative>" +
			"<alternative>30 - 40</alternative>" +
			"<alternative>vice nez 40</alternative>" +
			"</question>" +
			"<question id=\"-1\" max=\"4\" min=\"2\">" +
			"<text>Ktere ovoce mate radi</text>" +
			"<alternative>jablko</alternative>" +
			"<alternative>banan</alternative>" +
			"<alternative>citron</alternative>" +
			"<alternative>jahoda</alternative>" +
			"</question>" +
			"</voting>";
		byte[] expResult = xml.getBytes();
		byte[] result = XMLSerializer.serializeQuestions(qs, passwordNeeded);
		assertArrayEquals(expResult, result);
	}

	/**
	 * Test of parseVote method, of class XMLSerializer.
	 */
//	@Test
	public void testParseVote() {
		System.out.println("parseVote");
		InputStream in = null;
		List expResult = null;
		List result = XMLSerializer.parseVote(in);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}
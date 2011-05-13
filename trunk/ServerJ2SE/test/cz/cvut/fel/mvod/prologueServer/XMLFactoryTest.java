/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.prologueServer;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Murko
 */
public class XMLFactoryTest {

    public XMLFactoryTest() {
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
     * Test of makeIntroPage method, of class XMLFactory.
     */
    @Test
    public void testMakeIntroPage() throws Exception {
        System.out.println("makeIntroPage");
        String IP = "123.456.789.123";
        int port = 17711;
        XMLFactory instance = new XMLFactory();
        String expResult = "";
        String result = instance.makeIntroPage(IP, port);
        assertTrue(result.contains(IP) && result.contains(port+"") && result.contains("html"));
    }

}
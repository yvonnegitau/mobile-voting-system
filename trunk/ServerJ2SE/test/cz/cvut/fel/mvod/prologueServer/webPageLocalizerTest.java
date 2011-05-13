/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.prologueServer;

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
public class webPageLocalizerTest {

    public webPageLocalizerTest() {
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
     * Test of getWP method, of class webPageLocalizer.
     */
    @Test
    public void testGetWP() {
        System.out.println("getWP");
        String[] langs = {"en"};
        webPageLocalizer instance = new webPageLocalizer("test", "test");
        String expResult = "Hello";
        String result = instance.getWP(langs);
        assertEquals(expResult, result);
      
    }

}
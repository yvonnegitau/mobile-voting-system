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
public class XMLParserTest {

    public XMLParserTest() {
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
     * Test of getRegistrants method, of class XMLParser.
     */
    @Test
    public void testGetRegistrants() throws Exception {
        System.out.println("getRegistrants");
        XMLParser instance = new XMLParser();
        HashMap result = instance.getRegistrants();
        assertTrue(!result.isEmpty());
    }

}
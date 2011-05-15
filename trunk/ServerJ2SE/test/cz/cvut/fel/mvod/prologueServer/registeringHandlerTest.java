/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.prologueServer;

import com.sun.net.httpserver.HttpExchange;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
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
public class registeringHandlerTest {

    public registeringHandlerTest() {
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
     * Test of parsePost method, of class registeringHandler.
     */
    @Test
    public void testParsePost() {
        System.out.println("parsePost");
        String body = "name=Radovan&surname=Murin&username=murin&ID=asdf&pass1=1&pass2=1";
        registeringHandler instance = new registeringHandler();
        String expResult = GlobalSettingsAndNotifier.singleton.messages.getString("usernameExistsErr");
        String result = instance.parsePost(body);
        assertTrue(result.contains(expResult));
        
    }


}
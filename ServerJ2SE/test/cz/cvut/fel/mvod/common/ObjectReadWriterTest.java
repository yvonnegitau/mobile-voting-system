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

package cz.cvut.fel.mvod.common;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr
 */
public class ObjectReadWriterTest {

    public ObjectReadWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        File f = new File("Files" + File.separator + "testVoting.voting");
        if(f.exists())f.delete();
        f = new File("Files");
        if(f.exists())f.delete();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of saveToFile method, of class ObjectReadWriter.
     */
    @Test
    public void testSaveToFile() {
        System.out.println("saveToFile");
        Voting voting = new Voting();
        voting.setTest(true);
        File file = new File("Files" + File.separator + "testVoting.voting");
        File directory = new File("Files");
        
        //zkusime vytvorit soubor Files/testVoting.voting
        try{
            directory.mkdir();
            file.createNewFile();
        }
        catch(Exception e)
        {
            fail("Selhani testu - nepodarilo se vztvorit soubor pro ulozeni");
        }
        //zkusime na miste souboru pouzit adresar
        try{
            ObjectReadWriter.serializeVoting(voting, directory);
            fail("Zadna vyjimka - soubor je adresar");
        }
        catch(FileNotFoundException ex)
        {
            //OK
        }
        catch(Exception ex)
        {
            fail("Spatna vyjimka - soubor je adresar");
        }

        //zkusime zadat spravne udaje
        try{
            ObjectReadWriter.serializeVoting(voting, file);
            //OK
        }
        catch(Exception ex)
        {
            fail("Vypadla vyjimka, ackoliv soubor je platny.");
        }
    }

    /**
     * Test of loadFromFile method, of class ObjectReadWriter.
     */
    @Test
    public void testLoadFromFile() {
        System.out.println("loadFromFile");
        File file = new File("Files"+File.separator + "testEmpty.txt");
        File nonExistingFile = new File("nonExistingFile.txt");
        File directory = new File("Files");
        try{
            file.createNewFile();

        }
        catch(Exception ex)
        {
            fail("Nepodarilo se vytvorit prazdny soubor");
        }
        //zkusime nacist neexistujici soubor
        try{
            ObjectReadWriter.loadVoters(nonExistingFile);
            fail("Nebyla hozena vyjimka - soubor neexistuje");
        }
        catch(FileNotFoundException ex)
        {
            //OK
        }
        catch(Exception ex)
        {
            fail("Spatna vyjimka: "+ex.getClass().getName() + " - nacitany soubor neexistuje");
        }
        //zkusime nacist adresar
        try{
            ObjectReadWriter.loadVoters(directory);
            fail("Nebyla hozena vyjimka - soubor je adresar");
        }
        catch(FileNotFoundException ex)
        {
            //OK
        }
        catch(Exception ex)
        {
            fail("Spatna vyjimka: "+ex.getClass().getName() + " - nacitany soubor je adresar");
        }
        //zkusime nacist prazdny soubor
        try{
            ObjectReadWriter.loadVoters(file);
            fail("Nebyla hozena vyjimka - soubor je prazdny");
        }
        catch(FileNotFoundException ex)
        {
            fail("Spatna vyjimka: "+ex.getClass().getName() + " - nacitany soubor je prazdny");
        }
        catch(Exception ex)
        {
            System.out.println("Nacteni prazdneho souboru hodilo " + ex.getClass().getName());
            //OK, musi hodit jinou vyjimku nez FileNotFoundException
        }
    }
    
    @Test
    public void testSaveAndLoad()
    {
        System.out.println("Save and load");
        File file = new File("testFile");
        try{
            file.createNewFile();
        }
        catch(Exception e)
        {
            fail("Selhalo vytvoreni souboru");
        }
        Voting voting = new Voting();
        voting.setTest(true);
        try{
            ObjectReadWriter.serializeVoting(voting, file);
        }
        catch(Exception ex)
        {
            fail("Vyhozena vyjimka " + ex.getClass().getName() + " pri ukladani");
        }
        Voting loadedVoting;
        try{
            loadedVoting = ObjectReadWriter.loadVoting(file);
            assertTrue(voting.isTest() == loadedVoting.isTest());
            assertTrue(voting.getQuestionCount() == loadedVoting.getQuestionCount());
        }
        catch(Exception ex)
        {
            fail("Vyhozena vyjimka " + ex.getClass().getName() + " pri nacitani");
        }
                
    }

}
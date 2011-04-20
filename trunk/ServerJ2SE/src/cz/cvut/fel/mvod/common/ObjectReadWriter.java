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

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Knihovní třída obsahující funkce pro serializaci instancí tříd {@link Voting} a {@link Voter}.
 * @author Petr
 */
public final class ObjectReadWriter {

    /**
     * Ulozi objekt do souboru.
     * @param object objekt k uložení.
     * @param file cílový soubor
     * @throws IOException pokud selže zápis
     */
    private static void saveToFile(Object object, File file) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    /**
     * Nacte serializovaný objekt ze souboru.
     * @param file zdrojový soubor
     * @return načtený objekt
     * @throws FileNotFoundException pokud soubor neexistuje
     * @throws IOException pokud selže čtení souboru
     * @throws ClassNotFoundException
     */
    private static Object loadFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        Object object = null;
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            object = ois.readObject();
            return object;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    /**
     * Ulozi hlasovaní do souboru.
     * @param voting hlasování, kiteré bude uloženo.
     * @param file cílový soubor
     * @throws IOException pokud selže zápis do souboru.
     */
    public static void serializeVoting(Voting voting, File file) throws IOException {
        saveToFile(voting, file);
    }

    /**
     * Uloží seznam účastníků do souyboru.
     * @param voters seznam účastníků
     * @param file cílový soubor
     * @throws IOException pokud selže zápis do souboru.
     */
    public static void serializeVoters(List<Voter> voters, File file) throws IOException {
        saveToFile(voters, file);
    }

    /**
     * Načte hlasování ze souboru.
     * @param file zdrojový soubor
     * @return načtené hlasování
     * @throws IOException pokud selže čtení souboru
     * @throws IllegalArgumentException pokud soubor neobsahuje hlasování
     */
    public static Voting loadVoting(File file) throws IOException {
        try {
            return (Voting) loadFromFile(file);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Illegal file format.");
        }
    }

    /**
     * Načte seznam účastníků ze souboru
     * @param file zdrojový soubor
     * @return načtený seznam účastníků
     * @throws IOException pokud selže čtení souboru
     * @throws IllegalArgumentException pokud soubor neobsahujeseznam účastníků
     */
    public static List<Voter> loadVoters(File file) throws IOException {
        try {
            return (List<Voter>) loadFromFile(file);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Illegal file format.");
        }
    }

    public static ArrayList<networkAddressRange> loadIPTables(File file) throws IOException {
        try {
            return (ArrayList<networkAddressRange>) loadFromFile(file);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Illegal file format.");
        }
    }

    public static void saveIPTables(File file) throws IOException {

        saveToFile(GlobalSettingsAndNotifier.singleton.permited, file);

    }

    private ObjectReadWriter() {
        //library class
    }
}

/*
Copyright 2011 Radovan Murin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cz.cvut.fel.mvod.gui.settings;

import cz.cvut.fel.mvod.crypto.Base64;
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.gui.settings.panels.PrologueSettingsPanel;
import cz.cvut.fel.mvod.prologueServer.PrologueServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import cz.cvut.fel.mvod.net.Server;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * This class handles certificate changes - loading/unloading, checks decrypting.
 * @author Radovan Murin
 */
public class CertManager {

    public final static int PROLOGUE = 0;
    public final static int VOTING = 1;
    private static String certPrologue = null;
    private static String hashPrologue = null;

    private static String certVoting = null;
    private static String hashVoting = null;
    private static MessageDigest md;



    static {
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CertManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private CertManager() {
    }

    /**
     * Starts a certificate change on a particular system.
     * @param system the system to have the certificate settings changed.
     * @throws FileNotFoundException thrown if the file cannot be found.
     */
    public static void changeCert(int system) throws FileNotFoundException {
        boolean loadOK = false;
        String passphrase = "11111";
        int tries = 3;
        JFileChooser fc = null;
        int returnVal = 0;
        File file;
        while (!loadOK) {
            if (tries == 3) {
                fc = new JFileChooser();
                returnVal = fc.showOpenDialog(null);
                file = null;
                tries = 0;
            }
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {

                    file = fc.getSelectedFile();
                    KeyStore ks = KeyStore.getInstance("PKCS12");
                    ks.load(new FileInputStream(file.getAbsolutePath()), passphrase.toCharArray());

                    switch (system) {
                        case PROLOGUE: {
                            certPrologue = ks.getCertificate("1").toString();
                             md.update(ks.getCertificate("1").getEncoded());
                             hashPrologue = hexify(md.digest());

                           
                            GlobalSettingsAndNotifier.singleton.modifySettings("Prologue_certpath", file.getAbsolutePath(), true);
                            PrologueServer.setCertPass(passphrase);
                            

                            break;
                        }
                        case VOTING: {
                            certVoting = ks.getCertificate("1").toString();
                            md.update(ks.getCertificate("1").getEncoded());
                            hashVoting = hexify(md.digest());
                            GlobalSettingsAndNotifier.singleton.modifySettings("Voting_certpath", file.getAbsolutePath(), true);
                            Server.setCertPass(passphrase);
                            
                            break;

                        }
                    }
                    loadOK = true;
                } catch (KeyStoreException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    passphrase = JOptionPane.showInputDialog(null, GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallLabel"), GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallTitle"), JOptionPane.WARNING_MESSAGE);
                    tries++;
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CertificateException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                break;
            }
        }

    }
/**
 * Returns the Common name for a specific system
 * @param system the specific system
 * @return returns the CN
 */
    public static String getCN(int system) {
     
        Pattern mp = Pattern.compile("CN=[^,]*, ");
        Matcher m = null;
        switch (system) {
            case PROLOGUE: {
                if (certPrologue == null) {
                  
                    return "N/A";
                }
                m = mp.matcher(certPrologue);
                break;
            }
            case VOTING: {
                if (certVoting == null) {
                    return "N/A";
                }
                m = mp.matcher(certVoting);
                break;
            }
        }
        m.find();
        String res = m.group().replace("CN=", "").replace(",", "");
       
        return res;
    }
    /**
     * Returns the fingerprint of the certificate
     * @param system the system to which the certificate is attached to
     * @return the requested FP
     */
    public static String getFingerPrint(int system) {
      
        String ret = "";
       
        switch (system) {
            case PROLOGUE: {
                if (hashPrologue == null) {
                  
                    return "N/A";
                }
                return hashPrologue;

            }
            case VOTING: {
                if (hashVoting == null) {
                    return "N/A";
                }
                return Base64.encodeToString(hashVoting.getBytes(),Base64.DEFAULT);
            }
        }    
        return ret;
    }

    /**
	 *
	 * Gets the hex code from a byte array
	 * http://stackoverflow.com/questions/1270703/how-to-retrieve-compute-an-
	 * x509-certificates-thumbprint-in-java
	 *
	 * @param bytes
	 * @return a string of hex data
	 */
	public static String hexify(byte bytes[]) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		StringBuffer buf = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
                        if(i%2==0) buf.append(":");
		}

		return buf.toString();
	}


}

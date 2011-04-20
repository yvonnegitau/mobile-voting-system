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
package cz.cvut.fel.mvod.gui;

import cz.cvut.fel.mvod.persistence.DAOException;
import cz.cvut.fel.mvod.persistence.DAOFacadeImpl;
import cz.cvut.fel.mvod.persistence.DAOFactoryImpl;
import cz.cvut.fel.mvod.prologueServer.PrologueServer;
import cz.cvut.fel.mvod.prologueServer.RegistrantAuthorisationWindow;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jakub
 */
public class MobileVotingDevice {

    private static MainWindow mainWindow;

    public static void main(String[] args) {
        try {
            DAOFactoryImpl.initInstance();
            DAOFacadeImpl.initInstance();
        } catch (DAOException ex) {
            System.out.println("Nepodařilo se inicializovat databázi."
                    + "Zřejmě je spuštěná jiná instance programu.");
            System.exit(1);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
               
                mainWindow = new MainWindow();
                mainWindow.start();
            }
        });
    }
}

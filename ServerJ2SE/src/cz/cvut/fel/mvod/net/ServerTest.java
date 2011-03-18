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
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.crypto.CryptoUtils;
import cz.cvut.fel.mvod.persistence.DAOException;
import cz.cvut.fel.mvod.persistence.DAOFacadeImpl;
import cz.cvut.fel.mvod.persistence.DAOFactoryImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jakub
 */
public class ServerTest {

	public static void main(String[] args) {
		initDao();
		NetworkConnection instance = NetworkAccessManager.getInstance();
		Alternative alt1 = new Alternative(1, "Alternativa 1", false);
		Alternative alt2 = new Alternative(2, "Alternativa 2", true);
		List<Alternative> alts = new ArrayList<Alternative>();
		alts.add(alt1);
		alts.add(alt2);
		List<Question> qs = new ArrayList<Question>();
		qs.add(new Question("Otazka 1", 30, 2, 2, 1, 0, alts));
		qs.add(new Question("Otazka 2", 30, 2, 1, 1, 12, alts));
		instance.sendData(new Voter("Pepa", "Zdepa", CryptoUtils.passwordDigest("qwert", "voter" + 1), "voter" + 1), qs, false);
		instance.sendData(new Voter("Pepa", "Zdepa",  CryptoUtils.passwordDigest("qwert", "voter" + 2), "voter" + 2), qs, true);
		Server server = Server.getInstance();
		try {
			server.connect();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void initDao() {
		try {
			DAOFactoryImpl.initInstance();
			DAOFacadeImpl.initInstance();
		} catch(DAOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}

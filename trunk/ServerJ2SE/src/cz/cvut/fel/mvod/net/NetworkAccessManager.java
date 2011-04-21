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

import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Vote;
import cz.cvut.fel.mvod.common.Voter;
import cz.cvut.fel.mvod.crypto.CryptoUtils;
import cz.cvut.fel.mvod.persistence.DAOException;
import cz.cvut.fel.mvod.persistence.DAOFacade;
import cz.cvut.fel.mvod.persistence.DAOFacadeImpl;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tvoří rozhraní síťového modulu. 
 * @author jakub
 */
public class NetworkAccessManager implements NetworkConnection, DataProvider {

    /**
     * Instance singletonu.
     */
    private static final NetworkAccessManager instance = new NetworkAccessManager();
    /**
     * Fronta k odeslání.
     */
    private final Map<String, List<Question>> sendQueue;
    /**
     * Seznam uživatelů, kterým bude vynuceno heslo.
     */
    private final Set<String> passwordNeeded;
    /**
     * Seznam uživatelů (pro pársování odpovědí).
     */
    private final Map<String, Voter> voters;
    /**
     * Seznam otázek (pro pársování odpovědí).
     */
    private final Set<Question> questions;

    /**
     * Vytvoří novou instanci.
     */
    private NetworkAccessManager() {
        sendQueue = new HashMap<String, List<Question>>();
        passwordNeeded = new TreeSet<String>();
        voters = new Hashtable<String, Voter>();
        questions = new HashSet<Question>();
    }

    public static NetworkConnection getInstance() {
        return instance;
    }

    static DataProvider getDataProvider() {
        return instance;
    }

    /**
     * {@inheritDoc }
     */
    public void sendData(List<Voter> voters, List<Question> questions, boolean passwordRequired) {
        synchronized (sendQueue) {
            for (Voter voter : voters) {
                if (!sendQueue.containsKey(voter.getUserName())) {
                    this.voters.put(voter.getUserName(), voter);
                    sendQueue.put(voter.getUserName(), new ArrayList<Question>());
                }
                sendQueue.get(voter.getUserName()).addAll(questions);
            }
        }
        if (passwordRequired) {
            synchronized (passwordNeeded) {
                for (Voter voter : voters) {
                    passwordNeeded.add(voter.getUserName());
                }
            }
        }
        synchronized (this.questions) {
            this.questions.addAll(questions);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void sendData(Voter voter, List<Question> questions, boolean passwordRequired) {
        synchronized (sendQueue) {
            if (!sendQueue.containsKey(voter.getUserName())) {
                this.voters.put(voter.getUserName(), voter);
                sendQueue.put(voter.getUserName(), new ArrayList<Question>());
            }
            sendQueue.get(voter.getUserName()).addAll(questions);
        }
        if (passwordRequired) {
            synchronized (passwordNeeded) {
                passwordNeeded.add(voter.getUserName());
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    public boolean isPasswordNeeded(String userName) {
        synchronized (passwordNeeded) {
            return passwordNeeded.remove(userName);
        }
    }

    /**
     * {@inheritDoc }
     */
    public List<Question> getQuestions(String userName) {
        synchronized (sendQueue) {
            //Altered .remove to get
            return sendQueue.get(userName);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void setResponses(String userName, List<Vote> votes) {
        List<Question> qL = sendQueue.get(userName);

        DAOFacade dao = DAOFacadeImpl.getInstance();

        Voter voter = null;
        try {
            voter = dao.getVoter(userName);
        } catch (DAOException ex) {
            return;
        }
        if (voter == null) {
            return;
        }
        for (Vote vote : votes) {
            synchronized (questions) {
                if (questions.contains(vote.getQuestion())) {
                    vote.setVoter(voter);
                    try {
                        dao.saveVote(vote);
                        qL.remove(vote.getQuestion());
                        if (qL.isEmpty()) {
                            sendQueue.remove(userName);
                        } else {
                            sendQueue.put(userName, qL);
                        }

                    } catch (DAOException ex) {
//FIXME some reaction?
                        return;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    public boolean checkPassword(String userName, String password) {
        byte[] expected = voters.get(userName).getPassword();
        byte[] received = CryptoUtils.passwordDigest(password, userName);
        if (expected.length != received.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != received[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    public void startServer() throws IOException {
        try {
            Server.getInstance().connect();
        } catch (KeyStoreException ex) {
            Logger.getLogger(NetworkAccessManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NetworkAccessManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(NetworkAccessManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(NetworkAccessManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(NetworkAccessManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void stopServer() {
        Server.getInstance().stop();
    }

    /**
     * {@inheritDoc }
     */
    public void stopReceiving(Question question) {
        synchronized (questions) {
            questions.remove(question);
            Set<String> s = sendQueue.keySet();
            while (s.iterator().hasNext()) {
                String currentName = s.iterator().next();
                List<Question> QL = sendQueue.get(currentName);
                for (int i = 0; i < QL.size(); i++) {
                    if (QL.get(i).getId() == question.getId()) {
                        sendQueue.remove(currentName);
                    }
                }
            }

        }
    }

    /**
     * {@inheritDoc }
     */
    public void sendData(List<Voter> voters, Question question, boolean passwordRequired) {
        List<Question> q = new ArrayList<Question>();
        q.add(question);
        sendData(voters, q, passwordRequired);
    }
}

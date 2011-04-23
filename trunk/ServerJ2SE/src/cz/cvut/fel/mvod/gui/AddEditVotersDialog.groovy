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

package cz.cvut.fel.mvod.gui
import java.awt.GridLayout
import javax.swing.WindowConstants
import javax.swing.JOptionPane
import groovy.swing.SwingBuilder
import cz.cvut.fel.mvod.crypto.CryptoUtils
import cz.cvut.fel.mvod.common.Voter
import cz.cvut.fel.mvod.persistence.DAOException
import cz.cvut.fel.mvod.persistence.DAOFactoryImpl
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier

/**
 * Dialog pro editaci nebo přidání uživatele.
 * @author jakub
 */
class AddEditVotersDialog implements Showable {

        

	/**
	 * Instance třídy SwingBuilder. Vytváří dialog.
	 */
	def builder
	def voterDAO = DAOFactoryImpl.instance.voterDAO
	/**
	 * JTextField pro zobrazení jména.
	 */
	def name
	/**
	 * JTextField pro zobrazení příjmení.
	 */
	def surname
	/**
	 * JTextField pro zobrazení uživatelského jména.
	 */
	def username
	/**
	 * JTextField pro zobrazení hesla.
	 */
	def password1
	/**
	 * JTextField pro zobrazení ověření hesla.
	 */
	def password2
	/**
	 * JDIalog přidání/editace uživatele
	 */
	def editVoterDialog

	/**
	 * Vytvoří novou instanci.
	 * @param builder 
	 * @param create zda se jedná o vytvoření uživatele
	 * @param voter editovaný uživtel
	 */
	AddEditVotersDialog(SwingBuilder builder, boolean create, Voter voter) {
   
		this.builder = builder
		editVoterDialog = builder.dialog(title: GlobalSettingsAndNotifier.singleton.messages.getString("regFormTitle"),
				modal: true,
				defaultCloseOperation: WindowConstants.DISPOSE_ON_CLOSE,
				layout: new GridLayout(6, 2)) {
			label(text: GlobalSettingsAndNotifier.singleton.messages.getString("nameFormInput"))
			name = textField(text: voter.firstName)
			label(text: GlobalSettingsAndNotifier.singleton.messages.getString("surnameFormInput"))
			surname = textField(text: voter.lastName)
			label(text: GlobalSettingsAndNotifier.singleton.messages.getString("usernameFormInput"))
			username = textField(text: voter.userName)
			label(text: GlobalSettingsAndNotifier.singleton.messages.getString("passwordFormInput"))
			password1 = passwordField(text: "")
			label(text:GlobalSettingsAndNotifier.singleton.messages.getString("password2FormInput"))
			password2 = passwordField(text: "")
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("saveLabel"), actionPerformed: {saveAction(voter, create)})
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("cancelLabel"), actionPerformed: cancelAction)
		}
	}

	/**
	 * {@inheritDoc }
	 */
	void show() {
		editVoterDialog.pack()
		editVoterDialog.visible = true
	}

	/**
	 * {@inheritDoc }
	 */
	void hide() {
		editVoterDialog.dispose()
	}

	/**
	 * Zobrazí dialog s chybovou hláškou.
	 * @param msg chybová hláška
	 */
	void showError(String msg) {
		JOptionPane.showMessageDialog(editVoterDialog, msg, GlobalSettingsAndNotifier.singleton.messages.getString("errorLabel"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Zavře dialog a uvolní jeho paměť. Změny nebudou uloženy.
	 */
	def cancelAction = {
		hide()
	}
	/**
	 * Uložeí změny, zavře dialog a uvolní paměť.
	 * @param voter změněný uživatel k uložení
	 * @param create zda se jedná o vytvoření nového uživatele
	 */
	def saveAction = {def voter, def create ->
		if(password1.text != password2.text) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("passMismatchErr"))
			return
		}
		if(name.text == "" || surname.text == "" || username.text == "" || password1.text == "") {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("passMismatchErr"))
			return
		}
		def tmp = ['firstName': voter.firstName, 'lastName': voter.lastName, 'userName': voter.userName, 'password': voter.password]
		voter.firstName = name.text
		voter.lastName = surname.text
		voter.userName = username.text
		voter.password = CryptoUtils.passwordDigest(password1.text, voter.userName)
		try {
			if(create) {
				voterDAO.saveVoter(voter)
			} else {
				voterDAO.updateVoter(voter)
			}
		} catch(DAOException ex) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("usernameExistsErr"))
			voter.firstName = tmp['firstName']
			voter.lastName = tmp['lastName']
			voter.userName = tmp['userName']
			voter.password = tmp['password']
			return
		}
		hide()
	}
}


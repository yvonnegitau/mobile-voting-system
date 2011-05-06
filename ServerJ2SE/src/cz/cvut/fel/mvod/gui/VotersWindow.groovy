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
import cz.cvut.fel.mvod.persistence.DAOException
import java.awt.BorderLayout
import java.awt.FlowLayout
import cz.cvut.fel.mvod.common.Voter
import cz.cvut.fel.mvod.gui.table.VotersTableModel
import groovy.swing.SwingBuilder
import javax.swing.WindowConstants
import javax.swing.ScrollPaneConstants
import javax.swing.DefaultListSelectionModel
import javax.swing.JOptionPane
import cz.cvut.fel.mvod.persistence.DAOFactoryImpl
import cz.cvut.fel.mvod.persistence.DAOObserver
import cz.cvut.fel.mvod.persistence.DAOObserverEvent
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier

/**
 * Okno pro editaci seznamu účastníků.
 * @author jakub
 */
class VotersWindow implements Showable, DAOObserver {

	/**
	 * JTable zobrazující účastníky.
	 */
	def votersTable
	/**
	 * Model tabulky zobrazující účastníky (VotersTableModel).
	 */
	def votersTableModel = new VotersTableModel()
	/**
	 * SwingBuilder pro vytvoření grafického rozhraní.
	 */
	def builder
	def voterDAO = DAOFactoryImpl.instance.voterDAO

	VotersWindow(SwingBuilder builder) {
		this.builder = builder
		voterDAO.registerObserver(this)
	}

	/**
	 * {@inheritDoc }
	 */
	void show() {
		votersTableModel.setVoters(voterDAO.retrieveVoters())
		votersWindow.pack()
		votersWindow.visible = true
	}

	/**
	 * {@inheritDoc }
	 */
	void hide() {
		votersWindow.visible = false
	}

	/**
	 * {@inheritDoc }
	 */
	void dataChanged(DAOObserverEvent event) {
		switch(event) {
			case DAOObserverEvent.NEW_DATA:
			case DAOObserverEvent.UPDATE:
				votersTableModel.setVoters(voterDAO.retrieveVoters())
				break;
		}
	}

	/**
	 * Zobrazí chybový dialog.
	 * @param msg chybová hláška
	 */
	def void showError(String msg) {
		JOptionPane.showMessageDialog(votersWindow, msg, GlobalSettingsAndNotifier.singleton.messages.getString("errorLabel"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Vymaže vybraného uživatele.
	 */
	def deleteVoter = {
		def index = votersTable.getSelectedRow()
		if(index != -1) {
			voterDAO.deleteVoter(votersTableModel.remove(index))
		}
	}

	/**
	 * Zobrazí dialog pro editaci uživatele.
	 */
	def editVoter = {
		def index = votersTable.getSelectedRow()
		if(index != -1) {
			showAddEditVoterDialog(votersTableModel.getValueAt(index))
		}
	}

	/**
	 * Přidá účastníka.
	 */
	def addVoter = {
		showAddEditVoterDialog(new Voter(), true)
	}

	/**
	 * Zobrazí dialog pro editaci vybraného účastníka.
	 */
	def showAddEditVoterDialog = {voter, create = false ->
		def addEditVoterDialog = new AddEditVotersDialog(builder, create, voter)
		addEditVoterDialog.show()
	}

	/**
	 * Okno editace účastníků (JFrame).
	 */
	def votersWindow = builder.frame(title: GlobalSettingsAndNotifier.singleton.messages.getString("votingAttenLabel"),
			layout: new BorderLayout(),
			defaultCloseOperation: WindowConstants.HIDE_ON_CLOSE) {
		scrollPane(verticalScrollBarPolicy: ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				constraints: BorderLayout.CENTER ) {
			votersTable = table(model: votersTableModel,
					selectionMode: DefaultListSelectionModel.SINGLE_SELECTION)
		}
		panel(constraints: BorderLayout.SOUTH,
				layout: new FlowLayout(FlowLayout.RIGHT)) {
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("addLabel"), actionPerformed: addVoter)
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("editLabel"), actionPerformed: editVoter)
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("deleteLabel"), actionPerformed: deleteVoter)
		}
	}
}


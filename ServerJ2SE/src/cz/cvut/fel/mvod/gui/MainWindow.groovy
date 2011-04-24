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

import groovy.swing.SwingBuilder
import java.awt.FlowLayout
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.DefaultListSelectionModel
import javax.swing.ScrollPaneConstants
import javax.swing.SpinnerNumberModel
import javax.swing.JTable
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.DefaultComboBoxModel
import javax.swing.WindowConstants
import javax.swing.JSplitPane
import javax.swing.DropMode
import javax.swing.event.ListSelectionListener
import javax.swing.event.ListSelectionEvent
import cz.cvut.fel.mvod.net.NetworkAccessManager
import cz.cvut.fel.mvod.common.*
import cz.cvut.fel.mvod.export.*
import cz.cvut.fel.mvod.persistence.*
import cz.cvut.fel.mvod.gui.table.*
import cz.cvut.fel.mvod.gui.settings.MainSettingsWindow
import cz.cvut.fel.mvod.prologueServer.RegistrantAuthorisationWindow
import java.io.IOException
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import cz.cvut.fel.mvod.evaluation.VotingResult
import cz.cvut.fel.mvod.evaluation.VotingQuestionResult
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier

/**
 * Hlavní okno programu.
 * @author jakub
 */
class MainWindow implements ListSelectionListener, DAOObserver {

	/**
	 * Hlavní okno (instance JFrame).
	 */
	def mainWindow
	/**
	 * Tabulka s novými otázkami (JTable).
	 */
	def votingTable
	/**
	 * Model tabulky s novými otázkami (VotingTableModel).
	 */
	def votingTableModel
	/**
	 * Tabulka s odpověďmi na vybranou otázku (JTable).
	 */
	def questionTable
	/**
	 * Model tabulky s odpovědmi (QuestionTableModel).
	 */
	def questionTableModel
	/**
	 * Tabulka s otázkami, o kterých se právě hlasuje (JTable).
	 */
	def runningQuestionsTable
	/**
	 * Model tabulky s probíhajícími otázkami (VotingTableModel).
	 */
	def runningQuestionsTableModel
	/**
	 * Tabulka s ukončenými otázkami (JTable).
	 */
	def finishedQuestionsTable
	def finishedQuestionsTableModel = new QuestionsTableModel(false, Question.State.FINISHED)
	def builder = new SwingBuilder()

	def votersWindow = new VotersWindow(builder)
	/**
	 * JDialog pro vytvoření nového hlasování.
	 */
	def newVotingDialog 
	def dao = DAOFacadeImpl.instance
	def voterDAO = DAOFactoryImpl.instance.voterDAO
	def questionResultPanel = new QuestionResultPanel(builder)

	/**
	 * Vytvoří novou otázku.
	 */
	def createQuestion = {
		if(dao.currentVoting) {
			def q = new Question()
			dao.currentVoting.addQuestion(q)
			if(!dao.currentVoting.test) {
				q.addAlternative(new Alternative(text: GlobalSettingsAndNotifier.singleton.messages.getString("yesLabel")))
				q.addAlternative(new Alternative(text: GlobalSettingsAndNotifier.singleton.messages.getString("noLabel")))
				q.addAlternative(new Alternative(text: GlobalSettingsAndNotifier.singleton.messages.getString("abstainLabel")))
			}
			votingTableModel.addQuestion(q)
		} else {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noVotingCreatedErr"))
		}
	}

	/**
	 * Smaže otázku vybranou v tabulce {@link #votingTable}.
	 */
	def deleteQuestion = {
		if(!dao.currentVoting) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noVotingCreatedErr"))
			return
		}
		def index = votingTable.getSelectedRow()
		if(index != -1) {
			dao.currentVoting.questions.remove(votingTableModel.remove(index))
		}
	}

	/**
	 * Spustí hlasování o otázkách vybraných v {@link votingTable}.
	 * Otevře síťové spojení, pokud je potřeba.
	 */
	def startVoting = {
		if(!dao.currentVoting) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noVotingCreatedErr"))
			return
		}
		def questions = votingTableModel.getSelected()
		def voters = voterDAO.retrieveVoters()
		if(questions.size() == 0) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noQuestSelErr"))
			return
		}
		if(voters.size() == 0) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noPPLSelErr"))
			return
		}
		def network = NetworkAccessManager.getInstance()
		try {
			network.startServer()
		} catch(IOException ex) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("netErr"))
			return
		}
		questions.each({it.state = Question.State.RUNNING})
		dao.notifyVotingChanged();
		network.sendData(voters, questions, true)
	}

	/**
	 * Zastaví hlasování o otázkách vybraných v tabulce běžících otázek.
	 */
	def stopVoting = {def forced = false ->
		if(!dao.currentVoting) {
			
		}
		def questions
		if(forced) {
			 questions = dao.currentVoting.questions.findAll{ it.state == Question.State.RUNNING }
		} else {
			questions = runningQuestionsTableModel.getSelected()
		}
		if(questions) {
			def network = NetworkAccessManager.getInstance()
			questions.each({
					network.stopReceiving(it)
					it.state = Question.State.FINISHED
			})
			dao.notifyVotingChanged();
		}
		else {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("noCurQueSelErr"))
		}
	}

	/**
	 * Zobrazí okno pro registraci účastníků.
	 */
	def registerVoters = {
		votersWindow.show()
	}

        def showSettings = {
            def MainSettingsWindow f = new MainSettingsWindow();
            f.setVisible(true)
        }

        def showRegistrants = {
            def RegistrantAuthorisationWindow REG = new RegistrantAuthorisationWindow();
            REG.setVisible(true)
        }



	def exportVotingToFile = {
		def voting = dao.currentVoting
		if(voting) {
			def file = showSaveDialog('voting')
			if(file) {
				ObjectReadWriter.serializeVoting(voting, file)
			}
		}
	}

	/**
	 * Provede export do HTML.
	 */
	def exportVotingToHTML = {
		def voting = dao.currentVoting
		if(voting && !voting.test) {
			def fc = builder.fileChooser(dialogTitle: GlobalSettingsAndNotifier.singleton.messages.getString("saveLabel"),
				dialogType: JFileChooser.SAVE_DIALOG,
				fileSelectionMode : JFileChooser.DIRECTORIES_ONLY,
				fileFilter: [getDescription: {return ''}, accept:{def file-> file.isDirectory() }] as FileFilter
			)
			if(fc.showDialog(mainWindow, GlobalSettingsAndNotifier.singleton.messages.getString("saveLabel")) != JFileChooser.APPROVE_OPTION) {
				return
			}
			def generator = new VotingHTMLGenerator(
				new VotingResult(voting, DAOFactoryImpl.instance.voteDAO.retrieveVotes(), 
						voterDAO.retrieveVoters()), fc.selectedFile)
			try {
				generator.generate()
			} catch(IOException ex) {
				showError(GlobalSettingsAndNotifier.singleton.messages.getString("repFailErr"))
			}
		}
	}

	def importVotingFromFile = {
		def file = showOpenDialog('voting')
		if(file) {
			try {
				def voting = ObjectReadWriter.loadVoting(file)
				if(voting) {
					dao.currentVoting = voting
				} else {
					GlobalSettingsAndNotifier.singleton.messages.getString("loadVotingNoFindErr")
				}
			} catch(IOException ex) {
				showError(GlobalSettingsAndNotifier.singleton.messages.getString("fileReadErr"))
			}
		}
	}

	def exportVotersToFile = {
		def voters = voterDAO.retrieveVoters()
		if(voters?.size()) {
			def file = showSaveDialog('voters')
			if(file) {
				ObjectReadWriter.serializeVoters(voters, file)
			}
		}
	}

	def importVotersFromDatabase = {
		try {
			dao.retrieveVotersFromDatabase()
			registerVoters()
		} catch(DAOException ex) {
			showError(GlobalSettingsAndNotifier.singleton.messages.getString("databaseFailErr"));
		}
	}

	def importVotersFromFile = {
		def file = showOpenDialog('voters')
		if(file) {
			try {
				def voters = ObjectReadWriter.loadVoters(file)
				if(voters) {
					voters.each {
						//FIXME osetrit ukladani uzivatele, pokud se username nachazi v databazi
						voterDAO.saveVoter(it)
					}
					registerVoters()
				} else {
					showError(GlobalSettingsAndNotifier.singleton.messages.getString("loadVotersNoFindErr"))
				}
			} catch(IOException ex) {
				showError(GlobalSettingsAndNotifier.singleton.messages.getString(fileReadErr))
			}
		}
	}

	def newVoting = {
		if(dao.currentVoting) {
			if(runningQuestionsTableModel.getRowCount()) {
				if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainWindow,
						GlobalSettingsAndNotifier.singleton.messages.getString("loadVotersNoFindErr"), GlobalSettingsAndNotifier.singleton.messages.getString("voteInProgressErrTitle"),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
					stopVoting(true)
				} else {
					return
				}
			}
			switch(JOptionPane.showConfirmDialog(mainWindow,
					GlobalSettingsAndNotifier.singleton.messages.getString("unsavedDataTXT"),
					GlobalSettingsAndNotifier.singleton.messages.getString("unsavedDataTitle"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)) {
				case JOptionPane.YES_OPTION: exportVotingToFile(); break;
				case JOptionPane.CANCEL_OPTION: return;
			}
		}
		newVotingDialog.show()
	}

	/**
	 * Hlavní menu programu (JMenuBar).
	 */
	def appMenu = builder.menuBar {
		def votingMenu = menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("votingMenuLabel")) {
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("newLabel"), actionPerformed: newVoting)
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("startLabel"), actionPerformed: startVoting)
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("stopLabel"), actionPerformed: stopVoting)
			//menuItem(text: 'Nastavení', actionPerformed: {})//TODO voting settings?
			menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("exportLabel")) {
				menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("exportToFileLabel"), actionPerformed: exportVotingToFile)
				menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("exportToHTMLLabel"), actionPerformed: exportVotingToHTML)
			}
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("importFromFileLabel"), actionPerformed: importVotingFromFile)
		}
		def questionMenu = menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("questionLabel")) {
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("addLabel"), actionPerformed: createQuestion)
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("deleteLabel"), actionPerformed: deleteQuestion)
		}
		def participantMenu = menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("votersLabel")) {
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("registerLabel"), actionPerformed: registerVoters)
			menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("importLabel")) {
				menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("importFromFileLabel"), actionPerformed: importVotersFromFile)
				menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("importFromDatabaseLabel"), actionPerformed: importVotersFromDatabase)
			}
			menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("exportToFileLabel"), actionPerformed: exportVotersToFile)
		}

                def settingsMenu = menu(text: GlobalSettingsAndNotifier.singleton.messages.getString("optionsLabel")) {
                        menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("settingsLabel"), actionPerformed: showSettings)
                        menuItem(text: GlobalSettingsAndNotifier.singleton.messages.getString("registrantVerLabel"), actionPerformed: showRegistrants)
			//menuItem(text: 'Nastavenia informačného servera', actionPerformed: exportVotersToFile)



                }

		//def helpMenu = menu(text: 'Nápověda')
	}

	/**
	 * JPanel zobrazující nové otázky.
	 */
	def votingPanel = builder.panel(layout: new BorderLayout()) {
		scrollPane(verticalScrollBarPolicy: ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				constraints: BorderLayout.CENTER) {
			votingTable = table(selectionMode: DefaultListSelectionModel.SINGLE_SELECTION)
		}
		panel(layout: new FlowLayout(alignment: FlowLayout.RIGHT), constraints: BorderLayout.SOUTH){
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("addLabel"), actionPerformed: createQuestion)
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("deleteLabel"), actionPerformed: deleteQuestion)
		}
	}

	/**
	 * JPanel zobrazující detaily nových otázek.
	 */
	def questionPanel = builder.panel(layout: new BorderLayout()) {
		scrollPane(	verticalScrollBarPolicy: ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			constraints: BorderLayout.CENTER) {
			questionTable = table()
		}
		panel(layout: new FlowLayout(alignment: FlowLayout.RIGHT), constraints: BorderLayout.SOUTH){
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("addLabel"),
				actionPerformed: {
					questionTableModel.question?.addAlternative(new Alternative())
					questionTableModel.fireTableDataChanged()
				}
			)
			button(text: GlobalSettingsAndNotifier.singleton.messages.getString("deleteLabel"),
				actionPerformed: {
					def indexes = questionTable.getSelectedRows()
					if(indexes.length > 0) {
						for(i in (indexes.length - 1 .. 0)) {
							questionTableModel.question.removeAlternative(indexes[i])
						}
					}
				}
			)
		}
	}

	public MainWindow() {
		builder.lookAndFeel('system')
		dao.votings.registerObserver(this)
		mainWindow = builder.frame(title: GlobalSettingsAndNotifier.singleton.messages.getString("mainWindowTitle"), JMenuBar: appMenu, locationRelativeTo: null,
					size: [800, 600], defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {
				tabbedPane {
					splitPane(name: GlobalSettingsAndNotifier.singleton.messages.getString("newQLabel"), orientation: JSplitPane.VERTICAL_SPLIT, dividerLocation: 280,
						topComponent: votingPanel, bottomComponent: questionPanel)
					panel(name: GlobalSettingsAndNotifier.singleton.messages.getString("activeQuestionsLabel"), layout: new BorderLayout()) {
						scrollPane(verticalScrollBarPolicy: ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
								constraints: BorderLayout.CENTER) {
							runningQuestionsTable = table()
						}
					}
					splitPane(name: GlobalSettingsAndNotifier.singleton.messages.getString("finishedQLabel"), dividerLocation: 280,
						orientation: JSplitPane.VERTICAL_SPLIT,
						bottomComponent: questionResultPanel.panel,
						topComponent: scrollPane(verticalScrollBarPolicy: ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED){
							finishedQuestionsTable = table(model: finishedQuestionsTableModel)
							finishedQuestionsTable.columnModel.getColumn(0).maxWidth = 40
						})
				}
			}
		newVotingDialog = new NewVotingDialog(builder, mainWindow)
	}

	/**
	 * Spustí aplikaci.
	 */
	public void start() {
		mainWindow.visible = true
	}



	/**
	 * {@inheritDoc }
	 */
	public void valueChanged(ListSelectionEvent e) {
		if(e.source == votingTable.selectionModel) {
			def index = votingTable.getSelectedRow()
			if(index != -1) {
				questionTableModel.question = votingTableModel.getValueAt(index)
			} else {
				questionTableModel.question = null
			}
		} else if(e.source == finishedQuestionsTable.selectionModel) {
			if(dao.currentVoting.test) {
				return //TODO test result
			}
			def index = finishedQuestionsTable.getSelectedRow()
			if(index != -1) {
				def question = finishedQuestionsTableModel.getValueAt(index)
				questionResultPanel.setQuestionResult(
				new VotingQuestionResult(question, dao.currentVoting.minVoters,
					voterDAO.retrieveVoters().size(), DAOFactoryImpl.instance.voteDAO.getVotes(question)))
			} else {
				questionResultPanel.clear()
			}

		}
	}

	/**
	 * {@inheritDoc }
	 */
	public void dataChanged(DAOObserverEvent event) {
		switch(event) {
			case DAOObserverEvent.NEW_DATA:
				setVoting()
				break;
			case DAOObserverEvent.UPDATE:
				votingTableModel.voting = dao.currentVoting
				runningQuestionsTableModel.voting = dao.currentVoting
				finishedQuestionsTableModel.voting = dao.currentVoting
				break;
		}
	}

	/**
	 * Zozbrazí chybový dialog.
	 * @param msg chybová hláška
	 */
	private void showError(String msg) {
		JOptionPane.showMessageDialog(mainWindow, msg, GlobalSettingsAndNotifier.singleton.messages.getString("errorLabel"), JOptionPane.ERROR_MESSAGE)
	}

	/**
	 * Zobrazí dialog pro uložení.
	 * @param extension přípona souboru
	 */
	private File showSaveDialog(String extension) {
		def fc = builder.fileChooser(dialogTitle: GlobalSettingsAndNotifier.singleton.messages.getString("saveLabel"),
			dialogType: JFileChooser.SAVE_DIALOG,
			fileSelectionMode : JFileChooser.FILES_ONLY,
			fileFilter: [getDescription: {return "*.${extension}"}, accept:{def file-> file ==~ /.*?\.${extension}/ || file.isDirectory() }] as FileFilter
		)
		def file
		def flag = true
		while (flag){
			if(fc.showDialog(mainWindow, GlobalSettingsAndNotifier.singleton.messages.getString("saveLabel")) != JFileChooser.APPROVE_OPTION) {
				return null
			}
			file = fc.selectedFile
			if(! (file.absolutePath ==~ /.*?\.${extension}/)) {
				file = new File("${file.absolutePath}.${extension}")
			}
			if(file.exists()) {
				flag = JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(mainWindow,
							GlobalSettingsAndNotifier.singleton.messages.getString("fileExistsRewriteTXT"), GlobalSettingsAndNotifier.singleton.messages.getString("fileExistsRewriteTitle"),
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
			} else {
				flag = false
			}
		}
		return file
	}

	/**
	 * Zobrazí dialog pro otevření.
	 * @param extension přípona souboru
	 */
	private File showOpenDialog(String extension) {
		def fc = builder.fileChooser(dialogTitle: GlobalSettingsAndNotifier.singleton.messages.getString("openLabel"),
			dialogType: JFileChooser.OPEN_DIALOG,
			fileSelectionMode : JFileChooser.FILES_ONLY,
			fileFilter: [getDescription: {return "*.${extension}"}, accept:{def file-> file ==~ /.*?\.${extension}/ || file.isDirectory() }] as FileFilter
		)
		def flag = true
		while (flag){
			if(fc.showDialog(mainWindow, GlobalSettingsAndNotifier.singleton.messages.getString("openLabel")) != JFileChooser.APPROVE_OPTION) {
				return null
			}
			if(!fc.selectedFile.exists()) {
				showError(GlobalSettingsAndNotifier.singleton.messages.getString("fileNotExistsErr"))
			} else {
				flag = false
			}
		}
		return fc.selectedFile
	}




	/**
	 * Nastaví nové hlasování do všech tabulek.
	 */
	private void setVoting() {
		if(dao.currentVoting.test) {
			votingTableModel = new TestTableModel()
			runningQuestionsTableModel = new QuestionsTableModel(false, Question.State.RUNNING)
		} else {
			votingTableModel = new VotingTableModel()
			runningQuestionsTableModel = new QuestionsTableModel(true, Question.State.RUNNING)
		}
		questionTableModel = new AlternativesTableModel(dao.currentVoting.test)
		votingTable.selectionModel.addListSelectionListener(this)
		finishedQuestionsTable.selectionModel.addListSelectionListener(this)
		votingTable.model = votingTableModel
		questionTable.model = questionTableModel
		runningQuestionsTable.model = runningQuestionsTableModel
		votingTable.columnModel.getColumn(1).cellEditor = new TextCellEditor()
		votingTable.columnModel.getColumn(1).minWidth = 400
		votingTable.columnModel.getColumn(0).maxWidth = 40
		questionTable.columnModel.getColumn(1).cellEditor = new TextCellEditor()
		questionTable.columnModel.getColumn(1).minWidth = 400
		questionTable.columnModel.getColumn(0).maxWidth = 40
		runningQuestionsTable.columnModel.getColumn(0).maxWidth = 40
		votingTableModel.voting = dao.currentVoting
		runningQuestionsTableModel.voting = dao.currentVoting
		finishedQuestionsTableModel.voting = dao.currentVoting
		votingTableModel.fireTableDataChanged()
	}
}
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
import javax.swing.JPanel
import java.awt.BorderLayout
import cz.cvut.fel.mvod.evaluation.VotingQuestionResult
import groovy.model.ValueModel
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier

/**
 * Panel zobrazující výsledky otázky.
 * @author jakub
 */
class QuestionResultPanel {

	/**
	 * Instance třídy SwingBuilder pro vytvoření panelu.
	 */
	def builder
	/**
	 * Hlavní panel.
	 */
	def panel
	/**
	 * JLabel zobrazující detaily otázky.
	 */
	def text
	/**
	 * JTable zobrazující výsledky hlasování.
	 */
	def table

	QuestionResultPanel(SwingBuilder builder) {
		this.builder = builder
		panel = builder.panel(layout: new BorderLayout(10, 10)) {
			text = label(constraints: BorderLayout.NORTH)
			scrollPane(constraints: BorderLayout.CENTER) {
				table = table()
			}
		}
	}

	/**
	 * Nastaví zobrazované výsledky.
	 * @param result výsledky k zobrazení
	 */
	void setQuestionResult(VotingQuestionResult result) {

		text.text = """<html>\n
		<b>${GlobalSettingsAndNotifier.singleton.messages.getString("HTMLQText")}</b>	${result.question.text}<br>
		<b>${GlobalSettingsAndNotifier.singleton.messages.getString("HTMLValidVotes")}</b> ${result.votesCount}<br>
		<b>${GlobalSettingsAndNotifier.singleton.messages.getString("HTMLValidVotesP")}</b> ${result.votesPercent}%<br>
		<b>${GlobalSettingsAndNotifier.singleton.messages.getString("HTMLValidResuld")}</b> ${result.valid ? GlobalSettingsAndNotifier.singleton.messages.getString("yesLabel") : GlobalSettingsAndNotifier.singleton.messages.getString("noLabel")}<br>
		"""
		def data = []
		for(def alternative in result.question.alternatives) {
			data += [alt: alternative.text, count: result.getAlternativeVoteCount(alternative),
				percent: result.getAlternativePercent(alternative)]
		}
		table.model = builder.tableModel(list: data) {
				propertyColumn(header:GlobalSettingsAndNotifier.singleton.messages.getString("answerLabel"), propertyName:'alt')
				propertyColumn(header:GlobalSettingsAndNotifier.singleton.messages.getString("nOfVotesLabel"), propertyName:'count')
				propertyColumn(header:GlobalSettingsAndNotifier.singleton.messages.getString("nOfVotesPLabel"), propertyName:'percent')
			}
		panel.revalidate()
	}

	/**
	 * Vykreslí prázdný panel.
	 */
	void clear() {
		text.text = ''
		panel.revalidate()
	}

	/**
	 * Vrátí panel.
	 * @return panel
	 */
	JPanel getPanel() {
		return panel
	}
}


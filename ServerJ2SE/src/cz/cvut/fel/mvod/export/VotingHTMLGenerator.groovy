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

package cz.cvut.fel.mvod.export
import cz.cvut.fel.mvod.common.Voting
import java.io.File
import java.io.IOException
import groovy.xml.MarkupBuilder
import cz.cvut.fel.mvod.common.Question
import cz.cvut.fel.mvod.common.Voter
import cz.cvut.fel.mvod.persistence.DAOFactoryImpl
import cz.cvut.fel.mvod.evaluation.VotingResult
import cz.cvut.fel.mvod.evaluation.VotingQuestionResult
import cz.cvut.fel.mvod.common.Vote
import java.util.HashMap.Entry

/**
 * Generátor HTML reportů.
 * @author jakub
 */
class VotingHTMLGenerator {

	/**
	 * Vyhodnocení hlasování (instance třídy {@link cz.cvut.fel.mvod.evaluation.VotingResult}).
	 */
	def result
	/**
	 * Cílová složka pro uložení reportu (instance třídy java.io.File).
	 */
	def dest
	/**
	 * Boolean určující zda se jedná o tajné hlasování.
	 */
	def secret

	VotingHTMLGenerator(VotingResult result, File dest) {
		this.result = result
		if(!dest.directory) {
			throw new IllegalArgumentException('Destination must be folder.')
		}
		this.dest = dest
		secret = result.voting.secret
	}


	/**
	 * Vygeneruje jednotlivé soubory HTML reportu.
	 * @throws IOException pokus selže zápis do souboru
	 */
	public void generate() throws IOException {
		def writer = null
		try {
			writer = new FileWriter(dest.getAbsolutePath() + "/index.html")
			def builder = new MarkupBuilder(writer)
			printDocType(writer)
			builder.html {
				head {
					meta('http-equiv': 'Content-Type', content: 'text/html;charset=UTF-8')
					link(rel: 'stylesheet', href: 'style.css', type: 'text/css')
				}
				body {
					h1("Hlasování ${new Date().format('dd. MM. yyyy')}")
					span("Typ hlasování: ${result.voting.secret ? 'tajné' : 'veřejné'}")
					br()
					span("Hranice platnosti: ${result.voting.minVoters}%")
					br()
					table(rules: 'all', summary: 'Seznam otázek') {
						caption('Seznam otázek')
						thead {
							tr {
								th('Otázka')
								th('Počet hlasujících')
								th('Počet platných hlasů')
								th('Počet hlasů v %')
								th('Vítěz')
								th('Platný výsledek')
							}
						}
						tbody {
							result.questionResults.each {def q ->
								def rowspan = Math.max(q.winner.size(), 1)
								tr {
									td(rowspan: "${rowspan}") {
										a(href: "${q.question.id}.html", q.question.text)
									}
									td(rowspan: "${rowspan}", "${result.getVotersCount()}")
									td(rowspan: "${rowspan}", "${q.votesCount}")
									td(rowspan: "${rowspan}", "${q.votesPercent}")
									if(q.winner.size()) {
										td(q.winner[0]?.text)
									} else {
										td()
									}
									td(rowspan: "${rowspan}", q.valid ? 'ano' : 'ne')
								}
								for(int i = 1; i < q.winner.size(); i ++) {
									tr{
										td(q.winner[i].text)
									}
								}
							}
						}
						tfoot {
							tr {
								th(colspan: '5', 'Celkem otázek')
								td {
									mkp.yield(result.getQuestionCount())
								}
							}
							tr {
								th(colspan: '5', 'Celkem platných otázek')
								td("${result.validQuestionCount}")
							}
							tr {
								th(colspan: '5', 'Celkem neplatných otázek')
								td("${result.getQuestionCount() - result.validQuestionCount}")
							}
						}
					}
					br()
					table(rules: 'all', summary: 'Seznam účastníků') {
						caption('Seznam účastníků')
						thead {
							tr {
								th(colspan: '2', 'Jméno a příjmení účastníka')
							}
						}
						tbody {
							result.getVoters().each {def voter ->
								tr {
									if(secret) {
										td(colspan: '2', "${voter.firstName} ${voter.lastName}")
									} else {
										td(colspan: '2') {
											a(href: "${voter.userName}.html", "${voter.firstName} ${voter.lastName}")
										}
									}
								}
							}
						}
						tfoot {
							tr {
								th('Celkem účastníků')
								td("${result.getVotersCount()}")
							}
						}
					}
				}
			}
		} finally {
			try {
				writer?.close()
			} catch(IOException ex) {
				//ok
			}
		}
		for(def questionResult in result.questionResults) {
			generateQuestion(questionResult)
		}
		if(!secret) {
			for(def voter in result.getVoters()) {
				generateVoter(voter)
			}
		}
		copyCSS()
	}

	/**
	 * Vygeneruje HTML report otázky do souboru <question.id.html>.
	 * @param questionResult vyhodnocení otázky
	 * @throws IOException pokud selže zápis do souboru
	 */
	private void generateQuestion(VotingQuestionResult questionResult) throws IOException {
		def writer = null
		try {
			writer = new FileWriter(dest.getAbsolutePath() + "/${questionResult.question.id}.html")
			def builder = new MarkupBuilder(writer)
			printDocType(writer)
			builder.html {
				head {
					meta('http-equiv': 'Content-Type', content: 'text/html;charset=UTF-8')
					link(rel: 'stylesheet', href: 'style.css', type: 'text/css')
					title(questionResult.question.text)
				}
				body {
					h1('Detaily otázky')
					span("Text otázky: ${questionResult.question.text}")
					br()
					span("Počet platných hlasů: ${questionResult.votesCount}")
					br()
					span("Počet platných hlasů: ${questionResult.votesPercent}%")
					br()
					span("Platný výsledek: ${questionResult.valid ? 'ano' : 'ne'}")
					table(rules: 'all', summary: 'Výsledky') {
						caption('Výsledek')
						thead {
							tr {
								th('Odpověď')
								th('Počet hlasů')
								th('Počet hlasů v %')
							}
						}
						tbody {
							for(def alt in questionResult.question.alternatives) {
								def percent = questionResult.getAlternativePercent(alt)
								def blue = (255 - (int) (percent * 255 / 100.0)) & 0xff
								tr(style:"background-color: #FFFF${Integer.toHexString(blue) * ((blue < 10) ? 2 : 1)}") {
									td(alt.text)
									td("${questionResult.getAlternativeVoteCount(alt)}")
									td {
										strong("${percent}")
									}
								}
							}
						}
					}
					if(!secret) {
						br()
						def votes = result.getVotes(questionResult.question)
						table(rules: 'all', summary: 'Seznam hlasů') {
							caption('Přehled hlasů')
							thead {
								tr {
									th('Jméno a příjmení hlasujícího')
									th('Odpověď')
								}
							}
							tbody {
								result.getVoters().each { def voter ->
									def vote = votes.find {
										if(it.voter == voter) return it
									}
									def rowspan = 1
									if(vote) {
										rowspan = vote.checked.size()
									}
									tr {
										td(rowspan: rowspan) {
											a(href: "${voter.userName}.html",
												"${voter.firstName} ${voter.lastName}")
										}
										td("${vote ? vote.checked[0].text : 'Nehlasoval'}")
									}
									for(def i = 1; i < rowspan; i ++) {
										tr {
											td(vote.checked[i].text)
										}
									}
								}
							}
							tfoot {
								tr{
									th('Celkem hlasujících')
									td("${result.getVotersCount()}")
								}
								tr{
									th('Platných hlasů')
									td("${votes.size()}")
								}
								tr{
									th('Platných hlasů v %')
									td("${questionResult.votesPercent}")
								}
							}
						}
					}
				}
			}
		} finally {
			try {
				writer?.close()
			} catch(IOException ex) {
				//ok
			}
		}
	}

	/**
	 * Vygeneruje HTML soubor <voter.userName.html> obsahující informace
	 * o hlasování účastníka.
	 * @param voter účastník hlasování
	 * @throws IOException pokud selže zápis do souboru
	 */
	private void generateVoter(Voter voter) throws IOException {
		def writer = null
		try {
			writer = new FileWriter(dest.getAbsolutePath() + "/${voter.userName}.html")
			def builder = new MarkupBuilder(writer)
			def votes = result.getVotes(voter)
			printDocType(writer)
			builder.html {
				head {
					meta('http-equiv': 'Content-Type', content: 'text/html;charset=UTF-8')
					link(rel: 'stylesheet', href: 'style.css', type: 'text/css')
					title("${voter.firstName} ${voter.lastName}")
				}
				body {
					h1('Přehled hlasování')
					div {
						span("Jméno: ${voter.firstName}")
						br()
						span("Příjmení: ${voter.lastName}")
						br()
					}
					table(rules: 'all', summary: 'Přehled hlasů účastníka hlasování') {
						caption('Přehled hlasů')
						thead {
							tr {
								th('Otázka')
								th('Odpověď')
							}
						}
						tbody {
							result.getQuestions().each { def question ->
								def vote = votes.find {
									if(it.question == question) return it
								}
								def rowspan = 1
								if(vote) {
									rowspan = vote.checked.size()
								}
								tr {
									td(rowspan: rowspan) {
										a(href: "${question.id}.html", question.text)
									}
									td("${vote ? vote.checked[0].text : 'Nehlasoval'}")
								}
								for(def i = 1; i < rowspan; i ++) {
									tr {
										td(vote.checked[i].text)
									}
								}
							}
						}
						tfoot {
							tr{
								th('Celkem otázek')
								td("${result.getQuestionCount()}")
							}
							tr{
								th('Platných hlasů')
								td("${votes.size()}")
							}
							tr{
								th('Nehlasoval')
								td("${result.getQuestionCount() - votes.size()}")
							}
						}
					}
				}
			}
		} finally {
			try {
				writer?.close()
			} catch(IOException ex) {
				//ok
			}
		}
	}

	/**
	 * Zkopíruje soubor style.css do cílové složky.
	 * @throws IOException pokud selže zápis do souboru
	 */
	private void copyCSS() throws IOException {
		def writer = null
		try {
			def input = this.class.getResourceAsStream("/cz/cvut/fel/mvod/export/style.css")
			writer = new FileOutputStream(dest.getAbsolutePath() + "/style.css")
			def buffer = new byte[100]
			def length
			while((length = input.read(buffer)) > 0) {
				writer.write(buffer, 0, length)
			}
		} finally {
			try {
				writer?.close()
			} catch(IOException ex) {
				//ok
			}
		}
	}

	/**
	 * Zapíše do výstupního proudu informaci o typu dokumentu.
	 * @param writer výstupní proud
	 * @throws IOException pokud selže zápis do výstupního proudu
	 */
	private void printDocType(Writer writer) throws IOException {
		writer.write('<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">\n')
	}
}


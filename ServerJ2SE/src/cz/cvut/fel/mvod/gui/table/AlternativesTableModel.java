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

package cz.cvut.fel.mvod.gui.table;

import cz.cvut.fel.mvod.common.Alternative;
import cz.cvut.fel.mvod.common.Question;

/**
 * Modle tabulky zobrazující seznam odpovědí
 * @author jakub
 */
public class AlternativesTableModel extends AbstractTableModel<Alternative> {

	private final static TableColumnInformation NUMBER = new TableColumnInformation(0, "Číslo", Integer.class, false);
	private final static TableColumnInformation TEXT = new TableColumnInformation(1, "Text odpovědi", String.class, true);
	private final static TableColumnInformation CORRECT = new TableColumnInformation(2, "Správná", Boolean.class, true);

	private Question question = null;

	/**
	 * Vytvoří novou instanci.
	 * @param testQuestion zda se zobrazují odpovědi na testovou otázku
	 */
	public AlternativesTableModel(boolean testQuestion) {
		super(new TableColumnInformation[testQuestion ? 3 : 2]);
		if(testQuestion) {
			COLUMNS[2] = CORRECT;
		} else {
		}
		COLUMNS[0] = NUMBER;
		COLUMNS[1] = TEXT;
	}

	/**
	 * Vrátí počet řádků.
	 * @return počet řádků
	 */
	public int getRowCount() {
		if(question == null) {
			return 0;
		}
		return question.getAlternativesCount();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(question == null) {
			return null;
		}
		if(rowIndex > question.getAlternativesCount()) {
			throw new IndexOutOfBoundsException("Row index (" + rowIndex + ") is out of bounds.");
		}
		if(columnIndex == NUMBER.INDEX) {
			return rowIndex + 1;
		} else if(columnIndex == TEXT.INDEX) {
			return question.getAlternative(rowIndex).getText();
		} else if(columnIndex == CORRECT.INDEX) {
			return question.getAlternative(rowIndex).isCorrect();
		}
		throw new IndexOutOfBoundsException("No such collumn.");
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(question == null) {
			return;
		}
		if(rowIndex > question.getAlternativesCount()) {
			throw new IndexOutOfBoundsException("Row index (" + rowIndex + ") is out of bounds.");
		}
		if(isCellEditable(rowIndex, columnIndex)) {
			if(columnIndex == TEXT.INDEX) {
				question.getAlternative(rowIndex).setText((String) aValue);
			} else if(columnIndex == CORRECT.INDEX) {
				question.getAlternative(rowIndex).setCorrect((Boolean) aValue);
			}
		}
	}

	/**
	 * Nastaví otázku pro zobrazení v tabulce.
	 * @param question
	 */
	public void setQuestion(Question question) {
		this.question = question;
		fireTableDataChanged();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public Alternative remove(int index) {
		Alternative alt = question.getAlternatives().remove(index);
		fireTableDataChanged();
		return alt;
	}

	/**
	 * {@inheritDoc }
	 */
	public Alternative getValueAt(int rowIndex) {
		return question.getAlternatives().get(rowIndex);
	}

}

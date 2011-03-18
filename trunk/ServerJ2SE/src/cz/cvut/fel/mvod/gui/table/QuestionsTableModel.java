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

import cz.cvut.fel.mvod.common.Question;
import cz.cvut.fel.mvod.common.Voting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * Model tabulky zobrazující otázky.
 * @author jakub
 */
public class QuestionsTableModel extends AbstractTableModel<Question> {

	private final TableColumnInformation SELECTED;
	private final TableColumnInformation TEXT = new TableColumnInformation(1, "Text otázky", String.class, false);

	protected List<Item> rows = null;
	protected Map<Question, Item> questions = null;
	protected Voting voting = null;
	protected Question.State state;

	public QuestionsTableModel(boolean selectable, Question.State state) {
		super(new TableColumnInformation[2]);
		SELECTED = new TableColumnInformation(0, "Výběr", Boolean.class, selectable);
		COLUMNS[0] = SELECTED;
		COLUMNS[1] = TEXT;
		this.state = state;
	}

	/**
	 * Vytvoří novou instanci
	 * @param columnCount počet sloupců
	 * @param state stav zobrazovaných otázek
	 */
	QuestionsTableModel(int columnCount, Question.State state) {
		this(columnCount, false, state);
	}

	/**
	 * Vytvoří novou instanci
	 * @param columnCount počet sloupců
	 * @param defaultSelection hodnota prvního sloupce u nově vložených dat
	 * @param state stav zobrazovaných otázek
	 */
	QuestionsTableModel(int columnCount, boolean defaultSelection, Question.State state) {
		super(new TableColumnInformation[columnCount]);
		if(columnCount < 0) {
			throw new IllegalArgumentException();
		}
		this.state = state;
		SELECTED = new TableColumnInformation(0, "Výběr", Boolean.class, !defaultSelection);
	}

	/**
	 * {@inheritDoc  }
	 */
	public int getRowCount() {
		if(rows == null) {
			return 0;
		}
		return rows.size();
	}

	/**
	 * Nastaví hlasování.
	 * @param voting hlasování
	 */
	public void setVoting(Voting voting) {
		if(voting != null) {
			questions = new HashMap<Question, Item>();
			rows = new ArrayList<Item>();
			this.voting = voting;
			updateRows();
			fireTableDataChanged();
		}
	}


	/**
	 * Přidá další otázku.
	 * @param question
	 */
	public void addQuestion(Question question) {
		fireTableDataChanged();
	}

	/**
	 * {@inheritDoc }
	 */
	public Question getValueAt(int rowIndex) {
		return rows.get(rowIndex).question;
	}

	/**
	 * {@inheritDoc }
	 */
	public Question remove(int rowIndex) {
		Question question = rows.remove(rowIndex).question;
		fireTableDataChanged();
		return question;
	}

	/**
	 * Vrátí všechny položky na řádcích zaškrtnutých ve sloupci {@link #SELECTED}.
	 * @return zaškrtnuté otázky
	 */
	public List<Question> getSelected() {
		List<Question> selected = new ArrayList<Question>();
		for(Item i: rows) {
			if(i.selected) {
				selected.add(i.question);
			}
		}
		return selected;
	}

	/**
	 * Překreslí tabulku.
	 */
	protected void updateRows() {
		if(voting == null) {
			return;
		}
		for(Question question: voting.getQuestions()) {
			if(!questions.containsKey(question) && question.getState() == state) {
				Item row = new Item(question, !SELECTED.EDITABLE);
				questions.put(question, row);
				rows.add(row);
			}
		}
	}

	/**
	 * {@inheritDoc  }
	 */
	@Override
	public void fireTableDataChanged() {
		updateRows();
        fireTableChanged(new TableModelEvent(this));
    }

	/**
	 * {@inheritDoc  }
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == SELECTED.INDEX) {
			return rows.get(rowIndex).selected;
		} else if(columnIndex == TEXT.INDEX) {
			return rows.get(rowIndex).question.getText();
		}
		throw new IndexOutOfBoundsException("No such column.");
	}

	/**
	 * {@inheritDoc  }
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(isCellEditable(rowIndex, columnIndex)) {
			if(columnIndex == SELECTED.INDEX) {
				rows.get(rowIndex).selected = (Boolean) aValue;
			}
		}
	}

	/**
	 * Dekorátor pro třídu {@link cz.cvut.fel.mvod.common.Question}.
	 */
	protected class Item {
		public final Question question;
		public boolean selected;

		public Item(Question question, boolean selected) {
			this.question = question;
			this.selected = selected;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 79 * hash + (this.question != null ? this.question.hashCode() : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			if(getClass() != obj.getClass()) {
				return false;
			}
			final Item other = (Item) obj;
			if(this.question != other.question && (this.question == null || ! this.question.equals(other.question))) {
				return false;
			}
			return true;
		}


	}

}

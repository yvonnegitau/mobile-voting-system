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
import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;

/**
 * Model tabulky hlasovacích otázek.
 * @author jakub
 */
public class VotingTableModel extends QuestionsTableModel {

	private final static TableColumnInformation SELECTED = new TableColumnInformation(0, GlobalSettingsAndNotifier.singleton.messages.getString("selectionLabel"), Boolean.class, true);
	private final static TableColumnInformation TEXT = new TableColumnInformation(1, GlobalSettingsAndNotifier.singleton.messages.getString("qTextLabel"), String.class, true);
        private final static TableColumnInformation DETAILS = new TableColumnInformation(2,GlobalSettingsAndNotifier.singleton.messages.getString("qDescriptionLabel") ,String.class,true);
	private final static TableColumnInformation MIN_PERCENT = new TableColumnInformation(3, GlobalSettingsAndNotifier.singleton.messages.getString("minPForWinLabel"), Integer.class, true);
	private final static TableColumnInformation WINNERS = new TableColumnInformation(4, GlobalSettingsAndNotifier.singleton.messages.getString("nOfVictorsLabel"), Integer.class, true);
	private final static TableColumnInformation MAX_SELECTED = new TableColumnInformation(5, GlobalSettingsAndNotifier.singleton.messages.getString("maxSelectedLabel"), Integer.class, true);
	private final static TableColumnInformation MIN_SELECTED = new TableColumnInformation(6, GlobalSettingsAndNotifier.singleton.messages.getString("minSelectedLabel"), Integer.class, true);
	private final static int COLLUMN_COUNT = 7;

	public VotingTableModel() {
		super(COLLUMN_COUNT, Question.State.NOT_SET);
		COLUMNS[0] = SELECTED;
		COLUMNS[1] = TEXT;
                COLUMNS[2] = DETAILS;
		COLUMNS[3] = MIN_PERCENT;
		COLUMNS[4] = WINNERS;
		COLUMNS[5] = MAX_SELECTED;
		COLUMNS[6] = MIN_SELECTED;
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
		} else if(columnIndex == MIN_PERCENT.INDEX) {
			return rows.get(rowIndex).question.getMinPercent();
		} else if(columnIndex == WINNERS.INDEX) {
			return rows.get(rowIndex).question.getMaxWinners();
		} else if(columnIndex == MAX_SELECTED.INDEX) {
			return rows.get(rowIndex).question.getMaxSelect();
		} else if(columnIndex == MIN_SELECTED.INDEX) {
			return rows.get(rowIndex).question.getMinSelect();
		} else if(columnIndex == DETAILS.INDEX) {
                        return rows.get(rowIndex).question.getDetails();
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
			} else if(columnIndex == TEXT.INDEX) {
				rows.get(rowIndex).question.setText((String)aValue);
			} else if(columnIndex == MIN_PERCENT.INDEX) {
				rows.get(rowIndex).question.setMinPercent((Integer) aValue);
			} else if(columnIndex == WINNERS.INDEX) {
				rows.get(rowIndex).question.setMaxWinners((Integer) aValue);
			} else if(columnIndex == MAX_SELECTED.INDEX) {
				rows.get(rowIndex).question.setMaxSelect((Integer) aValue);
			} else if(columnIndex == MIN_SELECTED.INDEX) {
				rows.get(rowIndex).question.setMinSelect((Integer) aValue);
			} else if(columnIndex == DETAILS.INDEX) {
                                rows.get(rowIndex).question.setDetails((String) aValue);
                        }
		}
	}

}

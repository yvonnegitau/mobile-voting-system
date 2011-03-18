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

import cz.cvut.fel.mvod.common.Voter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Model tabulky seznamu účastníků.
 * @author jakub
 */
public class VotersTableModel extends AbstractTableModel<Voter> {

	private final static TableColumnInformation NAME = new TableColumnInformation(0, "Jméno", String.class, false);
	private final static TableColumnInformation SURNAME = new TableColumnInformation(1, "Příjmení", String.class, false);
	private final static TableColumnInformation USERNAME = new TableColumnInformation(2, "Uživatelské jméno", String.class, false);

	private List<Voter> rows = new ArrayList<Voter>();

	public VotersTableModel() {
		super(new TableColumnInformation[3]);
		COLUMNS[0] = NAME;
		COLUMNS[1] = SURNAME;
		COLUMNS[2] = USERNAME;
	}

	/**
	 * {@inheritDoc  }
	 */
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * {@inheritDoc  }
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == NAME.INDEX) {
			return rows.get(rowIndex).getFirstName();
		} else if(columnIndex == SURNAME.INDEX) {
			return rows.get(rowIndex).getLastName();
		} else if(columnIndex == USERNAME.INDEX) {
			return rows.get(rowIndex).getUserName();
		}
		throw new IndexOutOfBoundsException("No such column.");
	}

	/**
	 * {@inheritDoc  }
	 */
	public Voter getValueAt(int rowIndex) {
		if(rowIndex < 0 || rowIndex > rows.size()) {
			throw new IndexOutOfBoundsException("No such row.");
		}
		return rows.get(rowIndex);
	}

	/**
	 * Nastaví obsah tabulky.
	 * @param voters to show
	 */
	public void setVoters(Collection<Voter> voters) {
		rows.clear();
		rows.addAll(voters);
		fireTableDataChanged();
	}

	/**
	 * Přidá účastníka do tbulky.
	 * @param voter nový účastník
	 */
	public void addVoter(Voter voter) {
		rows.add(voter);
	}

	/**
	 * {@inheritDoc  }
	 */
	public Voter remove(int index) {
		Voter voter = rows.remove(index);
		fireTableDataChanged();
		return  voter;
	}

}

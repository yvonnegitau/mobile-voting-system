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

/**
 * Rozšířená implementace javax.swing.table.AbstractTableModel.
 * @author jakub
 */
abstract class AbstractTableModel<T> extends javax.swing.table.AbstractTableModel {

	/**
	 * Informace o jednotlivých sloupcích.
	 */
	protected final TableColumnInformation[] COLUMNS;

	public AbstractTableModel(TableColumnInformation[] COLLUMNS) {
		this.COLUMNS = COLLUMNS;
	}

	/**
	 * Vrátí počet sloupců definovaných v {@link #COLUMNS}.
	 * @return počet sloupců
	 */
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMNS[columnIndex].NAME;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMNS[columnIndex].TYPE;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return COLUMNS[columnIndex].EDITABLE;
	}

	/**
	 * Odstraní položku na vybraném řádku.
	 * @param rowIndex číslo řádky
	 * @return odstraněný prvek
	 */
	public abstract T remove(int rowIndex);

	/**
	 * Vrátí položku zobrazenou na vybraném řádku.
	 * @param rowIndex číslo řádku
	 * @return položka na vybraném řádku
	 */
	public abstract T getValueAt(int rowIndex);
}

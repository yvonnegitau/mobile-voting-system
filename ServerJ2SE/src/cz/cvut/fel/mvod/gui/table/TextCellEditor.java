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

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

/**
 * Víceřádková buňka tabulky pro editaci textu.
 * @author jakub
 */
public class TextCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JTextArea textArea = new JTextArea();
	private JScrollPane component = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JTable editedTable;
	private int editedRow;
	private int editedRowHeight;

	public TextCellEditor() {
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		component.setViewportView(textArea);
	}

	/**
	 * {@inheritDoc }
	 */
	public Object getCellEditorValue() {
		//prekresli radek do puvodni velikosti
		editedTable.setRowHeight(editedRow, editedRowHeight);
		return textArea.getText();
	}

	/**
	 * {@inheritDoc }
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editedTable = table;
		editedRow = row;
		editedRowHeight = table.getRowHeight(row);
		textArea.setText((String) value);
		table.setRowHeight(row, (int) (textArea.getPreferredSize().getHeight()) / textArea.getLineCount() * 10);
		return component;
	}

}

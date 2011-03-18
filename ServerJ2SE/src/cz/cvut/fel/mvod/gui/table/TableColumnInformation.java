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
 * Informace o sloupci tabulky.
 * @author jakub
 */
class TableColumnInformation {

	/**
	 * Index sloupce.
	 */
	public final int INDEX;
	/**
	 * Název sloupce zobrazený v hlavičce.
	 */
	public final String NAME;
	/**
	 * Datový typ zobrazený ve sloupci.
	 */
	public final Class<?> TYPE;
	/**
	 * Příznak označující jestli jsou data ve sloupci editovatelná.
	 */
	public final boolean EDITABLE;

	public TableColumnInformation(int INDEX, String NAME, Class<?> TYPE, boolean EDITABLE) {
		this.INDEX = INDEX;
		this.NAME = NAME;
		this.TYPE = TYPE;
		this.EDITABLE = EDITABLE;
	}

	public TableColumnInformation(int INDEX, String NAME, Class<?> TYPE) {
		this(INDEX, NAME, TYPE, false);
	}

}

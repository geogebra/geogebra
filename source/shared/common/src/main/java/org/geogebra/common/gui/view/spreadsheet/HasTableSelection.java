package org.geogebra.common.gui.view.spreadsheet;

/**
 * Tabular component with selection capabilities.
 */
public interface HasTableSelection {

	/**
	 * @return whether whole table is selected
	 */
	boolean isSelectAll();

	/**
	 * @param cellFormatString cell format
	 */
	void updateCellFormat(String cellFormatString);

	/**
	 * Repaint the table.
	 */
	void repaint();
}

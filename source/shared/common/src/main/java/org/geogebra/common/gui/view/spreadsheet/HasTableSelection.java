package org.geogebra.common.gui.view.spreadsheet;

/**
 * Tablular component with selection capabilities.
 */
public interface HasTableSelection {
	boolean isSelectAll();

	void updateCellFormat(String cellFormatString);

	void repaint();
}

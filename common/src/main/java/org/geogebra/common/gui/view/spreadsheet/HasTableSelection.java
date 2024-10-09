package org.geogebra.common.gui.view.spreadsheet;

public interface HasTableSelection {
	boolean isSelectAll();

	void updateCellFormat(String cellFormatString);

	void repaint();
}

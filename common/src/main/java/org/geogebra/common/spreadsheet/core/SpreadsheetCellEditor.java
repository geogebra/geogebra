package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

public interface SpreadsheetCellEditor {

	void setBounds(Rectangle editorBounds);

	void setTargetCell(int row, int column);

	/**
	 * @param content value of the cell being edited
	 */
	void setContent(Object content);

	/**
	 * Add text to current input, handle as if typed by keyboard.
	 * @param text partial input, doesn't have to be valid editor string
	 */
	void type(String text);

	void setAlign(int align);

	void scrollHorizontally();

	boolean isVisible();

	void hide();

	void onEnter();
}

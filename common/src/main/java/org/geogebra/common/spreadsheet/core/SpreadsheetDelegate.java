package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

public interface SpreadsheetDelegate {
	void showCellEditor(Rectangle bounds, Object data);

	void hideCellEditor();
}

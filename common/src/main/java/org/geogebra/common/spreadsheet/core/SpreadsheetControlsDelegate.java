package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {
	void showCellEditor(Rectangle bounds, Object data);

	void showContextMenu();

	void hideCellEditor();

	void hideContextMenu();
}

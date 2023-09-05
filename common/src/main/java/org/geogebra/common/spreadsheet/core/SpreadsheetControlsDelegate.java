package org.geogebra.common.spreadsheet.core;

import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {
	void showCellEditor(Rectangle bounds, Object data, GPoint coords);

	void showContextMenu(Map<String, Runnable> actions, GPoint coords);

	void hideCellEditor();

	void hideContextMenu();
}

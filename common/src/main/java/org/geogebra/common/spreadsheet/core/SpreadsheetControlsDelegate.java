package org.geogebra.common.spreadsheet.core;

import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {
	SpreadsheetCellEditor getCellEditor();

	void showContextMenu(Map<String, Runnable> actions, GPoint coords);

	void hideCellEditor();

	void hideContextMenu();
}

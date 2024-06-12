package org.geogebra.common.spreadsheet.core;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetCellProcessor;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {

	/**
	 * @return A cell editor instance. Clients should return the same, cached instance on
	 * successive calls if possible.
	 */
	// TODO document when/how often this is called
	SpreadsheetCellEditor getCellEditor();

	void showContextMenu(List<ContextMenuItem> actions, GPoint coords);

	void hideContextMenu();

	ClipboardInterface getClipboard();
}

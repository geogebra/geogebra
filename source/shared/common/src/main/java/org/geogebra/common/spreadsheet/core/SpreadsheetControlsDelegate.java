package org.geogebra.common.spreadsheet.core;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {

	/**
	 * @return A cell editor instance.
	 */
	SpreadsheetCellEditor getCellEditor();

	void showContextMenu(List<ContextMenuItem> actions, GPoint coords);

	void hideContextMenu();

	ClipboardInterface getClipboard();

	/**
	 * Show completion suggestions for the cell currently being edited. If the suggestions UI is not
	 * already visible, it should be shown at this point.
	 * @param input The current text of the cell being edited.
	 * @param editorBounds The bounds of the cell being edited (in viewport-relative coordinates).
	 * Use this to position the suggestions UI.
	 */
	void showAutoCompleteSuggestions(String input, Rectangle editorBounds);

	/**
	 * Hide the command suggestions UI (if currently visible).
	 */
	void hideAutoCompleteSuggestions();

	/**
	 * Give the `SpreadsheetControlsDelegate` an option to handle key presses during autocomplete.
	 * @param keyCode The pressed key.
	 * @return True if the `SpreadsheetControlsDelegate` did handle the key press (which means it
	 * shouldn't be processed any further), false otherwise.
	 */
	boolean handleKeyPressForAutoComplete(int keyCode);
}

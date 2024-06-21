package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.CheckForNull;

/**
 * The companion delegate to the {@link TableValuesKeyboardNavigationController}.
 * @apiNote All indexes are 0-based.
 */
public interface TableValuesKeyboardNavigationControllerDelegate {

	/**
	 * Focus (i.e., mark as selected, potentially scroll into view, and start editing)
	 * the given cell.
	 * @param row The row index.
	 * @param column The column index.
	 * @apiNote This method will only be called if the selection did actually change.
	 */
	void focusCell(int row, int column);

	/**
	 * Re-focus the current cell.
	 * @param row The row index.
	 * @param column The column index.
	 * @apiNote As a counterpart to {@link #focusCell(int, int)}, this method will be called
	 * if the selection did NOT change in response to some interaction.
	 * In some interaction cases, the table view's data set may change (e.g., when deleting the
	 * last row in the table), but the selection should stay on the same cell. In these cases, the
	 * delegate needs to re-focus the same cell after reloading the data, and this method should
	 * help with that. If clients handle re-focusing after a reload differently (e.g., by storing
	 * the currently selected cell index and checking that information after a reload), then this
	 * callback may be safely ignored.
	 */
	void refocusCell(int row, int column);

	/**
	 * Unfocus (i.e., end editing, remove selection border) the given cell.
	 * @param row The row index of the cell that should be unfocused.
	 * @param column The column index of the cell that should be unfocused.
	 */
	void unfocusCell(int row, int column);

	/**
	 * Get the contents of the editor of the given cell.
	 * @param row row index.
	 * @param column column index.
	 * @return The current content of the editor for the given cell. May return null or an
	 * empty string if the cell is empty.
	 */
	@CheckForNull
	String getCellEditorContent(int row, int column);

	/**
	 * Show a warning about invalid cell content.
	 * @param row The row index of the invalid cell.
	 * @param column The column index of the invalid cell.
	 */
	void invalidCellContentDetected(int row, int column);
}

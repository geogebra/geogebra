package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.CheckForNull;

/**
 * The companion delegate to the {@link TableValuesKeyboardController}.
 *
 * @apiNote All indexes are 0-based.
 */
public interface TableValuesKeyboardControllerDelegate {

    /**
     * Focus (i.e., mark as selected, potentially scroll into view, and start editing)
     * the given cell.
     *
     * @param row row index.
     * @param column column index.
     */
    void focusCell(int row, int column);

    /**
     * Get the contents of the editor of the given cell.
     *
     * @param row row index.
     * @param column column index.
     * @return The current content of the editor for the given cell. May return null or an
     * empty string if the cell is empty.
     */
    @CheckForNull
    String getCellEditorContent(int row, int column);

    /**
     * Show a warning about invalid cell content.
     *
     * @param row The row index of the invalid cell.
     * @param column The column index of the invalid cell.
     */
    void invalidCellContentDetected(int row, int column);

    /**
     * Hide the keyboard (if it is currently visible).
     *
     * TODO find out in which scenarios the keyboard needs to be dismissed and implement
     */
    void hideKeyboard();
}

package org.geogebra.common.gui.view.table.keyboard;

/**
 * The companion delegate to the {@link TableValuesKeyboardController}.
 *
 * @apiNote All indexes are 0-based.
 */
public interface TableValuesKeyboardControllerDelegate {

    /**
     * Focus (i.e., select, scroll into view, and start editing) the given cell.
     *
     * @param row row index.
     * @param column column index.
     */
    void focusCell(int row, int column);

    // TODO mark cell as invalid input

    /**
     * Query whether the given cell is empty or not.
     *
     * @param row row index.
     * @param column column index.
     * @return True if the given cell is empty.
     */
    boolean isCellEmpty(int row, int column);

    /**
     * End editing of the given cell, committing the returned value to the table model (if valid).
     *
     * @param row row index.
     * @param column column index.
     * @return The cell's content (which will be set on the model).
     */
    String commitCell(int row, int column);

    /**
     * Hide the keyboard (if it is currently visible).
     *
     * TODO find out in which scenarios the keyboard needs to be dismissed and implement
     */
    void hideKeyboard();
}

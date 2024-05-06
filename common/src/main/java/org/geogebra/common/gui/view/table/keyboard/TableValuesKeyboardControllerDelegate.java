package org.geogebra.common.gui.view.table.keyboard;

/**
 * @apiNote All indexes are 0-based.
 */
public interface TableValuesKeyboardControllerDelegate {

    /**
     * Focus (i.e., select, scroll into view, and start editing) the given cell.
     * @param row row index.
     * @param column column index.
     */
    void focusCell(int row, int column);

    /**
     * Commit any pending changes in the given cell.
     * @param row row index.
     * @param column column index.
     */
    void commitCell(int row, int column);

    /**
     * Hide the keyboard (if it is currently visible).
     */
    void hideKeyboard();
}

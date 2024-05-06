package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
//import org.geogebra.common.ownership.NonOwning;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for keyboard navigation in the "table of values" view.
 * <p>
 * This controller accepts key press events, figures out which cell to select in response (if any),
 * asks its delegate to focus the new cell if the selection changed, and possibly asks the
 * delegate to hide the keyboard.
 * <p>
 * Initially, no cell is selected - clients need to call {@link #select(int, int, boolean)} with a
 * valid row and column index (e.g., 0, 0) before keyboard events will result in a change in
 * selection.
 *
 * @apiNote All row and column indexes are 0-based.
 */
public final class TableValuesKeyboardController {

    public static enum Key {
        ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_DOWN, RETURN;
    }

    //	@NonOwning
    @Weak
    public TableValuesKeyboardControllerDelegate delegate;

    private final @Nonnull TableValues tableValuesView;
    private final @Nonnull TableValuesModel tableValuesModel;

    private int selectedRow = -1;
    private int selectedColumn = -1;
    private boolean addedTemporaryColumn = false;
    private boolean addedTemporaryRow = false;

    public TableValuesKeyboardController(@Nonnull TableValues tableValuesView) {
        this.tableValuesView = tableValuesView;
        this.tableValuesModel = tableValuesView.getTableValuesModel();
    }

    /**
     * @return the selected column index, or -1 if no cell is selected.
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * @return the selected row index, or -1 if no cell is selected.
     */
    public int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * Selects a cell. If either row or column is outside the valid range, nothing will happen.
     *
     * @param row            the row index to select, or -1 to clear any selection.
     * @param column         the column index to select, or -1 to clear any selection.
     * @param notifyDelegate Pass true to notify the delegate in case the selection did change.
     */
    public void select(int row, int column, boolean notifyDelegate) {
        if (row >= getMaxRowIndex(column) || column >= getMaxColumnIndex()) {
            return;
        }
        boolean changed = selectedRow != row || selectedColumn != column;
        selectedRow = row;
        selectedColumn = column;
        if (changed && notifyDelegate && delegate != null) {
            delegate.focusCell(selectedRow, selectedColumn);
        }
    }

    /**
     * Calls select(row, column, true).
     * @param row
     * @param column
     */
    public void select(int row, int column) {
        select(row, column, true);
    }

    /**
     * Clears (removes) any selection.
     */
    public void deselect(boolean notifyDelegate) {
        select(-1, -1, notifyDelegate);
    }

    public void keyPressed(Key key) {
        if (selectedRow < 0 || selectedColumn < 0) {
            return;
        }
        switch (key) {
            case ARROW_LEFT:
                handleArrowLeft();
                break;
            case ARROW_RIGHT:
                handleArrowRight();
                break;
            case ARROW_UP:
                handleArrowUp();
                break;
            case ARROW_DOWN:
                handleArrowDown();
                break;
            case RETURN:
                handleArrowDown();
                break;
        }
    }

    private void handleArrowLeft() {
        if (isFirstColumn(selectedColumn)) {
            return;
        }
        commitPendingChanges();
        select(selectedRow, findFirstFocusableColumnLeftOf(selectedColumn), true);
    }

    private void handleArrowRight() {
        int column = findFirstFocusableColumnRightOf(selectedColumn);
        if (column == -1) {
            if (tableValuesModel.getAllowsAddingColumns() && !addedTemporaryColumn) {
                addedTemporaryColumn = true; // TODO when to reset?
                column = getMaxColumnIndex() - 1;
            } else {
                commitPendingChanges();
                hideKeyboard(); // TODO necessary?
                return;
            }
        }
        commitPendingChanges();
        select(selectedRow, column, true);
    }

    private void handleArrowUp() {
        if (isFirstRow(selectedRow)) {
            return;
        }
        commitPendingChanges();
        select(selectedRow - 1, selectedColumn, true);
    }

    private void handleArrowDown() {
        if (isLastRow(selectedRow, selectedColumn)) {
            if (isColumnEditable(selectedColumn) && !addedTemporaryRow) {
                addedTemporaryRow = true;
            } else {
                commitPendingChanges();
                hideKeyboard();
                return;
            }
        }
        commitPendingChanges();
        select(selectedRow + 1, selectedColumn, true);
    }

    private boolean isFirstRow(int row) {
        return row == 0;
    }

    private boolean isLastRow(int row, int column) {
        return row == getMaxRowIndex(column) - 1;
    }

    private boolean isFirstColumn(int column) {
        return column == 0;
    }

    private boolean isLastColumn(int column) {
        return column == getMaxColumnIndex() - 1;
    }

    private int findFirstFocusableColumnLeftOf(int column) {
        for (int index = column - 1; index >= 0; index--) {
            if (isColumnEditable(index)) {
                return index;
            }
        }
        return -1;
    }

    private int findFirstFocusableColumnRightOf(int column) {
        for (int index = column + 1; index < getMaxColumnIndex(); index++) {
            if (isColumnEditable(index)) {
                return index;
            }
        }
        return -1;
    }

    private boolean isColumnEditable(int column) {
        if (column < 0) {
            return false;
        }
        GeoEvaluatable evaluatable = tableValuesView.getEvaluatable(column);
        if (evaluatable == null) {
            return false;
        }
        return !(evaluatable instanceof GeoFunctionable);
    }

    // note: the returned end index is exclusive
    private int getMaxRowIndex(int column) {
        if (!isColumnEditable(column)) {
            return tableValuesModel.getRowCount();
        }
        return tableValuesModel.getRowCount() + (addedTemporaryRow ? 1 : 0);
    }

    // note: the returned end index is exclusive
    private int getMaxColumnIndex() {
        return tableValuesModel.getColumnCount() + (addedTemporaryColumn ? 1 : 0);
    }

    private void hideKeyboard() {
        if (delegate != null) {
            delegate.hideKeyboard();
        }
    }

    private void commitPendingChanges() {
        if (selectedRow == -1 || selectedColumn == -1 || !isColumnEditable(selectedColumn)) {
            return;
        }
        if (delegate != null) {
            delegate.commitCell(selectedRow, selectedColumn);
        }
    }
}

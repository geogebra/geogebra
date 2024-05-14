package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for keyboard navigation in the "table of values" view.
 * <p>
 * This controller accepts key press events, figures out which cell to select in response (if any),
 * asks its delegate to focus the new cell if the selection actually changed, and possibly asks the
 * delegate to hide the keyboard.
 * </p>
 * <p>
 * Initially, no cell is selected - clients need to call {@link #select(int, int)} with a
 * valid row and column index (e.g., 0, 0) before keyboard events will result in a change in
 * selection.
 * </p>
 * <p>
 * Note: All row and column indexes are 0-based.
 * </p>
 * @apiNote This controller *requires* its delegate for correct operation. If the delegate is not
 * set, no exception is thrown, but the controller will not do anything useful.
 *
 * @implNote Currently, the code assumes that the first column is the x ("values") column,
 * and that it is always present (it may be empty, though).
 */
public final class TableValuesKeyboardController {

	public static enum Key {
		ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_DOWN, RETURN;
	}

	//@NonOwning
	@Weak
	public TableValuesKeyboardControllerDelegate delegate;

	//@NonOwning
	private final @Nonnull TableValues tableValuesView;
	//@NonOwning
	private final @Nonnull TableValuesModel tableValuesModel;

	private int selectedRow = -1;
	private int selectedColumn = -1;
	private boolean addedPlaceholderColumn = false;
	private boolean addedPlaceholderRow = false;

	/**
	 * Create a new instance.
	 *
	 * @param tableValuesView The table of values view.
	 * @param delegate The delegate (can be null here, but must be supplied through
	 * the public writable field before use).
	 *
	 */
	public TableValuesKeyboardController(@Nonnull TableValues tableValuesView,
			TableValuesKeyboardControllerDelegate delegate) {
		this.tableValuesView = tableValuesView;
		this.tableValuesModel = tableValuesView.getTableValuesModel();
		this.delegate = delegate;
	}

	/**
	 * @return The selected column index, or -1 if no cell is selected.
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @return The selected row index, or -1 if no cell is selected.
	 */
	public int getSelectedColumn() {
		return selectedColumn;
	}

	/**
	 * @return The overall number of "navigable" (reachable) rows in the table, including an
	 * additional placeholder row for appending new data if the table values model has editable
	 * columns.
	 *
	 * @apiNote This is not the same as {@link TableValuesModel#getRowCount()}, because that
	 * does not include the placeholder row.
	 */
	public int getNrOfNavigableRows() {
		return tableValuesModel.getRowCount()
				+ (tableValuesModel.hasEditableColumns() ? 1 : 0);
	}

	/**
	 * @return The overall number of "navigable" (reachable) columns in the table, including an
	 * additional placeholder column for inputting new data if the table values model allows
	 * adding columns (@see {@link TableValuesModel#allowsAddingColumns()}.
	 *
	 * @apiNote This is not the same as {@link TableValuesModel#getColumnCount()}, because that
	 * does not include the placeholder column.
	 */
	public int getNrOfNavigableColumns() {
		return tableValuesModel.getColumnCount()
				+ (tableValuesModel.allowsAddingColumns() ? 1 : 0);
	}

	/**
	 * @param column column index
	 * @return True if the column at index is editable
	 * (@see {@link TableValuesModel#isColumnEditable(int)} or is a placeholder column (which
	 * is also editable). This information can be used to display non-editable columns in a
	 * different color in the UI, for example.
	 */
	public boolean isColumnEditableOrPlaceholder(int column) {
		return (addedPlaceholderColumn && column == tableValuesModel.getColumnCount())
				|| tableValuesModel.isColumnEditable(column);
	}

	/**
	 * Select a cell.
	 *
	 * @param row the row index to select, or -1 to clear any selection.
	 * @param column the column index to select, or -1 to clear any selection.
	 * @param notifyDelegate Pass true to notify the delegate about a change in selection.
	 * @apiNote The delegate will only be notified if the selection actually changed.
	 */
	public void select(int row, int column, boolean notifyDelegate) {
		boolean changed = selectedRow != row || selectedColumn != column;
		if (!changed) {
			return;
		}
		selectedRow = row;
		selectedColumn = column;

		if (column >= tableValuesModel.getColumnCount()) {
			if (tableValuesModel.allowsAddingColumns() && !addedPlaceholderColumn) {
				addedPlaceholderColumn = true;
			}
		} else if (row >= tableValuesModel.getRowCount()) {
			if (tableValuesModel.isColumnEditable(selectedColumn) && !addedPlaceholderRow) {
				addedPlaceholderRow = true;
			}
		}

		if (notifyDelegate && delegate != null) {
			delegate.focusCell(selectedRow, selectedColumn);
		}
	}

	/**
	 * Eauivalent to <code>select(row, column, true)</code>.
	 *
	 * @param row the row index to select, or -1 to clear any selection.
	 * @param column the column index to select, or -1 to clear any selection.
	 */
	public void select(int row, int column) {
		select(row, column, true);
	}

	/**
	 * Clear (remove) any selection.
	 *
	 * Equivalent to <code>select(-1, -1, true)</code>.
	 */
	public void deselect() {
		select(-1, -1, true);
	}

	/**
	 * Handle a key event and inform the delegate about necessary actions.
	 *
	 * If no cell is currently selected, this method will have no effect.
	 * @param key the key that was pressed.
	 * @apiNote This class requires its delegate for correct operation. If the delegate is not
	 * set when {@link #keyPressed(Key)} is called, nothing will happen.
	 */
	public void keyPressed(Key key) {
		if (selectedRow < 0 || selectedColumn < 0) {
			return; // no selection, no keyboard navigation
		}
		if (delegate == null) {
			return; // see apiNote
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
		if (addedPlaceholderColumn) {
			if (!isCellEmpty(selectedRow, selectedColumn)) {
				commitPendingChanges(); // arrow left in non-empty placeholder column
			}
		}
		if (isFirstColumn(selectedColumn)) {
			return;
		}
		commitPendingChanges();
		addedPlaceholderColumn = false;
		select(selectedRow, findFirstFocusableColumnLeftOf(selectedColumn));
	}

	private void handleArrowRight() {
		if (addedPlaceholderColumn) {
			if (isCellEmpty(selectedRow, selectedColumn)) {
				return; // arrow right in empty placeholder column
			}
			// arrow right in non-empty placeholder column
			commitPendingChanges();
			select(selectedRow, selectedColumn + 1);
			return;
		}
		int column = findFirstFocusableColumnRightOf(selectedColumn);
		if (column == -1) {
			if (tableValuesModel.allowsAddingColumns() && !addedPlaceholderColumn) {
				addedPlaceholderColumn = true;
				column = getMaxColumnIndex() - 1;
			} else {
				return;
			}
		}
		commitPendingChanges();
		select(selectedRow, column);
	}

	private void handleArrowUp() {
		if (isFirstRow(selectedRow)) {
			return;
		}
		commitPendingChanges();
		select(selectedRow - 1, selectedColumn);
	}

	private void handleArrowDown() {
		if (isEditingPlaceholderColumn()) {
			if (selectedRow == tableValuesModel.getRowCount()
					&& isCellEmpty(selectedRow, selectedColumn)) {
				return; // arrow down in empty placeholder cell in last row
			}
		} else if (isLastRow(selectedRow, selectedColumn)) {
			if (tableValuesModel.isColumnEditable(selectedColumn) && !addedPlaceholderRow) {
				addedPlaceholderRow = true;
			} else {
				if (isCellEmpty(selectedRow, selectedColumn)) {
					return; // arrow down in empty placeholder row or non-editable column
				}
			}
		}
		commitPendingChanges();
		select(selectedRow + 1, selectedColumn);
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
			if (tableValuesModel.isColumnEditable(index)) {
				return index;
			}
		}
		return -1;
	}

	private int findFirstFocusableColumnRightOf(int column) {
		for (int index = column + 1; index < getMaxColumnIndex(); index++) {
			if (tableValuesModel.isColumnEditable(index)) {
				return index;
			}
		}
		return -1;
	}

	// note: the returned end index is exclusive!
	private int getMaxRowIndex(int column) {
		if (!tableValuesModel.isColumnEditable(column)) {
			return tableValuesModel.getRowCount();
		}
		return tableValuesModel.getRowCount() + (addedPlaceholderRow ? 1 : 0);
	}

	// note: the returned end index is exclusive!
	private int getMaxColumnIndex() {
		return tableValuesModel.getColumnCount() + (addedPlaceholderColumn ? 1 : 0);
	}

	private boolean isCellEmpty(int row, int column) {
		String cellContent = delegate.getCellEditorContent(row, column);
		return cellContent == null || cellContent.trim().isEmpty();
	}

	private void commitPendingChanges() {
		if (selectedRow == -1 || selectedColumn == -1) {
			return;
		}
		if (addedPlaceholderColumn || tableValuesModel.isColumnEditable(selectedColumn)) {
			String cellContent = delegate.getCellEditorContent(selectedRow, selectedColumn);
			if (cellContent == null) {
				cellContent = "";
			}
			GeoEvaluatable evaluatable = tableValuesView.getEvaluatable(selectedColumn);
			GeoList list = evaluatable instanceof GeoList ? (GeoList) evaluatable : null;
			tableValuesView.getProcessor().processInput(cellContent, list, selectedRow);

			if (selectedRow < tableValuesModel.getRowCount()
					&& selectedColumn < tableValuesModel.getColumnCount()
					&& tableValuesModel.getCellAt(selectedRow, selectedColumn).isErroneous()) {
				delegate.invalidCellContentDetected(selectedRow, selectedColumn);
			}
		}
		addedPlaceholderRow = false;
		addedPlaceholderColumn = false;
	}

	private void hideKeyboard() {
		delegate.hideKeyboard();
	}

	// Test support

	public boolean isEditingPlaceholderColumn() {
		return addedPlaceholderColumn && selectedColumn == getMaxColumnIndex() - 1;
	}

	public boolean isEditingPlaceholderRow() {
		return addedPlaceholderRow && selectedRow == getMaxRowIndex(selectedColumn) - 1;
	}
}

package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for keyboard and touch navigation in the "table of values" view.
 * <p>
 * This controller accepts key press events, and figures out which cell to select in response,
 * if any. Besides keyboard events, it also supports touch navigation (i.e., tapping on cells
 * to change the selection) by means of {@link #select(int, int)}.
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
 * @implNote Currently, the code assumes that the first column is the "x" column,
 * and that it is always present (it may be empty, though).
 */
public final class TableValuesKeyboardNavigationController {

	public enum Key {
		ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_DOWN, RETURN;
	}

	//@NonOwning
	@Weak
	public TableValuesKeyboardNavigationControllerDelegate delegate;

	//@NonOwning
	@Nonnull
	private final TableValues tableValuesView;
	//@NonOwning
	@Nonnull
	private final TableValuesModel tableValuesModel;

	private boolean isReadonly = false;
	private int selectedRow = -1;
	private int selectedColumn = -1;
	private boolean addedPlaceholderColumn = false;
	private boolean addedPlaceholderRow = false;

	/**
	 * Create a new instance.
	 * @param tableValuesView The table of values view.
	 * @param delegate The delegate (can be null here, but must be supplied through
	 * the public writable field before use).
	 */
	public TableValuesKeyboardNavigationController(@Nonnull TableValues tableValuesView,
			TableValuesKeyboardNavigationControllerDelegate delegate) {
		this.tableValuesView = tableValuesView;
		this.tableValuesModel = tableValuesView.getTableValuesModel();
		this.delegate = delegate;
	}

	/**
	 * Prevent selection & navigation.
	 */
	public void setReadonly(boolean readonly) {
		isReadonly = readonly;
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
	 * @apiNote This is not the same as {@link TableValuesModel#getRowCount()}, because that
	 * does not include the placeholder row.
	 */
	public int getNavigableRowsCount() {
		return tableValuesModel.getRowCount()
				+ (!isReadonly && tableValuesModel.hasEditableColumns() ? 1 : 0);
	}

	/**
	 * @return The overall number of "navigable" (reachable) columns in the table, including an
	 * additional placeholder column for inputting new data if the table values model allows
	 * adding columns (@see {@link TableValuesModel#allowsAddingColumns()}.
	 * @apiNote This is not the same as {@link TableValuesModel#getColumnCount()}, because that
	 * does not include the placeholder column.
	 */
	public int getNavigableColumnsCount() {
		return tableValuesModel.getColumnCount()
				+ (!isReadonly && tableValuesModel.allowsAddingColumns() ? 1 : 0);
	}

	/**
	 * @param column column index
	 * @return True if the column at index is editable
	 * (see {@link TableValuesModel#isColumnEditable(int)}) or is a placeholder column (which
	 * is also editable). This information can be used to display non-editable columns in a
	 * different color in the UI, for example.
	 */
	public boolean isColumnEditable(int column) {
		return tableValuesModel.isColumnEditable(column)
				|| (tableValuesModel.allowsAddingColumns()
				&& column == tableValuesModel.getColumnCount());
	}
	
	/**
	 * Select a cell.
	 * @param row the row index to select, or -1 to clear any selection.
	 * @param column the column index to select, or -1 to clear any selection.
	 */
	public void select(int row, int column) {
		if (isReadonly) {
			return;
		}
		boolean changed = selectedRow != row || selectedColumn != column;
		if (!changed) {
			if (delegate != null) {
				// notify delegate so it can re-focus the selected cell after a
				// potential reload (e.g., after receiving a datasetChanged event)
				delegate.refocusCell(selectedRow, selectedColumn);
			}
			return;
		}
		commitPendingChanges();

		int previouslySelectedRow = selectedRow;
		int previouslySelectedColumn = selectedColumn;
		selectedRow = row;
		selectedColumn = column;

		if (column >= tableValuesModel.getColumnCount()) {
			if (tableValuesModel.allowsAddingColumns() && !addedPlaceholderColumn) {
				addedPlaceholderColumn = true;
				selectedColumn = tableValuesModel.getColumnCount();
			}
		} else if (row >= tableValuesModel.getRowCount()) {
			if (isColumnEditable(selectedColumn) && !addedPlaceholderRow) {
				addedPlaceholderRow = true;
				selectedRow = tableValuesModel.getRowCount();
			}
		}

		if (delegate != null) {
			if (selectedRow >= 0 && selectedColumn >= 0) {
				if (previouslySelectedRow >= 0 && previouslySelectedColumn >= 0) {
					delegate.unfocusCell(previouslySelectedRow, previouslySelectedColumn, true);
				}
				delegate.focusCell(selectedRow, selectedColumn);
			} else {
				delegate.unfocusCell(previouslySelectedRow, previouslySelectedColumn, false);
			}
		}
	}
	
	/**
	 * Clear (remove) any selection.
	 *
	 * Equivalent to {@code select(-1, -1)}.
	 */
	public void deselect() {
		select(-1, -1);
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
		if (isFirstColumn(selectedColumn)) {
			// arrow left in first column -> no change in selection
			select(selectedRow, selectedColumn);
			return;
		}
		select(selectedRow, findFirstFocusableColumnLeftOf(selectedColumn));
	}

	private void handleArrowRight() {
		if (isEditingPlaceholderColumn()) {
			if (isCellEmpty(selectedRow, selectedColumn)) {
				select(selectedRow, selectedColumn);
				return; // arrow right in empty placeholder column -> no change in selection
			}
			// arrow right in non-empty placeholder column
			select(selectedRow, selectedColumn + 1);
			return;
		}
		int nextColumn = findFirstFocusableColumnRightOf(selectedColumn);
		if (nextColumn == -1) {
			if (tableValuesModel.allowsAddingColumns() && !addedPlaceholderColumn) {
				addedPlaceholderColumn = true;
				nextColumn = getMaxColumnIndex() - 1;
			} else {
				nextColumn = selectedColumn;
			}
		}
		select(selectedRow, nextColumn);
	}

	private void handleArrowUp() {
		if (isFirstRow(selectedRow)) {
			return;
		}
		select(selectedRow - 1, selectedColumn);
	}

	private void handleArrowDown() {
		if (isEditingPlaceholderColumn()) {
			if (selectedRow == tableValuesModel.getRowCount()
					&& isCellEmpty(selectedRow, selectedColumn)) {
				// arrow down in empty cell in placeholder column in last row
				// -> no change in selection
				select(selectedRow, selectedColumn);
				return;
			}
		} else if (addedPlaceholderRow && isCellEmpty(selectedRow, selectedColumn)) {
			// arrow down in empty placeholder row
			// -> no change in selection
			select(selectedRow, selectedColumn);
			return;
		}
		select(selectedRow + 1, selectedColumn);
	}

	private boolean isFirstRow(int row) {
		return row == 0;
	}

	private boolean isFirstColumn(int column) {
		return column == 0;
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
		return cellContent == null || StringUtil.isTrimmedEmpty(cellContent);
	}

	private void commitPendingChanges() {
		if (selectedRow == -1 || selectedColumn == -1) {
			return;
		}
		if (isEditingPlaceholderColumn() || tableValuesModel.isColumnEditable(selectedColumn)) {
			String cellContent = delegate.getCellEditorContent(selectedRow, selectedColumn);
			if (cellContent == null) {
				cellContent = "";
			}
			boolean cellContentChanged = cellContent.length() > 0;
			if (selectedRow < tableValuesModel.getRowCount()
					&& selectedColumn < tableValuesModel.getColumnCount()) {
				TableValuesCell cell = tableValuesModel.getCellAt(selectedRow, selectedColumn);
				cellContentChanged = cellContent.compareTo(cell.getInput()) != 0;
			}
			if (cellContentChanged) {
				GeoEvaluatable evaluatable = tableValuesView.getEvaluatable(selectedColumn);
				GeoList list = evaluatable instanceof GeoList ? (GeoList) evaluatable : null;
				tableValuesView.getProcessor().processInput(cellContent, list, selectedRow);

				if (selectedRow < tableValuesModel.getRowCount()
						&& selectedColumn < tableValuesModel.getColumnCount()
						&& tableValuesModel.getCellAt(selectedRow, selectedColumn).isErroneous()) {
					delegate.invalidCellContentDetected(selectedRow, selectedColumn);
				}
			}
		}
		addedPlaceholderRow = false;
		addedPlaceholderColumn = false;
	}

	// Test support

	public boolean isEditingPlaceholderColumn() {
		return addedPlaceholderColumn && selectedColumn == getMaxColumnIndex() - 1;
	}

	public boolean isEditingPlaceholderRow() {
		return addedPlaceholderRow && selectedRow == getMaxRowIndex(selectedColumn) - 1;
	}
}

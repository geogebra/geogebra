package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;

public class ContextMenuItems {
	static final int HEADER_INDEX = -1;
	private final TabularData tabularData;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;
	private final TableLayout layout;
	private @CheckForNull UndoManager undoManager;

	/**
	 * @param tabularData {@link TabularData}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut {@link CopyPasteCutTabularData}
	 * @param layout {@link TableLayout}
	 */
	public ContextMenuItems(TabularData tabularData,
			SpreadsheetSelectionController selectionController,
			CopyPasteCutTabularData copyPasteCut, TableLayout layout) {
		this.selectionController = selectionController;
		this.tabularData = tabularData;
		this.copyPasteCut = copyPasteCut;
		this.layout = layout;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	/**
	 * Gets the context menu items for the specific cell/row/column
	 * @param row of the cell.
	 * @param column of the cell.
	 * @return map of the menu key and its action.
	 */
	public List<ContextMenuItem> get(int row, int column) {
		if (row == HEADER_INDEX && column == HEADER_INDEX) {
			return tableItems(row, column);
		} else if (row == HEADER_INDEX) {
			return columnItems(column);
		} else if (column == HEADER_INDEX) {
			return rowItems(row);
		}
		return cellItems(row, column);
	}

	private List<ContextMenuItem> tableItems(int row, int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(row, column))
		);
	}

	private List<ContextMenuItem> cellItems(int row, int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(row, column)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_ROW_ABOVE,
						() -> insertRowAt(row, false)),
				new ContextMenuItem(Identifier.INSERT_ROW_BELOW,
						() -> insertRowAt(row + 1, true)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_LEFT,
						() -> insertColumnAt(column, false)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_RIGHT,
						() -> insertColumnAt(column + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_ROW, () -> deleteRowAt(row)),
				new ContextMenuItem(Identifier.DELETE_COLUMN,
						() -> deleteColumnAt(column))
		);
	}

	private void pasteCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.paste(row, column);
		} else {
			for (Selection selection: selections) {
				copyPasteCut.paste(selection.getRange());
			}
		}
	}

	private void copyCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.copyDeep(new TabularRange(row, row, column, column));
		} else {
			for (Selection selection: selections) {
				copyPasteCut.copyDeep(selection.getRange());
			}
		}
	}

	private void cutCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.cut(new TabularRange(row, row, column, column));
		} else {
			for (Selection selection: selections) {
				copyPasteCut.cut(selection.getRange());
			}
		}
	}

	/*private void deleteCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.setContent(row, column, null);
		} else {
			selections.stream().forEach(selection -> deleteCells(selection.getRange()));
		}
	}

	private void deleteCells(TabularRange range) {
		for (int row = range.getFromRow(); row < range.getToRow(); row++) {
			for (int column = range.getFromColumn(); column < range.getToRow(); column++) {
				tabularData.setContent(row, column, null);
			}
		}
	}*/

	private List<ContextMenuItem> rowItems(int row) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> {}),
				new ContextMenuItem(Identifier.COPY, () -> {}),
				new ContextMenuItem(Identifier.PASTE, () -> {}),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_ROW_ABOVE,
						() -> insertRowAt(row, false)),
				new ContextMenuItem(Identifier.INSERT_ROW_BELOW,
						() -> insertRowAt(row + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_ROW, () -> deleteRowAt(row))
		);
	}

	private void deleteRowAt(int row) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			deleteRowAt(row, row);
		} else {
			selections.stream().forEach(selection -> deleteRowAt(
					selection.getRange().getFromRow(), selection.getRange().getToRow()));
		}
	}

	private void deleteRowAt(int fromRow, int toRow) {
		for (int row = fromRow; row < toRow + 1; row++) {
			tabularData.deleteRowAt(fromRow);
		}
		if (layout != null) {
			resizeRemainingRowsAscending(toRow);
		}
		storeUndoInfo();
	}

	private List<ContextMenuItem> columnItems(int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> {}),
				new ContextMenuItem(Identifier.COPY, () -> {}),
				new ContextMenuItem(Identifier.PASTE, () -> {}),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_COLUMN_LEFT,
						() -> insertColumnAt(column, false)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_RIGHT,
						() -> insertColumnAt(column + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_COLUMN,
						() -> deleteColumnAt(column))
				);
	}

	private void deleteColumnAt(int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			deleteColumnAt(column, column);
		} else {
			selections.stream().forEach(selection -> deleteColumnAt(
					selection.getRange().getFromColumn(), selection.getRange().getToColumn()));
			}
		}

	private void deleteColumnAt(int fromColumn, int toColumn) {
		for (int column = fromColumn; column < toColumn + 1; column++) {
			tabularData.deleteColumnAt(fromColumn);
		}
		if (layout != null) {
			resizeRemainingColumnsAscending(toColumn);
		}
		storeUndoInfo();
	}

	/**
	 * Inserts a column at a given index
	 * @param column Index of where to insert the column
	 * @param right Whether the column is being inserted right of the currently selected column
	 */
	private void insertColumnAt(int column, boolean right) {
		tabularData.insertColumnAt(column);
		Selection lastSelection = selectionController.getLastSelection();
		if (right && lastSelection != null) {
			selectionController.setSelections(lastSelection.getRight(
					tabularData.numberOfColumns(), false));
		}
		if (layout != null) {
			resizeRemainingColumnsDescending(right ? column - 1 : column);
		}
		storeUndoInfo();
	}

	/**
	 * Inserts a row at a given index
	 * @param row Index of where to insert the row
	 * @param below Whether the row is being inserted below the currently selected row
	 */
	private void insertRowAt(int row, boolean below) {
		tabularData.insertRowAt(row);
		Selection lastSelection = selectionController.getLastSelection();
		if (below && lastSelection != null) {
			selectionController.setSelections(lastSelection.getBottom(
					tabularData.numberOfRows(), false));
		}
		if (layout != null) {
			resizeRemainingRowsDescending(below ? row - 1 : row);
		}
		storeUndoInfo();
	}

	/**
	 * After a row has been deleted, the reamining rows (i.e. the ones succeeding the deleted row)
	 * need to be resized. In this scenario, row 10 applies the hieght of row 11, row 11 applies
	 * the height of row 12, etc.
	 * @param resizeFrom Index of where to start resizing the remaining rows
	 */
	private void resizeRemainingRowsAscending(int resizeFrom) {
		int numberOfRows = tabularData.numberOfRows();
		for (int row = resizeFrom; row < numberOfRows - 1; row++) {
			layout.setHeightForRows(layout.getHeight(row + 1), row, row);
		}
		layout.setHeightForRows(layout.DEFAUL_CELL_HEIGHT, numberOfRows - 1, numberOfRows - 1);
	}

	/**
	 * Same as {@link #resizeRemainingRowsAscending(int)}, but for columns
	 * @param resizeFrom Index of where to start resizing the remaining columns
	 */
	private void resizeRemainingColumnsAscending(int resizeFrom) {
		int numberOfColumns = tabularData.numberOfColumns();
		for (int column = resizeFrom; column < numberOfColumns - 1; column++) {
			layout.setWidthForColumns(layout.getWidth(column + 1), column, column);
		}
		layout.setWidthForColumns(layout.DEFAULT_CELL_WIDTH,
				numberOfColumns - 1, numberOfColumns - 1);
	}

	/**
	 * After a row has been inserted, row 100 applies the size of row 99, row 99 applies the size
	 * of row 98, and so on. This is basically the direct counterpart to the operation that is
	 * performed when a row gets deleted.
	 * @param resizeUntil Until which row index the resizing should be performed
	 */
	private void resizeRemainingRowsDescending(int resizeUntil) {
		for (int row = tabularData.numberOfRows() - 1; row > resizeUntil; row--) {
			layout.setHeightForRows(layout.getHeight(row - 1), row, row);
		}
	}

	/**
	 * Same as {@link #resizeRemainingRowsDescending(int)}, but for columns
	 * @param resizeUntil Until which column index the resizing should be performed
	 */
	private void resizeRemainingColumnsDescending(int resizeUntil) {
		for (int column = tabularData.numberOfColumns() - 1; column > resizeUntil; column--) {
			layout.setWidthForColumns(layout.getWidth(column - 1), column, column);
		}
	}

	private void storeUndoInfo() {
		if (undoManager != null) {
			undoManager.storeUndoInfo();
		}
	}
}
package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;

public class ContextMenuItems {
	static final int HEADER_INDEX = -1;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param spreadsheetController {@link SpreadsheetController}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut {@link CopyPasteCutTabularData}
	 */
	public ContextMenuItems(SpreadsheetController spreadsheetController,
			SpreadsheetSelectionController selectionController,
			CopyPasteCutTabularData copyPasteCut) {
		this.spreadsheetController = spreadsheetController;
		this.selectionController = selectionController;
		this.copyPasteCut = copyPasteCut;
	}

	/**
	 * Gets the context menu items for the specific <b>single</b> cell / row / column
	 * @param row of the cell.
	 * @param column of the cell.
	 * @return map of the menu key and its action.
	 */
	public List<ContextMenuItem> get(int row, int column) {
		return get(row, row, column, column);
	}

	/**
	 * Gets the context menu items for the specific <b>multiple</b> cells / rows / columns
	 * @param fromRow Index of the uppermost row
	 * @param toRow Index of the bottommost row
	 * @param fromCol Index of the leftmost column
	 * @param toCol Index of the rightmost column
	 * @return map of the menu key and its action.
	 */
	public List<ContextMenuItem> get(int fromRow, int toRow, int fromCol, int toCol) {
		if (allCellsSelectionClicked(fromRow, toRow, fromCol, toCol)) {
			return tableItems(fromRow, fromCol);
		} else if (onlyColumnsSelected(fromRow, toRow)) {
			return columnItems(fromCol);
		} else if (onlyRowsSelected(fromCol, toCol)) {
			return rowItems(fromRow);
		}
		return cellItems(fromRow, toRow, fromCol, toCol);
	}

	private boolean allCellsSelectionClicked(int fromRow, int toRow, int fromCol, int toCol) {
		return fromRow == HEADER_INDEX && toRow == HEADER_INDEX
				&& fromCol == HEADER_INDEX && toCol == HEADER_INDEX;
	}

	private boolean onlyColumnsSelected(int fromRow, int toRow) {
		return fromRow == HEADER_INDEX && toRow == HEADER_INDEX;
	}

	private boolean onlyRowsSelected(int fromCol, int toCol) {
		return fromCol == HEADER_INDEX && toCol == HEADER_INDEX;
	}

	private List<ContextMenuItem> tableItems(int row, int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(row, column))
		);
	}

	private List<ContextMenuItem> cellItems(int fromRow, int toRow, int fromCol, int toCol) {
		return Arrays.asList(
				new ContextMenuItem(Identifier.CUT, () -> cutCells(1, 1)),
				new ContextMenuItem(Identifier.COPY, () -> copyCells(1, 1)),
				new ContextMenuItem(Identifier.PASTE, () -> pasteCells(1, 1)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.INSERT_ROW_ABOVE,
						() -> insertRowAt(fromRow, false)),
				new ContextMenuItem(Identifier.INSERT_ROW_BELOW,
						() -> insertRowAt(toRow + 1, true)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_LEFT,
						() -> insertColumnAt(fromCol, false)),
				new ContextMenuItem(Identifier.INSERT_COLUMN_RIGHT,
						() -> insertColumnAt(toCol + 1, true)),
				new ContextMenuItem(Identifier.DIVIDER),
				new ContextMenuItem(Identifier.DELETE_ROW, () -> deleteRowAt(fromRow)),
				new ContextMenuItem(Identifier.DELETE_COLUMN,
						() -> deleteColumnAt(fromCol))
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

	private void deleteRowAt(int row) {
		spreadsheetController.deleteRowAt(row);
	}

	private void deleteColumnAt(int column) {
		spreadsheetController.deleteColumnAt(column);
	}

	private void insertColumnAt(int column, boolean right) {
		spreadsheetController.insertColumnAt(column, right);
	}

	private void insertRowAt(int row, boolean below) {
		spreadsheetController.insertRowAt(row, below);
	}
}
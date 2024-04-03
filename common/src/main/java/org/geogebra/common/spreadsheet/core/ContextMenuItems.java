package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;

public class ContextMenuItems {
	static final int HEADER_INDEX = -1;
	private final TabularData tabularData;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;
	private final TableLayout layout;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param spreadsheetController {@link SpreadsheetController}
	 * @param tabularData {@link TabularData}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut {@link CopyPasteCutTabularData}
	 * @param layout {@link TableLayout}
	 */
	public ContextMenuItems(SpreadsheetController spreadsheetController, TabularData tabularData,
			SpreadsheetSelectionController selectionController,
			CopyPasteCutTabularData copyPasteCut, TableLayout layout) {
		this.spreadsheetController = spreadsheetController;
		this.selectionController = selectionController;
		this.tabularData = tabularData;
		this.copyPasteCut = copyPasteCut;
		this.layout = layout;
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
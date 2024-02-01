package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifer;

public class ContextMenuItems {
	static final int HEADER_INDEX = -1;
	private final TabularData tabularData;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;

	/**
	 * @param tabularData {@link TabularData}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut {@link CopyPasteCutTabularData}
	 */
	public ContextMenuItems(TabularData tabularData,
			SpreadsheetSelectionController selectionController,
			CopyPasteCutTabularData copyPasteCut) {
		this.selectionController = selectionController;
		this.tabularData = tabularData;
		this.copyPasteCut = copyPasteCut;
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
				new ContextMenuItem(Identifer.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifer.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifer.PASTE, () -> pasteCells(row, column))
		);
	}

	private List<ContextMenuItem> cellItems(int row, int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifer.CUT, () -> cutCells(row, column)),
				new ContextMenuItem(Identifer.COPY, () -> copyCells(row, column)),
				new ContextMenuItem(Identifer.PASTE, () -> pasteCells(row, column)),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.INSERT_ROW_ABOVE,
						() -> tabularData.insertRowAt(row)),
				new ContextMenuItem(Identifer.INSERT_ROW_BELOW,
						() -> tabularData.insertRowAt(row + 1)),
				new ContextMenuItem(Identifer.INSERT_COLUMN_LEFT,
						() -> tabularData.insertColumnAt(column)),
				new ContextMenuItem(Identifer.INSERT_COLUMN_RIGHT,
						() -> tabularData.insertColumnAt(column + 1)),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.DELETE_ROW, () -> deleteRowAt(row)),
				new ContextMenuItem(Identifer.DELETE_COLUMN,
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

	private void deleteCells(int row, int column) {
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
	}

	private List<ContextMenuItem> rowItems(int row) {
		return Arrays.asList(
				new ContextMenuItem(Identifer.CUT, () -> {}),
				new ContextMenuItem(Identifer.COPY, () -> {}),
				new ContextMenuItem(Identifer.PASTE, () -> {}),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.INSERT_ROW_ABOVE,
						() -> tabularData.insertRowAt(row)),
				new ContextMenuItem(Identifer.INSERT_ROW_BELOW,
						() -> tabularData.insertRowAt(row + 1)),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.DELETE_ROW, () -> deleteRowAt(row))
		);
	}

	private void deleteRowAt(int row) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.deleteRowAt(row);
		} else {
			selections.stream().filter(selection -> selection.isRowOnly())
					.forEach(selection -> deleteRowAt(selection.getRange().getFromRow(),
							selection.getRange().getToRow()));
			}
		}

	private void deleteRowAt(int fromRow, int toRow) {
		for (int row = fromRow; row < toRow + 1; row++) {
			tabularData.deleteRowAt(fromRow);
		}
	}

	private List<ContextMenuItem> columnItems(int column) {
		return Arrays.asList(
				new ContextMenuItem(Identifer.CUT, () -> {}),
				new ContextMenuItem(Identifer.COPY, () -> {}),
				new ContextMenuItem(Identifer.PASTE, () -> {}),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.INSERT_COLUMN_LEFT,
						() -> tabularData.insertColumnAt(column)),
				new ContextMenuItem(Identifer.INSERT_COLUMN_RIGHT,
						() -> tabularData.insertColumnAt(column + 1)),
				new ContextMenuItem(Identifer.DIVIDER),
				new ContextMenuItem(Identifer.DELETE_COLUMN,
						() -> deleteColumnAt(column))
				);
	}

	private void deleteColumnAt(int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.deleteColumnAt(column);
		} else {
			selections.stream().filter(selection -> selection.isColumnOnly())
					.forEach(selection -> deleteColumnAt(selection.getRange().getFromColumn(),
							selection.getRange().getToColumn()));
			}
		}

	private void deleteColumnAt(int fromColumn, int toColumn) {
		for (int column = fromColumn; column < toColumn + 1; column++) {
			tabularData.deleteColumnAt(fromColumn);
		}
	}
}
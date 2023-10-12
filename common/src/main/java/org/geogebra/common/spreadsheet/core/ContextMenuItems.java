package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextMenuItems {

	static final int HEADER_INDEX = -1;
	private final TabularData tabularData;
	private final CopyPasteCutTabularData copyPasteCut;
	private final SpreadsheetSelectionController selectionController;

	/**
	 * @param tabularData {@link TabularData}
	 * @param selectionController {@link SpreadsheetSelectionController}
	 * @param copyPasteCut
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
	public Map<String, Runnable> get(int row, int column) {
		if (row == HEADER_INDEX) {
			return columnItems(column);
		} else if (column == HEADER_INDEX) {
			return rowItems(row);
		}
		return cellItems(row, column);
	}

	private Map<String, Runnable> cellItems(int row, int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("Delete", () -> deleteCells(row, column));
		actions.put("Copy", () -> copyCells(row, column));
		actions.put("Paste", () -> {});
		actions.put("Cut", () -> copyPasteCut.cut(new TabularRange(row, column, row, column),
				""));
		return actions;
	}

	private void copyCells(int row, int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			copyPasteCut.copy(new TabularRange(row, column, row, column), "");
		} else {
			for (Selection selection: selections) {
				copyPasteCut.copy(selection.getRange(), "");
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
		for (int row = range.fromRow; row < range.toRow; row++) {
			for (int column = range.fromCol; column < range.toCol; column++) {
				tabularData.setContent(row, column, null);
			}

		}
	}

	private Map<String, Runnable> rowItems(int row) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("ContextMenu.insertRowAbove", () -> tabularData.insertRowAt(row));
		actions.put("ContextMenu.insertRowBelow", () -> tabularData.insertRowAt(row + 1));
		actions.put("ContextMenu.deleteRow", () -> deleteRowAt(row));
		return actions;
	}

	private void deleteRowAt(int row) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.deleteRowAt(row);
		} else {
			selections.stream().filter(selection -> selection.isRowOnly())
					.forEach(selection -> deleteRowAt(selection.getRange().fromRow,
							selection.getRange().toRow));
			}
		}

	private void deleteRowAt(int fromRow, int toRow) {
		for (int row = fromRow; row < toRow + 1; row++) {
			tabularData.deleteRowAt(fromRow);
		}
	}

	private Map<String, Runnable> columnItems(int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("ContextMenu.insertColumnLeft", () -> tabularData.insertColumnAt(column));
		actions.put("ContextMenu.insertColumnRight", () -> tabularData.insertColumnAt(column + 1));
		actions.put("ContextMenu.deleteColumn", () -> deleteColumnAt(column));
		return actions;
	}

	private void deleteColumnAt(int column) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.deleteColumnAt(column);
		} else {
			selections.stream().filter(selection -> selection.isColumnOnly())
					.forEach(selection -> deleteColumnAt(selection.getRange().fromCol,
							selection.getRange().toCol));
			}
		}

	private void deleteColumnAt(int fromColumn, int toColumn) {
		for (int column = fromColumn; column < toColumn + 1; column++) {
			tabularData.deleteColumnAt(fromColumn);
		}
	}
}
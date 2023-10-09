package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.util.debug.Log;

public class ContextMenuItems {

	private final TabularData tabularData;
	private final SpreadsheetSelectionController selectionController;

	public ContextMenuItems(TabularData tabularData,
			SpreadsheetSelectionController selectionController) {
		this.selectionController = selectionController;
		this.tabularData = tabularData;
	}

	public Map<String, Runnable> get(int column, int row) {
		Log.debug("col: " + column + " row: " + row);
		if (column == -1) {
			return rowItems(row);
		} else if (row == -1) {
			return columnItems(column);
		}
		return cellItems(column, row);
	}

	private Map<String, Runnable> cellItems(int column, int row) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("Delete", () -> tabularData.deleteColumnAt(column));
		actions.put("Insert", () -> tabularData.insertColumnAt(column));
		actions.put("Copy", () -> {tabularData.copy(column, row, column, row);});
		actions.put("Paste", () -> {});
		actions.put("Cut", () -> {tabularData.cut(column, row, column, row);});
		return actions;
	}

	private Map<String, Runnable> rowItems(int row) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("ContextMenu.insertRowAbove", () -> {tabularData.insertRowAt(row);});
		actions.put("ContextMenu.insertRowBelow", () -> {tabularData.insertRowAt(row + 1);});
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
		for (int row = fromRow; row < toRow; row++) {
			tabularData.deleteRowAt(row);
		}
	}

	private Map<String, Runnable> columnItems(int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("ContextMenu.insertColumnLeft", () -> {tabularData.insertColumnAt(column);});
		actions.put("ContextMenu.insertColumnRight", () -> {tabularData.insertColumnAt(column + 1);});
		actions.put("ContextMenu.deleteColumn", () -> {deleteColumnAt(column);});
		return actions;
	}

	private void deleteColumnAt(int row) {
		List<Selection> selections = selectionController.selections();
		if (selections.isEmpty()) {
			tabularData.deleteRowAt(row);
		} else {
			selections.stream().filter(selection -> selection.isRowOnly())
					.forEach(selection -> deleteColumnAt(selection.getRange().fromCol,
							selection.getRange().toCol));
			}
		}

	private void deleteColumnAt(int fromRow, int toRow) {
		for (int row = fromRow; row < toRow; row++) {
			tabularData.deleteRowAt(row);
		}
	}
}

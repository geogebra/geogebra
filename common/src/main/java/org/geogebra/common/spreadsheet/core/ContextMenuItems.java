package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.Map;

public class ContextMenuItems {

	private final TabularData tabularData;

	public ContextMenuItems(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	Map<String, Runnable> get(int column, int row) {
		if (column < 0) {
			return rowItems(column);
		} else if (row < 0) {
			return columnItems(row);
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
		actions.put("ContextMenu.deleteRow", () -> {tabularData.deleteRowAt(row);});
		return actions;
	}

	private Map<String, Runnable> columnItems(int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("ContextMenu.insertColumnLeft", () -> {tabularData.insertColumnAt(column);});
		actions.put("ContextMenu.insertColumnRight", () -> {tabularData.insertColumnAt(column + 1);});
		actions.put("ContextMenu.deleteColumn", () -> {tabularData.deleteColumnAt(column);});
		return actions;
	}
}

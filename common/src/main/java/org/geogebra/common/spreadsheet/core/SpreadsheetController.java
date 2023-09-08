package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularSelection {

	private SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
	}

	// - TabularData

	public Object contentAt(int row, int column) {
		return tabularData.contentAt(row, column);
	}

	// - TabularSelection

	@Override
	public void clearSelection() {
		selectionController.clearSelection();
	}

	@Override
	public void selectRow(int row) {
		selectionController.selectRow(row, false);
	}

	@Override
	public void selectColumn(int column) {
		selectionController.selectColumn(column, false);
	}

	@Override
	public void select(Selection selection, boolean extend) {
		selectionController.select(selection, extend);
	}

	@Override
	public void selectAll() {
		selectionController.selectAll();
	}

	public List<Selection> getSelection() {
		return selectionController.selections();
	}

	boolean isSelected(int row, int column) {
		return selectionController.selections().stream().anyMatch(s -> s.contains(row, column));
	}

	public String getColumnName(int column) {
		return tabularData.getColumnName(column);
	}

	public Map<String, Runnable> getContextMenu(int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("InsertColumn", () -> tabularData.insertColumnAt(column));
		actions.put("DeleteColumn", () -> tabularData.deleteColumnAt(column));
		return actions;
	}
}

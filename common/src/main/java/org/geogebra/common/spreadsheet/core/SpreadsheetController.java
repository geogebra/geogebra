package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularData, TabularSelection {

	SpreadsheetSelectionController selectionController = new SpreadsheetSelectionController();
	Map<Integer, Map<Integer, Object>> data = new HashMap<>();

	public SpreadsheetController(int initialNumberOfRows, int initialNumberOfColumns) {
		// not needed
	}

	// - TabularData

	@Override
	public void reset(int rows, int columns) {
	}

	@Override
	public int numberOfRows() {
		return 0;
	}

	@Override
	public int numberOfColumns() {
		return 0;
	}

	@Override
	public void appendRows(int rows) {
	}

	@Override
	public void insertRowAt(int row) {
	}

	@Override
	public void deleteRowAt(int row) {
	}

	@Override
	public void appendColumns(int columns) {
	}

	@Override
	public void insertColumnAt(int column) {
	}

	@Override
	public void deleteColumnAt(int column) {
	}

	@Override
	public void setContent(int row, int column, Object content) {
		data.computeIfAbsent(row, ignore -> new HashMap<Integer, Object>()).put(column, content);
	}

	@Override
	public Object contentAt(int row, int column) {
		if (data.get(row) == null) {
			return null;
		}
		return data.get(row).get(column);
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

	public boolean isSelected(int row, int column) {
		return selectionController.selections().stream().anyMatch(s -> s.contains(row, column));
	}
}

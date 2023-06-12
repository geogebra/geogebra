package org.geogebra.common.spreadsheet.core;

import java.util.List;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularData, TabularSelection {

	SpreadsheetSelectionController selections;

	public SpreadsheetController(int initialNumberOfRows, int initialNumberOfColumns) {
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
	}

	@Override
	public Object contentAt(int row, int column) {
		return null;
	}

	// - TabularSelection

	@Override
	public void clearSelection() {
		selections.clearSelection();
	}

	@Override
	public void selectRow(int row) {
		selections.selectRow(row, false);
	}

	@Override
	public void selectColumn(int column) {
		selections.selectColumn(column, false);
	}

	@Override
	public void select(Selection selection, boolean extend) {
		selections.select(selection, extend);
	}

	@Override
	public void selectAll() {
		selections.selectAll();
	}

	public List<Selection> getSelection() {
		return selections.selections();
	}

	public boolean isSelected(int x, int y) {
		return selections.selections().stream().anyMatch(s -> s.contains(x, y));
	}
}

package org.geogebra.common.spreadsheet;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularData, TabularSelection {

	private Selection selection;

//	private SparseTable<Cell> cells = new SparseTable<Cell>(Cell.class);

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
	}

	@Override
	public void selectRow(int row) {
	}

	@Override
	public void selectColumn(int column) {
	}

	@Override
	public void select(Selection selection) {
	}

	@Override
	public void selectAll() {
	}
}

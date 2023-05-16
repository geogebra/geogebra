package org.geogebra.common.spreadsheet;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.Kernel;

/**
 * A spreadsheet (of arbitrary size).
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularData, TabularSelection {

	private SpreadsheetController controller;
	private TableLayout layout;

	private boolean needsRedraw = true;

	private SpreadsheetStyle style = new SpreadsheetStyle();

	public Spreadsheet(int initialNumberOfRows, int initialNumberOfColumns, Kernel kernel) {
		controller = new SpreadsheetController(initialNumberOfRows, initialNumberOfColumns);
		layout = new TableLayout(initialNumberOfRows, initialNumberOfColumns, 20, 40);
	}

	// editing & validation

	void beginEditing(int row, int col) {
		// TODO show editor
	}

	// TODO validation

	void endEditing() { }

	// layout

	// styling

	// drawing
	void draw(GGraphics2D graphics) {
		if (!needsRedraw) {
			return;
		}
		needsRedraw = false;
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

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
	public void appendRows(int numberOfRows) {
		controller.appendRows(numberOfRows);
		needsRedraw = true;
	}

	@Override
	public void insertRowAt(int row) {
		controller.insertRowAt(row);
		needsRedraw = true;
	}

	@Override
	public void deleteRowAt(int row) {
		controller.deleteRowAt(row);
		needsRedraw = true;
	}

	@Override
	public void appendColumns(int numberOfColumns) {
		controller.appendColumns(numberOfColumns);
		needsRedraw = true;
	}

	@Override
	public void insertColumnAt(int column) {
		controller.insertColumnAt(column);
		needsRedraw = true;
	}

	@Override
	public void deleteColumnAt(int column) {
		controller.deleteColumnAt(column);
		needsRedraw = true;
	}

	@Override
	public void setContent(int row, int column, Object content) {
		controller.setContent(row, column, content);
		needsRedraw = true;
	}

	@Override
	public Object contentAt(int row, int column) {
		return controller.contentAt(row, column);
	}

	// - TabularSelection

	@Override
	public void clearSelection() {
		controller.clearSelection();
		needsRedraw = true;
	}

	@Override
	public void selectRow(int row) {
		controller.selectRow(row);
		needsRedraw = true;
	}

	@Override
	public void selectColumn(int column) {
		controller.selectColumn(column);
		needsRedraw = true;
	}

	@Override
	public void select(Selection selection) {
		controller.select(selection);
		needsRedraw = true;
	}

	@Override
	public void selectAll() {
		controller.selectAll();
		needsRedraw = true;
	}
}

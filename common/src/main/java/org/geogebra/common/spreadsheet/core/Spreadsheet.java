package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size).
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularData, TabularSelection {

	private final SpreadsheetController controller;
	private final TableLayout layout;

	private boolean needsRedraw = true;

	private SpreadsheetStyle style = new SpreadsheetStyle();
	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;
	private SpreadsheetControlsDelegate delegate;

	public Spreadsheet(int initialNumberOfRows, int initialNumberOfColumns, SpreadsheetDataConverter converter) {
		renderer = new SpreadsheetRenderer(this, converter);
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
	public void draw(GGraphics2D graphics) {
		if (!needsRedraw) {
			return;
		}
		renderer.draw(graphics, viewport);
		for (Selection selection: controller.getSelection()) {
			renderer.drawSelection(selection.getRange(), graphics, viewport);
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
		return layout.numberOfRows();
	}

	@Override
	public int numberOfColumns() {
		return layout.numberOfColumns();
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
	public void select(Selection selection, boolean extend) {
		controller.select(selection, extend);
		needsRedraw = true;
	}

	@Override
	public void selectAll() {
		controller.selectAll();
		needsRedraw = true;
	}

	public TableLayout getLayout() {
		return layout;
	}

	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
		needsRedraw = true;
	}

	public boolean isSelected(int row, int column) {
		return controller.isSelected(row, column);
	}

	private void showCellEditor(int row, int column) {
		if (delegate != null) {
			delegate.showCellEditor(layout.getBounds(row, column), controller.contentAt(row, column));
		}
	}

	private void hideCellEditor() {
		if (delegate != null) {
			delegate.hideCellEditor();
		}
	}


	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers
	 */
	public void handlePointerUp(int x, int y, int modifiers) {
		select(new Selection(SelectionType.CELLS, new TabularRange(layout.findRow(y),
				layout.findRow(y), layout.findColumn(x), layout.findColumn(x))), modifiers > 0);

	}

	public void handlePointerDown(int row, int column, int modifiers) {
		hideCellEditor();
		if (isSelected(row, column)) {
			showCellEditor(row, column);
		}
		// start selecting
	}

	public void handlePointerMove(int x, int y, int modifiers) {
		// extend selection
	}

	public void handleKeyPressed(int keyCode, int modifiers) {
		// extend selection
	}

	public SpreadsheetController getController() {
		return controller;
	}
}

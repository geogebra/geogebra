package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size).
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularSelection, TabularDataChangeListener {

	private final SpreadsheetController controller;
	private final TableLayout layout;

	private boolean needsRedraw = true;

	private SpreadsheetStyle style = new SpreadsheetStyle();
	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;
	private SpreadsheetControlsDelegate controlsDelegate;

	public Spreadsheet(TabularData tabularData, CellRendererFactory rendererFactory) {
		controller = new SpreadsheetController(tabularData);
		layout = new TableLayout(tabularData.numberOfRows(), tabularData.numberOfColumns(), 20, 40);
		renderer = new SpreadsheetRenderer(layout, rendererFactory);
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
		drawCells(graphics, viewport);
		for (Selection selection: controller.getSelection()) {
			renderer.drawSelection(selection.getRange(), graphics, viewport, layout);
		}
		needsRedraw = false;
	}

	void drawCells(GGraphics2D graphics, Rectangle rectangle) {
		TableLayout.Portion portion =
				layout.getLayoutIntersecting(rectangle);
		graphics.translate(-rectangle.getMinX(), -rectangle.getMinY());
		for (int column = 0; column < portion.numberOfColumns; column++) {
			renderer.drawColumnHeader(column, graphics);
		}
		for (int row = 0; row < portion.numberOfRows; row++) {
			renderer.drawRowHeader(row, graphics);
		}
		for (int column = 0; column < portion.numberOfColumns; column++) {
			for (int row = 0; row < portion.numberOfRows; row++) {
				renderer.drawCell(row + portion.fromRow, column + portion.fromColumn, graphics,
						controller.contentAt(row + portion.fromRow, column + portion.fromColumn));
			}
		}
		graphics.translate(rectangle.getMinX(), rectangle.getMinY());
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

	// - TabularData

	/** TODO move out */
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

	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
		needsRedraw = true;
	}

	private boolean isSelected(int row, int column) {
		return controller.isSelected(row, column);
	}

	private void showCellEditor(int row, int column) {
		if (controlsDelegate != null) {
			controlsDelegate.showCellEditor(layout.getBounds(row, column), controller.contentAt(row, column));
		}
	}

	private void hideCellEditor() {
		if (controlsDelegate != null) {
			controlsDelegate.hideCellEditor();
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

	@Override
	public void update(int row, int column) {
		renderer.invalidate(row, column);
	}

	public void setWidthForColumns(double width, int[] columnIndices) {
		layout.setWidthForColumns(width, columnIndices);
	}

	public void setHeightForRows(double height, int... rowIndices) {
		layout.setHeightForRows(height, rowIndices);
	}
}

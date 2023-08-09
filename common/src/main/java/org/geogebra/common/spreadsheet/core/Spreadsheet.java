package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size).
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularDataChangeListener {

	private final SpreadsheetController controller;
	private final TableLayout layout;

	private boolean needsRedraw = true;

	private SpreadsheetStyle style = new SpreadsheetStyle();
	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;
	private SpreadsheetControlsDelegate controlsDelegate;

	/**
	 * @param tabularData data source
	 * @param rendererFactory converts custom data type to rendable objects
	 */
	public Spreadsheet(TabularData tabularData, CellRenderableFactory rendererFactory) {
		controller = new SpreadsheetController(tabularData);
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), 20, 40);
		renderer = new SpreadsheetRenderer(layout, rendererFactory);
	}

	// layout

	// styling

	// drawing

	/**
	 * Draws current viewport of the spreadsheet
	 * @param graphics graphics to draw to
	 */
	public void draw(GGraphics2D graphics) {
		if (!needsRedraw) {
			return;
		}
		graphics.setColor(style.getTextColor());
		drawCells(graphics, viewport);
		for (Selection selection: controller.getSelection()) {
			renderer.drawSelection(selection.getRange(), graphics, viewport, layout);
		}
		needsRedraw = false;
	}

	void drawCells(GGraphics2D graphics, Rectangle rectangle) {
		TableLayout.Portion portion =
				layout.getLayoutIntersecting(rectangle);
		double offsetX = rectangle.getMinX() - layout.getRowHeaderWidth();
		double offsetY = rectangle.getMinY() - layout.getColumnHeaderHeight();
		graphics.translate(-offsetX, -offsetY);
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			for (int row = portion.fromRow; row <= portion.toRow; row++) {
				renderer.drawCell(row, column, graphics,
						controller.contentAt(row, column), style);
			}
		}
		graphics.setColor(GColor.GRAY);
		graphics.fillRect((int) offsetX, (int) offsetY, (int) rectangle.getWidth(),
				(int) layout.getColumnHeaderHeight());
		graphics.fillRect((int) offsetX, (int) offsetY, (int) layout.getRowHeaderWidth(),
				(int) rectangle.getHeight());
		graphics.setColor(style.getTextColor());
		graphics.translate(0, offsetY);
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			renderer.drawColumnHeader(column, graphics, controller.getColumnName(column));
		}

		graphics.translate(offsetX, -offsetY);
		for (int row = portion.fromRow; row <= portion.toRow; row++) {
			renderer.drawRowHeader(row, graphics);
		}
		graphics.translate(0, offsetY);
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

	// - TabularSelection

	/**
	 * @param viewport viewport relative to the table, in pixels
	 */
	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
		needsRedraw = true;
	}

	private boolean isSelected(int row, int column) {
		return controller.isSelected(row, column);
	}

	private void showCellEditor(int row, int column) {
		if (controlsDelegate != null) {
			controlsDelegate.showCellEditor(layout.getBounds(row, column),
					controller.contentAt(row, column));
		}
	}

	private void hideCellEditor() {
		if (controlsDelegate != null) {
			controlsDelegate.hideCellEditor();
		}
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerUp(int x, int y, int modifiers) {
		int row = layout.findRow(y + viewport.getMinY());
		int column = layout.findColumn(x + viewport.getMinX());
		controller.select(new Selection(SelectionType.CELLS, new TabularRange(row,
				row, column, column)), modifiers > 0);
		needsRedraw = true;
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerDown(int x, int y, int modifiers) {
		hideCellEditor();
		int row = layout.findColumn(x);
		int column = layout.findRow(y);
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

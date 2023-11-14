package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size). This class provides public API  for both rendering
 * and event handling, using {@link SpreadsheetRenderer} and {@link SpreadsheetController}.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularDataChangeListener {

	private final SpreadsheetController controller;

	private boolean needsRedraw = true;
	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;

	/**
	 * @param tabularData data source
	 * @param rendererFactory converts custom data type to rendable objects
	 */
	public Spreadsheet(TabularData<?> tabularData, CellRenderableFactory rendererFactory) {
		controller = new SpreadsheetController(tabularData);
		renderer = new SpreadsheetRenderer(controller.getLayout(), rendererFactory);
		viewport = new Rectangle(0, 0, 0, 0);
		tabularData.addChangeListener(this);
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
		graphics.setPaint(GColor.WHITE);
		graphics.fillRect(0, 0, (int) viewport.getWidth(), (int) viewport.getHeight());
		drawCells(graphics, viewport);
		for (TabularRange range: controller.getVisibleSelections()) {
			renderer.drawSelection(range, graphics,
					viewport, controller.getLayout());
		}
		needsRedraw = false;
	}

	void drawCells(GGraphics2D graphics, Rectangle rectangle) {
		TableLayout layout = controller.getLayout();
		TableLayout.Portion portion =
				layout.getLayoutIntersecting(rectangle);
		double offsetX = rectangle.getMinX() - layout.getRowHeaderWidth();
		double offsetY = rectangle.getMinY() - layout.getColumnHeaderHeight();
		graphics.translate(-offsetX, -offsetY);
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			for (int row = portion.fromRow; row <= portion.toRow; row++) {
				renderer.drawCell(row, column, graphics,
						controller.contentAt(row, column), controller.getStyle());
			}
		}
		renderer.drawHeaderBackgroundAndOutline(graphics, rectangle, offsetX, offsetY,
				controller.getStyle());
		graphics.translate(0, offsetY);
		for (int column = portion.fromColumn + 1; column <= portion.toColumn; column++) {
			renderer.drawColumnBorder(column, graphics, controller.getStyle());
		}
		graphics.setColor(controller.getStyle().getTextColor());
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			renderer.drawColumnHeader(column, graphics, controller.getColumnName(column));
		}

		graphics.translate(offsetX, -offsetY);
		graphics.setColor(controller.getStyle().getGridColor());
		for (int row = portion.fromRow + 1; row <= portion.toRow; row++) {
			renderer.drawRowBorder(row, graphics, controller.getStyle());
		}
		graphics.setColor(controller.getStyle().getTextColor());
		for (int row = portion.fromRow; row <= portion.toRow; row++) {
			renderer.drawRowHeader(row, graphics, controller.getRowName(row));
		}
		graphics.translate(0, offsetY);
		graphics.setColor(controller.getStyle().getHeaderBackgroundColor());
		graphics.fillRect(0, 0, (int) layout.getRowHeaderWidth() - 1,
				(int) layout.getColumnHeaderHeight() - 1);
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

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		controller.setControlsDelegate(controlsDelegate);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers) {
		controller.handlePointerUp(x, y, modifiers, viewport);
		needsRedraw = true;
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerDown(int x, int y, Modifiers modifiers) {
		needsRedraw = controller.handlePointerDown(x, y, modifiers, viewport);

		// start selecting
	}

	public void handlePointerMove(int x, int y, Modifiers modifiers) {
		needsRedraw = controller.handlePointerMove(x, y, modifiers);
	}

	public void handleKeyPressed(int keyCode, Modifiers modifiers) {
		needsRedraw = controller.handleKeyPressed(keyCode, modifiers);
	}

	public SpreadsheetController getController() {
		return controller;
	}

	@Override
	public void tabularDataDidChange(int row, int column) {
		renderer.invalidate(row, column);
		needsRedraw = true;
	}

	public void setWidthForColumns(double width, int minColumn, int maxColumn) {
		controller.getLayout().setWidthForColumns(width, minColumn, maxColumn);
	}

	public void setHeightForRows(double height, int minRow, int maxRow) {
		controller.getLayout().setHeightForRows(height, minRow, maxRow);
	}

	public boolean needsRedraw() {
		return needsRedraw;
	}

	public MouseCursor getCursor(int x, int y) {
		return controller.getCursor(x, y, viewport);
	}

	public double getTotalWidth() {
		return controller.getLayout().getTotalWidth();
	}

	public double getTotalHeight() {
		return controller.getLayout().getTotalHeight();
	}
}

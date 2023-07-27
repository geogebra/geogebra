package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renders global parts of spreadsheet (column headers, row headers, grid, cell backgrounds)
 * to a graphics object, delegates rendering of individual cells to respective {@link CellRenderer}
 * implementations.
 */
public class SpreadsheetRenderer {

	private final CellRendererFactory converter;
	private final TableLayout layout;

	public SpreadsheetRenderer(TableLayout layout, CellRendererFactory converter) {
		this.converter = converter;
		this.layout = layout;
	}

	void drawCell(int row, int column, GGraphics2D graphics, Object content) {
		graphics.setColor(GColor.BLUE);
		graphics.drawRect((int) layout.getX(column), (int) layout.getY(row),
				(int) layout.getWidth(column), (int) layout.getHeight(row));

		CellRenderer cellRenderer = converter.getRenderer(content);
		if (cellRenderer != null) {
			cellRenderer.draw(graphics, layout.getBounds(row, column));
		}
	}

	protected void drawRowHeader(int row, GGraphics2D graphics) {
	}

	protected void drawColumnHeader(int column, GGraphics2D graphics) {

	}

	void drawSelection(TabularRange selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		graphics.translate(-viewport.getMinX(), -viewport.getMinY());
		int minX = (int) layout.getX(selection.fromCol);
		int minY = (int) layout.getY(selection.fromRow);
		int maxX = (int) layout.getX(selection.toCol + 1);
		int maxY = (int) layout.getY(selection.toRow + 1);
		graphics.setColor(GColor.newColor(0, 0, 255, 100));
		graphics.fillRect(minX,	minY,
				maxX - minX, maxY - minY);

		graphics.translate(viewport.getMinX(), viewport.getMinY());
	}

	void invalidate(int row, int column) {
		// TODO renderers for each cell should be cached and here we invalidate the cache entry
	}
}

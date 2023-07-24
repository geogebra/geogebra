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

	private final SpreadsheetDataConverter converter;
	private Spreadsheet spreadsheet;

	public SpreadsheetRenderer(Spreadsheet spreadsheet, SpreadsheetDataConverter converter) {
		this.spreadsheet = spreadsheet;
		this.converter = converter;
	}

	public void draw(GGraphics2D graphics, Rectangle rectangle) {
		TableLayout.Portion portion =
				spreadsheet.getLayout().getLayoutIntersecting(rectangle);
		graphics.translate(-rectangle.getMinX(), -rectangle.getMinY());
		for (int column = 0; column < portion.numberOfColumns; column++) {
			drawColumnHeader(column, graphics);
		}
		for (int row = 0; row < portion.numberOfRows; row++) {
			drawRowHeader(row, graphics);
		}
		for (int column = 0; column < portion.numberOfColumns; column++) {
			for (int row = 0; row < portion.numberOfRows; row++) {
				drawCell(row + portion.fromRow, column + portion.fromColumn, graphics, spreadsheet);
			}
		}
		graphics.translate(rectangle.getMinX(), rectangle.getMinY());
	}

	private void drawCell(int row, int column, GGraphics2D graphics, Spreadsheet spreadsheet) {
		graphics.setColor(GColor.BLUE);
		TableLayout layout = spreadsheet.getLayout();
		graphics.drawRect((int) layout.getX(column), (int) layout.getY(row),
				(int) layout.getWidth(column), (int) layout.getHeight(row));

		CellRenderer cellRenderer = converter.getRenderer(spreadsheet.contentAt(row, column));
		int x = (int) layout.getX(column);
		int y = (int) layout.getY(row);
		cellRenderer.draw(graphics, x, y);
	}

	private void drawRowHeader(int row, GGraphics2D graphics) {
	}

	private void drawColumnHeader(int column, GGraphics2D graphics) {

	}

	public void drawSelection(TabularRange selection, GGraphics2D graphics,
			Rectangle viewport) {
		graphics.translate(-viewport.getMinX(), -viewport.getMinY());
		TableLayout layout = spreadsheet.getLayout();
		int minX = (int) layout.getX(selection.fromCol);
		int minY = (int) layout.getY(selection.fromRow);
		int maxX = (int) layout.getX(selection.toCol + 1);
		int maxY = (int) layout.getY(selection.toRow + 1);
		graphics.setColor(GColor.newColor(0, 0, 255, 100));
		graphics.fillRect(minX,
				minY,
				maxX - minX, maxY - minY);

		graphics.translate(viewport.getMinX(), viewport.getMinY());
	}
}

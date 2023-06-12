package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

public class SpreadsheetRenderer {

	private Spreadsheet spreadsheet;

	public SpreadsheetRenderer(Spreadsheet spreadsheet) {
		this.spreadsheet = spreadsheet;
	}

	public void draw(GGraphics2D graphics, Rectangle rectangle) {
		TableLayout.Portion portion =
				spreadsheet.getLayout().getLayoutIntersecting(rectangle);
		graphics.translate(-rectangle.getMinX(), -rectangle.getMinY());
		for (int i = 0; i < portion.numberOfColumns; i++) {
			drawColumnHeader(i, graphics);
		}
		for (int i = 0; i < portion.numberOfRows; i++) {
			drawRowHeader(i, graphics);
		}
		for (int i = 0; i < portion.numberOfColumns; i++) {
			for (int j = 0; j < portion.numberOfRows; j++) {
				drawCell(i + portion.fromColumn, j + portion.fromRow, graphics, spreadsheet);
			}
		}
		graphics.translate(rectangle.getMinX(), rectangle.getMinY());
	}

	private void drawCell(int i, int j, GGraphics2D graphics, Spreadsheet spreadsheet) {
		graphics.setColor(GColor.BLUE);
		TableLayout layout = spreadsheet.getLayout();
		graphics.drawRect((int) layout.getX(i), (int) layout.getY(j),
				(int) layout.getWidth(i), (int) layout.getHeight(j));
		spreadsheet.getRenderer(i, j).draw(graphics, (int) layout.getX(i), (int) layout.getY(j));
	}

	private void drawRowHeader(int i, GGraphics2D graphics) {
	}

	private void drawColumnHeader(int i, GGraphics2D graphics) {

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

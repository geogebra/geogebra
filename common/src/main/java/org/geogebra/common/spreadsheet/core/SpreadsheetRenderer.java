package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renders global parts of spreadsheet (column headers, row headers, grid, cell backgrounds)
 * to a graphics object, delegates rendering of individual cells to respective {@link CellRenderer}
 * implementations.
 */
public final class SpreadsheetRenderer {

	private final CellRenderableFactory converter;
	private final TableLayout layout;
	private final Map<GPoint, SelfRenderable> renderableCache = new HashMap<>();
	private final StringRenderer stringRenderer = new StringRenderer();
	private final static GBasicStroke gridStroke = AwtFactory.getPrototype().newBasicStroke(1);

	SpreadsheetRenderer(TableLayout layout, CellRenderableFactory converter) {
		this.converter = converter;
		this.layout = layout;
	}

	void drawCell(int row, int column, GGraphics2D graphics, Object content,
			SpreadsheetStyle style) {
		if (style.isShowGrid()) {

			graphics.setStroke(gridStroke);
			graphics.drawRect((int) layout.getX(column), (int) layout.getY(row),
					(int) layout.getWidth(column), (int) layout.getHeight(row));
		}

		SelfRenderable renderable = renderableCache.computeIfAbsent(new GPoint(row, column),
				ignore -> converter.getRenderable(content));
		if (renderable != null) {
			Rectangle cellBorder = layout.getBounds(row, column);
			renderable.draw(graphics, cellBorder);
		}
	}

	void drawRowHeader(int row, GGraphics2D graphics) {
		Rectangle cellBorder = layout.getRowHeaderBounds(row);
		graphics.drawRect(0, (int) layout.getY(row),
				(int) layout.getRowHeaderWidth(), (int) layout.getHeight(row));
		stringRenderer.draw(String.valueOf(row + 1), graphics, cellBorder);
	}

	void drawColumnHeader(int column, GGraphics2D graphics, String name) {
		Rectangle cellBorder = layout.getColumnHeaderBounds(column);
		graphics.drawRect((int) layout.getX(column), 0,
				(int) layout.getWidth(column), (int) layout.getColumnHeaderHeight());
		stringRenderer.draw(name, graphics, cellBorder);
	}

	void drawSelection(TabularRange selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		double offsetX = -viewport.getMinX() + layout.getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + layout.getColumnHeaderHeight();
		graphics.translate(offsetX, offsetY);
		if (selection.fromCol >= 0 && selection.fromRow >= 0) {
			int minX = (int) layout.getX(selection.fromCol);
			int minY = (int) layout.getY(selection.fromRow);
			int maxX = (int) layout.getX(selection.toCol + 1);
			int maxY = (int) layout.getY(selection.toRow + 1);
			graphics.setColor(GColor.newColor(0, 0, 255, 100));
			graphics.fillRect(minX, minY,
					maxX - minX, maxY - minY);
		}
		graphics.translate(-offsetX, -offsetY);
	}

	void invalidate(int row, int column) {
		renderableCache.remove(new GPoint(row, column));
	}
}

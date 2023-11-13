package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
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
	private final List<SelfRenderable> rowHeaders = new ArrayList<>();
	private final List<SelfRenderable> columnHeaders = new ArrayList<>();
	private final static GBasicStroke gridStroke = AwtFactory.getPrototype().newBasicStroke(1);
	private final static GBasicStroke borderStroke = AwtFactory.getPrototype().newBasicStroke(2);

	SpreadsheetRenderer(TableLayout layout, CellRenderableFactory converter) {
		this.converter = converter;
		this.layout = layout;
	}

	void drawCell(int row, int column, GGraphics2D graphics, Object content,
			SpreadsheetStyle style) {
		if (style.showBorder(row, column)) {
			graphics.setStroke(borderStroke);
			graphics.drawRect((int) layout.getX(column), (int) layout.getY(row),
					(int) layout.getWidth(column), (int) layout.getHeight(row));
		}

		SelfRenderable renderable = renderableCache.computeIfAbsent(new GPoint(row, column),
				ignore -> converter.getRenderable(content, style, row, column));
		if (renderable != null) {
			Rectangle cellBorder = layout.getBounds(row, column);
			if (renderable.getBackground() != null) {
				graphics.setColor(renderable.getBackground());
				graphics.fillRect((int) cellBorder.getMinX(), (int) cellBorder.getMinY(),
						(int) cellBorder.getWidth(), (int) cellBorder.getHeight());
			}
			graphics.setColor(style.getTextColor());
			renderable.draw(graphics, cellBorder);
		}
	}

	void drawRowHeader(int row, GGraphics2D graphics, String name) {
		Rectangle cellBorder = layout.getRowHeaderBounds(row);
		ensureHeaders(rowHeaders, row, String.valueOf(row + 1));
		rowHeaders.get(row).draw(graphics, cellBorder);
	}

	void drawRowBorder(int row, GGraphics2D graphics, SpreadsheetStyle style) {
		graphics.setStroke(gridStroke);
		graphics.drawStraightLine(0, layout.getY(row),
				style.isShowGrid()
						? layout.getTotalWidth() : layout.getRowHeaderWidth(), layout.getY(row));
	}

	private void ensureHeaders(List<SelfRenderable> rowHeaders, int row,
			String name) {
		for (int i = rowHeaders.size(); i <= row; i++) {
			rowHeaders.add(new SelfRenderable(stringRenderer, GFont.PLAIN,
					CellFormat.ALIGN_CENTER, name));
		}
	}

	void drawColumnBorder(int column, GGraphics2D graphics, SpreadsheetStyle style) {
		graphics.setStroke(gridStroke);
		graphics.drawStraightLine(layout.getX(column), 0, layout.getX(column),
				style.isShowGrid()
						? layout.getTotalHeight() : layout.getColumnHeaderHeight());
	}

	void drawColumnHeader(int column, GGraphics2D graphics, String name) {
		Rectangle cellBorder = layout.getColumnHeaderBounds(column);
		ensureHeaders(columnHeaders, column, name);
		columnHeaders.get(column).draw(graphics, cellBorder);
	}

	void drawHeaderBackgroundAndOutline(GGraphics2D graphics, Rectangle rectangle,
			double offsetX, double offsetY, SpreadsheetStyle style) {
		graphics.setColor(style.getHeaderBackgroundColor());
		graphics.fillRect((int) offsetX, (int) offsetY, (int) rectangle.getWidth(),
				(int) layout.getColumnHeaderHeight());
		graphics.fillRect((int) offsetX, (int) offsetY, (int) layout.getRowHeaderWidth(),
				(int) rectangle.getHeight());
		double bottom = offsetY + layout.getColumnHeaderHeight();
		graphics.setColor(style.getGridColor());
		graphics.drawStraightLine(offsetX, bottom, offsetX + rectangle.getWidth(), bottom);
		double right = offsetX + layout.getRowHeaderWidth();
		graphics.drawStraightLine(right, offsetY, right, offsetY + rectangle.getHeight());
	}

	void drawSelection(TabularRange selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		double offsetX = -viewport.getMinX() + layout.getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + layout.getColumnHeaderHeight();
		graphics.translate(offsetX, offsetY);
		if (selection.getMinColumn() >= 0 && selection.getMinRow() >= 0) {
			int minX = (int) layout.getX(selection.getMinColumn());
			int minY = (int) layout.getY(selection.getMinRow());
			int maxX = (int) layout.getX(selection.getMaxColumn() + 1);
			int maxY = (int) layout.getY(selection.getMaxRow() + 1);
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

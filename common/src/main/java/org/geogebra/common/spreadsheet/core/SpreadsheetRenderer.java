package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
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
	private final SpreadsheetStyle style;

	SpreadsheetRenderer(TableLayout layout, CellRenderableFactory converter,
			SpreadsheetStyle style) {
		this.converter = converter;
		this.layout = layout;
		this.style = style;
	}

	void drawCell(int row, int column, GGraphics2D graphics, Object content) {
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
		ensureHeaders(rowHeaders, row, name);
		rowHeaders.get(row).draw(graphics, cellBorder);
	}

	void drawRowBorder(int row, GGraphics2D graphics) {
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

	void drawColumnBorder(int column, GGraphics2D graphics) {
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
			double offsetX, double offsetY) {
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
		Rectangle rect = layout.getBounds(selection, viewport);
		if (rect != null) {
			graphics.setColor(style.getSelectionColor());
			graphics.fillRect((int) rect.getMinX(), (int) rect.getMinY(), (int) rect.getWidth(),
					(int) rect.getHeight());
		}
	}

	void drawSelectionBorder(TabularRange selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		Rectangle rect = layout.getBounds(selection, viewport);
		if (rect != null) {
			graphics.setStroke(borderStroke);
			graphics.setColor(style.getSelectionBorder());
			double minX = Math.max(rect.getMinX(), layout.getRowHeaderWidth());
			double minY = Math.max(rect.getMinY(), layout.getColumnHeaderHeight());
			if (minX < rect.getMaxX() && minY < rect.getMaxY()) {
				graphics.drawStraightLine(minX, minY,
						rect.getMaxX(), minY);
				graphics.drawStraightLine(minX, rect.getMaxY(),
						rect.getMaxX(), rect.getMaxY());
				graphics.drawStraightLine(minX, minY,
						minX, rect.getMaxY());
				graphics.drawStraightLine(rect.getMaxX(), minY,
						rect.getMaxX(), rect.getMaxY());
			}
		}
	}

	void drawSelectionHeader(Selection selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		double offsetX = -viewport.getMinX() + layout.getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + layout.getColumnHeaderHeight();
		TabularRange range = selection.getRange();

		int minX = (int) -offsetX;
		int minY, height, width;
		if (range.getMinRow() >= 0) {
			minY = (int) (layout.getY(range.getMinRow()));
			height = (int) (layout.getY(range.getMaxRow() + 1)
					- layout.getY(range.getMinRow()));
		} else {
			minY = (int) -offsetY;
			height = (int) viewport.getHeight();
		}
		graphics.setColor(range.getMinColumn() == -1 ? style.getSelectionHeaderColor()
				: style.getGridColor());
		graphics.fillRect(minX, minY,
				(int) layout.getRowHeaderWidth(), height);
		minY = (int) -offsetY;
		if (range.getMinColumn() >= 0) {
			minX = (int) (layout.getX(range.getMinColumn()));
			width = (int) (layout.getX(range.getMaxColumn() + 1)
					- layout.getX(range.getMinColumn()));
		} else {
			minX = (int) -offsetX;
			width = (int) viewport.getWidth();
		}
		graphics.setColor(range.getMinRow() == -1 ? style.getSelectionHeaderColor()
				: style.getGridColor());
		graphics.fillRect(minX, minY,
				width, (int) layout.getColumnHeaderHeight());

	}

	void invalidate(int row, int column) {
		renderableCache.remove(new GPoint(row, column));
	}

	void drawDraggingDot(GPoint2D dot, GGraphics2D graphics) {
		graphics.setColor(style.getSelectionBorder());
		graphics.fillRect((int) dot.getX() - 4, (int) dot.getY() - 4, 8, 8);
	}
}

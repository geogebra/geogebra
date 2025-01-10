package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.plugin.EuclidianStyleConstants;
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
	private final static GBasicStroke dashedGridStroke = EuclidianStatic.getStroke(
			gridStroke.getLineWidth(), EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
	private final static GBasicStroke borderStroke = AwtFactory.getPrototype().newBasicStroke(2);
	private final SpreadsheetStyle style;
	private final TabularData tabularData;
	private final static int ERROR_TRIANGLE_WIDTH = 10;
	private final static int TEXT_PADDING = 10;
	private final static int TEXT_HEIGHT = 16;

	SpreadsheetRenderer(TableLayout layout, CellRenderableFactory converter,
			SpreadsheetStyle style, TabularData tabularData) {
		this.converter = converter;
		this.layout = layout;
		this.style = style;
		this.tabularData = tabularData;
	}

	void drawCell(int row, int column, GGraphics2D graphics, Object content, boolean hasError) {
		if (style.showBorder(row, column)) {
			drawCellBorder(row, column, graphics);
		}

		if (content == null) {
			return;
		}

		SelfRenderable renderable = renderableCache.computeIfAbsent(new GPoint(row, column),
				ignore -> converter.getRenderable(content, style, row, column));
		if (renderable != null) {
			Rectangle cellBorder = layout.getBounds(row, column);
			if (renderable.getBackground() != null) {
				graphics.setColor(renderable.getBackground());
				fillRect(graphics, cellBorder.getMinX(), cellBorder.getMinY(),
						cellBorder.getWidth(), cellBorder.getHeight());
			}

			if (!hasError) {
				graphics.setColor(style.getTextColor());
				renderable.draw(graphics, cellBorder);
			}
		}
	}

	private void drawCellBorder(int row, int column, GGraphics2D graphics) {
		graphics.setStroke(borderStroke);
		drawRectangleWithStraightLines(graphics, layout.getX(column), layout.getY(row),
				layout.getWidth(column), layout.getHeight(row));
	}

	/**
	 * draw cell with error style
	 * @param row - row
	 * @param column - column
	 * @param graphics - graphics
	 * @param viewport Viewport relative to the table, in pixels
	 * @param offsetX - x offset
	 * @param offsetY - y offset
	 */
	public void drawErrorCell(int row, int column, GGraphics2D graphics, Rectangle viewport,
			double offsetX, double offsetY) {
		graphics.setColor(style.geErrorGridColor());
		graphics.setStroke(borderStroke);

		double topLeftX = Math.max(layout.getX(column) - offsetX, layout.getRowHeaderWidth());
		double topLeftY = Math.max(layout.getY(row) - offsetY, layout.getColumnHeaderHeight());
		double topRightX = layout.getX(column) - offsetX + layout.getWidth(column);
		double topRightY = layout.getY(row) - offsetY;

		double width = layout.getWidth(column);
		double height = layout.getHeight(row);
		if (leftOutOfBounds(column, offsetX)) {
			width = topRightX - layout.getRowHeaderWidth();
		}
		if (topOutOfBounds(row, offsetY)) {
			height = topRightY + layout.getHeight(row) - layout.getColumnHeaderHeight();
		}

		// Draw error border
		Rectangle bounds = layout.getBounds(new TabularRange(row, column), viewport);
		if (bounds != null) {
			drawVisibleSelectionBorders(graphics, bounds,
					topLeftX, topLeftY, topLeftX + width, topLeftY + height);
		}

		if (width > ERROR_TRIANGLE_WIDTH && height > ERROR_TRIANGLE_WIDTH) {
			drawErrorTriangle(graphics, topLeftX + width, topLeftY);
		}

		if (!leftOutOfBounds(column, offsetX - TEXT_PADDING)
				&& !topOutOfBounds(row, offsetY - TEXT_PADDING)) {
			drawErrorString(graphics, topLeftX, topLeftY);
		}
	}

	private boolean leftOutOfBounds(int column, double offsetX) {
		return layout.getX(column) - offsetX < layout.getRowHeaderWidth();
	}

	private boolean topOutOfBounds(int row, double offsetY) {
		return layout.getY(row) - offsetY < layout.getColumnHeaderHeight();
	}

	private void drawErrorTriangle(GGraphics2D graphics, double topRightX, double topRightY) {
		GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
		path.moveTo(topRightX - ERROR_TRIANGLE_WIDTH, topRightY);
		path.lineTo(topRightX, topRightY);
		path.lineTo(topRightX, topRightY + ERROR_TRIANGLE_WIDTH);
		path.closePath();

		graphics.draw(path);
		graphics.fill(path);
	}

	private void drawErrorString(GGraphics2D graphics, double topLeftX, double topLeftY) {
		graphics.setColor(style.getTextColor());
		graphics.setFont(graphics.getFont().deriveFont(GFont.ITALIC));
		graphics.drawString(tabularData.getErrorString(), topLeftX + TEXT_PADDING,
				topLeftY + TEXT_HEIGHT + TEXT_PADDING);
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

	void drawHeaderBackgroundAndOutline(GGraphics2D graphics, Rectangle rectangle) {
		graphics.setColor(style.getHeaderBackgroundColor());
		fillRect(graphics, 0, 0, rectangle.getWidth(), layout.getColumnHeaderHeight());
		fillRect(graphics, 0, 0, layout.getRowHeaderWidth(), rectangle.getHeight());
		double bottom = layout.getColumnHeaderHeight();
		graphics.setColor(style.getGridColor());
		graphics.drawStraightLine(0, bottom, rectangle.getWidth(), bottom);
		double right = layout.getRowHeaderWidth();
		graphics.drawStraightLine(right, 0, right, rectangle.getHeight());
	}

	void drawSelection(TabularRange selection, GGraphics2D graphics, Rectangle viewport) {
		Rectangle bounds = layout.getBounds(selection, viewport);
		if (bounds != null) {
			graphics.setColor(style.getSelectionColor());
			fillRect(graphics, bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());
		}
	}

	void drawSelectionBorder(TabularRange selection, GGraphics2D graphics, Rectangle viewport,
			boolean thickOutline, boolean dashed) {
		Rectangle bounds = layout.getBounds(selection, viewport);
		if (bounds != null) {
			setStroke(graphics, thickOutline, dashed);
			graphics.setColor(dashed ? style.getDashedSelectionBorderColor()
					: style.getSelectionBorderColor());
			double minX = Math.max(bounds.getMinX(), layout.getRowHeaderWidth());
			double minY = Math.max(bounds.getMinY(), layout.getColumnHeaderHeight());
			double maxX = bounds.getMaxX();
			double maxY = bounds.getMaxY();
			if (minX < maxX && minY < maxY) {
				drawVisibleSelectionBorders(graphics, bounds, minX, minY, maxX, maxY);
			}
			setStroke(graphics, false, false);
		}
	}

	private void setStroke(GGraphics2D graphics, boolean thickOutline, boolean dashed) {
		if (dashed) {
			graphics.setStroke(dashedGridStroke);
		} else {
			graphics.setStroke(thickOutline ? borderStroke : gridStroke);
		}
	}

	private void drawRectangleWithStraightLines(GGraphics2D graphics,
			double minX, double minY, double maxX, double maxY) {
		graphics.drawStraightLine(minX, minY, maxX, minY);
		graphics.drawStraightLine(minX, maxY, maxX, maxY);
		graphics.drawStraightLine(minX, minY, minX, maxY);
		graphics.drawStraightLine(maxX, minY, maxX, maxY);
	}

	/**
	 * Draws only the selection borders that should be fully visible
	 * @implNote Only checks if the top horizontal line and the left vertical line are visible
	 * since the bottom horizontal line and right vertical line are hidden behind the Scrollbar
	 */
	private void drawVisibleSelectionBorders(GGraphics2D graphics, Rectangle bounds,
			double minX, double minY, double maxX, double maxY) {
		if (bounds.getMinY() - layout.getColumnHeaderHeight() >= 0) {
			graphics.drawStraightLine(minX, minY, maxX, minY);
		}
		graphics.drawStraightLine(minX, maxY, maxX, maxY);

		if (bounds.getMinX() - layout.getRowHeaderWidth() >= 0) {
			graphics.drawStraightLine(minX, minY, minX, maxY);
		}
		graphics.drawStraightLine(maxX, minY, maxX, maxY);
	}

	void drawSelectionHeader(Selection selection, GGraphics2D graphics, Rectangle viewport) {
		double offsetX = -viewport.getMinX() + layout.getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + layout.getColumnHeaderHeight();
		TabularRange range = selection.getRange();

		double minX = 0;
		double minY, height, width;
		if (range.getMinRow() >= 0) {
			minY = layout.getY(range.getMinRow()) + offsetY;
			height = layout.getY(range.getMaxRow() + 1) - layout.getY(range.getMinRow());
		} else {
			minY = 0;
			height = viewport.getHeight();
		}
		graphics.setColor(range.getMinColumn() == -1 ? style.getSelectionHeaderColor()
				: style.getGridColor());
		fillRect(graphics, minX, minY, layout.getRowHeaderWidth(), height);
		minY = 0;
		if (range.getMinColumn() >= 0) {
			minX = layout.getX(range.getMinColumn()) + offsetX;
			width = layout.getX(range.getMaxColumn() + 1) - layout.getX(range.getMinColumn());
		} else {
			minX = 0;
			width = viewport.getWidth();
		}
		graphics.setColor(range.getMinRow() == -1 ? style.getSelectionHeaderColor()
				: style.getGridColor());
		fillRect(graphics, minX, minY, width, layout.getColumnHeaderHeight());

	}

	void invalidate(int row, int column) {
		renderableCache.remove(new GPoint(row, column));
	}

	void drawDraggingDot(GPoint2D dot, GGraphics2D graphics) {
		int dotSize = 4;
		graphics.setColor(style.getSelectionBorderColor());
		fillRect(graphics, dot.getX() - dotSize, dot.getY() - dotSize,
				dotSize * 2, dotSize * 2);
		graphics.setStroke(gridStroke);
		graphics.setColor(GColor.WHITE);
		drawRectangleWithStraightLines(graphics, dot.getX() - dotSize,
				dot.getY() - dotSize, dot.getX() + dotSize, dot.getY() + dotSize);
	}

	void drawEditorBorder(Rectangle bounds, GGraphics2D graphics) {
		graphics.setColor(style.getSelectionBorderColor());
		graphics.setStroke(borderStroke);
		drawRectangleWithStraightLines(graphics,
				bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
	}

	/**
	 * This method allows for filling a rectangle by making sure the passed values,
	 * which are doubles, are correctly rounded to the nearest integer values using
	 * {@link Math#round(double)}
	 * @param graphics {@link GGraphics2D}
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param width width
	 * @param height height
	 */
	void fillRect(GGraphics2D graphics, double x, double y, double width, double height) {
		graphics.fillRect((int) Math.round(x), (int) Math.round(y),
				(int) Math.round(width), (int) Math.round(height));
	}
}

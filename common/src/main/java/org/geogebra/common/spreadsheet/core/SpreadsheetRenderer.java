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
				graphics.fillRect((int) cellBorder.getMinX(), (int) cellBorder.getMinY(),
						(int) cellBorder.getWidth(), (int) cellBorder.getHeight());
			}

			if (!hasError) {
				graphics.setColor(style.getTextColor());
				renderable.draw(graphics, cellBorder);
			}
		}
	}

	private void drawCellBorder(int row, int column, GGraphics2D graphics) {
		graphics.setStroke(borderStroke);
		graphics.drawRect((int) layout.getX(column), (int) layout.getY(row),
				(int) layout.getWidth(column), (int) layout.getHeight(row));
	}

	/**
	 * draw cell with error style
	 * @param row - row
	 * @param column - column
	 * @param graphics - graphics
	 * @param offsetX - x offset
	 * @param offsetY - y offset
	 */
	public void drawErrorCell(int row, int column, GGraphics2D graphics,
			double offsetX, double offsetY) {
		graphics.setColor(style.geErrorGridColor());
		graphics.setStroke(borderStroke);

		int topLeftX = (int) Math.max(layout.getX(column) - offsetX, layout.getRowHeaderWidth());
		int topLeftY = (int) Math.max(layout.getY(row) - offsetY, layout.getColumnHeaderHeight());
		int topRightX = (int) (layout.getX(column) - offsetX + layout.getWidth(column));
		int topRightY = (int) (layout.getY(row) - offsetY);

		int width = (int) layout.getWidth(column);
		int height = (int) layout.getHeight(row);
		if (leftOutOfBounds(column, offsetX)) {
			width = (int) (topRightX - layout.getRowHeaderWidth());
		}
		if (topOutOfBounds(row, offsetY)) {
			height = (int) (topRightY + layout.getHeight(row) - layout.getColumnHeaderHeight());
		}

		graphics.drawRect(topLeftX, topLeftY, width, height); // draw error border
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

	private void drawErrorTriangle(GGraphics2D graphics, int topRightX, int topRightY) {
		GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
		path.moveTo(topRightX - ERROR_TRIANGLE_WIDTH, topRightY);
		path.lineTo(topRightX, topRightY);
		path.lineTo(topRightX, topRightY + ERROR_TRIANGLE_WIDTH);
		path.closePath();

		graphics.draw(path);
		graphics.fill(path);
	}

	private void drawErrorString(GGraphics2D graphics, int topLeftX, int topLeftY) {
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
		graphics.fillRect(0, 0, (int) rectangle.getWidth(),
				(int) layout.getColumnHeaderHeight());
		graphics.fillRect(0, 0, (int) layout.getRowHeaderWidth(),
				(int) rectangle.getHeight());
		double bottom = layout.getColumnHeaderHeight();
		graphics.setColor(style.getGridColor());
		graphics.drawStraightLine(0, bottom, rectangle.getWidth(), bottom);
		double right =  layout.getRowHeaderWidth();
		graphics.drawStraightLine(right, 0, right, rectangle.getHeight());
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
			Rectangle viewport, TableLayout layout, boolean thickOutline, boolean dashed) {
		Rectangle rect = layout.getBounds(selection, viewport);
		if (rect != null) {
			setStroke(graphics, thickOutline, dashed);
			graphics.setColor(dashed ? style.getDashedSelectionBorderColor()
					: style.getSelectionBorderColor());
			double minX = Math.max(rect.getMinX(), layout.getRowHeaderWidth());
			double minY = Math.max(rect.getMinY(), layout.getColumnHeaderHeight());
			double maxX = rect.getMaxX();
			double maxY = rect.getMaxY();
			if (minX < maxX && minY < maxY) {
				drawRectangleWithStraightLines(graphics, minX, minY, maxX, maxY);
			}
			if (dashed) {
				setStroke(graphics, thickOutline, false);
			}
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

	void drawSelectionHeader(Selection selection, GGraphics2D graphics,
			Rectangle viewport, TableLayout layout) {
		double offsetX = -viewport.getMinX() + layout.getRowHeaderWidth();
		double offsetY = -viewport.getMinY() + layout.getColumnHeaderHeight();
		TabularRange range = selection.getRange();

		int minX = 0;
		int minY, height, width;
		if (range.getMinRow() >= 0) {
			minY = (int) (layout.getY(range.getMinRow()) + offsetY);
			height = (int) (layout.getY(range.getMaxRow() + 1)
					- layout.getY(range.getMinRow()));
		} else {
			minY = 0;
			height = (int) viewport.getHeight();
		}
		graphics.setColor(range.getMinColumn() == -1 ? style.getSelectionHeaderColor()
				: style.getGridColor());
		graphics.fillRect(minX, minY,
				(int) layout.getRowHeaderWidth(), height);
		minY = 0;
		if (range.getMinColumn() >= 0) {
			minX = (int) (layout.getX(range.getMinColumn()) + offsetX);
			width = (int) (layout.getX(range.getMaxColumn() + 1)
					- layout.getX(range.getMinColumn()));
		} else {
			minX = 0;
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
		int dotSize = 4;
		graphics.setColor(style.getSelectionBorderColor());
		graphics.fillRect((int) dot.getX() - dotSize, (int) dot.getY() - dotSize,
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
}

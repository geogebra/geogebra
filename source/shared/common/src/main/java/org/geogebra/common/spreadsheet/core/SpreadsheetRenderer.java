package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renders global parts of spreadsheet (column headers, row headers, grid, cell backgrounds)
 * to a graphics object, delegates rendering of individual cells to respective {@link CellRenderer}
 * implementations.
 */
final class SpreadsheetRenderer {

	private final CellRenderableFactory converter;
	private final TableLayout layout;
	private final Map<SpreadsheetCoords, SelfRenderable> renderableCache = new HashMap<>();
	private final StringRenderer stringRenderer = new StringRenderer();
	private final List<SelfRenderable> rowHeaders = new ArrayList<>();
	private final List<SelfRenderable> columnHeaders = new ArrayList<>();
	private final static GBasicStroke gridStroke = AwtFactory.getPrototype().newBasicStroke(1);
	private final static GBasicStroke dashedGridStroke = EuclidianStatic.getStroke(
			gridStroke.getLineWidth(), EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
	private final static GBasicStroke borderStroke = AwtFactory.getPrototype().newBasicStroke(2);
	private final SpreadsheetStyling styling;
	private final TabularData tabularData;
	private final static int ERROR_TRIANGLE_WIDTH = 10;
	private final static int TEXT_PADDING = 10;
	private final static int TEXT_HEIGHT = 16;

	private final static int[] REFERENCE_COLOR_RGB_VALUES =
			{ 0x6557d2, 0xe0bf00, 0x3bb4a6, 0xda6a9d, 0x3b1c32, 0xff8c70 };
	private final static List<GColor> REFERENCE_COLORS =
			Arrays.stream(REFERENCE_COLOR_RGB_VALUES)
					.mapToObj(rgb -> GColor.newColorRGB(rgb))
					.collect(Collectors.toList());
	private final static int[] REFERENCE_STROKE_INDICES = { 0, 1, 2 };
	private final static List<GBasicStroke> referenceStrokes =
			Arrays.stream(REFERENCE_STROKE_INDICES)
				.mapToObj(index -> AwtFactory.getPrototype().newBasicStroke(2,
					GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER, 4,
					makeReferenceDashPattern(index)))
				.collect(Collectors.toList());

	SpreadsheetRenderer(@Nonnull TableLayout layout, @Nonnull CellRenderableFactory converter,
			@Nonnull SpreadsheetStyling styling, @Nonnull TabularData tabularData) {
		this.converter = converter;
		this.layout = layout;
		this.styling = styling;
		this.tabularData = tabularData;
	}

	void drawCell(int row, int column, GGraphics2D graphics, @CheckForNull Object content,
			boolean hasError) {
		Rectangle cellBounds = layout.getBounds(row, column);
		GColor backgroundColor = styling.getBackgroundColor(row, column, null);
		if (content == null) {
			drawCellBackgroundIfNeeded(graphics, backgroundColor, cellBounds);
			if (styling.showBorder(row, column)) {
				drawCellBorder(graphics, cellBounds);
			}
			return;
		}

		SelfRenderable renderable = renderableCache.computeIfAbsent(
				new SpreadsheetCoords(row, column),
				ignore -> converter.getRenderable(content, styling, row, column));
		if (renderable != null) {
			drawCellBackgroundIfNeeded(graphics, renderable.getBackground(), cellBounds);
			if (styling.showBorder(row, column)) {
				drawCellBorder(graphics, cellBounds);
			}
			if (!hasError) {
				graphics.setColor(styling.getTextColor(row, column, styling.getDefaultTextColor()));
				renderable.draw(graphics, cellBounds);
			}
		}
	}

	private void drawCellBorder(GGraphics2D graphics, Rectangle frame) {
		graphics.setStroke(borderStroke);
		drawRectangleWithStraightLines(graphics, frame.getMinX(), frame.getMinY(),
				frame.getWidth(), frame.getHeight());
	}

	private void drawCellBackgroundIfNeeded(GGraphics2D graphics, GColor color, Rectangle frame) {
		if (color != null && !GColor.WHITE.equals(color)) {
			graphics.setColor(color);
			fillRect(graphics, frame.getMinX(), frame.getMinY(), frame.getWidth(),
					frame.getHeight());
		}
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
	void drawErrorCell(int row, int column, GGraphics2D graphics, Rectangle viewport,
			double offsetX, double offsetY) {
		graphics.setColor(styling.getErrorGridColor());
		graphics.setStroke(borderStroke);

		double topLeftX = Math.max(layout.getMinX(column) - offsetX, layout.getRowHeaderWidth());
		double topLeftY = Math.max(layout.getMinY(row) - offsetY, layout.getColumnHeaderHeight());
		double topRightX = layout.getMinX(column) - offsetX + layout.getWidth(column);
		double topRightY = layout.getMinY(row) - offsetY;

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
		return layout.getMinX(column) - offsetX < layout.getRowHeaderWidth();
	}

	private boolean topOutOfBounds(int row, double offsetY) {
		return layout.getMinY(row) - offsetY < layout.getColumnHeaderHeight();
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
		graphics.setColor(styling.getDefaultTextColor());
		graphics.setFont(graphics.getFont().deriveFont(GFont.ITALIC));
		graphics.drawString(tabularData.getErrorString(), topLeftX + TEXT_PADDING,
				topLeftY + TEXT_HEIGHT + TEXT_PADDING);
	}

	void drawRowHeader(int row, GGraphics2D graphics, Function<Integer, String> nameProvider) {
		Rectangle cellBorder = layout.getRowHeaderBounds(row);
		ensureHeaders(rowHeaders, row, nameProvider);
		rowHeaders.get(row).draw(graphics, cellBorder);
	}

	void drawRowBorder(int row, GGraphics2D graphics) {
		graphics.setStroke(gridStroke);
		graphics.drawStraightLine(0, layout.getMinY(row),
				styling.isShowGrid()
						? layout.getTotalWidth() : layout.getRowHeaderWidth(), layout.getMinY(row));
	}

	private void ensureHeaders(List<SelfRenderable> rowHeaders, int row,
			Function<Integer, String> nameProvider) {
		for (int i = rowHeaders.size(); i <= row; i++) {
			rowHeaders.add(new SelfRenderable(stringRenderer, GFont.PLAIN,
					CellFormat.ALIGN_CENTER, nameProvider.apply(i)));
		}
	}

	void drawColumnBorder(int column, GGraphics2D graphics) {
		graphics.setStroke(gridStroke);
		graphics.drawStraightLine(layout.getMinX(column), 0, layout.getMinX(column),
				styling.isShowGrid()
						? layout.getTotalHeight() : layout.getColumnHeaderHeight());
	}

	void drawColumnHeader(int column, GGraphics2D graphics,
			Function<Integer, String> nameProvider) {
		Rectangle cellBorder = layout.getColumnHeaderBounds(column);
		ensureHeaders(columnHeaders, column, nameProvider);
		columnHeaders.get(column).draw(graphics, cellBorder);
	}

	void drawHeaderBackgroundAndOutline(GGraphics2D graphics, Rectangle rectangle) {
		graphics.setColor(styling.getHeaderBackgroundColor());
		fillRect(graphics, 0, 0, rectangle.getWidth(), layout.getColumnHeaderHeight());
		fillRect(graphics, 0, 0, layout.getRowHeaderWidth(), rectangle.getHeight());
		double bottom = layout.getColumnHeaderHeight();
		graphics.setColor(styling.getGridColor());
		graphics.drawStraightLine(0, bottom, rectangle.getWidth(), bottom);
		double right = layout.getRowHeaderWidth();
		graphics.drawStraightLine(right, 0, right, rectangle.getHeight());
	}

	void drawSelection(TabularRange selection, GGraphics2D graphics, Rectangle viewport) {
		Rectangle bounds = layout.getBounds(selection, viewport);
		if (bounds != null) {
			graphics.setColor(styling.getSelectionColor());
			fillRect(graphics, bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());
		}
	}

	void drawSelectionBorder(TabularRange selection, GGraphics2D graphics, Rectangle viewport,
			boolean thickOutline, boolean dashed) {
		Rectangle bounds = layout.getBounds(selection, viewport);
		if (bounds != null) {
			setStroke(graphics, thickOutline, dashed);
			graphics.setColor(dashed ? styling.getDashedSelectionBorderColor()
					: styling.getSelectionBorderColor());
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
			minY = layout.getMinY(range.getMinRow()) + offsetY;
			height = layout.getMinY(range.getMaxRow() + 1) - layout.getMinY(range.getMinRow());
		} else {
			minY = 0;
			height = viewport.getHeight();
		}
		graphics.setColor(range.getMinColumn() == -1 ? styling.getSelectionHeaderColor()
				: styling.getGridColor());
		fillRect(graphics, minX, minY, layout.getRowHeaderWidth(), height);
		minY = 0;
		if (range.getMinColumn() >= 0) {
			minX = layout.getMinX(range.getMinColumn()) + offsetX;
			width = layout.getMinX(range.getMaxColumn() + 1) - layout.getMinX(range.getMinColumn());
		} else {
			minX = 0;
			width = viewport.getWidth();
		}
		graphics.setColor(range.getMinRow() == -1 ? styling.getSelectionHeaderColor()
				: styling.getGridColor());
		fillRect(graphics, minX, minY, width, layout.getColumnHeaderHeight());

	}

	void invalidate(int row, int column) {
		renderableCache.remove(new SpreadsheetCoords(row, column));
	}

	void drawDraggingDot(Point location, GGraphics2D graphics) {
		int dotSize = 4;
		graphics.setColor(styling.getSelectionBorderColor());
		fillRect(graphics, location.x - dotSize, location.y - dotSize,
				dotSize * 2, dotSize * 2);
		graphics.setStroke(gridStroke);
		graphics.setColor(GColor.WHITE);
		drawRectangleWithStraightLines(graphics, location.x - dotSize,
				location.y - dotSize, location.x + dotSize, location.y + dotSize);
	}

	void drawEditorBorder(Rectangle bounds, GGraphics2D graphics) {
		graphics.setColor(styling.getSelectionBorderColor());
		graphics.setStroke(borderStroke);
		drawRectangleWithStraightLines(graphics,
				bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
	}

	/**
	 * Draw a spreadsheet reference.
	 * @param reference A cell or cell range reference.
	 * @param referenceIndex An index for drawing the different references using different colors.
	 * @param filled Pass {@code true} to fill and stroke the reference rectangle, {@code false} to
	 * only stroke the outline.
	 * @param graphics The Graphics to draw to.
	 * @param viewport The current viewport.
	 */
	void drawReference(@Nonnull SpreadsheetReference reference, int referenceIndex, boolean filled,
			GGraphics2D graphics, Rectangle viewport) {
		TabularRange range = makeTabularRange(reference);
		Rectangle bounds = layout.getBounds(range, viewport);
		if (bounds == null) {
			return;
		}
		GColor color = REFERENCE_COLORS.get(referenceIndex % REFERENCE_COLORS.size());
		if (filled) {
			graphics.setColor(color.deriveWithAlpha(25)); // 0.1 * 255
			fillRect(graphics, bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());
		}
		graphics.setColor(color);
		graphics.setStroke(referenceStrokes.get(referenceIndex % referenceStrokes.size()));
		drawRectangleWithStraightLines(graphics,
				bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
	}

	private static double[] makeReferenceDashPattern(int index) {
		return new double[]{ 4.0, (index % 3) + 1 };
	}

	private static TabularRange makeTabularRange(SpreadsheetReference reference) {
		if (reference.toCell == null) {
			return new TabularRange(reference.fromCell.rowIndex, reference.fromCell.columnIndex);
		}
		return new TabularRange(reference.fromCell.rowIndex, reference.fromCell.columnIndex,
				reference.toCell.rowIndex, reference.toCell.columnIndex);
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

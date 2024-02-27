package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.function.Predicate;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.ggbjdk.java.awt.geom.Line2D;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.base.Js;

public class TableCanvasExporter {

	private static final int CELL_PADDING_X = 4;
	private static final int CELL_PADDING_Y = 2;
	private final MyTableW table;
	private final AppW app;
	private final Grid ssGrid;
	private final int rowCount;
	private final int columnCount;
	private final GGraphics2DW graphics;

	/**
	 * @param table table
	 * @param app application
	 * @param offsetWidth parent panel width
	 * @param offsetHeight parent panel height
	 * @param context2d context
	 */
	public TableCanvasExporter(MyTableW table, AppW app, int offsetWidth, int offsetHeight,
			CanvasRenderingContext2D context2d) {
		this.table = table;
		this.app = app;
		this.ssGrid = table.ssGrid;
		rowCount = findFirst(table.getRowCount(),
				row -> table.getPixelRelative(0, row).y > offsetHeight);
		columnCount = findFirst(table.getColumnCount(),
				col -> table.getPixelRelative(col, 0).x > offsetWidth);
		graphics = new GGraphics2DW(context2d);
	}

	private int findFirst(int bound, Predicate<Integer> check) {
		int min = 0;
		int max = bound;
		while (max - min > 1) {
			int center = (max + min) / 2;
			if (check.test(center)) {
				max = center;
			} else {
				min = center;
			}
		}
		return max;
	}

	/**
	 * @param left0 horizontal offset
	 * @param top0 vertical offset
	 */
	public void paintToCanvas(int left0, int top0) {
		GFont font = app.getFontCommon(false, GFont.PLAIN,
				app.getSettings().getFontSettings().getAppFontSize());
		graphics.setFont(font);

		int headerWidth = table.rowHeader.getOffsetWidth();
		int headerHeight = table.columnHeader.getOffsetHeight();
		graphics.translate(left0, top0);
		drawHeadings(headerWidth, headerHeight);
		drawGrid(headerWidth, headerHeight);
		graphics.translate(headerWidth, headerHeight);
		for (int col = 0; col < columnCount; col++) {
			for (int row = 0; row < rowCount; row++) {
				drawCell(col, row);
			}
		}
		graphics.translate(-left0 - headerWidth, -top0 - headerHeight);
	}

	private void drawGrid(int headerWidth, int headerHeight) {
		graphics.setColor(GColor.LIGHT_GRAY);
		GLine2D line = new Line2D.Double(0, 0, 0, 0);
		for (int col = 0; col <= columnCount; col++) {
			GPoint topPx = table.getPixelRelative(col, 0);
			GPoint bottomPx = table.getPixelRelative(col, rowCount - 1);
			line.setLine(topPx.x + headerWidth, topPx.y,
					bottomPx.x + headerWidth, bottomPx.y + headerHeight);
			graphics.draw(line);
		}
		for (int row = 0; row < rowCount; row++) {
			GPoint topPx = table.getPixelRelative(0, row);
			GPoint bottomPx = table.getPixelRelative(columnCount, row);
			line.setLine(topPx.x , topPx.y + headerHeight,
					bottomPx.x + headerWidth, bottomPx.y + headerHeight);
			graphics.draw(line);
		}
	}

	private void drawHeadings(int headerWidth, int headerHeight) {
		graphics.setColor(GColor.newColorRGB(0xF8F8F8));
		int columnHeaderWidth = table.columnHeader.getContentWidth();
		int rowHeaderHeight = table.rowHeader.getContentHeight();
		graphics.getContext().fillRect(0, 0, columnHeaderWidth + headerWidth, headerHeight);
		graphics.getContext().fillRect(0, 0, headerWidth, rowHeaderHeight);
		graphics.setColor(GColor.BLACK);
		for (int col = 0; col < columnCount; col++) {
			String text = GeoElementSpreadsheet.getSpreadsheetColumnName(col);
			GPoint leftPx = table.getPixelRelative(col, 1);
			GPoint rightPx = table.getPixelRelative(col + 1, 1);

			drawCentered(graphics, text, (leftPx.x + rightPx.x) / 2
					+ headerWidth, leftPx.y / 2);
		}
		for (int row = 0; row < rowCount; row++) {
			String text = String.valueOf(row + 1);
			GPoint leftPx = table.getPixelRelative(0, row);
			GPoint rightPx = table.getPixelRelative(0, row + 1);
			drawCentered(graphics, text, headerWidth / 2,
					(leftPx.y + rightPx.y) / 2 + headerHeight);
		}
	}

	private void drawCentered(GGraphics2DW graphics, String text, int centerX, int yCenter) {
		double labelWidth = graphics.getContext().measureText(text).width;
		int fontSize = app.getSettings().getFontSettings().getAppFontSize();
		graphics.drawString(text, centerX - labelWidth / 2, yCenter + fontSize / 2.0);
	}

	private void drawCell(int col, int row) {
		CanvasRenderingContext2D context2d = graphics.getContext();
		Widget widget = ssGrid.getWidget(row, col);
		GeoElement cell = (GeoElement) app.getSpreadsheetTableModel().getValueAt(row, col);
		int alignment = table.getCellFormatHandler().getAlignment(col, row,
				cell != null && cell.isGeoText());

		if (widget instanceof Canvas) {
			HTMLCanvasElement formula = Js.uncheckedCast(widget.getElement());
			if (alignment == CellFormat.ALIGN_LEFT) {
				GPoint pt = table.getPixelRelative(col, row);
				context2d.drawImage(formula, pt.x + CELL_PADDING_X, pt.y + CELL_PADDING_Y);
			} else {
				GPoint pt = table.getPixelRelative(col + 1, row);
				context2d.drawImage(formula, pt.x - formula.offsetWidth - CELL_PADDING_X,
						pt.y + CELL_PADDING_Y);
			}
		}
		graphics.setColor(cell != null ? cell.getObjectColor() : GColor.BLACK);

		String txt = ssGrid.getText(row, col);
		if (alignment == CellFormat.ALIGN_LEFT) {
			GPoint pt = table.getPixelRelative(col, row + 1);
			context2d.fillText(txt, pt.x + CELL_PADDING_X, pt.y - CELL_PADDING_Y);
		} else {
			GPoint pt = table.getPixelRelative(col + 1, row + 1);
			double textWidth = context2d.measureText(txt).width;
			context2d.fillText(txt, pt.x - CELL_PADDING_X - textWidth, pt.y - CELL_PADDING_Y);
		}
	}

}

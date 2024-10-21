package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.desktop.awt.GColorD;

/**
 * Methods for drawing custom spreadsheet borders
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetBorders {

	/**
	 * Draws custom borders for all cells recorded in the spreadsheet border
	 * format map.
	 * 
	 * @param g2 graphics
	 * @param table table
	 */
	public static void drawFormatBorders(Graphics2D g2, MyTableD table) {

		g2.setColor(GColorD.getAwtColor(GColor.BLACK));
		g2.setStroke(new BasicStroke(1));

		HashMap<SpreadsheetCoords, Object> map = table.getCellFormatHandler()
				.getFormatMap(CellFormat.FORMAT_BORDER);
		Set<SpreadsheetCoords> formatCell = map.keySet();

		int column = 0, row = 0;
		for (SpreadsheetCoords cell : formatCell) {

			Byte b = (Byte) table.getCellFormatHandler().getCellFormat(cell.column,
					cell.row, CellFormat.FORMAT_BORDER);
			if (b != null) {
				column = cell.column;
				row = cell.row;
				if (column == -1 || row == -1) {
					handleRowOrColumnGridFormat(g2, table, column, row, b);
				} else {
					drawPartialBorder(g2, table, column, row, column + 1, row + 1, b);
				}
			}
		}

	}

	/**
	 * Draws row/column grid lines. Byte v determines the line type. (top/bottom
	 * row line or a left/right column line).
	 * 
	 * @param g2 graphics
	 * @param table table
	 * @param col column
	 * @param row row
	 * @param v line type
	 */
	public static void handleRowOrColumnGridFormat(Graphics2D g2,
			MyTableD table, int col, int row, byte v) {

		// row
		if (col == -1) {

			// if the format includes right or left border then draw borders for
			// each cell individually
			if (!CellFormat.isZeroBit(v, 0) || !CellFormat.isZeroBit(v, 2)) {
				for (int c = 0; c < table.getColumnCount(); c++) {
					drawPartialBorder(g2, table, c, row, c + 1, row + 1, v);
				}

				// if no row borders are given then this must be an inside
				// border
				// so inside row lines need to be drawn
				if (!CellFormat.isZeroBit(v, 1)
						|| !CellFormat.isZeroBit(v, 3)) {
					// how?
				}

			}

			// otherwise just draw a border line for an entire row
			else {
				// top bar
				if (!CellFormat.isZeroBit(v, 1)) {
					drawRowBorder(g2, table, row);
				}
				// bottom bar
				if (!CellFormat.isZeroBit(v, 3)) {
					drawRowBorder(g2, table, row + 1);
				}
			}
		}

		// column
		if (row == -1) {

			// if the format includes row borders then draw each cell
			// individually
			if (!CellFormat.isZeroBit(v, 1) || !CellFormat.isZeroBit(v, 3)) {
				for (int r = 0; r < table.getRowCount(); r++) {
					drawPartialBorder(g2, table, col, r, col + 1, r + 1, v);
				}
			}

			// otherwise just draw a border line for an entire column
			else {
				// left column
				if (!CellFormat.isZeroBit(v, 0)) {
					drawColumnBorder(g2, table, col);
				}
				// right column
				if (!CellFormat.isZeroBit(v, 2)) {
					drawColumnBorder(g2, table, col + 1);
				}
			}
		}
	}

	/**
	 * Draws a partial border around the rectangular region from row1 down to
	 * row2 and across from col1 to col2. Byte v determines which sides of the
	 * border are drawn.
	 * 
	 * @param g2 graphice
	 * @param table table
	 * @param col1 start column
	 * @param row1 start row
	 * @param col2 end column
	 * @param row2 end row
	 * @param v border type
	 */
	public static void drawPartialBorder(Graphics2D g2, MyTableD table,
			int col1, int row1, int col2, int row2, byte v) {

		Rectangle rect1 = table.getCellRect(row1, col1, true);
		int r1 = rect1.x - 1;
		int c1 = rect1.y - 1;
		Rectangle rect2 = table.getCellRect(row2, col2, true);
		int r2 = rect2.x - 1;
		int c2 = rect2.y - 1;

		// Draw bars by bit position
		//
		// 1
		// 0 2
		// 3
		//
		// left bar, 0
		if (!CellFormat.isZeroBit(v, 0)) {
			g2.drawLine(r1, c1, r1, c2);
		}
		// top bar, 1
		if (!CellFormat.isZeroBit(v, 1)) {
			g2.drawLine(r1, c1, r2, c1);
		}
		// right bar, 2
		if (!CellFormat.isZeroBit(v, 2)) {
			g2.drawLine(r2, c1, r2, c2);
		}
		// bottom bar, 3
		if (!CellFormat.isZeroBit(v, 3)) {
			g2.drawLine(r1, c2, r2, c2);
		}

	}

	/**
	 * Draws a grid line beneath the given row
	 * 
	 * @param g2 graphics
	 * @param table table
	 * @param row row
	 */
	public static void drawRowBorder(Graphics2D g2, MyTableD table, int row) {

		Rectangle rect1 = table.getCellRect(row, 0, true);
		int r1 = rect1.x - 1;
		int c1 = rect1.y - 1;
		Rectangle rect2 = table.getCellRect(row, table.getColumnCount(), true);
		int r2 = rect2.x - 1;
		int c2 = rect2.y - 1;

		g2.drawLine(r1, c1, r2, c2);

	}

	/**
	 * Draws a grid line to the right the give column
	 * 
	 * @param g2 graphics
	 * @param table table
	 * @param column column
	 */
	public static void drawColumnBorder(Graphics2D g2, MyTableD table,
			int column) {

		Rectangle rect1 = table.getCellRect(0, column, true);
		int r1 = rect1.x - 1;
		int c1 = rect1.y - 1;
		Rectangle rect2 = table.getCellRect(table.getRowCount(), column, true);
		int r2 = rect2.x - 1;
		int c2 = rect2.y - 1;

		g2.drawLine(r1, c1, r2, c2);

	}

}

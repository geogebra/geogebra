package geogebra.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import geogebra.common.gui.view.spreadsheet.CellRange;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingConstants;

/**
 * Helper class that handles cell formats for the spreadsheet table cell
 * renderer.
 * 
 * Format values are stored in an array of hash tables. Each hash table holds
 * values for a given format (e.g text alignment, background color). Table keys
 * are Point objects that locate cells, rows or columns as follows:
 * 
 * cell = (column index, row index) row = (-1, row index) column = (column
 * index, -1).
 * 
 * @author George Sturr, 2010-4-4
 * 
 */
public class CellFormat implements CellFormatInterface {

	MyTableD table;

	// Array of format tables
	private MyHashMap[] formatMapArray;

	// Format types.
	// These are also array indices, so they must be sequential: 0..n
	public static final int FORMAT_ALIGN = 0;
	public static final int FORMAT_BORDER = 1;
	public static final int FORMAT_BGCOLOR = 2;
	public static final int FORMAT_FONTSTYLE = 3;

	private int formatCount = 5;

	// Alignment constants
	public static final int ALIGN_LEFT = SwingConstants.LEFT;
	public static final int ALIGN_CENTER = SwingConstants.CENTER;
	public static final int ALIGN_RIGHT = SwingConstants.RIGHT;

	// Font syle constants
	public static final int STYLE_PLAIN = Font.PLAIN;
	public static final int STYLE_BOLD = Font.BOLD;
	public static final int STYLE_ITALIC = Font.ITALIC;
	public static final int STYLE_BOLD_ITALIC = Font.BOLD + Font.ITALIC;

	// Border style constants used by stylebar.
	// Keep this order, they are indices to the border popup button menu
	public static final int BORDER_STYLE_NONE = 0;
	public static final int BORDER_STYLE_FRAME = 1;
	public static final int BORDER_STYLE_INSIDE = 2;
	public static final int BORDER_STYLE_ALL = 3;
	public static final int BORDER_STYLE_TOP = 4;
	public static final int BORDER_STYLE_BOTTOM = 5;
	public static final int BORDER_STYLE_LEFT = 6;
	public static final int BORDER_STYLE_RIGHT = 7;

	// Border constants for painting
	// These are stored in a format map and are bit-decoded when painting
	// borders
	public static final byte BORDER_LEFT = 1;
	public static final byte BORDER_TOP = 2;
	public static final byte BORDER_RIGHT = 4;
	public static final byte BORDER_BOTTOM = 8;
	public static final byte BORDER_ALL = 15; // sum

	// XML tokens and delimiters
	private static final String formatDelimiter = ",";
	private static final String cellDelimiter = ":";
	private static final String alignToken = "a";
	private static final String borderToken = "b";
	private static final String fontStyleToken = "f";
	private static final String bgColorToken = "c";

	// map to convert token to format type
	private static HashMap<String, Integer> formatTokenMap = new HashMap<String, Integer>();
	static {
		formatTokenMap.put(alignToken, FORMAT_ALIGN);
		formatTokenMap.put(borderToken, FORMAT_BORDER);
		formatTokenMap.put(fontStyleToken, FORMAT_FONTSTYLE);
		formatTokenMap.put(bgColorToken, FORMAT_BGCOLOR);
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 */
	public CellFormat(MyTableD table) {

		this.table = table;
		// Create instances of the format hash maps
		formatMapArray = new MyHashMap[formatCount];
		for (int i = 0; i < formatCount; i++) {
			formatMapArray[i] = new MyHashMap();
		}
	}

	/**
	 * Clears all format objects from the maps
	 */
	public void clearAll() {
		for (int i = 0; i < formatMapArray.length; i++)
			formatMapArray[i].clear();
	}

	/**
	 * Class that extends HashMap so that null values cannot be mapped and a
	 * call to put(key, null) will remove a key if it exists already.
	 * 
	 */
	private static class MyHashMap extends HashMap {

		private static final long serialVersionUID = 1L;

		@Override
		public Object put(Object key, Object value) {
			if (value == null) {
				super.remove(key);
				return null;
			} 
			return super.put(key, value);
		}
	}

	// ========================================================
	// Getters
	// ========================================================

	/**
	 * Returns the format map for a given cell format
	 * 
	 * @param formatType
	 * @return
	 */
	public HashMap<GPoint, Object> getFormatMap(int formatType) {
		return formatMapArray[formatType];
	}

	/**
	 * Returns the format object for a given cell and a given format type. If
	 * format does not exist, returns null.
	 */
	public Object getCellFormat(GPoint cellKey, int formatType) {

		Object formatObject = null;

		// Create special keys for the row and column of the cell
		GPoint rowKey = new GPoint(-1, cellKey.y);
		GPoint columnKey = new GPoint(cellKey.x, -1);

		// Get the format table
		HashMap formatMap = formatMapArray[formatType];

		// Check there is a format for this cell
		if (formatMap.containsKey(cellKey)) {
			// System.out.println("found" + cellKey.toString());
			formatObject = formatMap.get(cellKey);
		}

		// Check if there is a row format for this cell
		else if (formatMap.containsKey(rowKey)) {
			formatObject = formatMap.get(rowKey);
		}

		// Check if there is a column format for this cell
		else if (formatMap.containsKey(columnKey)) {
			formatObject = formatMap.get(columnKey);
		}

		return formatObject;

	}

	/**
	 * Returns the format object shared by all cells in the given cell range for
	 * the given format type. If a format object does not exist, or not all
	 * cells share the same format object, null is returned.
	 */
	public Object getCellFormat(CellRange cr, int formatType) {

		// Get the format in the upper left cell
		GPoint cell = new GPoint(cr.getMinColumn(), cr.getMinRow());
		Object format = getCellFormat(cell, formatType);

		if (format == null)
			return null;

		// Iterate through the range and test if they cells have the same format
		for (int r = 0; r > cr.getMaxRow(); r++) {
			for (int c = 0; c > cr.getMaxColumn(); c++) {
				cell.x = c;
				cell.y = r;
				if (!format.equals(getCellFormat(cell, formatType))) {
					format = null;
					break;
				}
			}
		}
		return format;
	}

	// ========================================================
	// Setters
	// ========================================================

	/**
	 * Add a format value to a single cell.
	 */
	public void setFormat(GPoint cell, int formatType, Object formatValue) {
		ArrayList<CellRange> crList = new ArrayList<CellRange>();
		crList.add(new CellRange(table.app, cell.x, cell.y));
		setFormat(crList, formatType, formatValue);
	}

	/**
	 * Add a format value to a cell range.
	 */
	public void setFormat(CellRange cr, int formatType, Object formatValue) {
		ArrayList<CellRange> crList = new ArrayList<CellRange>();
		crList.add(cr);
		setFormat(crList, formatType, formatValue);
	}

	/**
	 * Add a format value to a list of cell ranges.
	 */
	public void setFormat(ArrayList<CellRange> crList, int formatType,
			Object value) {

		HashMap<GPoint, Object> formatTable = formatMapArray[formatType];

		// handle select all case first, then exit
		if (table.isSelectAll() && value == null) {
			formatTable.clear();
			return;
		}

		GPoint testCell = new GPoint();
		GPoint testRow = new GPoint();
		GPoint testColumn = new GPoint();

		for (CellRange cr : crList) {
			// cr.debug();
			if (cr.isRow()) {

				// iterate through each row in the selection
				for (int r = cr.getMinRow(); r <= cr.getMaxRow(); ++r) {

					// format the row
					formatTable.put(new GPoint(-1, r), value);

					// handle cells in the row with prior formatting
					for (int col = 0; col < table.getColumnCount(); col++) {
						testCell.setLocation(col, r);
						testColumn.setLocation(col, -1);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testColumn)) {
							formatTable.put(testCell, value);
						}
					}
				}
			}

			else if (cr.isColumn()) {
				// iterate through each column in the selection
				for (int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c) {

					// format the column
					formatTable.put(new GPoint(c, -1), value);

					// handle cells in the column with prior formatting
					for (int row = 0; row < table.getRowCount(); row++) {

						testCell.setLocation(c, row);
						testRow.setLocation(-1, row);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testRow)) {
							// System.out.println(row);
							formatTable.put(testCell, value);
						}
					}
				}

			}

			else {
				// System.out.println("other");
				for (GPoint cellPoint : cr.toCellList(true))
					formatTable.put(cellPoint, value);
			}
		}

		setCellFormatString();
		table.repaint();

	}

	private void setCellFormatString() {
		StringBuilder sb = encodeFormats();
		if (sb == null)
			table.getView().updateCellFormat(null);
		else
			table.getView().updateCellFormat(sb.toString());
	}

	/**
	 * Iterates through the cell ranges of the given list of cell ranges and
	 * sets the border format needed for each cell in order to produce the
	 * specified border style
	 * 
	 * @param cr
	 * @param borderStyle
	 */
	public void setBorderStyle(ArrayList<CellRange> list, int borderStyle) {
		for (CellRange cr : list)
			setBorderStyle(cr, borderStyle);
	}

	/**
	 * Iterates through the cells of the given cell range and sets the border
	 * format needed for each cell in order to produce the specified border
	 * style
	 * 
	 * @param cr
	 * @param borderStyle
	 */
	public void setBorderStyle(CellRange cr, int borderStyle) {

		int r1 = cr.getMinRow();
		int r2 = cr.getMaxRow();
		int c1 = cr.getMinColumn();
		int c2 = cr.getMaxColumn();

		GPoint cell = new GPoint();
		GPoint cell2 = new GPoint();

		// handle select all case first, then exit
		if (table.isSelectAll() && borderStyle == BORDER_STYLE_NONE) {
			formatMapArray[FORMAT_BORDER].clear();
			return;
		}

		if (cr.isRow()) {

			switch (borderStyle) {

			case BORDER_STYLE_NONE:
				setFormat(cr, FORMAT_BORDER, null);
				break;

			case BORDER_STYLE_LEFT:
			case BORDER_STYLE_RIGHT:
				// nothing to draw
				break;

			case BORDER_STYLE_TOP:
				setFormat(cr, FORMAT_BORDER, BORDER_TOP);
				break;

			case BORDER_STYLE_BOTTOM:
				setFormat(cr, FORMAT_BORDER, BORDER_BOTTOM);
				break;

			case BORDER_STYLE_ALL:
				setFormat(cr, FORMAT_BORDER, BORDER_ALL);
				break;

			case BORDER_STYLE_INSIDE:
				setFormat(
						new CellRange(table.app, -1, cr.getMinRow(), -1,
								cr.getMinRow()), FORMAT_BORDER, BORDER_LEFT);
				if (cr.getMinRow() < cr.getMaxRow()) {
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(new CellRange(table.app, -1, cr.getMinRow() + 1,
							-1, cr.getMaxRow()), FORMAT_BORDER, b);
				}
				break;

			case BORDER_STYLE_FRAME:
				setFormat(
						new CellRange(table.app, -1, cr.getMinRow(), -1,
								cr.getMinRow()), FORMAT_BORDER, BORDER_TOP);
				setFormat(
						new CellRange(table.app, -1, cr.getMaxRow(), -1,
								cr.getMaxRow()), FORMAT_BORDER, BORDER_BOTTOM);
				break;
			}

			return;
		}

		if (cr.isColumn()) {

			switch (borderStyle) {

			case BORDER_STYLE_NONE:
				setFormat(cr, FORMAT_BORDER, null);
				break;

			case BORDER_STYLE_TOP:
			case BORDER_STYLE_BOTTOM:
				// nothing to draw

				break;

			case BORDER_STYLE_LEFT:
				setFormat(cr, FORMAT_BORDER, BORDER_LEFT);
				break;

			case BORDER_STYLE_RIGHT:
				setFormat(cr, FORMAT_BORDER, BORDER_RIGHT);
				break;

			case BORDER_STYLE_ALL:
				setFormat(cr, FORMAT_BORDER, BORDER_ALL);
				break;

			case BORDER_STYLE_INSIDE:
				setFormat(
						new CellRange(table.app, cr.getMinColumn(), -1,
								cr.getMinColumn(), -1), FORMAT_BORDER,
						BORDER_TOP);
				if (cr.getMinColumn() < cr.getMaxColumn()) {
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(new CellRange(table.app, cr.getMinColumn() + 1,
							-1, cr.getMaxColumn(), -1), FORMAT_BORDER, b);
				}
				break;

			case BORDER_STYLE_FRAME:
				setFormat(
						new CellRange(table.app, cr.getMinColumn(), -1,
								cr.getMinColumn(), -1), FORMAT_BORDER,
						BORDER_LEFT);
				setFormat(
						new CellRange(table.app, cr.getMaxColumn(), -1,
								cr.getMaxColumn(), -1), FORMAT_BORDER,
						BORDER_RIGHT);
				break;

			}

			return;
		}

		// handle all other selection types

		switch (borderStyle) {
		case BORDER_STYLE_NONE:
			for (int r = r1; r <= r2; r++)
				for (int c = c1; c <= c2; c++)
					setFormat(cr, FORMAT_BORDER, null);
			break;

		case BORDER_STYLE_ALL:

			for (int r = r1; r <= r2; r++)
				for (int c = c1; c <= c2; c++) {
					cell.x = c;
					cell.y = r;
					setFormat(cell, FORMAT_BORDER, BORDER_ALL);
				}
			break;

		case BORDER_STYLE_FRAME:

			// single cell
			if (r1 == r2 && c1 == c2) {
				cell.x = c1;
				cell.y = r1;
				setFormat(cell, FORMAT_BORDER, BORDER_ALL);
				return;
			}

			// top & bottom
			cell.y = r1;
			cell2.y = r2;
			for (int c = c1 + 1; c <= c2 - 1; c++) {
				cell.x = c;
				cell2.x = c;
				if (r1 == r2) {
					byte b = BORDER_TOP + BORDER_BOTTOM;
					setFormat(cell, FORMAT_BORDER, b);
				} else {
					setFormat(cell, FORMAT_BORDER, BORDER_TOP);
					setFormat(cell2, FORMAT_BORDER, BORDER_BOTTOM);
				}
			}
			// left & right
			cell.x = c1;
			cell2.x = c2;
			for (int r = r1 + 1; r <= r2 - 1; r++) {
				cell.y = r;
				cell2.y = r;
				if (c1 == c2) {
					byte b = BORDER_LEFT + BORDER_RIGHT;
					setFormat(cell, FORMAT_BORDER, b);
				} else {
					setFormat(cell, FORMAT_BORDER, BORDER_LEFT);
					setFormat(cell2, FORMAT_BORDER, BORDER_RIGHT);
				}
			}

			// CORNERS

			// case 1: column corners
			if (c1 == c2) {
				cell.x = c1;
				cell.y = r1;
				byte b = BORDER_LEFT + BORDER_RIGHT
						+ BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.x = c1;
				cell.y = r2;
				b = BORDER_LEFT + BORDER_RIGHT
						+ BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);
			}
			// case 2: row corners
			else if (r1 == r2) {
				cell.x = c1;
				cell.y = r1;
				byte b = BORDER_LEFT + BORDER_TOP
						+ BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

				cell.x = c2;
				cell.y = r1;
				b = BORDER_RIGHT + BORDER_TOP + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

			}

			// case 3: block corners
			else {
				cell.y = r1;
				cell.x = c1;
				byte b = BORDER_LEFT + BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.y = r1;
				cell.x = c2;
				b = BORDER_RIGHT + BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.y = r2;
				cell.x = c2;
				b = BORDER_RIGHT + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

				cell.y = r2;
				cell.x = c1;
				b = BORDER_LEFT + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);
			}

			break;

		case BORDER_STYLE_INSIDE:

			for (int r = r1 + 1; r <= r2; r++) {
				cell.x = c1;
				cell.y = r;
				setFormat(cell, FORMAT_BORDER, BORDER_TOP);
			}

			for (int c = c1 + 1; c <= c2; c++) {
				cell.x = c;
				cell.y = r1;
				setFormat(cell, FORMAT_BORDER, BORDER_LEFT);
			}

			for (int r = r1 + 1; r <= r2; r++) {
				for (int c = c1 + 1; c <= c2; c++) {
					cell.x = c;
					cell.y = r;
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(cell, FORMAT_BORDER, b);
				}
			}

			break;

		case BORDER_STYLE_TOP:
			cell.y = r1;
			for (int c = c1; c <= c2; c++) {
				cell.x = c;
				setFormat(cell, FORMAT_BORDER, BORDER_TOP);
			}
			break;

		case BORDER_STYLE_BOTTOM:
			cell.y = r2;
			for (int c = c1; c <= c2; c++) {
				cell.x = c;
				setFormat(cell, FORMAT_BORDER, BORDER_BOTTOM);
			}
			break;

		case BORDER_STYLE_LEFT:
			cell.x = c1;
			for (int r = r1; r <= r2; r++) {
				cell.y = r;
				setFormat(cell, FORMAT_BORDER, BORDER_LEFT);
			}
			break;

		case BORDER_STYLE_RIGHT:
			cell.x = c2;
			for (int r = r1; r <= r2; r++) {
				cell.y = r;
				setFormat(cell, FORMAT_BORDER, BORDER_RIGHT);
			}
			break;

		}
	}

	// ========================================================
	// XML handling
	// ========================================================

	/**
	 * Returns XML representation of the format maps
	 */
	public void getXML(StringBuilder sb) {

		StringBuilder cellFormat = encodeFormats();
		if (cellFormat == null)
			return;

		sb.append("\t<spreadsheetCellFormat ");
		sb.append(" formatMap=\"");
		sb.append(cellFormat);
		sb.append("\"");
		sb.append("/>\n");

	}

	/**
	 * Returns StringBuilder containing all current formats encoded as strings
	 * 
	 * @return
	 */
	public StringBuilder encodeFormats() {

		StringBuilder sb = new StringBuilder();

		// create a set containing all cells with formats
		HashSet<GPoint> masterKeySet = new HashSet<GPoint>();
		for (int i = 0; i < formatMapArray.length; i++)
			masterKeySet.addAll(formatMapArray[i].keySet());
		if (masterKeySet.size() == 0)
			return null;

		// iterate through the set creating XML tags for each cell and its
		// formats
		for (GPoint cell : masterKeySet) {

			sb.append(cellDelimiter);

			sb.append(cell.x);
			sb.append(formatDelimiter);
			sb.append(cell.y);

			Integer align = (Integer) formatMapArray[FORMAT_ALIGN].get(cell);
			if (align != null) {
				sb.append(formatDelimiter);
				sb.append(alignToken);
				sb.append(formatDelimiter);
				sb.append(align);
			}

			Byte border = (Byte) formatMapArray[FORMAT_BORDER].get(cell);
			if (border != null) {
				sb.append(formatDelimiter);
				sb.append(borderToken);
				sb.append(formatDelimiter);
				sb.append(border);
			}

			Color bgColor = (Color) formatMapArray[FORMAT_BGCOLOR].get(cell);
			if (bgColor != null) {
				sb.append(formatDelimiter);
				sb.append(bgColorToken);
				sb.append(formatDelimiter);
				sb.append(bgColor.getRGB()); // convert to RGB integer
			}

			Integer fStyle = (Integer) formatMapArray[FORMAT_FONTSTYLE]
					.get(cell);
			if (fStyle != null) {
				sb.append(formatDelimiter);
				sb.append(fontStyleToken);
				sb.append(formatDelimiter);
				sb.append(fStyle);
			}

		}

		// remove the first delimiter
		sb.deleteCharAt(0);

		return sb;

	}

	/**
	 * Decodes XML string and puts format values into the format maps.
	 * 
	 * @param xml
	 */
	public void processXMLString(String xml) {
		clearAll();
		if (xml == null)
			return;

		// System.out.println("XML:  " + xml);
		String[] cellGroup = xml.split(cellDelimiter);
		// System.out.println("cellGroup:  " +
		// java.util.Arrays.toString(cellGroup));
		for (int i = 0; i < cellGroup.length; i++) {
			if (cellGroup.length > 0)
				processCellFormatString(cellGroup[i]);
		}

	}

	/**
	 * Decodes a string representing a the format objects for a single cell and
	 * then puts these formats into the format maps.
	 * 
	 * @param formatStr
	 */
	private void processCellFormatString(String formatStr) {
		// System.out.println("cellFormat:  " + formatStr);
		String[] f = formatStr.split(formatDelimiter);
		GPoint cell = new GPoint(Integer.parseInt(f[0]), Integer.parseInt(f[1]));
		int formatType;
		Object formatValue;
		for (int i = 2; i < f.length; i = i + 2) {
			formatType = formatTokenMap.get(f[i]);
			formatValue = Integer.parseInt(f[i + 1]);
			if (formatType == FORMAT_BGCOLOR)
				formatValue = new Color((Integer) formatValue);
			else if (formatType == FORMAT_BORDER) {
				int b = (Integer) formatValue;
				formatValue = (byte) b;
			}
			this.setFormat(cell, formatType, formatValue);
		}

	}

}

package org.geogebra.common.spreadsheet.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.view.spreadsheet.HasTableSelection;
import org.geogebra.common.spreadsheet.core.Direction;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;

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

	HasTableSelection table;

	private int highestIndexRow = 0;
	private int highestIndexColumn = 0;
	private String cellFormatString;

	// Array of format tables
	private NonNullHashMap[] formatMapArray;

	// Format types.
	// These are also array indices, so they must be sequential: 0..n
	public static final int FORMAT_ALIGN = 0;
	public static final int FORMAT_BORDER = 1;
	public static final int FORMAT_BGCOLOR = 2;
	public static final int FORMAT_FONTSTYLE = 3;

	private int formatCount = 5;

	// Alignment constants
	public static final int ALIGN_LEFT = 2; // SwingConstants.LEFT;
	public static final int ALIGN_CENTER = 0; // SwingConstants.CENTER;
	public static final int ALIGN_RIGHT = 4; // SwingConstants.RIGHT;

	// Font style constants
	public static final int STYLE_PLAIN = GFont.PLAIN; // Font.PLAIN;
	public static final int STYLE_BOLD = GFont.BOLD; // Font.BOLD;
	public static final int STYLE_ITALIC = GFont.ITALIC; // Font.ITALIC;
	public static final int STYLE_BOLD_ITALIC = GFont.BOLD + GFont.ITALIC;

	// Border style constants used by style bar.
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
	private static HashMap<String, Integer> formatTokenMap = new HashMap<>();

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
	 *            table
	 */
	public CellFormat(HasTableSelection table) {
		this.table = table;

		// Create instances of the format hash maps
		formatMapArray = new NonNullHashMap[formatCount];
		for (int i = 0; i < formatCount; i++) {
			formatMapArray[i] = new NonNullHashMap();
		}
	}

	@Override
	public int getAlignment(int col, int row, boolean isText) {
		Integer alignment = (Integer) getCellFormat(col, row, CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			return alignment;
		} else if (isText) {
			return ALIGN_LEFT;
		} else {
			return ALIGN_RIGHT;
		}
	}

	// ========================================================
	// MyHashMap
	// ========================================================

	/**
	 * Class that extends HashMap so that null values cannot be mapped and a
	 * call to put(key, null) will remove a key if it exists already.
	 * 
	 * TODO: It would be better practice to use an immutable key, e.g. a string
	 * to record the cell location.
	 */
	private static class NonNullHashMap extends HashMap<SpreadsheetCoords, Object> {

		private static final long serialVersionUID = 1L;

		protected NonNullHashMap() {
			// Auto-generated constructor stub
		}

		@Override
		public Object put(SpreadsheetCoords key, Object value) {
			if (value == null) {
				super.remove(key);
				return null;
			}
			return super.put(key, value);
		}
	}

	// ========================================================
	// Clear and Shift Formats
	// ========================================================

	/**
	 * Clears all format objects from the maps
	 */
	public void clearAll() {
		highestIndexRow = 0;
		highestIndexColumn = 0;
		for (int i = 0; i < formatMapArray.length; i++) {
			formatMapArray[i].clear();
		}
	}

	/**
	 * Shifts the formats a set of rows or columns over a given number indices.
	 * The set of rows or columns to be shifted is a block that begins at a
	 * specified start index and includes all larger indices.
	 * 
	 * @param startIndex
	 *            Index of the first row or column to shift.
	 * @param shiftAmount
	 *            Number of indices to increment each row or column
	 * @param direction
	 *            Direction to shift rows or columns (Up or Down = shift rows,
	 *            Left or Right = columns)
	 */
	@Override
	public void shiftFormats(int startIndex, int shiftAmount,
			Direction direction) {

		if (startIndex - shiftAmount < 0) {
			return;
		}

		// shift rows for each format type map
		for (int i = 0; i < formatMapArray.length; i++) {
			if (direction == Direction.Up) {
				shiftRowsUp(formatMapArray[i], startIndex, shiftAmount);
			}
			if (direction == Direction.Down) {
				shiftRowsDown(formatMapArray[i], startIndex, shiftAmount);
			}
			if (direction == Direction.Left) {
				shiftColumnsLeft(formatMapArray[i], startIndex, shiftAmount);
			} else if (direction == Direction.Right) {
				shiftColumnsRight(formatMapArray[i], startIndex, shiftAmount);
			}
		}

		if (direction == Direction.Left) {
			highestIndexColumn = highestIndexColumn - shiftAmount;
		} else if (direction == Direction.Right) {
			highestIndexColumn = highestIndexColumn + shiftAmount;
		} else if (direction == Direction.Up) {
			highestIndexRow = highestIndexRow - shiftAmount;
		} else if (direction == Direction.Down) {
			highestIndexRow = highestIndexRow + shiftAmount;
		}
	}

	private void shiftRowsUp(NonNullHashMap formatMap, int rowStart,
			int shiftAmount) {
		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;
		SpreadsheetCoords shiftKey = null;

		// clear first row to be shifted into
		clearRows(formatMap, rowStart - shiftAmount, rowStart - shiftAmount);

		// shift row formats
		for (int r = rowStart; r <= highestIndexRow; r++) {
			key = newCoords(-1, r);
			if (formatMap.containsKey(key)) {
				shiftKey = newCoords(-1, r - shiftAmount);
				formatMap.put(shiftKey, formatMap.remove(key));
			}
		}

		// shift cell formats
		for (int r = rowStart; r <= highestIndexRow; r++) {
			for (int c = 0; c <= highestIndexColumn; c++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					shiftKey = newCoords(c, r - shiftAmount);
					formatMap.put(shiftKey, formatMap.remove(key));
				}
			}
		}
	}

	private SpreadsheetCoords newCoords(int col, int row) {
		return new SpreadsheetCoords(row, col);
	}

	private void shiftRowsDown(NonNullHashMap formatMap, int rowStart,
			int shiftAmount) {

		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;
		SpreadsheetCoords shiftKey = null;

		// shift row formats
		for (int r = highestIndexRow; r >= rowStart; r--) {
			key = newCoords(-1, r);
			if (formatMap.containsKey(key)) {
				shiftKey = newCoords(-1, r + shiftAmount);
				formatMap.put(shiftKey, formatMap.remove(key));
			}
		}

		// shift cell formats
		for (int r = highestIndexRow; r >= rowStart; r--) {
			for (int c = 0; c <= highestIndexColumn; c++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					shiftKey = newCoords(c, r + shiftAmount);
					formatMap.put(shiftKey, formatMap.remove(key));
				}
			}
		}
	}

	private void clearRows(NonNullHashMap formatMap, int rowStart, int rowEnd) {

		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;

		// clear all row formats
		for (int r = rowStart; r <= rowEnd; r++) {
			key = newCoords(-1, r);
			if (formatMap.containsKey(key)) {
				formatMap.remove(key);
			}
		}

		// clear all cell formats
		for (int r = rowStart; r <= rowEnd; r++) {
			for (int c = 0; c <= highestIndexColumn; c++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					formatMap.remove(key);
				}
			}
		}
	}

	private void shiftColumnsLeft(NonNullHashMap formatMap, int columnStart,
			int shiftAmount) {

		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;
		SpreadsheetCoords shiftKey = null;

		// clear first column to be shifted into
		clearColumns(formatMap, columnStart - shiftAmount,
				columnStart - shiftAmount);

		// shift column formats
		for (int c = columnStart; c <= highestIndexColumn; c++) {
			key = newCoords(c, -1);
			if (formatMap.containsKey(key)) {
				shiftKey = newCoords(c - shiftAmount, -1);
				formatMap.put(shiftKey, formatMap.remove(key));
			}
		}

		// shift cell formats
		for (int c = columnStart; c <= highestIndexColumn; c++) {
			for (int r = 0; r <= highestIndexRow; r++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					shiftKey = newCoords(c - shiftAmount, r);
					formatMap.put(shiftKey, formatMap.remove(key));
				}
			}
		}
	}

	private void shiftColumnsRight(NonNullHashMap formatMap, int columnStart,
			int shiftAmount) {

		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;
		SpreadsheetCoords shiftKey = null;

		// shift column formats
		for (int c = highestIndexColumn; c >= columnStart; c--) {
			key = newCoords(c, -1);
			if (formatMap.containsKey(key)) {
				shiftKey = newCoords(c + shiftAmount, -1);
				formatMap.put(shiftKey, formatMap.remove(key));
			}
		}

		// shift cell formats
		for (int c = highestIndexColumn; c >= columnStart; c--) {
			for (int r = 0; r <= highestIndexRow; r++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					shiftKey = newCoords(c + shiftAmount, r);
					formatMap.put(shiftKey, formatMap.remove(key));
				}
			}
		}

	}

	private void clearColumns(NonNullHashMap formatMap, int columnStart,
			int columnEnd) {

		if (formatMap == null || formatMap.isEmpty()) {
			return;
		}

		SpreadsheetCoords key = null;

		// clear all column formats
		for (int c = columnStart; c <= columnEnd; c++) {
			key = newCoords(c, -1);
			if (formatMap.containsKey(key)) {
				formatMap.remove(key);
			}
		}

		// clear all cell formats
		for (int c = columnStart; c <= columnEnd; c++) {
			for (int r = 0; r <= highestIndexRow; r++) {
				key = newCoords(c, r);
				if (formatMap.containsKey(key)) {
					formatMap.remove(key);
				}
			}
		}
	}

	// ========================================================
	// Getters
	// ========================================================

	/**
	 * Returns the format map for a given cell format
	 * 
	 * @param formatType
	 *            format type
	 * @return map point -&gt; format
	 */
	@Override
	public HashMap<SpreadsheetCoords, Object> getFormatMap(int formatType) {
		return formatMapArray[formatType];
	}

	/**
	 * Returns the format object for a given cell and a given format type. If
	 * format does not exist, returns null.
	 */
	@Override
	public Object getCellFormat(int x, int y, int formatType) {

		NonNullHashMap formatMap = formatMapArray[formatType];
		if (formatMap == null || formatMap.isEmpty()) {
			return null;
		}
		Object formatObject = null;

		// Create special keys for the cell, row and column
		SpreadsheetCoords rowKey = newCoords(-1, y);
		SpreadsheetCoords columnKey = newCoords(x, -1);
		SpreadsheetCoords cellKey = newCoords(x, y);

		// Check there is a format for this cell
		if (formatMap.containsKey(cellKey)) {
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
	 * 
	 * @param cr
	 *            range
	 * @param formatType
	 *            format type
	 * @return cell format
	 */
	public Object getCellFormat(TabularRange cr, int formatType) {
		if (cr == null) {
			return null;
		}
		// Get the format in the upper left cell
		Object format = getCellFormat(cr.getMinColumn(), cr.getMinRow(),
				formatType);

		if (format == null) {
			return null;
		}

		// Iterate through the range and test if they cells have the same format
		for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
				if (!format.equals(getCellFormat(col, row, formatType))) {
					return null;
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
	@Override
	public void setFormat(SpreadsheetCoords cell, int formatType, Object formatValue) {
		ArrayList<TabularRange> crList = new ArrayList<>();
		crList.add(new TabularRange(cell.row, cell.column));
		setFormat(crList, formatType, formatValue);
	}

	/**
	 * @param cell
	 *            cell
	 * @param formatType
	 *            format type
	 * @param formatValue
	 *            format value
	 */
	public void doSetFormat(SpreadsheetCoords cell, int formatType, Object formatValue) {
		ArrayList<TabularRange> crList = new ArrayList<>();
		crList.add(new TabularRange(cell.row, cell.column));
		doSetFormat(crList, formatType, formatValue);
	}

	/**
	 * Add a format value to a cell range.
	 */
	public void setFormat(TabularRange cr, int formatType, Object formatValue) {
		ArrayList<TabularRange> crList = new ArrayList<>();
		crList.add(cr);
		setFormat(crList, formatType, formatValue);
	}

	/**
	 * Add a format value to a list of cell ranges.
	 */
	public void setFormat(ArrayList<TabularRange> crList, int formatType,
			Object value) {

		doSetFormat(crList, formatType, value);

		setCellFormatString();
		if (table != null) {
			table.updateCellFormat(cellFormatString);
			table.repaint();
		}
	}

	private void doSetFormat(ArrayList<TabularRange> crList, int formatType,
			Object value) {
		HashMap<SpreadsheetCoords, Object> formatTable = formatMapArray[formatType];

		// handle select all case first, then exit
		if (table != null && table.isSelectAll() && value == null) {
			formatTable.clear();
			return;
		}

		SpreadsheetCoords testCell = new SpreadsheetCoords();
		SpreadsheetCoords testRow = new SpreadsheetCoords();
		SpreadsheetCoords testColumn = new SpreadsheetCoords();

		for (TabularRange cr : crList) {
			// cr.debug();
			if (cr.isRow()) {

				if (highestIndexRow < cr.getMaxRow()) {
					highestIndexRow = cr.getMaxRow();
				}

				// iterate through each row in the selection
				for (int r = cr.getMinRow(); r <= cr.getMaxRow(); ++r) {

					// format the row
					formatTable.put(newCoords(-1, r), value);
					// handle cells in the row with prior formatting
					for (int col = 0; col < highestIndexColumn; col++) {
						testCell.setLocation(r, col);
						testColumn.setLocation(-1, col);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testColumn)) {
							formatTable.put(testCell, value);
						}
					}
				}
			}

			else if (cr.isColumn()) {

				if (highestIndexColumn < cr.getMaxColumn()) {
					highestIndexColumn = cr.getMaxColumn();
				}

				// iterate through each column in the selection
				for (int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c) {

					// format the column
					formatTable.put(newCoords(c, -1), value);

					// handle cells in the column with prior formatting
					for (int row = 0; row < highestIndexRow; row++) {

						testCell.setLocation(row, c);
						testRow.setLocation(row, -1);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testRow)) {
							formatTable.put(testCell, value);
						}
					}
				}

			}

			else {

				if (highestIndexRow < cr.getMaxRow()) {
					highestIndexRow = cr.getMaxRow();
				}
				if (highestIndexColumn < cr.getMaxColumn()) {
					highestIndexColumn = cr.getMaxColumn();
				}

				for (SpreadsheetCoords cellPoint : cr.toCellList(true)) {
					formatTable.put(cellPoint, value);
				}
			}
		}
	}

	private void setCellFormatString() {
		StringBuilder sb = encodeFormats();
		if (sb == null) {
			cellFormatString = null;
		} else {
			cellFormatString = sb.toString();
		}
	}

	/**
	 * Iterates through the cell ranges of the given list of cell ranges and
	 * sets the border format needed for each cell in order to produce the
	 * specified border style
	 * 
	 * @param list
	 *            cell ranges
	 * @param borderStyle
	 *            border style
	 */
	public void setBorderStyle(ArrayList<TabularRange> list, int borderStyle) {
		for (TabularRange cr : list) {
			setBorderStyle(cr, borderStyle);
		}
	}

	/**
	 * Iterates through the cells of the given cell range and sets the border
	 * format needed for each cell in order to produce the specified border
	 * style
	 * 
	 * @param cr
	 *            cell range
	 * @param borderStyle
	 *            border style
	 */
	public void setBorderStyle(TabularRange cr, int borderStyle) {

		int r1 = cr.getMinRow();
		int r2 = cr.getMaxRow();
		int c1 = cr.getMinColumn();
		int c2 = cr.getMaxColumn();

		SpreadsheetCoords cell = new SpreadsheetCoords();
		SpreadsheetCoords cell2 = new SpreadsheetCoords();

		// handle select all case first, then exit
		if (table.isSelectAll() && borderStyle == BORDER_STYLE_NONE) {
			formatMapArray[FORMAT_BORDER].clear();
			return;
		}

		if (cr.isRow()) {

			switch (borderStyle) {

			default:
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
				setFormat(new TabularRange(cr.getMinRow(), -1, cr.getMinRow(), -1
				), FORMAT_BORDER, BORDER_LEFT);
				if (cr.getMinRow() < cr.getMaxRow()) {
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(new TabularRange(cr.getMinRow() + 1, -1, cr.getMaxRow(), -1
					), FORMAT_BORDER, b);
				}
				break;

			case BORDER_STYLE_FRAME:
				setFormat(new TabularRange(cr.getMinRow(), -1, cr.getMinRow(), -1
				), FORMAT_BORDER, BORDER_TOP);
				setFormat(new TabularRange(cr.getMaxRow(), -1, cr.getMaxRow(), -1
				), FORMAT_BORDER, BORDER_BOTTOM);
				break;
			}

			return;
		}

		if (cr.isColumn()) {

			switch (borderStyle) {

			default:
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
						new TabularRange(-1, cr.getMinColumn(),
								-1, cr.getMinColumn()),
						FORMAT_BORDER, BORDER_TOP);
				if (cr.getMinColumn() < cr.getMaxColumn()) {
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(new TabularRange(-1, cr.getMinColumn() + 1,
							-1, cr.getMaxColumn()), FORMAT_BORDER, b);
				}
				break;

			case BORDER_STYLE_FRAME:
				setFormat(
						new TabularRange(-1, cr.getMinColumn(),
								-1, cr.getMinColumn()),
						FORMAT_BORDER, BORDER_LEFT);
				setFormat(
						new TabularRange(-1, cr.getMaxColumn(),
								-1, cr.getMaxColumn()),
						FORMAT_BORDER, BORDER_RIGHT);
				break;

			}

			return;
		}

		// handle all other selection types

		switch (borderStyle) {
		case BORDER_STYLE_NONE:
			for (int r = r1; r <= r2; r++) {
				for (int c = c1; c <= c2; c++) {
					setFormat(cr, FORMAT_BORDER, null);
				}
			}
			break;

		case BORDER_STYLE_ALL:

			for (int r = r1; r <= r2; r++) {
				for (int c = c1; c <= c2; c++) {
					cell.column = c;
					cell.row = r;
					setFormat(cell, FORMAT_BORDER, BORDER_ALL);
				}
			}
			break;

		case BORDER_STYLE_FRAME:

			// single cell
			if (r1 == r2 && c1 == c2) {
				cell.column = c1;
				cell.row = r1;
				setFormat(cell, FORMAT_BORDER, BORDER_ALL);
				return;
			}

			// top & bottom
			cell.row = r1;
			cell2.row = r2;
			for (int c = c1 + 1; c <= c2 - 1; c++) {
				cell.column = c;
				cell2.column = c;
				if (r1 == r2) {
					byte b = BORDER_TOP + BORDER_BOTTOM;
					setFormat(cell, FORMAT_BORDER, b);
				} else {
					setFormat(cell, FORMAT_BORDER, BORDER_TOP);
					setFormat(cell2, FORMAT_BORDER, BORDER_BOTTOM);
				}
			}
			// left & right
			cell.column = c1;
			cell2.column = c2;
			for (int r = r1 + 1; r <= r2 - 1; r++) {
				cell.row = r;
				cell2.row = r;
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
				cell.column = c1;
				cell.row = r1;
				byte b = BORDER_LEFT + BORDER_RIGHT + BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.column = c1;
				cell.row = r2;
				b = BORDER_LEFT + BORDER_RIGHT + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);
			}
			// case 2: row corners
			else if (r1 == r2) {
				cell.column = c1;
				cell.row = r1;
				byte b = BORDER_LEFT + BORDER_TOP + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

				cell.column = c2;
				cell.row = r1;
				b = BORDER_RIGHT + BORDER_TOP + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

			}

			// case 3: block corners
			else {
				cell.row = r1;
				cell.column = c1;
				byte b = BORDER_LEFT + BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.row = r1;
				cell.column = c2;
				b = BORDER_RIGHT + BORDER_TOP;
				setFormat(cell, FORMAT_BORDER, b);

				cell.row = r2;
				cell.column = c2;
				b = BORDER_RIGHT + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);

				cell.row = r2;
				cell.column = c1;
				b = BORDER_LEFT + BORDER_BOTTOM;
				setFormat(cell, FORMAT_BORDER, b);
			}

			break;

		case BORDER_STYLE_INSIDE:

			for (int r = r1 + 1; r <= r2; r++) {
				cell.column = c1;
				cell.row = r;
				setFormat(cell, FORMAT_BORDER, BORDER_TOP);
			}

			for (int c = c1 + 1; c <= c2; c++) {
				cell.column = c;
				cell.row = r1;
				setFormat(cell, FORMAT_BORDER, BORDER_LEFT);
			}

			for (int r = r1 + 1; r <= r2; r++) {
				for (int c = c1 + 1; c <= c2; c++) {
					cell.column = c;
					cell.row = r;
					byte b = BORDER_LEFT + BORDER_TOP;
					setFormat(cell, FORMAT_BORDER, b);
				}
			}

			break;

		case BORDER_STYLE_TOP:
			cell.row = r1;
			for (int c = c1; c <= c2; c++) {
				cell.column = c;
				setFormat(cell, FORMAT_BORDER, BORDER_TOP);
			}
			break;

		case BORDER_STYLE_BOTTOM:
			cell.row = r2;
			for (int c = c1; c <= c2; c++) {
				cell.column = c;
				setFormat(cell, FORMAT_BORDER, BORDER_BOTTOM);
			}
			break;

		case BORDER_STYLE_LEFT:
			cell.column = c1;
			for (int r = r1; r <= r2; r++) {
				cell.row = r;
				setFormat(cell, FORMAT_BORDER, BORDER_LEFT);
			}
			break;

		case BORDER_STYLE_RIGHT:
			cell.column = c2;
			for (int r = r1; r <= r2; r++) {
				cell.row = r;
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
	@Override
	public void getXML(StringBuilder sb) {

		StringBuilder cellFormat = encodeFormats();
		if (cellFormat == null) {
			return;
		}

		sb.append("\t<spreadsheetCellFormat ");
		sb.append(" formatMap=\"");
		sb.append(cellFormat);
		sb.append("\"");
		sb.append("/>\n");

	}

	/**
	 * 
	 * @return StringBuilder object containing all current formats encoded as
	 *         strings
	 */
	public StringBuilder encodeFormats() {

		StringBuilder sb = new StringBuilder();

		// create a set containing all cells with formats
		HashSet<SpreadsheetCoords> masterKeySet = new HashSet<>();
		for (int i = 0; i < formatMapArray.length; i++) {
			masterKeySet.addAll(formatMapArray[i].keySet());
		}
		if (masterKeySet.size() == 0) {
			return null;
		}

		// iterate through the set creating XML tags for each cell and its
		// formats
		for (SpreadsheetCoords cell : masterKeySet) {

			sb.append(cellDelimiter);

			sb.append(cell.column);
			sb.append(formatDelimiter);
			sb.append(cell.row);

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

			GColor bgColor = (GColor) formatMapArray[FORMAT_BGCOLOR].get(cell);
			if (bgColor != null) {
				sb.append(formatDelimiter);
				sb.append(bgColorToken);
				sb.append(formatDelimiter);
				sb.append(bgColor.getARGB()); // convert to RGB integer
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
	 *            String to be decoded
	 */
	@Override
	public void processXMLString(String xml) {
		clearAll();
		if (xml == null) {
			return;
		}

		String[] cellGroup = xml.split(cellDelimiter);
		for (int i = 0; i < cellGroup.length; i++) {
			if (cellGroup.length > 0) {
				processCellFormatString(cellGroup[i]);
			}
		}
		setCellFormatString();
		if (table != null) {
			table.updateCellFormat(cellFormatString);
		}
	}

	/**
	 * Decodes a string representing the format objects for a single cell and
	 * then puts these formats into the format maps.
	 * 
	 * @param formatStr
	 *            format string
	 */
	private void processCellFormatString(String formatStr) {
		if ("null".equals(formatStr)) {
			return;
		}
		String[] f = formatStr.split(formatDelimiter);
		SpreadsheetCoords cell = newCoords(Integer.parseInt(f[0]),
				Integer.parseInt(f[1]));
		int formatType;
		Object formatValue;
		for (int i = 2; i < f.length; i = i + 2) {
			formatType = formatTokenMap.get(f[i]);
			if (formatType == FORMAT_BGCOLOR) {

				// #4299 changed to Long
				// this Integer is of the form 0xAARRGGBB,
				// so remove the alpha channel to make it positive
				int fv = (int) (Long.parseLong(f[i + 1]) & 0x00ffffff);

				formatValue = GColor.newColorRGB(fv);
			} else if (formatType == FORMAT_BORDER) {
				long b = Long.parseLong(f[i + 1]);
				formatValue = (byte) b;
			} else {
				formatValue = Integer.parseInt(f[i + 1]);
			}
			this.doSetFormat(cell, formatType, formatValue);
		}

	}

	/**
	 * @param value
	 *            value
	 * @param position
	 *            bit position
	 * @return whether given bit is 0
	 */
	public static boolean isZeroBit(int value, int position) {
		return (value & (1 << position)) == 0;
	}

	/**
	 * @param value
	 *            value
	 * @param position
	 *            bit position
	 * @return whether given bit is 1
	 */
	public static boolean isOneBit(Byte value, int position) {
		if (value == null) {
			return false;
		}
		return ((int) value & (1 << position)) != 0;
	}

	/**
	 * @param alignment
	 *            alignment, see ALIGN_ constants
	 * @return "l", "c" or "r" for left/center/right
	 */
	public static char getAlignmentString(int alignment) {
		switch (alignment) {
		default:
		case CellFormat.ALIGN_LEFT:
			return 'l';
		case CellFormat.ALIGN_CENTER:
			return 'c';
		case CellFormat.ALIGN_RIGHT:
			return 'r';
		}
	}

	@Override
	public void setTable(HasTableSelection table) {
		this.table = table;
	}

}

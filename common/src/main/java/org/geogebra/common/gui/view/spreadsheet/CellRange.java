package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * Utility class for spreadsheet cell ranges.
 * 
 * A cell range is any rectangular block of cells defined by the column and row
 * index values of two diagonal corner cells. One corner is designated as the
 * anchor cell.
 * 
 * Rows and columns are defined using index values of -1 as follows. row:
 * minColumn and maxColumn = -1 column: minRow and maxRow = -1
 * 
 * @author George Sturr, 2010-1-23
 */

final public class CellRange {

	private int minColumn = -1;
	private int minRow = -1;
	private int maxColumn = -1;
	private int maxRow = -1;
	private int anchorColumn = -1;
	private int anchorRow = -1;

	// private MyTable table;
	App app;
	private SpreadsheetTableModel tableModel;

	/**
	 * Constructs an empty CellRange
	 * 
	 * @param app
	 *            application
	 */
	public CellRange(App app) {
		this.tableModel = app.getSpreadsheetTableModel();
		this.app = app;
	}

	/**
	 * Constructs a CellRange using all row/column indices
	 * 
	 * @param app
	 *            application
	 * @param anchorColumn
	 *            anchor column
	 * @param anchorRow
	 *            anchor row
	 * @param minColumn
	 *            min column
	 * @param minRow
	 *            min row
	 * @param maxColumn
	 *            max column
	 * @param maxRow
	 *            max row
	 */
	public CellRange(App app, int anchorColumn, int anchorRow, int minColumn,
			int minRow, int maxColumn, int maxRow) {

		this.tableModel = app.getSpreadsheetTableModel();
		this.app = app;
		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
		this.minColumn = minColumn;
		this.minRow = minRow;
		this.maxColumn = maxColumn;
		this.maxRow = maxRow;
	}

	/**
	 * Constructs a CellRange from an anchor and opposite corner
	 * 
	 * @param app
	 *            application
	 * @param anchorColumn
	 *            anchor column
	 * @param anchorRow
	 *            anchor row
	 * @param col2
	 *            opposite column
	 * @param row2
	 *            opposite row
	 */
	public CellRange(App app, int anchorColumn, int anchorRow, int col2,
			int row2) {

		this.tableModel = app.getSpreadsheetTableModel();
		this.app = app;
		setCellRange(anchorColumn, anchorRow, col2, row2);

	}

	/**
	 * Constructs a CellRange for single cell
	 * 
	 * @param app
	 *            app
	 * @param anchorColumn
	 *            anchor column
	 * @param anchorRow
	 *            anchor row
	 */
	public CellRange(App app, int anchorColumn, int anchorRow) {

		this.tableModel = app.getSpreadsheetTableModel();
		this.app = app;
		setCellRange(anchorColumn, anchorRow, anchorColumn, anchorRow);

	}

	// TODO Constructor with string parameter, e.g. CellRange("A1:B10")

	/**
	 * Set cell range for a single cell
	 * 
	 * @param anchorColumn
	 *            anchor column
	 * @param anchorRow
	 *            anchor row
	 */
	public void setCellRange(int anchorColumn, int anchorRow) {
		setCellRange(anchorColumn, anchorRow, anchorColumn, anchorRow);
	}

	/**
	 * Set a cell range using diagonal corner cells. Can be any two diagonal
	 * corners in either order.
	 */
	public void setCellRange(int anchorColumn, int anchorRow, int col2,
			int row2) {

		minColumn = Math.min(anchorColumn, col2);
		maxColumn = Math.max(anchorColumn, col2);
		minRow = Math.min(anchorRow, row2);
		maxRow = Math.max(anchorRow, row2);

		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;

	}

	public int getAnchorColumn() {
		return anchorColumn;
	}

	public int getAnchorRow() {
		return anchorRow;
	}

	public int getMinColumn() {
		return minColumn;
	}

	public int getMinRow() {
		return minRow;
	}

	public int getMaxColumn() {
		return maxColumn;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public boolean isSingleCell() {
		return (maxColumn == minColumn) && (maxRow == minRow);
	}

	public boolean isColumn() {
		return anchorRow == -1;
	}

	public boolean isRow() {
		return anchorColumn == -1;
	}

	/**
	 * @return height and width
	 */
	public int[] getActualDimensions() {
		int[] d = new int[2];
		CellRange cr = getActualRange();
		d[0] = cr.maxRow - cr.minRow + 1;
		d[1] = cr.maxColumn - cr.minColumn + 1;
		return d;
	}

	// TODO -- refactor this name, should mean has either exactly 2 rows or
	// exactly 2 columns
	/**
	 * @return true if cell range is 2xn or nx2
	 */
	public boolean is2D() {
		return (maxColumn - minColumn == 1) || (maxRow - minRow == 1);
	}

	/**
	 * @return true if cell range is 3xn or nx3
	 */
	public boolean is3D() {
		return (maxColumn - minColumn == 2) || (maxRow - minRow == 2);
	}

	/**
	 * @return true if cell range is 1xn, nx1, a row or a column
	 */
	public boolean is1D() {
		return (maxColumn - minColumn == 0) || (maxRow - minRow == 0);
	}

	/**
	 * @return true if cell range is part of a row, but bigger than one cell
	 */
	public boolean isPartialRow() {
		return !isSingleCell() && !isRow() && (maxRow - minRow == 0);
	}

	/**
	 * @return true if cell range is part of a column, but bigger than one cell
	 */
	public boolean isPartialColumn() {
		return !isSingleCell() && !isColumn() && (maxColumn - minColumn == 0);
	}

	/** @return true if this cell range contains no geos */
	public boolean isEmpty() {
		return toGeoList().size() == 0;
	}

	/** @return true if this range contains no cells */
	public boolean isEmptyRange() {
		return minColumn == -1 && maxColumn == -1 && minRow == -1
				&& maxRow == -1;
	}

	/**
	 * @return true if all non-empty cells in the given range are GeoPoint
	 */
	public boolean isPointList() {
		for (int col = minColumn; col <= maxColumn; ++col) {
			for (int row = minRow; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);

				if (geo != null && !geo.isGeoPoint()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return a new cell range that holds the actual cell range, e.g.
	 *         (-1,1,-1,4) ---&gt; (0,1,100,4)
	 */
	public CellRange getActualRange() {

		CellRange adjustedCellRange = duplicate();

		if (minRow == -1 && maxRow == -1 && minColumn != -1) {
			adjustedCellRange.minRow = 0;
			adjustedCellRange.maxRow = tableModel.getRowCount() - 1;
		}

		if (minColumn == -1 && maxColumn == -1 && minRow != -1) {
			adjustedCellRange.minColumn = 0;
			adjustedCellRange.maxColumn = tableModel.getColumnCount() - 1;
		}

		return adjustedCellRange;
	}

	/**
	 * Sets the corners of a row or column to the actual cell range e.g.
	 * (-1,1,-1,4) ---&gt; (0,1,100,4)
	 */
	public void setActualRange() {

		if (minRow == -1 && maxRow == -1 && minColumn == -1 && maxColumn == -1) {
			return;
		}

		if (minRow == -1 && maxRow == -1) {
			minRow = 0;
			maxRow = tableModel.getRowCount() - 1;
		}

		if (minColumn == -1 && maxColumn == -1) {
			minColumn = 0;
			maxColumn = tableModel.getColumnCount() - 1;
		}

	}

	public int getWidth() {
		return maxColumn - minColumn + 1;
	}

	public int getHeight() {
		return maxRow - minRow + 1;
	}

	/**
	 * ArrayList of all geos found in the cell range
	 * 
	 * @return list of elements
	 */
	public ArrayList<GeoElement> toGeoList() {

		ArrayList<GeoElement> list = new ArrayList<>();

		for (int col = minColumn; col <= maxColumn; ++col) {
			for (int row = minRow; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo != null) {
					list.add(geo);
				}
			}
		}
		return list;
	}

	/**
	 * ArrayList of labels for each geo found in the cell range
	 * 
	 * @param scanByColumn
	 *            whether to sort by column first
	 * @param copyByValue
	 *            whether to copy values
	 * @return list of definitions or values
	 */
	public ArrayList<String> toGeoLabelList(boolean scanByColumn,
			boolean copyByValue) {

		ArrayList<String> list = new ArrayList<>();

		if (scanByColumn) {
			for (int col = minColumn; col <= maxColumn; ++col) {
				for (int row = minRow; row <= maxRow; ++row) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null) {
						if (copyByValue) {
							list.add(geo.getValueForInputBar());
						} else {
							list.add(geo
									.getLabel(StringTemplate.defaultTemplate));
						}
					}
				}
			}
		} else {
			for (int row = minRow; row <= maxRow; ++row) {
				for (int col = minColumn; col <= maxColumn; ++col) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null) {
						if (copyByValue) {
							list.add(geo.getValueForInputBar());
						} else {
							list.add(geo
									.getLabel(StringTemplate.defaultTemplate));
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * @return description e.g. A2:C3
	 */
	public String getLabel() {
		return GeoElementSpreadsheet.getSpreadsheetCellName(minColumn, minRow)
				+ ":" + GeoElementSpreadsheet.getSpreadsheetCellName(maxColumn,
						maxRow);
	}

	/**
	 * ArrayList of geo value string for each geo found in the given cell range
	 * 
	 * @param scanByColumn
	 *            whether to sort by column
	 * @return list of values
	 */
	public ArrayList<String> toGeoValueList(boolean scanByColumn) {

		ArrayList<String> list = new ArrayList<>();
		CellRange cr = getActualRange();

		if (scanByColumn) {
			for (int col = cr.minColumn; col <= cr.maxColumn; ++col) {
				for (int row = cr.minRow; row <= cr.maxRow; ++row) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null) {
						list.add(geo
								.toValueString(StringTemplate.defaultTemplate));
					}
				}
			}
		} else {
			for (int row = cr.minRow; row <= cr.maxRow; ++row) {
				for (int col = cr.minColumn; col <= cr.maxColumn; ++col) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null) {
						list.add(geo
								.toValueString(StringTemplate.defaultTemplate));
					}
				}
			}
		}

		return list;
	}

	/**
	 * @return list of single column ranges that cover this range
	 */
	public ArrayList<CellRange> toPartialColumnList() {
		ArrayList<CellRange> list = new ArrayList<>();

		if (isColumn()) {
			for (int col = minColumn; col <= maxColumn; col++) {
				CellRange cr = new CellRange(app, col, -1, col, 0, col, maxRow);
				list.add(cr);
				// cr.debug();
			}
		} else {
			for (int col = minColumn; col <= maxColumn; col++) {
				list.add(new CellRange(app, col, minRow, col, maxRow));
			}
		}

		return list;
	}

	/**
	 * @return list of single row ranges that cover this range
	 */
	public ArrayList<CellRange> toPartialRowList() {
		ArrayList<CellRange> list = new ArrayList<>();

		if (isRow()) {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new CellRange(app, 0, row, -1, row, maxColumn, row));
			}
		} else {
			for (int row = minRow; row <= maxRow; row++) {
				list.add(new CellRange(app, minColumn, row, maxColumn, row));
			}
		}
		return list;
	}

	/**
	 * ArrayList of all cells found in the cell range
	 * 
	 * @param scanByColumn
	 *            whether to sort by column
	 * @return list of all coords in the range
	 */
	public ArrayList<GPoint> toCellList(boolean scanByColumn) {

		ArrayList<GPoint> list = new ArrayList<>();
		if (scanByColumn) {
			for (int col = minColumn; col <= maxColumn; ++col) {
				for (int row = minRow; row <= maxRow; ++row) {
					list.add(new GPoint(col, row));
				}
			}
		} else {
			for (int row = minRow; row <= maxRow; ++row) {
				for (int col = minColumn; col <= maxColumn; ++col) {
					list.add(new GPoint(col, row));
				}
			}
		}

		return list;
	}

	/**
	 * @param cr
	 *            other range
	 * @return whether this has same anchor coords as other range
	 */
	public boolean hasSameAnchor(CellRange cr) {
		return (cr.anchorRow == anchorRow) && (cr.anchorColumn == anchorColumn);
	}

	/** @return true if at least one cell is empty (has no geo) */
	public boolean hasEmptyCells() {
		boolean hasEmptyCells = false;
		for (int col = minColumn; col <= maxColumn; ++col) {
			for (int row = minRow; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo == null) {
					return true;
				}
			}
		}

		return hasEmptyCells;
	}

	/**
	 * Returns the number of GeoElements of a given GeoClass type contained in
	 * this cell range
	 * 
	 * @param geoClass
	 *            the GeoClass type to count. If null, then all GeoElements are
	 *            counted
	 * @return count of geos of given type in the range
	 */
	public int getGeoCount(GeoClass geoClass) {
		int count = 0;
		if (geoClass != null) {
			for (int col = getMinColumn(); col <= getMaxColumn(); ++col) {
				for (int row = getMinRow(); row <= getMaxRow(); ++row) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null && geo.getGeoClassType() == geoClass) {
						++count;
					}
				}
			}
		} else {
			for (int col = getMinColumn(); col <= getMaxColumn(); ++col) {
				for (int row = getMinRow(); row <= getMaxRow(); ++row) {
					if (RelativeCopy.getValue(app, col, row) != null) {
						++count;
					}
				}
			}

		}
		return count;
	}

	/**
	 * @param geoClass
	 *            class of construction elements
	 * @return true if this CellRange contains a GeoElement of the given
	 *         GeoClass type
	 */
	public boolean containsGeoClass(GeoClass geoClass) {
		for (int col = getMinColumn(); col <= getMaxColumn(); ++col) {
			for (int row = getMinRow(); row <= getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo != null && geo.getGeoClassType() == geoClass) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return true if the cell range has valid coordinates for this table
	 */
	public boolean isValid() {
		return (minRow >= -1 && minRow < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP)
				&& (maxRow >= -1
						&& maxRow < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP)
				&& (minColumn >= -1
						&& minColumn < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP)
				&& (maxColumn >= -1
						&& maxColumn < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP);
	}

	/**
	 * @return cuplicate of this range
	 */
	public CellRange duplicate() {
		CellRange cr = new CellRange(app);
		cr.anchorColumn = anchorColumn;
		cr.anchorRow = anchorRow;
		cr.minColumn = minColumn;
		cr.maxColumn = maxColumn;
		cr.minRow = minRow;
		cr.maxRow = maxRow;
		return cr;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CellRange) {
			CellRange cr = (CellRange) obj;
			return cr.minColumn == minColumn && cr.minRow == minRow
					&& cr.maxColumn == maxColumn && cr.maxRow == maxRow
					&& cr.anchorColumn == anchorColumn
					&& cr.anchorRow == anchorRow;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (minColumn << 24) ^ ((maxColumn - minColumn) << 16)
				^ (minRow << 8) ^ (maxRow - minRow);
	}

	/**
	 * @param geo
	 *            construction element
	 * @return true if the given GeoElement is a spreadsheet cell contained
	 *         inside this cell range
	 */
	public boolean contains(GeoElement geo) {
		return contains(geo.getSpreadsheetCoords());
	}

	/**
	 * @param location
	 *            spreadsheet coords
	 * @return true if the given spreadsheet cell location is contained in this
	 *         cell range
	 */
	public boolean contains(GPoint location) {
		if (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP
				&& location.y < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP) {
			setActualRange();
			return location.y >= minRow && location.y <= maxRow
					&& location.x >= minColumn && location.x <= maxColumn;
		}
		return false;
	}

	/**
	 * Prints debugging information about the cell range
	 */
	public void debug() {
		Log.debug("-------------------------\n");
		Log.debug(toString());
	}

	@Override
	public String toString() {
		return "(" + anchorColumn + "," + anchorRow + ") in (" + minColumn + ","
				+ minRow + ") : (" + maxColumn + "," + maxRow + ")"
				+ (isRow() ? "row" : "") + (isColumn() ? "column" : "");
	}

}

/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.spreadsheet.core.TabularRange;

/**
 * Utility class for spreadsheet tabular ranges.
 * 
 * <p>Contains methods that cannot be part of TabularRange because they need to
 * depend on the particular data provider (by reference Kernel, App or SpreadsheetTableModel)
 */

final public class CellRangeUtil {

	/**
	 * @param selection cell range
	 * @param model spreadsheet model
	 * @return true if this cell range contains no geos
	 */
	public static boolean isEmpty(@CheckForNull TabularRange selection,
			SpreadsheetTableModel model) {
		return selection != null && toGeoList(selection, model).isEmpty();
	}

	/**
	 * @param selection cell range
	 * @param model spreadsheet model
	 * @return true if all non-empty cells in the given range are GeoPoint
	 */
	public static boolean isPointList(TabularRange selection, SpreadsheetTableModel model) {
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(model, col, row);

				if (geo != null && !geo.isGeoPoint()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param general arbitrary selection
	 * @param tableModel table model
	 * @return intersection of potential selection with table model
	 */
	public static TabularRange getActual(TabularRange general, SpreadsheetTableModel tableModel) {
		return general.restrictTo(tableModel.getRowCount(), tableModel.getColumnCount());
	}

	/**
	 * ArrayList of all geos found in the cell range
	 * @param selection cell range
	 * @param model spreadsheet model
	 * @return list of elements
	 */
	public static ArrayList<GeoElement> toGeoList(TabularRange selection,
			SpreadsheetTableModel model) {

		ArrayList<GeoElement> list = new ArrayList<>();

		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(model, col, row);
				if (geo != null) {
					list.add(geo);
				}
			}
		}
		return list;
	}

	/**
	 * @param selection cell range
	 * @return description e.g. A2:C3
	 */
	public static String getLabel(TabularRange selection) {
		return GeoElementSpreadsheet.getSpreadsheetCellName(selection.getMinColumn(),
				selection.getMinRow())
				+ ":" + GeoElementSpreadsheet.getSpreadsheetCellName(selection.getMaxColumn(),
						selection.getMaxRow());
	}

	/**
	 * @param selection cell range
	 * @param tableModel table model
	 * @return true if at least one cell is empty (has no geo)
	 */
	public static  boolean hasEmptyCells(TabularRange selection,
			SpreadsheetTableModel tableModel) {
		boolean hasEmptyCells = false;
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(tableModel, col, row);
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
	 * @param selection cell range
	 * @param geoClass
	 *            the GeoClass type to count. If null, then all GeoElements are
	 *            counted
	 * @param tableModel table model
	 * @return count of geos of given type in the range
	 */
	public static int getGeoCount(TabularRange selection, GeoClass geoClass,
			SpreadsheetTableModel tableModel) {
		int count = 0;
		if (geoClass != null) {
			for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
				for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
					GeoElement geo = RelativeCopy.getValue(tableModel, col, row);
					if (geo != null && geo.getGeoClassType() == geoClass) {
						++count;
					}
				}
			}
		} else {
			for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
				for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
					if (RelativeCopy.getValue(tableModel, col, row) != null) {
						++count;
					}
				}
			}

		}
		return count;
	}

	/**
	 * @param selection selected range
	 * @param geoClass
	 *            class of construction elements
	 * @param tableModel table model
	 * @return true if this range contains a GeoElement of the given
	 *         GeoClass type
	 */
	public static boolean containsGeoClass(TabularRange selection, GeoClass geoClass,
			SpreadsheetTableModel tableModel) {
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(tableModel, col, row);
				if (geo != null && geo.getGeoClassType() == geoClass) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param ranges
	 *            selected ranges
	 * @param geoClass
	 *            desired class
	 * @param tableModel table model
	 * @return true if the given ranges contains a GeoElement of the given
	 *         GeoClass type
	 */
	public static boolean containsGeoClass(List<TabularRange> ranges,
			GeoClass geoClass, SpreadsheetTableModel tableModel) {
		for (TabularRange range : ranges) {
			if (CellRangeUtil.containsGeoClass(range, geoClass, tableModel)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param range
	 *            range
	 * @param onlyFirstRowColumn
	 *            whether to return only first column
	 * @param loc localization
	 * @return range description ("Row 7", "Column B", "A1:D3")
	 */
	public static String getCellRangeString(TabularRange range,
			boolean onlyFirstRowColumn, Localization loc) {
		String s;

		if (range.isContiguousColumns()) {
			s = loc.getCommand("Column") + " " + GeoElementSpreadsheet
					.getSpreadsheetColumnName(range.getMinColumn());
			if (!onlyFirstRowColumn && !range.is1D()) {
				s += " : " + loc.getCommand("Column") + " "
						+ GeoElementSpreadsheet
						.getSpreadsheetColumnName(range.getMaxColumn());
			}

		} else if (range.isContiguousRows()) {
			s = loc.getCommand("Row") + " " + (range.getMinRow() + 1);

			if (!onlyFirstRowColumn && !range.is1D()) {
				s += " : " + loc.getCommand("Row") + " "
						+ (range.getMaxRow() + 1);
			}

		} else {
			s = GeoElementSpreadsheet.getSpreadsheetCellName(
					range.getMinColumn(), range.getMinRow());
			s += ":";
			s += GeoElementSpreadsheet.getSpreadsheetCellName(
					range.getMaxColumn(), range.getMaxRow());
		}

		return s;
	}

	/**
	 * @param ranges
	 *            list of ranges
	 * @param loc localization
	 * @return list of range descriptions ("Row 7", "Column B", "A1:D3")
	 */
	public static String getCellRangeString(List<TabularRange> ranges, Localization loc) {
		StringBuilder sb = new StringBuilder();
		for (TabularRange range : ranges) {
			sb.append(getCellRangeString(range, false, loc));
			sb.append(", ");
		}
		sb.deleteCharAt(sb.lastIndexOf(", "));

		return sb.toString();
	}

}

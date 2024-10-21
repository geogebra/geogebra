package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
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

	/** @return true if this cell range contains no geos */
	public static boolean isEmpty(@Nullable TabularRange selection, App app) {
		return selection != null && toGeoList(selection, app).isEmpty();
	}

	/**
	 * @return true if all non-empty cells in the given range are GeoPoint
	 */
	public static boolean isPointList(TabularRange selection, App app) {
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);

				if (geo != null && !geo.isGeoPoint()) {
					return false;
				}
			}
		}
		return true;
	}

	public static TabularRange getActual(TabularRange selection, App app) {
		return getActual(selection, app.getSpreadsheetTableModel());
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
	 * 
	 * @return list of elements
	 */
	public static ArrayList<GeoElement> toGeoList(TabularRange selection, App app) {

		ArrayList<GeoElement> list = new ArrayList<>();

		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo != null) {
					list.add(geo);
				}
			}
		}
		return list;
	}

	/**
	 * @return description e.g. A2:C3
	 */
	public static String getLabel(TabularRange selection) {
		return GeoElementSpreadsheet.getSpreadsheetCellName(selection.getMinColumn(),
				selection.getMinRow())
				+ ":" + GeoElementSpreadsheet.getSpreadsheetCellName(selection.getMaxColumn(),
						selection.getMaxRow());
	}

	/** @return true if at least one cell is empty (has no geo) */
	public static  boolean hasEmptyCells(TabularRange selection, App app) {
		boolean hasEmptyCells = false;
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
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
	public static int getGeoCount(TabularRange selection, GeoClass geoClass, App app) {
		int count = 0;
		if (geoClass != null) {
			for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
				for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
					GeoElement geo = RelativeCopy.getValue(app, col, row);
					if (geo != null && geo.getGeoClassType() == geoClass) {
						++count;
					}
				}
			}
		} else {
			for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
				for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
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
	 * @return true if this range contains a GeoElement of the given
	 *         GeoClass type
	 */
	public static boolean containsGeoClass(TabularRange selection, GeoClass geoClass, App app) {
		for (int col = selection.getMinColumn(); col <= selection.getMaxColumn(); ++col) {
			for (int row = selection.getMinRow(); row <= selection.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo != null && geo.getGeoClassType() == geoClass) {
					return true;
				}
			}
		}
		return false;
	}

}

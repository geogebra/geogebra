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

package org.geogebra.common.gui.view.data;

//import geogebra.gui.GuiManagerD;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetToolProcessor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

/**
 * A DataItem maintains a reference to an existing GeoList or a set of
 * GeoElements that can be used to generate a single list of data values.
 * 
 * DataItems are the basic elements used by the classes DataVariable and
 * DataSource to manage data for DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataItem {

	/**
	 * Identifiers for the possible sources of a DataItem
	 */
	public enum SourceType {
		SPREADSHEET, LIST, CLASS, EMPTY
	}

	private SourceType sourceType;
	private GeoClass geoClass = GeoClass.NUMERIC;

	// source objects
	private List<TabularRange> rangeList;
	private GeoList geoList;
	private double[] leftBorder;

	private String description = " ";
	private final SpreadsheetTableModel tableModel;

	// =======================================
	// Constructors
	// =======================================

	/**
	 * Constructs a DataItem from a GeoList.
	 * 
	 * @param geoList
	 *            data list
	 */
	public DataItem(GeoList geoList) {
		this.geoList = geoList;
		this.sourceType = SourceType.LIST;
		this.tableModel = geoList.getApp().getSpreadsheetTableModel();
	}

	/**
	 * Constructs a DataItem from a list of spreadsheet cell ranges.
	 * 
	 * @param rangeList
	 *            range list
	 * @param tableModel table model
	 */
	public DataItem(List<TabularRange> rangeList, SpreadsheetTableModel tableModel) {
		this.rangeList = rangeList;
		this.sourceType = SourceType.SPREADSHEET;
		this.tableModel = tableModel;
	}

	/**
	 * Constructs a DataItem from a single spreadsheet cell range.
	 * 
	 * @param tabularRange
	 *            cell range
	 * @param tableModel table model
	 */
	public DataItem(TabularRange tabularRange, SpreadsheetTableModel tableModel) {
		rangeList = new ArrayList<>();
		rangeList.add(tabularRange);
		this.sourceType = SourceType.SPREADSHEET;
		this.tableModel = tableModel;
	}

	/**
	 * Constructs a DataItem from an array of double (used for class borders
	 * when classes are generated automatically).
	 * 
	 * @param leftBorder
	 *            left class borders
	 * @param tableModel table model
	 */
	public DataItem(double[] leftBorder, SpreadsheetTableModel tableModel) {
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
		this.tableModel = tableModel;
	}

	/**
	 * Constructs a DataItem without a given source object.
	 * @param tableModel table model
	 */
	public DataItem(SpreadsheetTableModel tableModel) {
		sourceType = SourceType.EMPTY;
		this.tableModel = tableModel;
	}

	// ============================================
	// Getters/Setters
	// ============================================

	/**
	 * Clears this DataItem and sets the source to the given array of double.
	 * (used for class borders when classes are generated automatically).
	 * 
	 * @param leftBorder
	 *            list of class borders
	 */
	public void setDataItem(double[] leftBorder) {
		clearItem();
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
	}

	/**
	 * @return list of ranges
	 */
	public List<TabularRange> getRangeList() {
		return rangeList;
	}

	/**
	 * @return list of data
	 */
	public GeoList getGeoList() {
		return geoList;
	}

	/**
	 * @return list of class borders
	 */
	public double[] getLeftBorder() {
		return leftBorder;
	}

	public SourceType getType() {
		return sourceType;
	}

	public GeoClass getGeoClass() {
		return geoClass;
	}

	public void setGeoClass(GeoClass geoClass) {
		this.geoClass = geoClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// ==============================================
	// Utility methods
	// ==============================================

	/**
	 * Clear the references.
	 */
	public void clearItem() {
		// TODO: dereference the geo fields -- needed ??
	}

	/**
	 * @param geoClassType
	 *            element class
	 * @return whether some element in the source has the class
	 */
	public boolean containsGeoClass(GeoClass geoClassType) {
		switch (sourceType) {
		case EMPTY:
			return false;
		case LIST:
			if (geoList == null) {
				return false;
			}
			for (int i = 0; i < geoList.size(); i++) {
				if (geoList.get(i).getGeoClassType() == geoClassType) {
					return true;
				}
			}
			break;
		case SPREADSHEET:
			if (rangeList == null) {
				return false;
			}
			for (TabularRange range : rangeList) {
				if (CellRangeUtil.containsGeoClass(range, geoClassType, tableModel)) {
					return true;
				}
			}
			break;
		}
		return false;
	}

	/**
	 * @return true if this DataItem has a null source
	 */
	private boolean isEmptyItem() {

		// TODO also check for empty sources?
		/*
		 * if(getGeoCount() == 0){ return false; }
		 */

		switch (sourceType) {
		case LIST:
			return geoList == null;
		case SPREADSHEET:
			return rangeList == null;
		case CLASS:
			return leftBorder == null;
		case EMPTY:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return number of GeoElements referenced by this DataItem
	 */
	public int getGeoCount() {
		switch (sourceType) {
		case LIST:
			return geoList.size();
		case SPREADSHEET:
			int count = 0;
			for (TabularRange range : rangeList) {
				count += CellRangeUtil.getGeoCount(range, null, tableModel);
			}
			return count;
		case CLASS:
			return leftBorder.length;
		case EMPTY:
			return 0;
		default:
			return 0;
		}
	}

	/**
	 * Returns a string description of the data source
	 * 
	 * @param loc
	 *            localization
	 * 
	 * @return either a spreadsheet cell range name or a GeoList label
	 */
	public String getSourceString(Localization loc) {

		String sourceString;

		switch (sourceType) {
		case LIST:
			sourceString = getGeoList()
					.getLabel(StringTemplate.defaultTemplate);
			break;

		case SPREADSHEET:
			sourceString = CellRangeUtil
					.getCellRangeString(getRangeList(), loc);
			break;

		default:
		case CLASS:
			sourceString = " ";
			break;
		}

		return sourceString;
	}

	/**
	 * @param loc
	 *            localization
	 * @param enableHeader
	 *            whether to enable header
	 * @return DataItem title
	 */
	public String getDataTitle(Localization loc, boolean enableHeader) {
		if (!enableHeader || sourceType == SourceType.LIST) {
			return getSourceString(loc);

		} else if (sourceType == SourceType.SPREADSHEET) {

			StringTemplate tpl = StringTemplate.defaultTemplate;
			TabularRange range = getRangeList().get(0);

			if (range.isContiguousColumns() || range.isPartialColumn()) {
				GeoElement geo = RelativeCopy.getValue(tableModel,
						range.getMinColumn(), range.getMinRow());

				if (geo != null) {
					return geo.toDefinedValueString(tpl);
				}
			}
		}

		return loc.getMenu("Untitled");
	}

	/**
	 * Converts DataItem into a GeoList
	 * 
	 * @param app
	 *            application
	 * @param enableHeader
	 *            whether to enable header
	 * @param leftToRight
	 *            whether to swap X and Y
	 * @param doCopy
	 *            whether to copy elements
	 * 
	 * @return A GeoList containing elements corresponding to this DataItem
	 */
	protected GeoList toGeoList(App app, boolean enableHeader,
			boolean leftToRight, boolean doCopy) {

		if (sourceType == SourceType.EMPTY) {
			return null;
		}

		Construction cons = app.getKernel().getConstruction();
		GeoList list;

		switch (sourceType) {

		case LIST:

			if (doCopy) {
				list = dependentListCopy(cons, getGeoList());
			} else {
				list = getGeoList();
			}
			if (!leftToRight) {
				swapXYCoords(list);
			}

			break;

		case SPREADSHEET:

			boolean scanByColumn = true;
			boolean setLabel = false;

			try {
				ArrayList<TabularRange> rangeListCopy = rangeListCopy(
						getRangeList(), enableHeader);
				SpreadsheetToolProcessor processor = new SpreadsheetToolProcessor(app,
						app.getSpreadsheetTableModel().getCellFormat(null));
				list = processor
						.createList(rangeListCopy, scanByColumn,
								doCopy, geoClass, setLabel);

			} catch (Exception e) {
				Log.debug(e);
				return null;
			}
			break;

		case CLASS:

			list = new GeoList(cons);
			for (int i = 0; i < getLeftBorder().length; i++) {
				list.add(new GeoNumeric(cons, getLeftBorder()[i]));
			}

			break;
		default:
			return null;
		}

		if (!leftToRight && list.getElementType() == GeoClass.POINT) {
			swapXYCoords(list);
		}

		return list;
	}

	/**
	 * Copies a list of cell ranges with option to remove header cell
	 * 
	 * @param list
	 *            list of ranges
	 * @param removeHeaderCell
	 *            whether to remove headers
	 * @return copies of ranges
	 */
	private static ArrayList<TabularRange> rangeListCopy(List<TabularRange> list,
			boolean removeHeaderCell) {

		ArrayList<TabularRange> list2 = new ArrayList<>();

		list2.add(rangeCopy(list.get(0), removeHeaderCell));

		for (int i = 1; i < list.size(); i++) {
			list2.add(list.get(i).duplicate());
		}
		return list2;
	}

	/**
	 * Copies a cell range with option to remove header cell
	 * 
	 * @param range
	 *            cell range
	 * @param removeHeaderCell
	 *            whether to remove header cell
	 * @return duplicate cell range
	 */
	private static TabularRange rangeCopy(TabularRange range, boolean removeHeaderCell) {
		if (removeHeaderCell) {
			return new TabularRange(range.getMinRow() + 1, range.getMinColumn(),
					range.getMaxRow(), range.getMaxColumn());
		} else {
			return range.duplicate();
		}
	}

	private static GeoList dependentListCopy(Construction cons,
			GeoList geoList) {
		ArrayList<GeoElement> copyList = new ArrayList<>();

		for (int i = 0; i < geoList.size(); i++) {
			copyList.add(geoList.get(i).copy());
		}
		AlgoDependentList algo = new AlgoDependentList(cons, copyList, false);
		cons.removeFromConstructionList(algo);

		return (GeoList) algo.getGeoElements()[0];
	}

	private static void swapXYCoords(GeoList geoList) {
		if (geoList.getElementType() != GeoClass.POINT) {
			return;
		}
		for (int i = 0; i < geoList.size(); i++) {
			double x = ((GeoPoint) geoList.get(i)).x;
			double y = ((GeoPoint) geoList.get(i)).y;
			((GeoPoint) geoList.get(i)).setCoords(y, x, 1.0);
		}
		geoList.updateCascade();
	}

	/**
	 * @param enableHeader
	 *            whether to add header
	 * @return list of descriptions of data in this data item
	 */
	public String[] toStringArray(boolean enableHeader) {
		ArrayList<String> list = toStringList(enableHeader);
		if (list == null) {
			return new String[0];
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @param enableHeader
	 *            if true then the first cell of a spreadsheet range is ignored
	 * @return list of String descriptions of the data represented by this
	 *         DataItem
	 */
	public ArrayList<String> toStringList(boolean enableHeader) {

		if (isEmptyItem()) {
			return null;
		}

		ArrayList<String> strList = new ArrayList<>();

		try {
			switch (sourceType) {

			case LIST:
				for (int i = 0; i < geoList.size(); i++) {

					if (geoList.get(i) == null
							|| !geoList.get(i).isDefined()) {
						continue;
					}
					if (isValidDataType(geoList.get(i))) {
						strList.add(i, geoList.get(i).getValueForInputBar());
					} else {
						strList.add(i,
								"<html><i><font color = gray>"
										+ geoList.get(i).getValueForInputBar()
										+ "</font></i></html>");
					}
				}

				break;

			case SPREADSHEET:

				boolean skipFirstCell = enableHeader;

				for (TabularRange range : rangeList) {

					ArrayList<GeoElement> list = CellRangeUtil.toGeoList(range, tableModel);

					// iterate through the list and set the row values
					for (int i = 0; i < list.size(); i++) {
						if (skipFirstCell) {
							skipFirstCell = false;
							continue;
						}
						if (list.get(i) == null || !list.get(i).isDefined()) {
							continue;
						}
						if (isValidDataType(list.get(i))) {
							strList.add(list.get(i).getValueForInputBar());
						} else {
							strList.add("<html><i><font color = gray>"
									+ list.get(i).getValueForInputBar()
									+ "</font></i></html>");
						}
					}
				}
				break;
			case CLASS:
				double[] leftBorder1 = getLeftBorder();

				// load the array into the column
				for (int i = 0; i < leftBorder1.length - 1; i++) {
					if (i < leftBorder1.length) {
						String interval = leftBorder1[i] + " - "
								+ leftBorder1[i + 1];
						strList.add(i, interval);
					} else {
						strList.add(i, " ");
					}
				}
			}

		} catch (Exception e) {
			Log.debug(e);
		}

		return strList;
	}

	/**
	 * @return true if the given GeoElement is a valid element for this DataItem
	 */
	private boolean isValidDataType(GeoElement geo) {
		return geo.getGeoClassType() == geoClass;
	}

}

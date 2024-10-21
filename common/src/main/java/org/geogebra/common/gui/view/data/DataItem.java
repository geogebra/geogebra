package org.geogebra.common.gui.view.data;

//import geogebra.gui.GuiManagerD;

import java.util.ArrayList;

import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
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
	public static enum SourceType {
		SPREADSHEET, LIST, CLASS, INTERNAL, EMPTY
	}

	private SourceType sourceType;
	private GeoClass geoClass = GeoClass.NUMERIC;

	// source objects
	private ArrayList<TabularRange> rangeList;
	private GeoList geoList;
	private Double[] leftBorder;
	private String[] strInternal;

	private String description = " ";
	private final App app;

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
		this.app = geoList.getApp();
	}

	/**
	 * Constructs a DataItem from a list of spreadsheet cell ranges.
	 * 
	 * @param rangeList
	 *            range list
	 */
	public DataItem(ArrayList<TabularRange> rangeList, App app) {
		this.rangeList = rangeList;
		this.sourceType = SourceType.SPREADSHEET;
		this.app = app;
	}

	/**
	 * Constructs a DataItem from a single spreadsheet cell range.
	 * 
	 * @param tabularRange
	 *            cell range
	 */
	public DataItem(TabularRange tabularRange, App app) {
		rangeList = new ArrayList<>();
		rangeList.add(tabularRange);
		this.sourceType = SourceType.SPREADSHEET;
		this.app = app;
	}

	/**
	 * Constructs a DataItem from an array of double (used for class borders
	 * when classes are generated automatically).
	 * 
	 * @param leftBorder
	 *            left class borders
	 */
	public DataItem(Double[] leftBorder, App app) {
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
		this.app = app;
	}

	/**
	 * @param internalData
	 *            internal data
	 */
	public DataItem(String[] internalData, App app) {
		this.sourceType = SourceType.INTERNAL;
		this.strInternal = internalData;
		this.app = app;
	}

	/**
	 * Constructs a DataItem without a given source object.
	 */
	public DataItem(App app) {
		sourceType = SourceType.EMPTY;
		this.app = app;
	}

	// ============================================
	// Getters/Setters
	// ============================================

	/**
	 * Clears this DataItem and sets the source to the given list of spreadsheet
	 * cell ranges.
	 * 
	 * @param rangeList
	 *            list of cell ranges
	 */
	public void setDataItem(ArrayList<TabularRange> rangeList) {
		clearItem();
		this.rangeList = rangeList;
		this.sourceType = SourceType.SPREADSHEET;
	}

	/**
	 * Clears this DataItem and sets the source to the given GeoList.
	 * 
	 * @param geoList
	 *            list of data
	 */
	public void setDataItem(GeoList geoList) {
		clearItem();
		this.geoList = geoList;
		this.sourceType = SourceType.LIST;
	}

	/**
	 * Clears this DataItem and sets the source to the given array of double.
	 * (used for class borders when classes are generated automatically).
	 * 
	 * @param leftBorder
	 *            list of class borders
	 */
	public void setDataItem(Double[] leftBorder) {
		clearItem();
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
	}

	/**
	 * @return list of ranges
	 */
	public ArrayList<TabularRange> getRangeList() {
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
	public Double[] getLeftBorder() {
		return leftBorder;
	}

	public String[] getStrInternal() {
		return strInternal;
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

	public void clearItem() {
		// TODO: dereference the geo fields -- needed ??
	}

	/**
	 * @param geoClassType
	 *            element class
	 * @return whether some element in the source has the class
	 */
	public boolean containsGeoClass(GeoClass geoClassType) {
		if (sourceType == SourceType.EMPTY) {
			return false;
		}

		switch (sourceType) {
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
			for (TabularRange cr : rangeList) {
				if (CellRangeUtil.containsGeoClass(cr, geoClassType, app)) {
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
		case INTERNAL:
			return strInternal == null;
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
			for (TabularRange cr : rangeList) {
				count += CellRangeUtil.getGeoCount(cr, null, app);
			}
			return count;
		case CLASS:
			return leftBorder.length;
		case INTERNAL:
			return strInternal.length;
		case EMPTY:
			return 0;
		default:
			return 0;
		}
	}

	/**
	 * Returns a string description of the data source
	 * 
	 * @param app
	 *            application
	 * 
	 * @return either a spreadsheet cell range name or a GeoList label
	 */
	public String getSourceString(App app) {

		String sourceString;

		switch (sourceType) {
		case LIST:
			sourceString = getGeoList()
					.getLabel(StringTemplate.defaultTemplate);
			break;

		case SPREADSHEET:
			sourceString = spreadsheetTable(app).getCellRangeProcessor()
					.getCellRangeString(getRangeList());
			break;

		case INTERNAL:
			sourceString = "Untitled";
			break;

		default:
		case CLASS:
			sourceString = " ";
			break;
		}

		return sourceString;
	}

	/**
	 * @param app
	 *            application
	 * @param enableHeader
	 *            whether to enable header
	 * @return DataItem title
	 */
	public String getDataTitle(App app, boolean enableHeader) {

		if (!enableHeader || sourceType == SourceType.LIST) {
			return getSourceString(app);

		} else if (enableHeader && sourceType == SourceType.SPREADSHEET) {

			StringTemplate tpl = StringTemplate.defaultTemplate;
			TabularRange range = getRangeList().get(0);

			if (range.isColumn() || range.isPartialColumn()) {
				GeoElement geo = RelativeCopy.getValue(app,
						range.getMinColumn(), range.getMinRow());

				if (geo != null) {
					return geo.toDefinedValueString(tpl);
				}
			}
		}

		return app.getLocalization().getMenu("Untitled");
	}

	/**
	 * Converts DataItem into a GeoList
	 * 
	 * @param app
	 *            appliction
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
			boolean copyByValue = doCopy; // allows dynamic changes
			boolean doStoreUndo = false;
			boolean isSorted = false;
			boolean setLabel = false;

			try {
				ArrayList<TabularRange> rangeListCopy = rangeListCopy(
						getRangeList(), enableHeader);

				list = crProcessor(app).createList(rangeListCopy, scanByColumn,
						copyByValue, isSorted, doStoreUndo, geoClass, setLabel);

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

		case INTERNAL:

			String[] s = getStrInternal();

			boolean oldSuppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			list = new GeoList(cons);
			for (int i = 0; i < s.length; i++) {

				try {
					double num = Double.parseDouble(s[i]);
					GeoElement geo = new GeoNumeric(cons);
					((GeoNumeric) geo).setValue(num);
					list.add(geo);
				} catch (Exception e) {
					Log.error(e.getMessage());
				}
			}
			cons.setSuppressLabelCreation(oldSuppress);
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
	private static ArrayList<TabularRange> rangeListCopy(ArrayList<TabularRange> list,
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
	 * @param cr
	 *            cell range
	 * @param removeHeaderCell
	 *            whether to remove header cell
	 * @return duplicate cell range
	 */
	private static TabularRange rangeCopy(TabularRange cr, boolean removeHeaderCell) {
		if (removeHeaderCell) {
			return new TabularRange(cr.getMinRow() + 1, cr.getMinColumn(),
					cr.getMaxRow(), cr.getMaxColumn());
		} else {
			return cr.duplicate();
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
							|| !(geoList.get(i).isDefined())) {
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

				for (TabularRange cr : rangeList) {

					ArrayList<GeoElement> list = CellRangeUtil.toGeoList(cr, app);

					// iterate through the list and set the row values
					for (int i = 0; i < list.size(); i++) {
						if (skipFirstCell) {
							skipFirstCell = false;
							continue;
						}
						if (list.get(i) == null || !(list.get(i).isDefined())) {
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

			case INTERNAL:
				String[] str = getStrInternal();

				// load the array into the column
				for (int i = 0; i < str.length; i++) {
					if (i < str.length && str[i] != null) {
						strList.add(i, str[i]);
					} else {
						strList.add(i, " ");
					}
				}
				break;

			case CLASS:
				Double[] leftBorder1 = getLeftBorder();

				// load the array into the column
				for (int i = 0; i < leftBorder1.length - 1; i++) {
					if (i < leftBorder1.length && leftBorder1[i] != null) {
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

	private static CellRangeProcessor crProcessor(App app) {
		return spreadsheetTable(app).getCellRangeProcessor();
	}

	private static MyTable spreadsheetTable(App app) {
		SpreadsheetViewInterface spvi = app
				.getGuiManager().getSpreadsheetView();
		return (MyTable) spvi.getSpreadsheetTable();
	}

}

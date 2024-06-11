package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataItem.SourceType;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

/**
 * A DataVariable is a collection of DataItems representing data as a list of
 * raw data values, data values with corresponding frequencies, or as classes
 * with corresponding frequencies.
 * 
 * @author G. Sturr
 * 
 */
public class DataVariable {

	private final App app;

	/**
	 * Identifier for the data grouping type
	 */
	public enum GroupType {
		RAWDATA, FREQUENCY, CLASS
	}

	// private App app;
	private GroupType groupType;
	private GeoClass geoClass;

	private DataItem frequency;
	private DataItem label;
	private DataItem classes;
	private ArrayList<DataItem> values;

	private boolean enableHeader = false;
	private double classStart = 0.0;
	private double classWidth = 1.0;
	private final Localization loc;

	/**
	 * Constructs a DataVariable
	 * 
	 * @param app
	 *            application
	 */
	public DataVariable(App app) {
		this.loc = app.getLocalization();
		this.app = app;
	}

	// =============================================
	// Set the data
	// =============================================
	/**
	 * @param geoClass
	 *            class of geo elements
	 * @param valueItemList
	 *            data item list
	 */
	public void setDataVariableAsRawData(GeoClass geoClass,
			ArrayList<DataItem> valueItemList) {
		setDataVariable(GroupType.RAWDATA, geoClass, valueItemList, null, null,
				null);
	}

	/**
	 * @param groupType
	 *            group type
	 * @param geoClass
	 *            geo element class
	 * @param valueItemList
	 *            data item list
	 * @param frequency
	 *            frequency
	 * @param classes
	 *            classes
	 * @param label
	 *            labels
	 */
	public void setDataVariable(GroupType groupType, GeoClass geoClass,
			ArrayList<DataItem> valueItemList, DataItem frequency,
			DataItem classes,
			DataItem label) {

		this.values = valueItemList;
		this.frequency = frequency;
		this.classes = classes;
		this.label = label;

		this.geoClass = geoClass;
		setGroupType(groupType);
		if (values != null) {
			for (DataItem item : values) {
				item.setGeoClass(geoClass);
			}
		}
	}

	// ========================================================
	// Getters/Setters
	// ========================================================

	/**
	 * @param groupType
	 *            group type
	 */
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
		switch (groupType) {
		case RAWDATA:
			frequency = null;
			classes = null;
			if (values.size() == 0) {
				values.add(new DataItem(app));
			}
			break;

		case FREQUENCY:
			if (frequency == null) {
				frequency = new DataItem(app);
				frequency.setGeoClass(GeoClass.NUMERIC);
				frequency.setDescription(loc.getMenu("Frequency"));
			}
			classes = null;
			break;

		case CLASS:
			if (frequency == null) {
				frequency = new DataItem(app);
				frequency.setGeoClass(GeoClass.NUMERIC);
				frequency.setDescription(loc.getMenu("Frequency"));
			}
			if (classes == null) {
				classes = new DataItem(new Double[0], app);
				classes.setDescription(loc.getMenu("Classes"));
			}

			for (DataItem item : values) {
				item.clearItem();
			}
			values.clear();

			break;
		}

	}

	public GroupType getGroupType() {
		return groupType;
	}

	/**
	 * @param geoClass
	 *            geo element class
	 */
	public void setGeoClass(GeoClass geoClass) {
		this.geoClass = geoClass;
		for (DataItem item : values) {
			item.setGeoClass(geoClass);
		}
	}

	public GeoClass getGeoClass() {
		return geoClass;
	}

	public double getClassStart() {
		return classStart;
	}

	/**
	 * Change start and update automatic classes
	 * 
	 * @param classStart
	 *            start of fist class
	 */
	public void setClassStart(double classStart) {
		this.classStart = classStart;
		if (groupType == GroupType.CLASS) {
			updateAutomaticClasses();
		}
	}

	/**
	 * @return class width
	 */
	public double getClassWidth() {
		return classWidth;
	}

	/**
	 * Change class width and update automatic classes
	 * 
	 * @param classWidth
	 *            class width
	 */
	public void setClassWidth(double classWidth) {
		this.classWidth = classWidth;
		if (groupType == GroupType.CLASS) {
			updateAutomaticClasses();
		}
	}

	private void updateAutomaticClasses() {

		if (classes == null) {
			return;
		}

		int numClasses = 0;
		if (frequency != null) {
			numClasses = frequency.getGeoCount();
		}

		Double[] leftBorder = new Double[numClasses + 1];
		leftBorder[0] = classStart;
		for (int i = 1; i < leftBorder.length; i++) {
			leftBorder[i] = leftBorder[i - 1] + classWidth;
		}

		classes.setDataItem(leftBorder);
	}

	public DataItem getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            frequency
	 */
	public void setFrequency(DataItem frequency) {
		this.frequency = frequency;
		if (frequency != null) {
			frequency.setGeoClass(GeoClass.NUMERIC);
			frequency.setDescription(loc.getMenu("Frequency"));
		}
	}

	public DataItem getClasses() {
		return classes;
	}

	public boolean enableHeader() {
		return enableHeader;
	}

	public void setEnableHeader(boolean enableHeader) {
		this.enableHeader = enableHeader;
	}

	public ArrayList<DataItem> getValues() {
		return values;
	}

	/**
	 * @param values
	 *            data items
	 */
	public void setValueItemList(ArrayList<DataItem> values) {
		this.values = values;
		for (DataItem item : values) {
			item.setDescription(loc.getMenu("Data"));
		}
	}

	/**
	 * @param valueItem
	 *            data items
	 */
	public void setValueItems(DataItem... valueItem) {
		values = new ArrayList<>();
		for (DataItem item : valueItem) {
			values.add(item);
			item.setDescription(loc.getMenu("Data"));
		}
	}

	/**
	 * @param itemIndex
	 *            index
	 * @param item
	 *            data item
	 */
	public void setDataItem(int itemIndex, DataItem item) {
		if (itemIndex < values.size()) {
			values.set(itemIndex, item);
		} else {
			frequency = item;
		}
	}

	/**
	 * Remove last value
	 */
	public void removeLastValue() {
		if (values == null || values.size() == 0) {
			return;
		}
		values.get(values.size() - 1).clearItem();
		values.remove(values.size() - 1);
	}

	/**
	 * Add new value
	 */
	public void addNewValue() {
		if (values == null) {
			values = new ArrayList<>();
		}

		DataItem item = new DataItem(app);
		item.setGeoClass(geoClass);
		values.add(item);
	}

	// ========================================================
	// Export to GeoList or table data
	// ========================================================

	/**
	 * @return item list
	 */
	public ArrayList<DataItem> getItemList() {

		ArrayList<DataItem> list = new ArrayList<>();

		if (label != null) {
			list.add(label);
		}

		list.addAll(values);

		if (classes != null) {
			list.add(classes);
		}

		if (frequency != null) {
			list.add(frequency);
		}

		return list;
	}

	/**
	 * @param app
	 *            application
	 * @param item
	 *            data item
	 * @param mode
	 *            mode
	 * @param leftToRight
	 *            whether to use LTR borders
	 * @param doCopy
	 *            whether to copy elements
	 * @return GeoList containing elements corresponding to the DataItem
	 */
	public GeoList toGeoList(App app, DataItem item, int mode,
			boolean leftToRight, boolean doCopy) {
		return item.toGeoList(app, enableHeader, leftToRight, doCopy);
	}

	/**
	 * @param app
	 *            application
	 * @param mode
	 *            mode
	 * @param leftToRight
	 *            whether to use LTR borders
	 * @param doCopy
	 *            whether to copy elements
	 * @return GeoList matrix with labels, values, classes and frequencies
	 *         (unavailable categories are skipped)
	 */
	public ArrayList<GeoList> getGeoListData(App app, int mode,
			boolean leftToRight, boolean doCopy) {

		ArrayList<GeoList> list = new ArrayList<>();

		if (label != null) {
			list.add(label.toGeoList(app, enableHeader, leftToRight, doCopy));
		}

		if (mode == DataAnalysisModel.MODE_REGRESSION
				&& geoClass == GeoClass.NUMERIC) {
			list.add(getPointList(leftToRight, doCopy, app));
		} else {
			for (DataItem item : values) {
				list.add(
						item.toGeoList(app, enableHeader, leftToRight, doCopy));
			}
		}

		if (classes != null) {
			list.add(classes.toGeoList(app, enableHeader, leftToRight, doCopy));
		}

		if (frequency != null) {
			list.add(frequency.toGeoList(app, enableHeader, leftToRight,
					doCopy));
		}

		return list;

	}

	private GeoList getPointList(boolean leftToRight, boolean doCopy, App app) {

		if (values.size() < 2) {
			return null;
		}

		GeoList list0 = values.get(0).toGeoList(app, enableHeader, leftToRight,
				doCopy);
		GeoList list1 = values.get(1).toGeoList(app, enableHeader, leftToRight,
				doCopy);

		return createPointGeoList(list0, list1, doCopy, leftToRight);
	}

	private static GeoList createPointGeoList(GeoList xList, GeoList yList,
			boolean byValue, boolean leftToRight) {

		Construction cons = xList.getKernel().getConstruction();

		ArrayList<GeoElement> list = new ArrayList<>();

		try {
			GeoElement xCoord, yCoord;

			for (int i = 0; i < xList.size(); ++i) {

				xCoord = xList.get(i);
				yCoord = yList.get(i);

				// don't process the point if either coordinate is null or
				// non-numeric,
				if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
						|| !yCoord.isGeoNumeric()) {
					continue;
				}

				GeoPoint geoPoint;
				AlgoDependentPoint pointAlgo = null;

				if (byValue) {
					if (leftToRight) {
						geoPoint = new GeoPoint(cons,
								((GeoNumeric) xCoord).getDouble(),
								((GeoNumeric) yCoord).getDouble(), 1.0);
					} else {
						geoPoint = new GeoPoint(cons,
								((GeoNumeric) yCoord).getDouble(),
								((GeoNumeric) xCoord).getDouble(), 1.0);
					}

				} else {

					MyVecNode vec = new MyVecNode(xList.getKernel(),
							leftToRight ? xCoord : yCoord,
							leftToRight ? yCoord : xCoord);
					ExpressionNode point = new ExpressionNode(xList.getKernel(),
							vec, Operation.NO_OPERATION, null);
					point.setForcePoint();

					pointAlgo = new AlgoDependentPoint(cons, point, false);

					geoPoint = (GeoPoint) pointAlgo.getGeoElements()[0];
				}

				if (pointAlgo != null) {
					cons.removeFromConstructionList(pointAlgo);
				}

				list.add(geoPoint);

				if (yCoord.isAngle() || xCoord.isAngle()) {
					geoPoint.setPolar();
				}
			}
		} catch (Exception ex) {
			Log.debug(
					"Creating list of points expression failed with exception "
							+ ex);
		}

		AlgoDependentList dl = new AlgoDependentList(cons, list, false);
		cons.removeFromConstructionList(dl);
		GeoList ret = (GeoList) dl.getGeoElements()[0];

		ret.setSelectionAllowed(false);

		return ret;
	}

	/**
	 * @return data as strings
	 */
	public ArrayList<String[]> getStringData() {

		ArrayList<String[]> list = new ArrayList<>();

		if (groupType == GroupType.CLASS && classes != null) {
			updateAutomaticClasses();
		}
		for (DataItem item : getItemList()) {
			list.add(item.toStringArray(enableHeader));
		}
		return list;
	}

	/**
	 * @param app
	 *            application
	 * @return titles
	 */
	public ArrayList<String> getTitles(App app) {
		ArrayList<String> list = new ArrayList<>();
		for (DataItem item : getItemList()) {
			list.add(item.getDataTitle(app, enableHeader));
		}
		return list;
	}

	/**
	 * @return column names
	 */
	public ArrayList<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		for (DataItem item : getItemList()) {
			list.add(item.getDescription());
		}
		return list;
	}

	/**
	 * @param mode
	 *            app mode
	 * @param index
	 *            column index for multivar analysis
	 * @return list of column names for ???
	 */
	public ArrayList<String> getTableColumnDescriptions(int mode,
			int index) {
		ArrayList<String> list = new ArrayList<>();

		for (DataItem item : values) {

			switch (mode) {

			default:
			case DataAnalysisModel.MODE_ONEVAR:
				switch (groupType) {
				case RAWDATA:
					list.add(loc.getMenu("Data"));
					break;
				case FREQUENCY:
					list.add(loc.getMenu("Data"));
					list.add(loc.getMenu("Frequency"));
					break;
				case CLASS:
					list.add(loc.getMenu("Classes"));
					list.add(loc.getMenu("Frequency"));
					break;
				}

				break;

			case DataAnalysisModel.MODE_REGRESSION:
				if (item.getGeoClass() == GeoClass.POINT) {
					list.add("(" + loc.getMenu("Column.X") + ","
							+ loc.getMenu("Column.Y") + ")");
				} else {
					list.add(loc.getMenu("Column.X"));
					list.add(loc.getMenu("Column.Y"));
				}
				break;

			case DataAnalysisModel.MODE_MULTIVAR:
				list.add("# " + index);
				break;
			}
		}

		return list;
	}

	/**
	 * Returns true if this contains the specified GeoElement
	 * 
	 * @param geo
	 *            element
	 * @return whether geo is in data source
	 */
	public boolean isInDataSource(GeoElement geo) {
		if (geo == null) {
			return false;
		}

		ArrayList<DataItem> itemList = getItemList();

		// CASE 1: GeoList
		if (geo instanceof GeoList) {
			for (DataItem item : itemList) {
				if (item.getGeoList() != null && item.getGeoList() == geo) {
					return true;
				}
			}
		}

		// CASE 2: spreadsheet cell
		SpreadsheetCoords location = geo.getSpreadsheetCoords();
		boolean isCell = location != null
				&& location.column < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP
				&& location.row < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP;

		if (isCell) {
			for (DataItem dataItem : itemList) {
				if (dataItem.getType() == SourceType.SPREADSHEET) {
					try {
						if (dataItem.getRangeList() != null) {
							for (TabularRange cr : dataItem.getRangeList()) {
								if (cr.contains(geo.getSpreadsheetCoords())) {
									return true;
								}
							}
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			}
		}

		return false;
	}

	/**
	 * Serialize to XML
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void getXML(StringBuilder sb) {
		// save these fields to XML:
		// groupType, enableHeader
		sb.append("<variable>\n");
		// save the DataItems to XML
		for (DataItem item : values) {

			sb.append("<item ranges=\"");
			ArrayList<TabularRange> crList = item.getRangeList();
			if (crList != null) {
				appendTabularRanges(sb, crList);
			}
			sb.append("\"/>\n");
		}
		if (frequency != null) {
			// save the frequencies to XML
			sb.append("<item frequencies=\"");
			appendTabularRanges(sb, frequency.getRangeList());
			sb.append("\"/>\n");
		}
		if (classes != null) {
			// write item XML
		}
		if (label != null) {
			// write item XML
		}
		sb.append("</variable>\n");
	}

	private void appendTabularRanges(StringBuilder sb, ArrayList<TabularRange> crList) {
		boolean first = true;
		for (TabularRange cr : crList) {
			if (cr != null) {
				sb.append(first ? "" : ",");
				sb.append(CellRangeUtil.getLabel(cr));
				first = false;
			}
		}
	}

}

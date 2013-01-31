package geogebra.gui.view.data;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDependentList;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.gui.view.data.DataItem.SourceType;

import java.util.ArrayList;

/**
 * A DataVariable is a collection of DataItems representing data as a list of
 * raw data values, data values with corresponding frequencies, or as classes
 * with corresponding frequencies.
 * 
 * @author G. Sturr
 * 
 */
public class DataVariable {

	/**
	 * Identifier for the data grouping type
	 */
	public enum GroupType {
		RAWDATA, FREQUENCY, CLASS
	}

	private App app;
	private GroupType groupType;
	private GeoClass geoClass;

	private DataItem frequency, label, classes;
	private ArrayList<DataItem> values;

	private boolean enableHeader = false;
	private double classStart = 0.0;
	private double classWidth = 1.0;

	/**
	 * Constructs a DataVariable
	 * 
	 * @param app
	 */
	public DataVariable(App app) {
		this.app = app;
	}

	// =============================================
	// Set the data
	// =============================================
	/**
	 * @param geoClass
	 * @param valueItemList
	 */
	public void setDataVariableAsRawData(GeoClass geoClass,
			ArrayList valueItemList) {

		setDataVariable(GroupType.RAWDATA, geoClass, valueItemList, null, null,
				null);

	}

	/**
	 * @param groupType
	 * @param geoClass
	 * @param valueItemList
	 * @param frequency
	 * @param classes
	 * @param label
	 */
	public void setDataVariable(GroupType groupType, GeoClass geoClass,
			ArrayList valueItemList, DataItem frequency, DataItem classes,
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

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
		switch (groupType) {
		case RAWDATA:
			frequency = null;
			classes = null;
			if(values.size() == 0){
				values.add(new DataItem());
			}
			break;

		case FREQUENCY:
			if (frequency == null) {
				frequency = new DataItem();
				frequency.setGeoClass(GeoClass.NUMERIC);
				frequency.setDescription(app.getMenu("Frequency"));
			}
			classes = null;
			break;

		case CLASS:
			if (frequency == null) {
				frequency = new DataItem();
				frequency.setGeoClass(GeoClass.NUMERIC);
				frequency.setDescription(app.getMenu("Frequency"));
			}
			if (classes == null) {
				classes = new DataItem(new Double[0]);
				classes.setDescription(app.getMenu("Classes"));
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

	public void setClassStart(double classStart) {
		this.classStart = classStart;
		if (groupType == GroupType.CLASS) {
			updateAutomaticClasses();
		}
	}

	public double getClassWidth() {
		return classWidth;
	}

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

	public void setFrequency(DataItem frequency) {
		this.frequency = frequency;
		if (frequency != null) {
			frequency.setGeoClass(GeoClass.NUMERIC);
			frequency.setDescription(app.getMenu("Frequency"));
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

	public void setValueItemList(ArrayList<DataItem> values) {
		this.values = values;
		for (DataItem item : values) {
			item.setDescription(app.getMenu("Data"));
		}
	}

	public void setValueItems(DataItem... valueItem) {
		values = new ArrayList<DataItem>();
		for (DataItem item : valueItem) {
			values.add(item);
			item.setDescription(app.getMenu("Data"));
		}
	}

	public void setDataItem(int itemIndex, DataItem item) {
		if (itemIndex < values.size()) {
			values.set(itemIndex, item);
		} else {
			frequency = item;
		}
	}

	public void removeLastValue() {
		if (values == null || values.size() == 0) {
			return;
		}
		values.get(values.size() - 1).clearItem();
		values.remove(values.size() - 1);
	}

	public void addNewValue() {
		if (values == null) {
			values = new ArrayList<DataItem>();
		}

		DataItem item = new DataItem();
		item.setGeoClass(geoClass);
		values.add(item);
	}

	// ========================================================
	// Export to GeoList or table data
	// ========================================================

	public ArrayList<DataItem> getItemList() {

		ArrayList<DataItem> list = new ArrayList<DataItem>();

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
	 * @param item
	 * @param mode
	 * @param leftToRight
	 * @param doCopy
	 * @return
	 */
	public GeoList toGeoList(App app, DataItem item, int mode,
			boolean leftToRight, boolean doCopy) {

		return item.toGeoList(app, enableHeader, leftToRight, doCopy);
	}

	/**
	 * @param app
	 * @param mode
	 * @param leftToRight
	 * @param doCopy
	 * @return
	 */
	public ArrayList<GeoList> getGeoListData(App app, int mode,
			boolean leftToRight, boolean doCopy) {

		ArrayList<GeoList> list = new ArrayList<GeoList>();

		if (label != null) {
			list.add(label.toGeoList(app, enableHeader, leftToRight, doCopy));
		}

		if (mode == DataAnalysisViewD.MODE_REGRESSION
				&& geoClass == GeoClass.NUMERIC) {
			list.add(getPointList(leftToRight, doCopy));
		} else {
			for (DataItem item : values) {
				list.add(item.toGeoList(app, enableHeader, leftToRight, doCopy));
			}
		}

		if (classes != null) {
			list.add(classes.toGeoList(app, enableHeader, leftToRight, doCopy));
		}

		if (frequency != null) {
			list.add(frequency
					.toGeoList(app, enableHeader, leftToRight, doCopy));
		}

		return list;

	}

	private GeoList getPointList(boolean leftToRight, boolean doCopy) {

		if (values.size() < 2) {
			return null;
		}

		GeoList list0 = values.get(0).toGeoList(app, enableHeader, leftToRight,
				doCopy);
		GeoList list1 = values.get(1).toGeoList(app, enableHeader, leftToRight,
				doCopy);

		return createPointGeoList(list0, list1, doCopy, leftToRight);
	}

	private GeoList createPointGeoList(GeoList xList, GeoList yList,
			boolean byValue, boolean leftToRight) {

		Construction cons = app.getKernel().getConstruction();

		ArrayList<GeoElement> list = new ArrayList<GeoElement>();

		try {
			GeoElement xCoord, yCoord;

			for (int i = 0; i < xList.size(); ++i) {

				xCoord = xList.get(i);
				yCoord = yList.get(i);

				// don't process the point if either coordinate is null or
				// non-numeric,
				if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
						|| !yCoord.isGeoNumeric())
					continue;

				GeoPoint geoPoint;
				AlgoDependentPoint pointAlgo = null;

				if (byValue) {
					if (leftToRight)
						geoPoint = new GeoPoint(cons,
								((GeoNumeric) xCoord).getDouble(),
								((GeoNumeric) yCoord).getDouble(), 1.0);
					else
						geoPoint = new GeoPoint(cons,
								((GeoNumeric) yCoord).getDouble(),
								((GeoNumeric) xCoord).getDouble(), 1.0);

				} else {

					MyVecNode vec = new MyVecNode(app.getKernel(),
							leftToRight ? xCoord : yCoord, leftToRight ? yCoord
									: xCoord);
					ExpressionNode point = new ExpressionNode(app.getKernel(),
							vec, Operation.NO_OPERATION, null);
					point.setForcePoint();

					pointAlgo = new AlgoDependentPoint(cons, point, false);

					geoPoint = (GeoPoint) pointAlgo.getGeoElements()[0];

				}

				if (pointAlgo != null)
					cons.removeFromConstructionList(pointAlgo);

				list.add(geoPoint);

				if (yCoord.isAngle() || xCoord.isAngle())
					geoPoint.setPolar();

			}
		}

		catch (Exception ex) {
			App.debug("Creating list of points expression failed with exception "
					+ ex);
		}

		AlgoDependentList dl = new AlgoDependentList(cons, list, false);
		cons.removeFromConstructionList(dl);
		return (GeoList) dl.getGeoElements()[0];

	}

	public ArrayList<String[]> getStringData() {

		ArrayList<String[]> list = new ArrayList<String[]>();

		if (groupType == GroupType.CLASS && classes != null) {
			updateAutomaticClasses();
		}
		for (DataItem item : getItemList()) {
			list.add(item.toStringArray(enableHeader));
		}
		return list;
	}

	public ArrayList<String> getTitles(App app) {

		ArrayList<String> list = new ArrayList<String>();
		for (DataItem item : getItemList()) {
			list.add(item.getDataTitle(app, enableHeader));
		}
		return list;
	}

	public ArrayList<String> getColumnNames() {

		ArrayList<String> list = new ArrayList<String>();
		for (DataItem item : getItemList()) {
			list.add(item.getDescription());
		}
		return list;
	}

	/**
	 * @param mode
	 * @return list of column names for ???
	 */
	public ArrayList<String> getTableColumnDescriptions(App app, int mode,
			int index) {

		ArrayList<String> list = new ArrayList<String>();

		for (DataItem item : values) {

			switch (mode) {

			case DataAnalysisViewD.MODE_ONEVAR:
				switch (groupType) {
				case RAWDATA:
					list.add(app.getMenu("Data"));
					break;
				case FREQUENCY:
					list.add(app.getMenu("Data"));
					list.add(app.getMenu("Frequency"));
					break;
				case CLASS:
					list.add(app.getMenu("Classes"));
					list.add(app.getMenu("Frequency"));
					break;
				}

				break;

			case DataAnalysisViewD.MODE_REGRESSION:
				if (item.getGeoClass() == GeoClass.POINT) {
					list.add("(" + app.getMenu("Column.X") + ","
							+ app.getMenu("Column.Y") + ")");
				} else {
					list.add(app.getMenu("Column.X"));
					list.add(app.getMenu("Column.Y"));
				}
				break;

			case DataAnalysisViewD.MODE_MULTIVAR:
				list.add("# " + index);
				break;
			}
		}

		return list;
	}

	/**
	 * Returns true if this contains the specified GeoElement
	 */
	public boolean isInDataSource(GeoElement geo) {

		if (geo == null)
			return false;

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
		GPoint location = geo.getSpreadsheetCoords();
		boolean isCell = (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP && location.y < Kernel.MAX_SPREADSHEET_ROWS_DESKTOP);

		if (isCell) {
			for (DataItem dataItem : itemList) {
				if (dataItem.getType() == SourceType.SPREADSHEET)
					try {
						if (dataItem.getRangeList() != null) {
							for (CellRange cr : dataItem.getRangeList())
								if (cr.contains(geo)) {
									return true;
								}
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}
			}
		}

		return false;
	}

	private void getXML() {

		// save these fields to XML:
		// groupType, enableHeader

		// save the DataItems to XML
		for (int i = 0; i < values.size(); i++) {
			// write item XML for value1, value2 etc.
		}
		if (frequency != null) {
			// write item XML
		}
		if (classes != null) {
			// write item XML
		}
		if (label != null) {
			// write item XML
		}

	}

}

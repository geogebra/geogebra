package geogebra.gui.view.data;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.plugin.GeoClass;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.data.DataVariable.GroupType;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;

import java.util.ArrayList;

/**
 * Manages a list of DataVariables for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataSource {

	private static final long serialVersionUID = 1L;

	private GroupType defaultGroupType = GroupType.RAWDATA;
	private AppD app;

	private ArrayList<DataVariable> dataList;
	private int selectedIndex;

	// ====================================
	// Constructor
	// ====================================

	/**
	 * @param app
	 */
	public DataSource(AppD app) {
		this.app = app;
		dataList = new ArrayList<DataVariable>();
		selectedIndex = 0;
	}

	// ====================================
	// Add/Remove
	// ====================================

	public boolean isEmpty() {
		return dataList.size() == 0;
	}

	public void clearData() {

		// TODO dereference geos from all DataItems
		dataList.clear();
	}

	// ====================================
	// Getters/Setters
	// ====================================

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public boolean enableHeader() {
		return getSelectedDataVariable().enableHeader();
	}

	public void setEnableHeader(boolean enableHeader) {
		getSelectedDataVariable().setEnableHeader(enableHeader);
	}

	public boolean isNumericData() {
		if(getSelectedDataVariable()== null){
			return false;
		}
		return getSelectedDataVariable().getGeoClass() == GeoClass.NUMERIC;
	}

	public void setNumericData(boolean isNumericData) {
		getSelectedDataVariable().setGeoClass(GeoClass.NUMERIC);
	}

	public GeoClass getGeoClass() {
		return getSelectedDataVariable().getGeoClass();
	}

	public void setGeoClass(GeoClass geoClass) {
		getSelectedDataVariable().setGeoClass(geoClass);
	}

	public boolean isPointData() {
		return getSelectedDataVariable().getGeoClass() == GeoClass.POINT;
	}

	public DataVariable getDataVariable(int index) {
		return dataList.get(index);
	}

	public DataVariable getSelectedDataVariable() {
		if(selectedIndex >= dataList.size()){
			return null;
		}
		return dataList.get(selectedIndex);
	}

	public GroupType getGroupType() {
		if (isEmpty()) {
			return GroupType.RAWDATA; // default
		}
		return getSelectedDataVariable().getGroupType();
	}

	public GroupType getGroupType(int varIndex) {
		if (varIndex >= dataList.size()) {
			return GroupType.RAWDATA; // default
		}
		return dataList.get(varIndex).getGroupType();
	}

	public void setGroupType(GroupType groupType, int varIndex) {
		dataList.get(varIndex).setGroupType(groupType);
	}

	public double getClassStart() {
		return getSelectedDataVariable().getClassStart();
	}

	public void setClassStart(double classStart) {
		getSelectedDataVariable().setClassStart(classStart);
	}

	public double getClassWidth() {
		return getSelectedDataVariable().getClassWidth();
	}

	public void setClassWidth(double classWidth) {
		getSelectedDataVariable().setClassWidth(classWidth);
	}

	private MyTableD spreadsheetTable() {
		return (MyTableD) ((GuiManagerD) app.getGuiManager())
				.getSpreadsheetView().getSpreadsheetTable();
	}

	private CellRangeProcessor crProcessor() {
		return spreadsheetTable().getCellRangeProcessor();
	}

	/**
	 * Sets the DataItem at a given location to reference the currently selected
	 * GeoElements
	 * 
	 * @param dataIndex
	 *            index of a DataVariable in dataList
	 * @param itemIndex
	 *            index of a DataItem in the given DataVariable
	 * 
	 */
	public void setDataItemToGeoSelection(int dataIndex, int itemIndex) {

		if (dataList.get(dataIndex) == null) {
			return;
		}

		dataList.get(dataIndex).setDataItem(itemIndex,
				createDataItemFromGeoSelection());
	}

	/**
	 * Returns a DataItem that references data from the currently selected geos.
	 * 
	 * @return Either a spreadsheet cell range, a GeoList or null if the
	 *         selected geos cannot form a DataItem
	 */
	private DataItem createDataItemFromGeoSelection() {

		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			return null;
		}

		GeoElement geo = app.getSelectedGeos().get(0);

		if (geo.isGeoList()) {
			return new DataItem((GeoList) geo);
		}

		else if (geo.getSpreadsheetCoords() != null) {
			return new DataItem(CellRangeProcessor.clone(spreadsheetTable()
					.getSelectedCellRanges()));
		}

		return null;
	}

	// =====================================
	// Getters for the source dialog table
	// =====================================

	/**
	 * @return 2D array of data from the currently selected DataVariable
	 */
	public String[][] getTableData() {

		return getTableData(getSelectedIndex());
	}

	/**
	 * @param dataIndex
	 * @return 2D array of data from the DataVariable at the given index
	 *         position
	 */
	public String[][] getTableData(int dataIndex) {

		if (dataIndex >= dataList.size()) {
			return null;
		}

		ArrayList<String[]> list = new ArrayList<String[]>();
		list.addAll(dataList.get(dataIndex).getStringData());

		// get maximum row count
		int rowCount = 0;
		for (String[] s : list) {
			rowCount = Math.max(rowCount, s.length);
		}

		// create data array
		String[][] data = new String[rowCount][list.size()];

		for (int c = 0; c < list.size(); c++) {
			for (int r = 0; r < list.get(c).length; r++) {
				data[r][c] = list.get(c)[r];
			}
		}

		return data;
	}

	/**
	 * @return data titles from the currently selected DataVariable
	 */
	public String[] getTitles() {
		return getTitles(getSelectedIndex());
	}

	/**
	 * @param dataIndex
	 * @return data titles from the DataVariable at the given index position
	 */
	public String[] getTitles(int dataIndex) {

		if (dataIndex >= dataList.size()) {
			return null;
		}

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(dataList.get(dataIndex).getTitles(app));

		String[] s = list.toArray(new String[list.size()]);

		return s;
	}

	/**
	 * @return descriptions (e.g. "Data", "Frequency" etc.) of the DataItems in
	 *         the currently selected DataVariable
	 */
	public String[] getDescriptions() {

		ArrayList<String> list = getSelectedDataVariable().getColumnNames();
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @return descriptions (e.g. "Data", "Frequency" etc.) of the DataItems in
	 *         the DataVariable at the given index position
	 */
	public String[] getDescriptions(int dataIndex) {

		if (dataIndex >= dataList.size()) {
			return null;
		}

		ArrayList<String> list = dataList.get(dataIndex).getColumnNames();
		return list.toArray(new String[list.size()]);
	}

	// =========================================
	// GeoLists for DataAnalysisView
	// =========================================

	/**
	 * Converts the currently selected DataVariable to a list of GeoLists
	 * 
	 * @param mode
	 * @param leftToRight
	 * @param doCopy
	 * @return arrayList of GeoLists corresponding to data stored in the given
	 *         DataVariable
	 */
	public ArrayList<GeoList> toGeoList(int mode, boolean leftToRight,
			boolean doCopy) {

		return toGeoList(mode, leftToRight, doCopy, getSelectedIndex());
	}

	/**
	 * Converts a DataVariable at a given index position in dataList to a list
	 * of GeoLists
	 * 
	 * @param mode
	 * @param leftToRight
	 * @param doCopy
	 * @param dataIndex
	 * @return arrayList of GeoLists corresponding to data stored in the
	 *         DataVariable at the given index position
	 */
	public ArrayList<GeoList> toGeoList(int mode, boolean leftToRight,
			boolean doCopy, int dataIndex) {

		if (dataList == null || dataList.size() == 0) {
			return null;
		}

		return dataList.get(dataIndex).getGeoListData(app, mode, leftToRight,
				doCopy);
	}

	/**
	 * @param mode
	 * @param leftToRight
	 * @param doCopy
	 * @return
	 */
	public ArrayList<GeoList> toGeoListAll(int mode, boolean leftToRight,
			boolean doCopy) {

		if (dataList == null || dataList.size() == 0) {
			return null;
		}

		ArrayList<GeoList> list = new ArrayList<GeoList>();

		for (DataVariable var : dataList) {
			list.addAll(var.getGeoListData(app, mode, leftToRight, doCopy));
		}

		return list;
	}

	// ====================================
	// Automatic Source Generation
	// ====================================

	/**
	 * Sets this DataSource to the currently selected GeoElements.
	 * 
	 * @param mode
	 *            Data analysis mode
	 */
	public void setDataListFromSelection(int mode) {

		dataList.clear();

		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			return;
		}

		try {
			// if the first selected geo is a spreadsheet cell then use the
			// spreadsheet's selected cell range list
			if (app.getSelectedGeos().get(0).getSpreadsheetCoords() != null) {
				setDataListFromSpreadsheet(mode, defaultGroupType);

			} else {
				// otherwise add all selected GeoLists
				setDataListFromGeoList(mode);
			}

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Creates a new list of DataVariables from the currently selected GeoLists
	 */
	private void setDataListFromGeoList(int mode) {

		// create a list of GeoLists from the selected elements
		ArrayList<GeoList> list = new ArrayList<GeoList>();
		for (GeoElement geo : app.getSelectedGeos()) {
			if (geo.isGeoList() && !((GeoList) geo).isMatrix()) {
				list.add((GeoList) geo);
			}
		}
		if (list.size() == 0) {
			return;
		}

		ArrayList<DataItem> itemList = new ArrayList<DataItem>();
		DataVariable var = new DataVariable(app);

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:
			itemList.add(new DataItem(list.get(0)));
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			if (list.get(0).getElementType() == GeoClass.POINT) {
				itemList.add(new DataItem(list.get(0)));
				var.setDataVariableAsRawData(GeoClass.POINT, itemList);
			} else {
				itemList.add(new DataItem(list.get(0)));
				if (list.size() == 1) {
					itemList.add(new DataItem());
				}
				var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			}
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			for (GeoList geo : list) {
				itemList.add(new DataItem(geo));
			}
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			break;

		}

		dataList.add(var);
	}

	/**
	 * Creates a new list of DataVariables from the current spreadsheet
	 * selection.
	 */
	private void setDataListFromSpreadsheet(int mode, GroupType groupType) {

		// The cell range list returned by the spreadsheet can change
		// dynamically, so we need to use a copy.
		ArrayList<CellRange> rangeList = CellRangeProcessor
				.clone(spreadsheetTable().getSelectedCellRanges());

		DataVariable var = new DataVariable(app);

		ArrayList<DataItem> itemList = new ArrayList<DataItem>();

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:
			itemList.add(new DataItem(rangeList));
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			break;

		case DataAnalysisViewD.MODE_REGRESSION:

			// test if there is at least one GeoPoint in the selection
			boolean hasPoint = crProcessor().containsGeoClass(rangeList,
					GeoClass.POINT);

			if (hasPoint) {
				// single list of points
				itemList.add(new DataItem(rangeList));
				var.setDataVariableAsRawData(GeoClass.POINT, itemList);

			} else {
				// separate x, y lists
				add1DCellRanges(rangeList, itemList);
				if (itemList.size() < 2) {
					itemList.add(new DataItem());
				}
				var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			}
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			ArrayList<CellRange> r;
			for (CellRange cr : rangeList) {
				if (cr.isRow() || cr.isPartialRow()) {
					r = cr.toPartialRowList();
					for (CellRange cr2 : r) {
						itemList.add(new DataItem(cr2));
					}
				} else {
					r = cr.toPartialColumnList();
					for (CellRange cr2 : r) {
						itemList.add(new DataItem(cr2));
					}
				}
			}
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);

			break;
		}

		dataList.add(var);
	}

	/**
	 * Attempts to extract two 1D cell ranges from the given cell range list and
	 * then add these as DataItems to the given DataItem list. The orientation
	 * of the 1D cell ranges (vertical or horizontal) is determined from the
	 * shape of the given cell ranges.
	 */
	private static void add1DCellRanges(ArrayList<CellRange> rangeList,
			ArrayList<DataItem> itemList) {

		ArrayList<CellRange> r = null;

		boolean scanByColumn = rangeList.get(0).getActualDimensions()[1] <= 2;

		if (rangeList.size() == 1) { // single cell range

			if (scanByColumn) {
				// list of vertical cell ranges
				r = rangeList.get(0).toPartialColumnList();
			} else {
				// list of horizontal cell ranges
				r = rangeList.get(0).toPartialRowList();
			}

			if (r != null) {
				if (r.size() > 0)
					itemList.add(new DataItem(r.get(0)));
				if (r.size() > 1)
					itemList.add(new DataItem(r.get(1)));
			}

		} else if (rangeList.size() == 2) { // two separate cell ranges

			if (scanByColumn) {
				// extract vertical cell ranges
				itemList.add(new DataItem(rangeList.get(0)
						.toPartialColumnList().get(0)));
				itemList.add(new DataItem(rangeList.get(1)
						.toPartialColumnList().get(0)));

			} else {
				// extract horizontal cell range
				itemList.add(new DataItem(rangeList.get(0).toPartialRowList()
						.get(0)));
				itemList.add(new DataItem(rangeList.get(1).toPartialRowList()
						.get(0)));
			}
		}
	}

	// ====================================
	// Utility methods
	// ====================================

	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo) {

		for (DataVariable var : dataList) {
			if (var.isInDataSource(geo)) {
				return true;
			}
		}
		return false;
	}
}

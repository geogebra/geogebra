package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

/**
 * Manages a list of DataVariables for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataSource {

	private final App app;
	private final SelectionManager selection;

	private ArrayList<DataVariable> dataList;
	private int selectedIndex;
	private boolean frequencyFromColumn = false;

	// ====================================
	// Constructor
	// ====================================

	/**
	 * @param app
	 *            application
	 */
	public DataSource(App app) {
		this.app = app;
		this.selection = app.getSelectionManager();
		dataList = new ArrayList<>();
		selectedIndex = 0;
	}

	// ====================================
	// Add/Remove
	// ====================================

	public boolean isEmpty() {
		return dataList.size() == 0;
	}

	/**
	 * Clear all data
	 */
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

	/**
	 * @return whether the data is numeric
	 */
	public boolean isNumericData() {
		if (getSelectedDataVariable() == null) {
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

	/**
	 * @return selected variable
	 */
	public DataVariable getSelectedDataVariable() {
		if (selectedIndex >= dataList.size()) {
			return null;
		}
		return dataList.get(selectedIndex);
	}

	/**
	 * @return group type
	 */
	public GroupType getGroupType() {
		if (isEmpty()) {
			return GroupType.RAWDATA; // default
		}
		return getSelectedDataVariable().getGroupType();
	}

	/**
	 * @param varIndex
	 *            variable index
	 * @return group type
	 */
	public GroupType getGroupType(int varIndex) {
		if (varIndex >= dataList.size()) {
			return GroupType.RAWDATA; // default
		}
		return dataList.get(varIndex).getGroupType();
	}

	/**
	 * @param groupType
	 *            group type
	 * @param varIndex
	 *            variable index
	 */
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

	protected CellRangeProcessor crProcessor() {
		return spreadsheetTable().getCellRangeProcessor();
	}

	private MyTable spreadsheetTable() {
		SpreadsheetViewInterface spvi = app
				.getGuiManager().getSpreadsheetView();
		return (MyTable) spvi.getSpreadsheetTable();
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
		if (selection.getSelectedGeos() == null
				|| selection.getSelectedGeos().size() == 0) {
			return null;
		}

		GeoElement geo = selection.getSelectedGeos().get(0);

		if (geo.isGeoList()) {
			return new DataItem((GeoList) geo);
		}

		else if (geo.getSpreadsheetCoords() != null) {
			return new DataItem(CellRangeProcessor
					.clone(spreadsheetTable().getSelectedRanges()), app);
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
	 *            data index
	 * @return 2D array of data from the DataVariable at the given index
	 *         position
	 */
	public String[][] getTableData(int dataIndex) {
		if (dataIndex >= dataList.size()) {
			return null;
		}

		ArrayList<String[]> list = new ArrayList<>();
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
	 *            data index
	 * @return data titles from the DataVariable at the given index position
	 */
	public String[] getTitles(int dataIndex) {
		if (dataIndex >= dataList.size()) {
			return null;
		}

		ArrayList<String> list = new ArrayList<>();
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
	 * @param dataIndex
	 *            data index
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
	 *            mode
	 * @param leftToRight
	 *            whether to swap X and Y
	 * @param doCopy
	 *            whether to copy elements
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
	 *            mode
	 * @param leftToRight
	 *            whether to swap X and Y
	 * @param doCopy
	 *            whether to copy elements
	 * @param dataIndex
	 *            data index
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
	 *            mode
	 * @param leftToRight
	 *            whether to swap X and Y
	 * @param doCopy
	 *            whether to copy elements
	 * @return all variables in a list
	 */
	public ArrayList<GeoList> toGeoListAll(int mode, boolean leftToRight,
			boolean doCopy) {

		if (dataList == null || dataList.size() == 0) {
			return null;
		}

		ArrayList<GeoList> list = new ArrayList<>();

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

		if (selection.getSelectedGeos() == null
				|| selection.getSelectedGeos().size() == 0) {
			return;
		}

		try {
			// if the first selected geo is a spreadsheet cell then use the
			// spreadsheet's selected cell range list
			if (selection.getSelectedGeos().get(0)
					.getSpreadsheetCoords() != null) {
				setDataListFromSpreadsheet(mode);

			} else {
				// otherwise add all selected GeoLists
				setDataListFromGeoList(mode);
			}

			return;

		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * @param items items
	 * @param frequencies frequencies
	 * @param mode mode
	 */
	public void setDataListFromSettings(ArrayList<String> items, String frequencies, int mode) {
		dataList.clear();
		ArrayList<TabularRange> ranges = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			String range = items.get(i);

			SpreadsheetCoords start = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(
					range.substring(0, range.indexOf(':')));

			SpreadsheetCoords end = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(
					range.substring(range.indexOf(':') + 1));

			TabularRange tr = new TabularRange(start.row, start.column, end.row, end.column);
			ranges.add(tr);
		}

		if (frequencies != null) {
			setFrequencyFromColumn(true);

			SpreadsheetCoords start = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(
					frequencies.substring(0, frequencies.indexOf(':')));

			SpreadsheetCoords end = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(
					frequencies.substring(frequencies.indexOf(':') + 1));

			TabularRange tr = new TabularRange(start.row, start.column, end.row, end.column);
			ranges.add(tr);
		}

		setDataListFromSpreadsheet(mode, ranges);
	}

	/**
	 * Creates a new list of DataVariables from the currently selected GeoLists
	 */
	private void setDataListFromGeoList(int mode) {

		// create a list of GeoLists from the selected elements
		ArrayList<GeoList> list = new ArrayList<>();
		for (GeoElement geo : selection.getSelectedGeos()) {
			if (geo.isGeoList() && !((GeoList) geo).isMatrix()) {
				list.add((GeoList) geo);
			}
		}
		if (list.size() == 0) {
			return;
		}

		ArrayList<DataItem> itemList = new ArrayList<>();
		DataVariable var = new DataVariable(app);

		switch (mode) {

		default:
		case DataAnalysisModel.MODE_ONEVAR:
			itemList.add(new DataItem(list.get(0)));
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			break;

		case DataAnalysisModel.MODE_REGRESSION:
			if (list.get(0).getElementType() == GeoClass.POINT) {
				itemList.add(new DataItem(list.get(0)));
				var.setDataVariableAsRawData(GeoClass.POINT, itemList);
			} else {
				itemList.add(new DataItem(list.get(0)));
				if (list.size() == 1) {
					itemList.add(new DataItem(app));
				}
				var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			}
			break;

		case DataAnalysisModel.MODE_MULTIVAR:
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
	private void setDataListFromSpreadsheet(int mode) {

		// The cell range list returned by the spreadsheet can change
		// dynamically, so we need to use a copy.
		ArrayList<TabularRange> rangeList = CellRangeProcessor
				.clone(spreadsheetTable().getSelectedRanges());
		setDataListFromSpreadsheet(mode, rangeList);
	}

	private void setDataListFromSpreadsheet(int mode,
			ArrayList<TabularRange> rangeList) {
		DataVariable var = new DataVariable(app);

		ArrayList<DataItem> itemList = new ArrayList<>();

		switch (mode) {

		default:
		case DataAnalysisModel.MODE_ONEVAR:
			if (isFrequencyFromColumn()) {
				TabularRange tr = rangeList.get(0);

				if (tr.is2D() || rangeListContainsFrequencies(rangeList)) {
					var.setGroupType(GroupType.FREQUENCY);
					add1DTabularRanges(rangeList, itemList);
					ArrayList<DataItem> values = new ArrayList<>();
					values.add(itemList.get(0));
					var.setDataVariable(GroupType.FREQUENCY, GeoClass.NUMERIC,
							values, itemList.get(1), null, null);
					break;

				}
			}
			itemList.add(new DataItem(rangeList, app));
			var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			break;

		case DataAnalysisModel.MODE_REGRESSION:

			// test if there is at least one GeoPoint in the selection
			boolean hasPoint = crProcessor().containsGeoClass(rangeList,
					GeoClass.POINT);

			if (hasPoint) {
				// single list of points
				itemList.add(new DataItem(rangeList, app));
				var.setDataVariableAsRawData(GeoClass.POINT, itemList);

			} else {
				// separate x, y lists
				add1DTabularRanges(rangeList, itemList);
				if (itemList.size() < 2) {
					itemList.add(new DataItem(app));
				}
				var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
			}
			break;

		case DataAnalysisModel.MODE_MULTIVAR:
			ArrayList<TabularRange> r;
			for (TabularRange cr : rangeList) {
				if (cr.isRow() || cr.isPartialRow()) {
					r = cr.toPartialRowList();
					for (TabularRange cr2 : r) {
						itemList.add(new DataItem(cr2, app));
					}
				} else {
					r = cr.toPartialColumnList();
					for (TabularRange cr2 : r) {
						itemList.add(new DataItem(cr2, app));
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
	private void add1DTabularRanges(ArrayList<TabularRange> rangeList,
			ArrayList<DataItem> itemList) {

		ArrayList<TabularRange> r = null;
		TabularRange sel = CellRangeUtil.getActual(rangeList.get(0), app);
		boolean scanByColumn = sel.getWidth() <= 2;

		if (rangeList.size() == 1) { // single cell range

			if (scanByColumn) {
				// list of vertical cell ranges
				r = sel.toPartialColumnList();
			} else {
				// list of horizontal cell ranges
				r = sel.toPartialRowList();
			}

			if (r != null) {
				if (r.size() > 0) {
					itemList.add(new DataItem(r.get(0), app));
				}
				if (r.size() > 1) {
					itemList.add(new DataItem(r.get(1), app));
				}
			}

		} else if (rangeList.size() == 2) { // two separate cell ranges

			if (scanByColumn) {
				// extract vertical cell ranges
				itemList.add(new DataItem(
						rangeList.get(0).toPartialColumnList().get(0), app));
				itemList.add(new DataItem(
						rangeList.get(1).toPartialColumnList().get(0), app));

			} else {
				// extract horizontal cell range
				itemList.add(new DataItem(
						rangeList.get(0).toPartialRowList().get(0), app));
				itemList.add(new DataItem(
						rangeList.get(1).toPartialRowList().get(0), app));
			}
		}
	}

	// ====================================
	// Utility methods
	// ====================================

	/**
	 * Returns true if the current data source contains the specified GeoElement
	 * 
	 * @param geo
	 *            element
	 * @return whether element is in the source
	 */
	protected boolean isInDataSource(GeoElement geo) {

		for (DataVariable var : dataList) {
			if (var.isInDataSource(geo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get variable descriptions
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void getXMLDescription(StringBuilder sb) {
		for (DataVariable var : dataList) {
			var.getXML(sb);
		}
	}

	/**
	 * 
	 * @return if frequency data comes from column.
	 */
	public boolean isFrequencyFromColumn() {
		return frequencyFromColumn;
	}

	/**
	 * When set to true, spreadsheet 2nd column of selected cells are treated as
	 * frequency data for One-variable analysis.
	 * 
	 * @param value
	 *            to set
	 */
	public void setFrequencyFromColumn(boolean value) {
		this.frequencyFromColumn = value;
	}

	/**
	 * Frequency data for One-variable analysis is stored as a seperate entry in the rangeList,
	 * this method checks whether the list actually contains frequency data or not by checking
	 * if the first and last list entry are neighbors
	 * @param rangeList rangeList
	 * @return returns true if the first and last entries in the rangeList are neighboring cells
	 * either column-wize or row-wize.
	 */
	public boolean rangeListContainsFrequencies(ArrayList<TabularRange> rangeList) {
		if (!rangeList.isEmpty()) {
			TabularRange first = rangeList.get(0);
			TabularRange last = rangeList.get(rangeList.size() - 1);
			return (last.getMaxColumn() - first.getMinColumn() == 1)
					|| (last.getMaxRow() - first.getMinRow() == 1);
		}
		return false;
	}
}

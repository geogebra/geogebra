package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;

import java.util.ArrayList;

/**
 * Class to manage a data source for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */

public class DataSource {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private ArrayList<Object> list;

	private boolean enableHeader = false;

	private int mode;
	private int sourceType;
	private boolean isNumericData = true;

	private MyTableD spreadsheetTable;
	private CellRangeProcessor crp;

	/***************************************************
	 * Constructor
	 * 
	 * @param app
	 *            GeoGebra application
	 */
	public DataSource(AppD app) {
		this.app = app;
		list = new ArrayList<Object>();
	}

	// ====================================
	// List wrapper methods
	// ====================================

	/**
	 * Returns an object that can be used to reference data from the the
	 * currently selected geos.
	 * 
	 * @return Either a spreadsheet cell range, a GeoList or null if the
	 *         selected geos cannot be used for data analysis
	 */
	private Object getCurrentGeoSelection() {

		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			return null;
		}

		GeoElement geo = app.getSelectedGeos().get(0);

		if (geo.isGeoList()) {
			if (((GeoList) geo).getGeoElementForPropertiesDialog()
					.isGeoNumeric()) {
				return geo;
			}
			return null;
		}

		else if (geo.getSpreadsheetCoords() != null) {
			return spreadsheetTable().getSelectedCellRanges().clone();
		}

		return null;
	}

	/**
	 * Adds the currently selected geos to this data source
	 * 
	 * @param index
	 */
	public void addCurrentGeoSelection(int index) {

		// ensure list is large enough
		ensureSize(index + 1);

		// add the new data source object
		list.set(index, getCurrentGeoSelection());

		debug();
	}

	public void ensureSize(int size) {
		while (size > list.size()) {
			list.add(null);
		}
	}

	/**
	 * Adds a data source object to the end of the list if it passes the
	 * validDataSourceObject test.
	 * 
	 * @param obj
	 * @return true if valid data object
	 */
	public boolean add(Object obj) {
		// app.printStacktrace("");
		return list.add(validDataSourceObject(obj));
	}

	/**
	 * Wraps a single cell range inside an ArrayList<CellRange> and then adds
	 * this to the end of the list if it passes the validDataSourceObject test.
	 * 
	 * @param cellRange
	 * @return true if valid data object
	 */
	private boolean addCellRange(CellRange cellRange) {
		ArrayList<CellRange> rangeList = new ArrayList<CellRange>();
		rangeList.add(cellRange);
		return list.add(rangeList);
	}

	/**
	 * Adds an empty object to the source list
	 */
	public void addEmpty() {
		list.add(null);
	}

	public Object get(int index) {
		return list.get(index);
	}

	public void remove(int index) {
		list.remove(index);
	}

	public void removeLast() {
		list.remove(list.size() - 1);
	}

	public int size() {
		return list.size();
	}

	public void clear() {
		list.clear();
	}

	public void emptyAll() {
		for (Object obj : list)
			obj = null;
	}

	// ====================================
	// Getters/Setters
	// ====================================

	public boolean enableHeader() {
		return enableHeader;
	}

	public void setEnableHeader(boolean enableHeader) {
		this.enableHeader = enableHeader;
	}

	public boolean isNumericData() {
		return isNumericData;
	}

	public void setNumericData(boolean isNumericData) {
		this.isNumericData = isNumericData;
	}

	// ====================================
	// Utility methods
	// ====================================

	private MyTableD spreadsheetTable() {
		if (spreadsheetTable == null) {
			spreadsheetTable = (MyTableD) ((GuiManagerD) app.getGuiManager())
					.getSpreadsheetView().getSpreadsheetTable();
		}
		return spreadsheetTable;
	}

	private CellRangeProcessor crProcessor() {
		if (crp == null) {
			crp = spreadsheetTable().getCellRangeProcessor();
		}
		return crp;
	}

	public void debug() {
		System.out.println("================================");
		if (list.size() == 0) {
			System.out.println("empty data source list");
		} else {
			for (int i = 0; i < list.size(); i++) {
				System.out.println(i + ": " + getSourceString(i));
			}
		}
		System.out.println("================================");
	}

	// ====================================
	// Validation
	// ====================================

	/**
	 * @param obj
	 *            object to be examined
	 * @return the given obj if it is a 1D GeoList or spreadsheet cell range
	 *         list; return null otherwise
	 */
	public Object validDataSourceObject(Object obj) {

		if (obj == null) {
			return null;
		}

		// 1D GeoList?
		if (obj instanceof GeoList && !((GeoList) obj).isMatrix()) {
			return obj;
		}

		// spreadsheet range list?
		try {
			ArrayList<CellRange> rangeList = (ArrayList<CellRange>) obj;
			return obj;

		} catch (Exception e) {
			App.error(e.getMessage());
			return null;
		}

	}

	// TODO why this?
	public boolean isGeoList(int index) {
		return list.get(index) instanceof GeoList;
	}

	// ====================================
	// Automatic Source Generation
	// ====================================

	// TODO: throw exception or return boolean if invalid data?
	/**
	 * Sets this DataSource to the currently selected GeoElements if they form a
	 * valid data source.
	 * 
	 * @param mode
	 *            Data analysis mode
	 * @param sourceType
	 *            data type (raw data, value/frequency etc.)
	 */
	public void setDataSourceAutomatically(int mode, int sourceType) {

		list.clear();
		boolean isValidData = true;

		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			isValidData = false;
			return;
		}

		// use first selected geo for testing
		GeoElement geo0 = app.getSelectedGeos().get(0);

		// set the data source
		try {
			// spreadsheet range list
			if (geo0.getSpreadsheetCoords() != null) {
				isValidData = setDataSourceFromSpreadsheet(mode, sourceType);

				// GeoList(s)
			} else if (geo0.isGeoList()) {
				isValidData = setDataSourceFromGeoLists(mode);

			} else {
				isValidData = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			isValidData = false;
		}
		return;
	}

	private boolean setDataSourceFromGeoLists(int mode) {

		for (GeoElement geo : app.getSelectedGeos()) {
			if (geo.isGeoList() && !((GeoList) geo).isMatrix()) {
				list.add(geo);
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean setDataSourceFromSpreadsheet(int mode, int sourceType) {

		// The cell range list returned by the spreadsheet can change
		// dynamically, so we need to use a copy.

		ArrayList<CellRange> rangeList = CellRangeProcessor
				.clone(spreadsheetTable().selectedCellRanges);

		// exit if the spreadsheet range is not appropriate for the given mode
		// and source type
		if (!isSpreadsheetDataOK(rangeList, mode)) {
			return false;
		}

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:

			if (sourceType == DataAnalysisViewD.SOURCE_RAWDATA) {
				list.add(rangeList);

			} else if (sourceType == DataAnalysisViewD.SOURCE_VALUE_FREQUENCY) {

				add1DCellRanges(rangeList);

				// TODO handle class/frequency source
			} else if (sourceType == DataAnalysisViewD.SOURCE_CLASS_FREQUENCY) {
				list.add(rangeList);
			}
			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			add1DCellRanges(rangeList);
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			ArrayList<CellRange> r;
			for (CellRange cr : rangeList) {
				if (cr.isRow() || cr.isPartialRow()) {
					r = cr.toPartialRowList();
					for (CellRange cr2 : r) {
						addCellRange(cr2);
					}
				} else {
					r = cr.toPartialColumnList();
					for (CellRange cr2 : r) {
						addCellRange(cr2);
					}
				}
			}

			break;
		}

		return true;
	}

	/**
	 * Attempts to extract two 1D cell ranges from the given cell range list and
	 * then add these to the source list. The orientation of the 1D cell ranges
	 * (vertical or horizontal) is determined from the shape of the given cell
	 * ranges.
	 * 
	 * @param rangeList
	 *            given list of CellRange objects
	 */
	private void add1DCellRanges(ArrayList<CellRange> rangeList) {

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
					addCellRange(r.get(0));
				if (r.size() > 1)
					addCellRange(r.get(1));
			}

		} else if (rangeList.size() == 2) { // two separate cell ranges

			if (scanByColumn) {
				// extract vertical cell ranges
				addCellRange(rangeList.get(0).toPartialColumnList().get(0));
				addCellRange(rangeList.get(1).toPartialColumnList().get(0));

			} else {
				// extract horizontal cell range
				addCellRange(rangeList.get(0).toPartialRowList().get(0));
				addCellRange(rangeList.get(1).toPartialRowList().get(0));
			}
		}
	}

	/**
	 * @param rangeList
	 * @param mode
	 * @return
	 */
	private boolean isSpreadsheetDataOK(ArrayList<CellRange> rangeList, int mode) {

		switch (mode) {
		case DataAnalysisViewD.MODE_ONEVAR:
			return crProcessor().isOneVarStatsPossible(rangeList);

		case DataAnalysisViewD.MODE_REGRESSION:
			return CellRangeProcessor.isCreatePointListPossible(rangeList);

		case DataAnalysisViewD.MODE_MULTIVAR:
			return crProcessor().isMultiVarStatsPossible(rangeList);

		default:
			App.error("data analysis test for valid spreadsheet data failed");
			return false;
		}
	}

	public void toPointList() {

	}

	private String[][] getFreqValueArray() {

		String[][] s = new String[2][];

		ArrayList<CellRange> rangeList = spreadsheetTable().selectedCellRanges;
		CellRangeProcessor cr = spreadsheetTable().getCellRangeProcessor();
		ArrayList<String> list0 = null;
		ArrayList<String> list1 = null;
		ArrayList<CellRange> r;

		boolean scanByColumn = rangeList.get(0).getActualDimensions()[1] <= 2;

		// System.out.println("scanby column = " + scanByColumn + "  " +
		// rangeList.get(0).getActualDimensions()[0]
		// + "," + rangeList.get(0).getActualDimensions()[1]);

		// =================
		// step 1: get lists of values and frequencies from spreadsheet cells

		if (rangeList.size() == 1) { // single cell range

			if (scanByColumn) {
				r = rangeList.get(0).toPartialColumnList();
			} else {
				r = rangeList.get(0).toPartialRowList();
			}

			list0 = r.get(0).toGeoValueList(scanByColumn);
			list1 = r.get(1).toGeoValueList(scanByColumn);

		} else if (rangeList.size() == 2) { // two separate cell ranges

			if (scanByColumn) {
				// extract column values
				list0 = rangeList.get(0).toPartialColumnList().get(0)
						.toGeoValueList(true);
				list1 = rangeList.get(1).toPartialColumnList().get(0)
						.toGeoValueList(true);
			} else {
				// extract row values
				list0 = rangeList.get(0).toPartialRowList().get(0)
						.toGeoValueList(false);
				list1 = rangeList.get(1).toPartialRowList().get(0)
						.toGeoValueList(false);
			}

		}

		// =================
		// step 2: convert lists to arrays

		s[0] = new String[list0.size()];
		list0.toArray(s[0]);
		s[1] = new String[list1.size()];
		list1.toArray(s[1]);

		return s;

	}

	/**
	 * @param mode
	 * @param sourceType
	 * @return
	 */
	public boolean isValidDataSource(int mode, int sourceType) {

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:
			if (sourceType == DataAnalysisViewD.SOURCE_RAWDATA) {
				return list.size() == 1;
			} else if (sourceType == DataAnalysisViewD.SOURCE_VALUE_FREQUENCY) {
				return list.size() == 2;
			} else if (sourceType == DataAnalysisViewD.SOURCE_CLASS_FREQUENCY) {
				return (list.size() == 2 || list.size() == 3);
			}
			return false;

		case DataAnalysisViewD.MODE_REGRESSION:
			return (list.size() == 2);

		case DataAnalysisViewD.MODE_MULTIVAR:
			return list.size() > 0;
		}

		return false;
	}

	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo) {

		if (geo == null)
			return false;

		// TODO handle case of GeoList data source
		if (geo instanceof GeoList) {
			for (Object obj : list) {
				if (obj instanceof GeoList && (geo == (GeoList) obj)) {
					return true;
				}
			}
		}

		GPoint location = geo.getSpreadsheetCoords();
		boolean isCell = (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS && location.y < Kernel.MAX_SPREADSHEET_ROWS);

		if (isCell) {
			// Application.debug("---------> is cell:" + geo.toString());
			for (Object obj : list) {
				try {
					ArrayList<CellRange> rangeList = (ArrayList<CellRange>) obj;
					for (CellRange cr : (ArrayList<CellRange>) rangeList)
						if (cr.contains(geo)) {
							return true;
						}

				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * Gets the data titles from the source cells.
	 * 
	 * @return String array of data titles
	 */
	public String[] getDataTitles2(int mode) {

		if (true) {
			String[] s = new String[list.size()];
			for (int i = 0; i < s.length; i++) {
				s[i] = "temp";
			}
			return s;
		}

		Object dataSource = null;

		ArrayList<CellRange> rangeList = spreadsheetTable().selectedCellRanges;
		CellRangeProcessor cellRangeProc = spreadsheetTable()
				.getCellRangeProcessor();

		String[] title = null;

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:

			title = new String[1];
			StringTemplate tpl = StringTemplate.defaultTemplate;

			if (dataSource instanceof GeoList) {
				title[0] = ((GeoList) dataSource).getLabel(tpl);

			} else {
				CellRange range = ((ArrayList<CellRange>) dataSource).get(0);
				if (range.isColumn()) {
					GeoElement geo = RelativeCopy.getValue(app,
							range.getMinColumn(), range.getMinRow());
					if (geo != null && geo.isGeoText())
						title[0] = geo.toDefinedValueString(tpl);
					else
						title[0] = app.getCommand("Column")
								+ " "
								+ GeoElementSpreadsheet
										.getSpreadsheetColumnName(range
												.getMinColumn());

				} else {
					title[0] = app.getMenu("Untitled");
				}
			}

			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			if (dataSource instanceof GeoList) {
				// TODO -- handle geolist data source titles
				// title[0] = ((GeoList) dataSource).getLabel();
			} else {
				// title = cellRangeProc.getPointListTitles(
				// (ArrayList<CellRange>) dataSource, leftToRight);
			}
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			if (dataSource instanceof GeoList) {
				// TODO -- handle geolist data source titles
				// title[0] = ((GeoList) dataSource).getLabel();
			} else {

				// data is in a single cell range
				if (((ArrayList<CellRange>) dataSource).size() == 1) {
					CellRange cr = ((ArrayList<CellRange>) dataSource).get(0);
					title = new String[cr.getMaxColumn() - cr.getMinColumn()
							+ 1];
					for (int i = 0; i < title.length; i++) {
						CellRange cr2 = new CellRange(app, cr.getMinColumn()
								+ i, cr.getMinRow(), cr.getMinColumn() + i,
								cr.getMaxRow());

						title[i] = cellRangeProc.getCellRangeString(cr2);
					}
				}

				// data is in columns
				else {
					title = cellRangeProc
							.getColumnTitles((ArrayList<CellRange>) dataSource);
				}
			}
			break;

		}

		// return title;

		String[] temp = { "1", "2", "3", "4", "5" };
		return temp;
	}

	/**
	 * Returns the data titles for each data source object in the source list.
	 * 
	 * @return String array of data titles
	 */
	public String[] getDataTitles(int mode) {

		String[] s = new String[list.size()];

		for (int i = 0; i < s.length; i++) {
			s[i] = getDataTitle(i);
		}
		return s;
	}

	/**
	 * Returns the data title for a data source object.
	 * 
	 * @param index
	 *            position of data source object in the data source list.
	 * @return String data title
	 */
	public String getDataTitle(int index) {

		Object obj = list.get(index);

		if (!enableHeader || obj instanceof GeoList) {
			return (this.getSourceString(index));

		} else if (obj instanceof ArrayList<?>) {

			StringTemplate tpl = StringTemplate.defaultTemplate;
			CellRange range = ((ArrayList<CellRange>) obj).get(0);

			if (range.isColumn() || range.isPartialColumn()) {
				GeoElement geo = RelativeCopy.getValue(app,
						range.getMinColumn(), range.getMinRow());
				if (geo != null) {
					return geo.toDefinedValueString(tpl);
				}
			}
		}

		return app.getMenu("Untitled");
	}

	/**
	 * Returns a string description for the data source at the given index
	 * position.
	 * 
	 * @return either a spreadsheet cell range name or a GeoList label
	 */
	public String getSourceString(int index) {

		String sourceString = " ";

		if (index >= size() || get(index) == null) {
			return sourceString;
		}

		Object obj = get(index);

		if (obj instanceof GeoList) {
			sourceString = ((GeoList) obj)
					.getLabel(StringTemplate.defaultTemplate);

		} else {
			sourceString = spreadsheetTable().getCellRangeProcessor()
					.getCellRangeString((ArrayList<CellRange>) obj);
		}

		return sourceString;
	}

	/**
	 * Loads references to GeoElements contained in (Object) dataSource into
	 * (GeoList) dataListSelected
	 * 
	 * @param dataSelected
	 * @param mode
	 * @param sourceType
	 * @return
	 */
	protected ArrayList<GeoList> loadDataLists(int mode, int sourceType) {

		if (list.size() == 0 || list.get(0) == null) {
			return null;
		}

		ArrayList<GeoList> sourceList = new ArrayList<GeoList>();

		boolean leftToRight = false;
		boolean scanByColumn = true;
		boolean copyByValue = false;
		boolean doStoreUndo = false;
		boolean isSorted = false;
		boolean doCreateFreePoints = false;
		boolean setLabel = false;

		if (mode == DataAnalysisViewD.MODE_REGRESSION && size() == 1) {

			// ============================================
			// case 1: list of points
			// ============================================

			if (get(0) instanceof GeoList) {
				sourceList.add((GeoList) get(0));
				return sourceList;
			}

			try {
				GeoList geoList = (GeoList) crProcessor().createList(
						(ArrayList<CellRange>) get(0), scanByColumn,
						copyByValue, isSorted, doStoreUndo, GeoClass.NUMERIC,
						setLabel);
				sourceList.add(geoList);
				return sourceList;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		} else if (mode == DataAnalysisViewD.MODE_REGRESSION && size() == 2) {

			// ============================================
			// case 2: two number lists converted to list of points
			// ============================================

			if (list.size() < 2 || list.get(1) == null) {
				return null;
			}

			if (get(0) instanceof GeoList) {
				// TODO handle this case
				return null;
			}

			try {

				CellRange xRange = ((ArrayList<CellRange>) list.get(0)).get(0)
						.clone();
				CellRange yRange = ((ArrayList<CellRange>) list.get(1)).get(0)
						.clone();

				ArrayList<CellRange> xyList = new ArrayList<CellRange>();
				xyList.add(xRange);
				xyList.add(yRange);

				GeoList geoList = crProcessor().createPointGeoList(xyList,
						copyByValue, leftToRight, isSorted, doStoreUndo,
						doCreateFreePoints);

				sourceList.add(geoList);
				return sourceList;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		} else {

			// ============================================
			// case 3: lists for other modes
			// ============================================

			for (Object obj : list) {
				if (obj instanceof GeoList) {
					sourceList.add((GeoList) obj);
				} else {
					try {
						GeoList geoList = (GeoList) crProcessor().createList(
								(ArrayList<CellRange>) obj, scanByColumn,
								copyByValue, isSorted, doStoreUndo,
								GeoClass.NUMERIC, setLabel);
						sourceList.add(geoList);
						//App.error(geoList
							//	.toOutputValueString(StringTemplate.defaultTemplate));
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			return sourceList;

		}

	}

	/**
	 * Loads references to GeoElements contained in (Object) dataSource into
	 * (GeoList) dataListSelected and (ArrayList) dataArray .
	 */
	protected GeoList loadDataLists2(int mode, int sourceType) {

		boolean leftToRight = false;

		GeoList dataSelected = null;

		CellRangeProcessor crProcessor = spreadsheetTable()
				.getCellRangeProcessor();

		boolean scanByColumn = true;
		boolean copyByValue = false;
		boolean doStoreUndo = false;
		boolean isSorted = false;
		boolean doCreateFreePoints = false;
		boolean setLabel = false;

		// =======================================
		// create/update dataListAll

		{

			ArrayList<CellRange> cellRangeList = (ArrayList<CellRange>) this
					.get(0);

			switch (mode) {

			case DataAnalysisViewD.MODE_ONEVAR:
				if (sourceType == DataAnalysisViewD.SOURCE_RAWDATA) {
					dataSelected = (GeoList) crProcessor.createList(
							cellRangeList, scanByColumn, copyByValue, isSorted,
							doStoreUndo, GeoClass.NUMERIC, setLabel);
				}

				else if (sourceType == DataAnalysisViewD.SOURCE_VALUE_FREQUENCY) {
					dataSelected = crProcessor.createCollectionList(
							cellRangeList, copyByValue, setLabel, scanByColumn);
				} else {
					dataSelected = (GeoList) crProcessor.createList(
							cellRangeList, scanByColumn, copyByValue, isSorted,
							doStoreUndo, GeoClass.NUMERIC, setLabel);
				}
				break;

			case DataAnalysisViewD.MODE_REGRESSION:

				// data is a cell range of points
				if (cellRangeList.size() == 1
						&& cellRangeList.get(0).isPointList()) {
					dataSelected = (GeoList) crProcessor.createList(
							cellRangeList, scanByColumn, copyByValue, isSorted,
							doStoreUndo, GeoClass.POINT, setLabel);
				}

				// data is from two cell ranges of numbers that must be
				// converted to points
				else {
					dataSelected = crProcessor.createPointGeoList(
							cellRangeList, copyByValue, leftToRight, isSorted,
							doStoreUndo, doCreateFreePoints);
				}
				break;

			case DataAnalysisViewD.MODE_MULTIVAR:
				dataSelected = crProcessor.createCollectionList(cellRangeList,
						copyByValue, setLabel, scanByColumn);
				break;

			}
		}

		return dataSelected;
	}

}

package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;

import java.util.ArrayList;

/**
 * Class to control data management for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class StatDialogController {

	private AppD app;
	private Kernel kernel;
	private Construction cons;
	private MyTableD spreadsheetTable;
	private StatDialog sd;
	private StatGeo statGeo;

	private Object dataSource;

	private ArrayList<GeoElement> dataArray;

	public ArrayList<GeoElement> getDataArray() {
		return dataArray;
	}

	private GeoList dataSelected;

	public GeoList getDataSelected() {
		return dataSelected;
	}

	private boolean leftToRight = true;
	private boolean isValidData;

	public boolean isValidData() {
		return isValidData;
	}

	public void setValidData(boolean isValidData) {
		this.isValidData = isValidData;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	private GeoElement geoRegression;

	public GeoElement getRegressionModel() {
		return geoRegression;
	}

	public void setRegressionModel(GeoFunction regressionModel) {
		this.geoRegression = regressionModel;
	}

	/****************************************************
	 * Constructs a StatDialogController
	 * 
	 * @param app
	 * @param spView
	 * @param statDialog
	 */
	public StatDialogController(AppD app, StatDialog statDialog) {

		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.spreadsheetTable = (MyTableD) app.getGuiManager()
				.getSpreadsheetView().getTable();
		this.sd = statDialog;
		this.statGeo = sd.getStatGeo();

	}

	private int mode() {
		return sd.getMode();
	}

	/**
	 * Sets the field dataSource to the currently selected GeoElements if they
	 * form a valid data source. If valid, then dataSource is either a GeoList
	 * or a spreadsheet cell range. If invalid, dataSource = null.
	 * 
	 * The boolean field isValidData is also set as a flag for valid/invalid
	 * data.
	 */
	protected void setDataSource() {

		dataSource = null;
		isValidData = true;
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();

		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			isValidData = false;
			return;
		}

		try {

			GeoElement geo = app.getSelectedGeos().get(0);
			if (geo.isGeoList()) {
				// TODO: handle validation for a geoList source
				dataSource = geo;
			} else {
				ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;
				isValidData = isSpreadsheetDataOK(rangeList, mode());
				if (isValidData) {
					dataSource = rangeList.clone();
					// rangeList.get(0).debug();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			isValidData = false;
		}

		return;
	}

	protected boolean isSpreadsheetDataOK(ArrayList<CellRange> rangeList, int mode) {

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();

		switch (mode) {
		case StatDialog.MODE_ONEVAR:
			return cr.isOneVarStatsPossible(rangeList);

		case StatDialog.MODE_REGRESSION:
			return cr.isCreatePointListPossible(rangeList);

		case StatDialog.MODE_MULTIVAR:
			return cr.isMultiVarStatsPossible(rangeList);
		default:
			App.error("data analysis test for valid spreadsheet data failed");
			return false;
		}
	}
	
	
	protected boolean is1DSource() {

		ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;
		return(spreadsheetTable.getCellRangeProcessor().is1DRangeList(rangeList));

	}
	
	
	
	
	

	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo) {

		if (dataSource == null)
			return false;

		// TODO handle case of GeoList data source
		if (dataSource instanceof GeoList) {
			return geo.equals(dataSource);
		}

		GPoint location = geo.getSpreadsheetCoords();
		boolean isCell = (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS && location.y < Kernel.MAX_SPREADSHEET_ROWS);

		if (isCell) {
			// Application.debug("---------> is cell:" + geo.toString());
			for (CellRange cr : (ArrayList<CellRange>) dataSource)
				if (cr.contains(geo))
					return true;

			// Application.debug("---------> is not in data source:" +
			// geo.toString());
		}

		return false;
	}

	/**
	 * Loads references to GeoElements contained in (Object) dataSource into
	 * (GeoList) dataListSelected and (ArrayList) dataArray .
	 */
	protected void loadDataLists() {

		if (dataSource == null)
			return;

		CellRangeProcessor crProcessor = spreadsheetTable
				.getCellRangeProcessor();

		boolean scanByColumn = true;
		boolean copyByValue = false;
		boolean doStoreUndo = false;
		boolean isSorted = false;
		boolean doCreateFreePoints = false;
		boolean setLabel = false;

		// =======================================
		// create/update dataListAll
		if (dataSelected != null)
			dataSelected.remove();

		// TODO: handle dataSource of type geoList
		if (dataSource instanceof GeoList) {
			// TODO
		} else {

			ArrayList<CellRange> cellRangeList = (ArrayList<CellRange>) dataSource;

			switch (mode()) {

			case StatDialog.MODE_ONEVAR:
				if (sd.getSourceType() == StatDialog.SOURCE_FREQUENCY_VALUE) {
					dataSelected = crProcessor.createCollectionList(
							cellRangeList, copyByValue, setLabel, scanByColumn);
				} else {
					dataSelected = (GeoList) crProcessor.createList(
							cellRangeList, scanByColumn, copyByValue, isSorted,
							doStoreUndo, GeoClass.NUMERIC, setLabel);
				}
				break;

			case StatDialog.MODE_REGRESSION:

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

			case StatDialog.MODE_MULTIVAR:
				dataSelected = crProcessor.createCollectionList(cellRangeList,
						copyByValue, setLabel, scanByColumn);
				break;

			case StatDialog.MODE_GROUPDATA:
				dataSelected = (GeoList) crProcessor.createList(cellRangeList,
						scanByColumn, copyByValue, isSorted, doStoreUndo,
						GeoClass.NUMERIC, setLabel);

				break;
			}
		}

		// create and update dataArray (list of all geos contained in
		// dataSelected)

		if (dataSelected != null) {

			if (dataArray == null)
				dataArray = new ArrayList<GeoElement>();

			dataArray.clear();
			for (int i = 0; i < dataSelected.size(); i++) {
				dataArray.add(i, dataSelected.get(i));
			}

			// load dataPanel with dataArray
			if (mode() != StatDialog.MODE_MULTIVAR
					&& mode() != StatDialog.MODE_GROUPDATA) {
				sd.getDataPanel().loadDataTable(dataArray);
			}
		} else {
			App.error("null dataSelected, mode = " + mode());
		}
	}

	/**
	 * Add/remove elements from the selected data list. Called by the data panel
	 * on checkbox click.
	 */
	public void updateSelectedDataList(int index, boolean doAdd) {

		GeoElement geo = dataArray.get(index);

		if (doAdd) {
			dataSelected.add(geo);
		} else {
			dataSelected.remove(geo);
		}

		dataSelected.updateCascade();
		updateAllStatPanels(false);
		if (sd.regressionPanel != null)
			sd.regressionPanel.updateRegressionPanel();
		// Application.debug("updateSelectedList: " + index + doAdd);

	}

	/**
	 * Gets the data titles from the source cells.
	 * 
	 * @return String array of data titles
	 */
	public String[] getDataTitles() {

		if (dataSource == null)
			return null;

		CellRangeProcessor cellRangeProc = spreadsheetTable
				.getCellRangeProcessor();
		String[] title = null;

		switch (mode()) {

		case StatDialog.MODE_ONEVAR:

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

		case StatDialog.MODE_REGRESSION:
			if (dataSource instanceof GeoList) {
				// TODO -- handle geolist data source titles
				// title[0] = ((GeoList) dataSource).getLabel();
			} else {
				title = cellRangeProc.getPointListTitles(
						(ArrayList<CellRange>) dataSource, leftToRight);
			}
			break;

		case StatDialog.MODE_MULTIVAR:
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

		return title;
	}

	/**
	 * Returns a description of the data source.
	 * 
	 * @return either a spreadsheet cell range name or a GeoList label
	 */
	public String getSourceString() {

		if (dataSource == null)
			return null;

		String title = null;

		if (dataSource instanceof GeoList) {
			title = ((GeoList) dataSource)
					.getLabel(StringTemplate.defaultTemplate);

		} else {
			title = spreadsheetTable.getCellRangeProcessor()
					.getCellRangeString((ArrayList<CellRange>) dataSource);
		}

		return title;
	}

	public void swapXY() {
		leftToRight = !leftToRight;
		updateDataAnalysisView(false);
		sd.regressionPanel.clearPredictionPanel();
	}

	/**
	 * Updates the view to reflect the current values of the GeoElements in the
	 * data source, or values from a new data source.
	 * 
	 * @param doSetDataSource
	 *            true = set the data source to a new collection of GeoElements
	 */
	public void updateDataAnalysisView(boolean doSetDataSource) {

		updateDataSource(doSetDataSource);

		if (isValidData) {
			if (mode() == StatDialog.MODE_REGRESSION) {
				setRegressionGeo();
			}

		} else {
			// TODO --- handle bad data
			App.error("error in updateDialog");
		}

		updateAllStatPanels(true);
		sd.updateGUI();
		sd.revalidate();
		sd.repaint();

	}

	public void updateDataSource(boolean doSetDataSource) {

		removeStatGeos();

		if (doSetDataSource) {
			setDataSource();
		}

		if (isValidData) {
			loadDataLists();
		} else {
			// TODO --- handle bad data
			App.error("error in updateDataSource");
		}

		return;
	}

	public void updateAllStatPanels(boolean doCreateGeo) {

		sd.comboStatPanel.updatePlot(doCreateGeo);
		if (sd.comboStatPanel2 != null)
			sd.comboStatPanel2.updatePlot(doCreateGeo);
		if (sd.statisticsPanel != null) {
			sd.statisticsPanel.updatePanel();
		}

	}

	protected void handleRemovedDataGeo(GeoElement geo) {

		// System.out.println("removed: " + geo.toString());
		if (isInDataSource(geo)) {
			// System.out.println("stat dialog removed: " + geo.toString());
			// removeStatGeos();
			dataSource = null;
			updateDataAnalysisView(false);
		}

	}

	public void setRegressionGeo() {

		removeRegressionGeo();

		geoRegression = statGeo.createRegressionPlot(dataSelected,
				sd.getRegressionMode(), sd.getRegressionOrder(), false);

		if (sd.regressionPanel != null)
			sd.regressionPanel.updateRegressionPanel();
	}

	public void removeRegressionGeo() {
		if (geoRegression != null) {
			geoRegression.remove();
			geoRegression.doRemove();
			geoRegression = null;
		}
	}

	public void disposeDataListSelected() {
		dataSelected = null;
	}

	/**
	 * Removes all geos maintained by this dialog and its child components
	 */
	public void removeStatGeos() {

		if (dataSelected != null) {
			dataSelected.remove();
			dataSelected = null;
		}

		removeRegressionGeo();

		if (sd.comboStatPanel != null) {
			sd.comboStatPanel.removeGeos();
		}

		if (sd.comboStatPanel2 != null) {
			sd.comboStatPanel2.removeGeos();
		}
	}

	public double[] getValueArray(GeoList dataList) {
		ArrayList<Double> list = new ArrayList<Double>();
		for (int i = 0; i < dataList.size(); i++) {
			GeoElement geo = dataList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				list.add(num.getDouble());
			}
		}
		double[] val = new double[list.size()];
		for (int i = 0; i < list.size(); i++)
			val[i] = list.get(i);

		return val;
	}

}

package geogebra.gui.view.data;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
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
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;

import java.util.ArrayList;

/**
 * Class to control data management for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisControllerD {

	private AppD app;
	private Kernel kernel;
	private Construction cons;
	private MyTableD spreadsheetTable;
	private DataAnalysisViewD view;
	private StatGeo statGeo;

	private DataSource dataSource;

	private ArrayList<GeoElement> dataArray;

	public ArrayList<GeoElement> getDataArray() {
		return dataArray;
	}

	private GeoList dataSelected;

	public GeoList getDataSelected() {
		return dataSelected;
	}

	private boolean leftToRight = true;
	private boolean isValidData = true;

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
	public DataAnalysisControllerD(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.spreadsheetTable = (MyTableD) ((GuiManagerD) app.getGuiManager())
				.getSpreadsheetView().getSpreadsheetTable();
		this.view = statDialog;
		this.statGeo = view.getStatGeo();

	}

	private int mode() {
		return view.getMode();
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
				// dataSource = geo;
			} else {
				ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;
				isValidData = isSpreadsheetDataOK(rangeList, mode());
				if (isValidData) {
					// dataSource = rangeList.clone();
					// rangeList.get(0).debug();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			isValidData = false;
		}

		return;
	}

	protected void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected boolean isSpreadsheetDataOK(ArrayList<CellRange> rangeList,
			int mode) {

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();

		switch (mode) {
		case DataAnalysisViewD.MODE_ONEVAR:
			return cr.isOneVarStatsPossible(rangeList);

		case DataAnalysisViewD.MODE_REGRESSION:
			return cr.isCreatePointListPossible(rangeList);

		case DataAnalysisViewD.MODE_MULTIVAR:
			return cr.isMultiVarStatsPossible(rangeList);
		default:
			App.error("data analysis test for valid spreadsheet data failed");
			return false;
		}
	}

	protected boolean is1DSource() {

		ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;
		return (spreadsheetTable.getCellRangeProcessor()
				.is1DRangeList(rangeList));

	}

	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo) {
		if (dataSource == null) {
			return false;
		}
		return dataSource.isInDataSource(geo);
	}

	/**
	 * Loads references to GeoElements contained in (Object) dataSource into
	 * (GeoList) dataListSelected and (ArrayList) dataArray .
	 */
	protected void loadDataLists() {

		if (dataSelected != null)
			dataSelected.remove();

		if (dataSource == null) {
			this.setValidData(false);
			return;
		}

		ArrayList<GeoList> list = dataSource.loadDataLists(mode(),
				view.getSourceType());

		if (list == null) {
			this.setValidData(false);
			return;
		}

		if (list.size() == 1) {
			dataSelected = list.get(0);
		} else {
			dataSelected = new GeoList(cons);
			for (GeoList geoList : list) {
				// TODO: suppress label creation!!!!
				dataSelected.add(geoList);
			}
		}

		loadDataArray();
	}

	private void loadDataArray() {

		// create and update dataArray (list of all geos contained in
		// dataSelected)

		if (dataSelected != null) {

			if (dataArray == null)
				dataArray = new ArrayList<GeoElement>();

			dataArray.clear();
			for (int i = 0; i < dataSelected.size(); i++) {
				dataArray.add(i, dataSelected.get(i));
				// App.error(dataSelected.get(i).toOutputValueString(
				// StringTemplate.defaultTemplate));
			}

			// load dataPanel with dataArray
			if (mode() != DataAnalysisViewD.MODE_MULTIVAR
					&& mode() != DataAnalysisViewD.MODE_GROUPDATA) {
				view.getDataPanel().loadDataTable(dataArray);
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
		if (view.regressionPanel != null)
			view.regressionPanel.updateRegressionPanel();
		// Application.debug("updateSelectedList: " + index + doAdd);

	}

	/**
	 * Gets the data titles from the source cells.
	 * 
	 * @return String array of data titles
	 */
	public String[] getDataTitles() {
		return dataSource.getDataTitles(mode());
	}

	public void swapXY() {
		leftToRight = !leftToRight;
		updateDataAnalysisView();
		view.regressionPanel.clearPredictionPanel();
	}

	/**
	 * Updates the view to reflect the current values of the GeoElements in the
	 * data source.
	 */
	public void updateDataAnalysisView() {

		updateDataLists();

		if (isValidData) {
			if (mode() == DataAnalysisViewD.MODE_REGRESSION) {
				setRegressionGeo();
			}

		} else {
			// TODO --- handle bad data
			App.error("error in updateDialog");
		}

		updateAllStatPanels(true);
		view.updateGUI();
		view.revalidate();
		view.repaint();

	}

	public void updateDataLists() {

		removeStatGeos();
		loadDataLists();
		return;
	}

	public void updateAllStatPanels(boolean doCreateGeo) {
		App.error("updateAllStatPanel --- start");
		view.comboStatPanel.updatePlot(doCreateGeo);
		if (view.comboStatPanel2 != null)
			view.comboStatPanel2.updatePlot(doCreateGeo);
		if (view.statisticsPanel != null) {
			App.error("updateAllStatPanel --- statPANEL");
			view.statisticsPanel.updatePanel();
		}

	}

	protected void handleRemovedDataGeo(GeoElement geo) {

		// System.out.println("removed: " + geo.toString());
		if (isInDataSource(geo)) {
			// System.out.println("stat dialog removed: " + geo.toString());
			// removeStatGeos();
			dataSource.clear();
			this.setValidData(false);
			updateDataAnalysisView();
		}

	}

	public void setRegressionGeo() {

		removeRegressionGeo();

		geoRegression = statGeo.createRegressionPlot(dataSelected,
				view.getRegressionMode(), view.getRegressionOrder(), false);

		if (view.regressionPanel != null)
			view.regressionPanel.updateRegressionPanel();
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

		if (view.comboStatPanel != null) {
			view.comboStatPanel.removeGeos();
		}

		if (view.comboStatPanel2 != null) {
			view.comboStatPanel2.removeGeos();
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

package geogebra.gui.view.data;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
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
	private DataAnalysisViewD view;
	private StatGeo statGeo;

	private DataSource dataSource;

	private ArrayList<GeoElement> dataArray;
	private GeoList dataSelected;

	private boolean leftToRight = true;
	private boolean isValidData = true;

	private GeoElement geoRegression;

	/****************************************************
	 * Constructs a StatDialogController
	 * 
	 * @param app
	 * @param statDialog
	 */
	public DataAnalysisControllerD(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.view = statDialog;
		this.statGeo = view.getStatGeo();

	}

	// ==========================================
	// Getters/Setters
	// ==========================================

	private int mode() {
		return view.getMode();
	}

	public ArrayList<GeoElement> getDataArray() {
		return dataArray;
	}

	public GeoList getDataSelected() {
		return dataSelected;
	}

	public boolean isValidData() {
		return isValidData;
	}

	public void setValidData(boolean isValidData) {
		if (isValidData == false) {
			// app.printStacktrace("invalid data");
		}
		this.isValidData = isValidData;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public boolean isLeftToRight() {
		return leftToRight;
	}

	public GeoElement getRegressionModel() {
		return geoRegression;
	}

	public void setRegressionModel(GeoFunction regressionModel) {
		this.geoRegression = regressionModel;
	}

	// ==========================================
	// Data source
	// ==========================================

	public DataSource getDataSource() {
		return dataSource;
	}

	protected void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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
	 * Loads GeoElements from dataSource into (GeoList) dataListSelected and
	 * (ArrayList) dataArray .
	 * 
	 * @param doCopy
	 *            if true lists are loaded as copies
	 */
	protected void loadDataLists(boolean doCopy) {

		if (dataSelected != null)
			dataSelected.remove();

		if (dataSource == null) {
			setValidData(false);
			return;
		}

		// retrieve data from the data source as a list of GeoLists

		ArrayList<GeoList> list = new ArrayList<GeoList>();

		switch (mode()) {
		case DataAnalysisViewD.MODE_ONEVAR:
		case DataAnalysisViewD.MODE_REGRESSION:
			list = dataSource.toGeoList(mode(), leftToRight, doCopy, 0);
			break;
		case DataAnalysisViewD.MODE_MULTIVAR:
			list = dataSource.toGeoListAll(mode(), leftToRight, doCopy);
			break;
		}

		// validate
		if (!isValidList(list)) {
			setValidData(false);
			return;
		}
		setValidData(true);

		// convert the list to (GeoList) dataSelected
		// TODO: dataSelected should always be a list of lists
		if (list.size() == 1) {
			dataSelected = list.get(0);
		} else {
			dataSelected = new GeoList(cons);
			for (GeoList geoList : list) {
				// TODO: suppress label creation?
				dataSelected.add(geoList);
			}
		}

		// add the selected geos to the dataPanel array
		loadDataPanelArray();
	}

	private boolean isValidList(ArrayList<GeoList> list) {

		if (list == null || list.size()==0) {
			return false;
		}

		// check for empty lists
		for (int i = 0; i < list.size(); i++) {
			System.out.println("data list " + i + "  size: " + list.get(i).size());
			if (list.get(i).size() < 1) {
				System.out.println("invalid data list");
				return false;
			}
		}

		// TODO: add validation checks for different modes, e.g. are data
		// lengths equal?

		return true;
	}

	/**
	 * Loads the DataPanel array with references to all geos in the DataSource.
	 * This is used by DataPanel to add/remove geos from the dataSelected list
	 * without needing to change the DataSource.
	 * 
	 * TODO: use a more efficient method: maintain a map of removed geos as
	 * index/geo pairs
	 */
	private void loadDataPanelArray() {

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
			if (mode() != DataAnalysisViewD.MODE_MULTIVAR) {
				view.loadDataTable(dataArray);
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

		// debugDataSelected();
		dataSelected.updateRepaint();
		updateAllPanels(false);

		if (view.getRegressionPanel() != null)
			view.getRegressionPanel().updateRegressionPanel();

		// System.out.println("updateSelectedList: " + index + " " + doAdd);

	}

	private void debugDataSelected() {
		if (dataSelected == null) {
			return;
		}
		System.out.println("==========================");
		System.out.println("dataSelected: ");
		for (int i = 0; i < dataSelected.size(); i++) {
			System.out.println(dataSelected.get(i).toString(
					StringTemplate.defaultTemplate));
		}
		System.out.println("==========================");

	}

	/**
	 * Gets the data titles from the source cells.
	 * 
	 * @return String array of data titles
	 */
	public String[] getDataTitles() {
		return dataSource.getTitles();
		//return dataSource.getDataTitles(mode(), leftToRight);
	}

	public void swapXY() {
		leftToRight = !leftToRight;
		updateDataAnalysisView();
		view.getRegressionPanel().clearPredictionPanel();
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

			// update the panels
			// TODO: internal geos are all redefined, this is not efficient and
			// needs optimizing
			updateAllPanels(true);

		} else {
			// App.error("error in updateDialog");
		}

		view.updateGUI();
	}

	/**
	 * Loads the data lists with GeoElements references from the data source.
	 * All internal GeoElements are removed to prevent orphan links to
	 * previously loaded GeoElements.
	 */
	public void updateDataLists() {
		removeStatGeos();
		loadDataLists(true);
		return;
	}

	/**
	 * Updates all panels in the DataAnalysisView.
	 * 
	 * @param doRedefine
	 *            if true then the internal GeoElements will be redefined.
	 */
	public void updateAllPanels(boolean doRedefine) {

		view.getDataDisplayPanel1().updatePlot(doRedefine);
		if (mode() != DataAnalysisViewD.MODE_MULTIVAR
				&& view.getDataDisplayPanel2() != null)
			view.getDataDisplayPanel2().updatePlot(doRedefine);

		if (view.getStatisticsPanel() != null && view.showStatPanel()) {
			view.getStatisticsPanel().updatePanel();
		}

		if (view.getDataPanel() != null && view.showDataPanel()) {
			view.getDataPanel().updatePanel();
		}
	}

	protected void handleRemovedDataGeo(GeoElement geo) {

		// System.out.println("removed: " + geo.toString());
		if (isInDataSource(geo)) {
			// System.out.println("stat dialog removed: " + geo.toString());

			dataSource.clearData();
			this.setValidData(false);
			updateDataAnalysisView();
		}

	}

	public void setRegressionGeo() {

		removeRegressionGeo();

		geoRegression = statGeo.createRegressionPlot(dataSelected,
				view.getRegressionMode(), view.getRegressionOrder(), false);

		if (view.getRegressionPanel() != null)
			view.getRegressionPanel().updateRegressionPanel();
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

		if (view.getDataDisplayPanel1() != null) {
			view.getDataDisplayPanel1().removeGeos();
		}

		if (view.getDataDisplayPanel2() != null) {
			view.getDataDisplayPanel2().removeGeos();
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

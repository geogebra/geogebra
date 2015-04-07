package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;



/**
 * View to display plots and statistical analysis of data.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisModel {

	public interface IDataAnalysisListener extends ICreateColor {
		void onModeChange();
		void setPlotPanelOVNotNumeric(int mode);
		void setPlotPanelOVRawData(int mode);
		void setPlotPanelOVFrequency(int mode);
		void setPlotPanelOVClass(int mode);
		void setPlotPanelRegression(int mode);
		void setPlotPanelMultiVar(int mode);
		void loadDataTable(ArrayList<GeoElement> dataArray);
		void updateStatDataPanelVisibility();
		DataAnalysisController getController();
		void showComboPanel2(boolean show);
		void updateGUI();
	}
	
	public interface ICreateColor {
		GColor createColor(int idx);
	}
	private static final long serialVersionUID = 1L;

	// ggb
	private App app;
	private Kernel kernel;
	private StatGeo statGeo;

	public static final int MODE_ONEVAR = EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS;
	public static final int MODE_REGRESSION = EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS;
	public static final int MODE_MULTIVAR = EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS;
	private int mode = -1;

	// colors
	public static final int TABLE_GRID_COLOR_IDX = 0;
	public static final int TABLE_HEADER_COLOR_IDX = 1;
	public static final int HISTOGRAM_COLOR_IDX = 2;
	public static final int BOXPLOT_COLOR_IDX = 3;
	public static final int BARCHART_COLOR_IDX = 4;

	public static final int DOTPLOT_COLOR_IDX = 5;
	public static final int NQPLOT_COLOR_IDX = 6;
	public static final int REGRESSION_COLOR_IDX = 7;
	public static final int OVERLAY_COLOR_IDX = 8;
	public static final int BLACK_COLOR_IDX = 9;
	public static final int WHITE_COLOR_IDX = 10;

	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showDataDisplayPanel2 = false;
	private boolean isIniting = true;

	public static final float opacityBarChart = 0.3f;
	public static final int thicknessCurve = 4;
	public static final int thicknessBarChart = 3;

	/**
	 * @author mrb
	 * 
	 *         Order determines order in Two Variable Regression Analysis menu
	 *         For each String, getMenu(s) must be defined
	 */
	public enum Regression {
		NONE("None"), LINEAR("Linear"), LOG("Log"), POLY("Polynomial"), POW(
				"Power"), EXP("Exponential"), GROWTH("Growth"), SIN("Sin"), LOGISTIC(
				"Logistic");

		// getMenu(label) must be defined
		private String label;

		Regression(String s) {
			this.label = s;
		}

		public String getLabel() {
			return label;
		}
	}

	// rounding constants for local number format
	private int printDecimals = 4, printFigures = -1;

	// public static final int regressionTypes = 9;
	private Regression regressionMode = Regression.NONE;
	private int regressionOrder = 2;

	private int defaultDividerSize;

	private DataAnalysisController ctrl;

	final static String MainCard = "Card with main panel";
	final static String SourceCard = "Card with data type options";

	private IDataAnalysisListener listener;
	
	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisModel(App app, int mode, IDataAnalysisListener listener,
			DataAnalysisController ctrl) {
		setIniting(true);
		this.app = app;
		this.kernel = app.getKernel();
		
		this.setListener(listener);
		this.ctrl = ctrl;
		ctrl.setModel(this);
		setIniting(false);

	}

	/*************************************************
	 * END constructor
	 */

	public void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {

		ctrl.setDataSource(dataSource);

		if (dataSource == null) {
			ctrl.setValidData(false);
		} else {
			ctrl.setValidData(true);
		}

		if (mode == MODE_ONEVAR) {
			if (showDataPanel == true
					&& dataSource.getGroupType() != GroupType.RAWDATA) {
				setShowDataPanel(false);
			}
		}

		// reinit the GUI if mode is changed
		if (this.getMode() != mode || forceModeUpdate) {

			this.setMode(mode);
			// first update the lists to make sure onModeChange 
			// does not fail on one var -> two var mode change
			ctrl.updateDataLists();
			getListener().onModeChange();
			
		
			// TODO: why do this here?
			ctrl.updateDataAnalysisView();

		} else {
			// just update data source
			ctrl.updateDataAnalysisView();
		}

		// TODO is this needed?
		ctrl.setLeftToRight(true);

	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels() {

		switch (getMode()) {

		case MODE_ONEVAR:
			if (!isNumericData()) {
				getListener().setPlotPanelOVNotNumeric(getMode());

			} else if (groupType() == GroupType.RAWDATA) {
				getListener().setPlotPanelOVRawData(getMode());
			} else if (groupType() == GroupType.FREQUENCY) {
				getListener().setPlotPanelOVFrequency(getMode());

			} else if (groupType() == GroupType.CLASS) {
				getListener().setPlotPanelOVClass(getMode());
			}
			break;

		case MODE_REGRESSION:
			getListener().setPlotPanelRegression(getMode());
			break;

		case MODE_MULTIVAR:
			getListener().setPlotPanelMultiVar(getMode());
			showDataDisplayPanel2 = false;
			break;
		}
	}

	// ======================================
	// Getters/setters
	// ======================================

	public DataAnalysisController getDaCtrl() {
		return ctrl;
	}

	public DataSource getDataSource() {
		return ctrl.getDataSource();
	}

	public void setDataSource(DataSource dataSource) {
		ctrl.setDataSource(dataSource);
	}
	public GroupType groupType() {
		return ctrl.getDataSource().getGroupType();
	}



	public boolean showDataDisplayPanel2() {
		return showDataDisplayPanel2;
	}

	public boolean showDataPanel() {
		return showDataPanel;
	}

	public void setShowDataPanel(boolean isVisible) {
		if (showDataPanel == isVisible) {
			return;
		}
		showDataPanel = isVisible;
		getListener().updateStatDataPanelVisibility();
	}

	public void setShowStatistics(boolean isVisible) {
		if (showStatPanel == isVisible) {
			return;
		}
		showStatPanel = isVisible;
		getListener().updateStatDataPanelVisibility();
	}

	public boolean showStatPanel() {
		return showStatPanel;
	}

	public DataAnalysisController getController() {
		return ctrl;
	}

	public GeoElement getRegressionModel() {
		return ctrl.getRegressionModel();
	}

	public StatGeo getStatGeo() {
		if (statGeo == null)
			statGeo = new StatGeo(app, getListener());
		return statGeo;
	}

	public int getRegressionOrder() {
		return regressionOrder;
	}

	public void setRegressionMode(int regressionMode) {

		for (Regression l : Regression.values()) {
			if (l.ordinal() == regressionMode) {
				this.regressionMode = l;

				ctrl.setRegressionGeo();
				ctrl.updateAllPanels(true);

				return;
			}
		}

		Log.warn("no mode set in setRegressionMode()");
		this.regressionMode = Regression.NONE;

	}

	public Regression getRegressionMode() {
		return regressionMode;
	}

	public void setRegressionOrder(int regressionOrder) {
		this.regressionOrder = regressionOrder;
	}

	public App getApp() {
		return app;
	}

	public int getMode() {
		return mode;
	}

	public void setShowDataOptionsDialog(boolean showDialog) {
		// if (showDialog) {
		// showSourcePanel();
		// this.dataTypePanel;
		// } else {
		// showMainPanel();
		// }

		app.getDialogManager().showDataSourceDialog(getMode(), false);

	}

	public boolean isNumericData() {
		if (ctrl.getDataSource() == null) {
			return false;
		}
		return ctrl.getDataSource().isNumericData();
	}

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	public void setShowComboPanel2(boolean showComboPanel2) {

		this.showDataDisplayPanel2 = showComboPanel2;
		getListener().showComboPanel2(showComboPanel2);
	}

	// =================================================
	// Number Format
	//
	// (use GeoGebra rounding settings unless decimals < 4)
	// =================================================

	/**
	 * Converts a double numeric value to formatted String
	 * 
	 * @param x
	 *            number to be converted
	 * @return formatted number string
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		// override the default decimal place setting if less than 4 decimals
		if (printDecimals >= 0) {
			int d = printDecimals < 4 ? 4 : printDecimals;
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					d, false);
		} else {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					printFigures, false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	/**
	 * Adjust local rounding constants to match global rounding constants and
	 * update GUI when needed
	 */
	private void updateRounding() {

		if (kernel.useSignificantFigures) {
			if (printFigures != kernel.getPrintFigures()) {
				printFigures = kernel.getPrintFigures();
				printDecimals = -1;
				getListener().updateGUI();
			}
		} else if (printDecimals != kernel.getPrintDecimals()) {
			printDecimals = kernel.getPrintDecimals();
			getListener().updateGUI();
		}
	}

	public int getPrintDecimals() {
		return printDecimals;
	}

	public int getPrintFigures() {
		return printFigures;
	}

	public void remove(GeoElement geo) {
		// Application.debug("removed geo: " + geo.toString());
		ctrl.handleRemovedDataGeo(geo);
	}

	public void update(GeoElement geo) {

		updateRounding();

		// update the view if the geo is in the data source
		if (!isIniting() && ctrl.isInDataSource(geo)) {

			// use a runnable to allow spreadsheet table model to update
			app.getGuiManager().invokeLater(new Runnable() {
				public void run() {
					ctrl.updateDataAnalysisView();
				}
			});
		}
	}

	public String[] getDataTitles() {
		return ctrl.getDataTitles();
	}

	public void updateSelection() {
		// updateDialog(true);
	}

	public boolean isIniting() {
		return isIniting;
	}

	public void setIniting(boolean isIniting) {
		this.isIniting = isIniting;
	}

	public boolean isMultiVar() {
		return getMode() == MODE_MULTIVAR;
	}

	public boolean isRegressionMode() {
		return getMode() == MODE_REGRESSION;
	}

	public void updateGUI() {
		getListener().updateGUI();
	}

	public IDataAnalysisListener getListener() {
		return listener;
	}

	public void setListener(IDataAnalysisListener listener) {
		this.listener = listener;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	 
	public String modeString() {
		String str = "[DAMODE] ";
		switch (mode) {
		case MODE_ONEVAR:
			str += "ONEVAR";
			break;
		case MODE_REGRESSION:
			str += "TWOVAR - REGRESSION";
			break;
		case MODE_MULTIVAR:
			str += "MULTIVAR";
			break;
				
		}
		return str;
	}
}

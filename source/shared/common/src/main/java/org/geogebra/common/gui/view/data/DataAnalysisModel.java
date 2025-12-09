/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.DataAnalysisSettings;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;

/**
 * View to display plots and statistical analysis of data.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisModel {
	// ggb
	private App app;
	private Kernel kernel;
	private StatGeo statGeo;

	public static final int MODE_ONEVAR = EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS;
	public static final int MODE_REGRESSION = EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS;
	public static final int MODE_MULTIVAR = EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS;

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
	// rounding constants for local number format
	private int printDecimals = 4;
	private int printFigures = -1;

	// public static final int regressionTypes = 9;
	private int regressionOrder = 2;

	private DataAnalysisController ctrl;

	private IDataAnalysisListener listener;

	public static final float OPACITY_BAR_CHART = 0.3f;
	public static final int THICKNESS_CURVE = 4;
	public static final int THICKNESS_BAR_CHART = 3;

	/**
	 * UI delegate for the model.
	 */
	public interface IDataAnalysisListener extends ICreateColor {
		/**
		 * @param polyType1 type of plot in the first panel
		 * @param polyType2 type of plot in the second panel
		 */
		void onModeChange(PlotType polyType1, PlotType polyType2);

		/**
		 * Sets the plot types if data is not numeric.
		 * TODO this and the next 4 methods do the same, simplify
		 * @param mode app mode
		 * @param plotType1 plot type in the first panel
		 * @param plotType2 plot type in the second panel
		 */
		void setPlotPanelOVNotNumeric(int mode, PlotType plotType1,
				PlotType plotType2);

		/**
		 * Sets the plot types for raw data grouping.
		 * @param mode app mode
		 * @param plotType1 plot type in the first panel
		 * @param plotType2 plot type in the second panel
		 */
		void setPlotPanelOVRawData(int mode, PlotType plotType1, PlotType plotType2);

		/**
		 * Sets the plot types for frequency grouping.
		 * @param mode app mode
		 * @param plotType1 plot type in the first panel
		 * @param plotType2 plot type in the second panel
		 */
		void setPlotPanelOVFrequency(int mode, PlotType plotType1,
				PlotType plotType2);

		/**
		 * Sets the plot types for class grouping.
		 * @param mode app mode
		 * @param plotType1 plot type in the first panel
		 * @param plotType2 plot type in the second panel
		 */
		void setPlotPanelOVClass(int mode, PlotType plotType1,
				PlotType plotType2);

		/**
		 * Sets the plot types for regressions.
		 * @param mode app mode
		 * @param plotType1 plot type in the first panel
		 * @param plotType2 plot type in the second panel
		 */
		void setPlotPanelRegression(int mode, PlotType plotType1,
				PlotType plotType2);

		/**
		 * Sets the plot types for multi-variable plot.
		 * @param mode app mode
		 * @param plotType plot type in the first panel
		 */
		void setPlotPanelMultiVar(int mode, PlotType plotType);

		/**
		 * @param dataArray data
		 */
		void loadDataTable(ArrayList<GeoElement> dataArray);

		/**
		 * Show or hide statistics data panel.
		 */
		void updateStatDataPanelVisibility();

		/**
		 * @return data analysis controller
		 */
		DataAnalysisController getController();

		/**
		 * Show or hide second plot panel.
		 * @param show whether to show second plot panel
		 */
		void showComboPanel2(boolean show);

		@MissingDoc
		void updateGUI();

		/**
		 * @return data analysis model
		 */
		DataAnalysisModel getModel();

		/**
		 * TODO model should not be accessed through UI delegate, refactor
		 * Get model for one of the panels.
		 * @param zeroBasedIndex 0 or 1
		 * @return data display model
		 */
		DataDisplayModel getDisplayModel(int zeroBasedIndex);
	}

	/**
	 * TODO just move the colors array to common
	 * Color provider.
	 */
	public interface ICreateColor {
		/**
		 * @param idx index
		 * @return color
		 */
		GColor createColor(int idx);
	}

	/**
	 * Constructs the model for DA view.
	 * 
	 * @param app
	 *            application
	 * @param listener
	 *            dialog listening to this
	 */
	public DataAnalysisModel(App app, IDataAnalysisListener listener,
			DataAnalysisController ctrl) {
		setIniting(true);
		this.app = app;
		this.kernel = app.getKernel();

		this.setListener(listener);
		this.ctrl = ctrl;
		ctrl.setModel(this);
		setIniting(false);
		// full file contains elements needed for initialization,
		// undo XML does not => refresh needed
		app.getEventDispatcher().addEventListener(evt -> {
			if (evt.getType() == EventType.UNDO || evt.getType() == EventType.REDO) {
				getController().updateDataAnalysisView();
			}
		});
	}

	private void setView(DataSource dataSource, int mode, PlotType plotType1,
			PlotType plotType2, boolean forceModeUpdate) {
		ctrl.setDataSource(dataSource);

		if (dataSource == null) {
			ctrl.setValidData(false);
		} else {
			ctrl.setValidData(true);
		}

		if (mode == MODE_ONEVAR) {
			if (showDataPanel
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

			// GGB-2385 need to pass plotType1, plotType2 here
			getListener().onModeChange(plotType1, plotType2);

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
	 * 
	 * @param plotType1
	 *            plot type in first panel
	 * @param plotType2
	 *            plot type in second panel
	 */
	public void setDataPlotPanels(PlotType plotType1, PlotType plotType2) {

		switch (getMode()) {

		default:
		case MODE_ONEVAR:
			if (!isNumericData()) {
				getListener().setPlotPanelOVNotNumeric(getMode(),
						barchart(plotType1), barchart(plotType2));

			} else if (groupType() == GroupType.RAWDATA) {
				getListener().setPlotPanelOVRawData(getMode(),
						histogram(plotType1), boxplot(plotType2));
			} else if (groupType() == GroupType.FREQUENCY) {
				getListener().setPlotPanelOVFrequency(getMode(),
						barchart(plotType1), boxplot(plotType2));

			} else if (groupType() == GroupType.CLASS) {
				getListener().setPlotPanelOVClass(getMode(),
						histogram(plotType1), histogram(plotType2));
			}
			break;

		case MODE_REGRESSION:
			getListener().setPlotPanelRegression(getMode(),
					scatterplot(plotType1), residual(plotType2));
			break;

		case MODE_MULTIVAR:
			getListener().setPlotPanelMultiVar(getMode(),
					multiboxplot(plotType1));
			showDataDisplayPanel2 = false;
			break;
		}
	}

	// ======================================
	// Getters/setters
	// ======================================

	private static PlotType residual(PlotType pt) {
		if (pt == null) {
			return PlotType.RESIDUAL;
		}
		return pt;
	}

	private static PlotType scatterplot(PlotType pt) {
		if (pt == null) {
			return PlotType.SCATTERPLOT;
		}
		return pt;
	}

	private static PlotType multiboxplot(PlotType pt) {
		if (pt == null) {
			return PlotType.MULTIBOXPLOT;
		}
		return pt;
	}

	private static PlotType histogram(PlotType pt) {
		if (pt == null) {
			return PlotType.HISTOGRAM;
		}
		return pt;
	}

	private static PlotType boxplot(PlotType pt) {
		if (pt == null) {
			return PlotType.BOXPLOT;
		}
		return pt;
	}

	private static PlotType barchart(PlotType pt) {
		if (pt == null) {
			return PlotType.BARCHART;
		}
		return pt;
	}

	public DataAnalysisController getDaCtrl() {
		return ctrl;
	}

	public DataSource getDataSource() {
		return ctrl.getDataSource();
	}

	/**
	 * @return group type
	 */
	public GroupType groupType() {
		return ctrl.getDataSource().getGroupType();
	}

	/**
	 * @return whether 2nd display panel is shown
	 */
	public boolean showDataDisplayPanel2() {
		return showDataDisplayPanel2;
	}

	/**
	 * @return whether data panel is shown
	 */
	public boolean showDataPanel() {
		return showDataPanel;
	}

	/**
	 * @param isVisible
	 *            whether to show data panel
	 */
	public void setShowDataPanel(boolean isVisible) {
		if (showDataPanel == isVisible) {
			return;
		}
		showDataPanel = isVisible;
		getListener().updateStatDataPanelVisibility();
	}

	/**
	 * @param isVisible
	 *            whether to show stats
	 */
	public void setShowStatistics(boolean isVisible) {
		if (showStatPanel == isVisible) {
			return;
		}
		showStatPanel = isVisible;
		getListener().updateStatDataPanelVisibility();
	}

	/**
	 * @return whether stat panel is shown
	 */
	public boolean showStatPanel() {
		return showStatPanel;
	}

	public DataAnalysisController getController() {
		return ctrl;
	}

	public GeoElement getRegressionModel() {
		return ctrl.getRegressionModel();
	}

	/**
	 * @return factory for statistic algos / geos
	 */
	public StatGeo getStatGeo() {
		if (statGeo == null) {
			statGeo = new StatGeo(app, getListener());
		}
		return statGeo;
	}

	public int getRegressionOrder() {
		return regressionOrder;
	}

	/**
	 * @param regressionMode
	 *            regression mode (ordianl of {@link Regression})
	 */
	public void setRegressionMode(int regressionMode) {

		for (Regression l : Regression.values()) {
			if (l.ordinal() == regressionMode) {
				app.getSettings().getDataAnalysis().setRegression(l);

				ctrl.setRegressionGeo();
				ctrl.updateAllPanels(true);

				return;
			}
		}

		Log.warn("no mode set in setRegressionMode()");
		// this.regressionMode = Regression.NONE;

	}

	public Regression getRegressionMode() {
		return app.getSettings().getDataAnalysis().getRegression();
	}

	public void setRegressionOrder(int regressionOrder) {
		this.regressionOrder = regressionOrder;
	}

	public App getApp() {
		return app;
	}

	public int getMode() {
		return app.getSettings().getDataAnalysis().getMode();
	}

	/**
	 * @return whether the data is numeric
	 */
	public boolean isNumericData() {
		if (ctrl.getDataSource() == null) {
			return false;
		}
		return ctrl.getDataSource().isNumericData();
	}

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	/**
	 * @param showComboPanel2
	 *            whether to show second panel
	 */
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
	 * @param val
	 *            number to be converted
	 * @return formatted number string
	 */
	public String format(double val) {
		StringTemplate highPrecision;

		// override the default decimal place setting if less than 4 decimals
		if (printDecimals >= 0) {
			int d = printDecimals < 4 ? 4 : printDecimals;
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, d,
					false);
		} else {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					printFigures, false);
		}
		// get the formatted string

		return kernel.format(val, highPrecision);
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

	/**
	 * @param geo
	 *            element to be removed
	 */
	public void remove(GeoElement geo) {
		ctrl.handleRemovedDataGeo(geo);
	}

	/**
	 * @param geo
	 *            updated element
	 */
	public void update(GeoElement geo) {
		updateRounding();

		// update the view if the geo is in the data source
		if (!isIniting() && ctrl.isInDataSource(geo)) {

			// use a runnable to allow spreadsheet table model to update
			app.invokeLater(ctrl::updateDataAnalysisView);
		}
	}

	public String[] getDataTitles() {
		return ctrl.getDataTitles();
	}

	/**
	 * TODO remove?
	 * Update selection.
	 */
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

	/**
	 * Update the UI.
	 */
	public void updateGUI() {
		getListener().updateGUI();
	}

	public IDataAnalysisListener getListener() {
		return listener;
	}

	public void setListener(IDataAnalysisListener listener) {
		this.listener = listener;
	}

	/**
	 * Set analysis mode.
	 * @param mode EuclidianConstants.MODE_*
	 */
	public void setMode(int mode) {
		app.getSettings().getDataAnalysis().setMode(mode);
	}

	/**
	 * Update UI from settings
	 */
	public void updateFromSettings() {
		DataAnalysisSettings settings = app.getSettings().getDataAnalysis();
		if (settings.getItems().size() > 0) {
			DataSource source = new DataSource(app);
			source.setDataListFromSettings(settings.getItems(), settings.getFrequencies(),
					settings.getMode());
			// no need to guess here
			setView(source, settings.getMode(), settings, true);
			settings.getItems().clear();
		}
	}

	/**
	 * Serialize to XML
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void getXML(XMLStringBuilder sb) {
		sb.startOpeningTag("dataAnalysis", 0).attr("mode", getMode());
		if (getListener().getDisplayModel(0).getSelectedPlot() != null) {
			sb.attr("plot1",
					getListener().getDisplayModel(0).getSelectedPlot());
		}
		if (getListener().getDisplayModel(1).getSelectedPlot() != null) {
			sb.attr("plot2",
					getListener().getDisplayModel(1).getSelectedPlot());
		}
		if (getRegressionMode() != null) {
			sb.attr("regression", getRegressionMode());
		}
		sb.endTag();
		getDataSource().getXMLDescription(sb);
		sb.closeTag("dataAnalysis");
	}

	/**
	 * @param dataSource
	 *            data source
	 * @param mode
	 *            app mode
	 * @param dataAnalysis
	 *            analysis settings (may be updated)
	 * @param forceModeUpdate
	 *            whether to force reset for new mode
	 */
	public void setView(DataSource dataSource, int mode,
			DataAnalysisSettings dataAnalysis, boolean forceModeUpdate) {
		dataAnalysis.setMode(mode);
		setView(dataSource, mode,
				dataAnalysis.getPlotType(0, null),
				dataAnalysis.getPlotType(1, null),
				forceModeUpdate);
	}
}

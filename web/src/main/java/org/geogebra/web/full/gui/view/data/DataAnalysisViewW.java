package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.IDataAnalysisListener;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataSource;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataAnalysisViewW extends FlowPanel implements View, 
		ProvidesResize, RequiresResize, SetLabels, IDataAnalysisListener,
		PrintableW {
	private AppW app;
	private Kernel kernel;
	private DataAnalysisModel model;
	protected DataAnalysisControllerW daCtrl;
	private DataAnalysisStyleBarW stylebar;

	public static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
	public static final GColor TABLE_HEADER_COLOR = GColor.newColor(240, 240,
			240);
	public static final GColor HISTOGRAM_COLOR = GColor.BLUE;
	public static final GColor BOXPLOT_COLOR = GeoGebraColorConstants.GGB_RED;
	public static final GColor BARCHART_COLOR = GeoGebraColorConstants.GGB_GREEN;

	public static final GColor DOTPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor NQPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor REGRESSION_COLOR = GColor.RED;
	public static final GColor OVERLAY_COLOR = GeoGebraColorConstants.DARKBLUE;

	private GColor[] colors = { TABLE_GRID_COLOR, TABLE_HEADER_COLOR,
			HISTOGRAM_COLOR, BOXPLOT_COLOR, BARCHART_COLOR, DOTPLOT_COLOR,
			NQPLOT_COLOR, REGRESSION_COLOR, OVERLAY_COLOR, GColor.BLACK,
			GColor.WHITE };
	// main GUI panels
	private DataPanelW dataPanel;
	private StatisticsPanelW statisticsPanel;
	private RegressionPanelW regressionPanel;
	private DataDisplayPanelW dataDisplayPanel1;
	private DataDisplayPanelW dataDisplayPanel2;

	private SplitLayoutPanel comboPanelSplit;
	private SplitLayoutPanel mainSplit;

	/**
	 * For calling the onResize method in a deferred way
	 */
	private ScheduledCommand deferredOnRes = this::onResize;

	private ScheduledCommand deferredDataPanelOnRes = this::resizeDataPanels;

	private DataSource dataSource;

	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 *            application
	 * @param mode
	 *            initial mode
	 */
	public DataAnalysisViewW(AppW app, int mode) {
		this.app = app;
		this.kernel = app.getKernel();

		daCtrl = new DataAnalysisControllerW(app, this);
		model = new DataAnalysisModel(app, this, daCtrl);

		dataSource = new DataSource(app);

		daCtrl.loadDataLists(true);

		setView(dataSource, mode, true);
		model.setIniting(false);
	}

	/**
	 * Update panels after resize.
	 */
	protected void resizeDataPanels() {
		if (model.isMultiVar() && model.showStatPanel()) {
			Log.debug("Showing MultiVar stat panel");
			dataDisplayPanel1.resize(getOffsetWidth(),
					getOffsetHeight() - statisticsPanel.getOffsetHeight(),
					true);
		} else {
			dataDisplayPanel1.onResize();
			dataDisplayPanel2.onResize();
		}

		if (model.showDataPanel()) {
			dataPanel.onResize();
		}
	}

	/**
	 * @param mode
	 *            app mode
	 */
	public void changeMode(int mode) {
		model = new DataAnalysisModel(app, this, daCtrl);
		setView(dataSource, mode, true);
	}

	/**
	 * @param dataSource
	 *            data source
	 * @param mode
	 *            app mode
	 * @param forceModeUpdate
	 *            whether to force mode change
	 */
	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {
		
		dataSource.setFrequencyFromColumn(true);

		dataSource.setDataListFromSelection(mode);
		dataDisplayPanel1 = new DataDisplayPanelW(this, 0);
		dataDisplayPanel2 = new DataDisplayPanelW(this, 1);

		comboPanelSplit = new SplitLayoutPanel();
		comboPanelSplit.setStyleName("comboSplitLayout");
		comboPanelSplit.add(dataDisplayPanel1);

		mainSplit = new SplitLayoutPanel();
		mainSplit.add(comboPanelSplit);
		mainSplit.setWidgetMinSize(comboPanelSplit, 500);
		mainSplit.setStyleName("daMainSplit");
		add(mainSplit);
		app.getAsyncManager().runOrSchedule(() -> {
			model.setView(dataSource, mode,
					app.getSettings().getDataAnalysis(),
					forceModeUpdate);
			setLabels();
			updateGUI();
		});
	}

	/**
	 * @return stylebar
	 */
	public Widget getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBarW(app, this);
		}
		return stylebar;
	}

	private void buildStatisticsPanel() {
		if (statisticsPanel != null) {
			// TODO handle any orphaned geo children of stat panel
		}
		statisticsPanel = new StatisticsPanelW(app, this);
	}

	@Override
	public void setPlotPanelOVNotNumeric(int mode, PlotType plotType1,
			PlotType plotType2) {
		dataDisplayPanel1.setPanel(plotType1, mode);
		dataDisplayPanel2.setPanel(plotType2, mode);

	}

	@Override
	public void setPlotPanelOVRawData(int mode, PlotType plotType1,
			PlotType plotType2) {
		dataDisplayPanel1.setPanel(plotType1, mode);
		dataDisplayPanel2.setPanel(plotType2, mode);
	}

	@Override
	public void setPlotPanelOVFrequency(int mode, PlotType pt1, PlotType pt2) {
		dataDisplayPanel1.setPanel(pt1, mode);
		dataDisplayPanel2.setPanel(pt2, mode);
	}

	@Override
	public void setPlotPanelOVClass(int mode, PlotType pt1, PlotType pt2) {
		dataDisplayPanel1.setPanel(pt1, mode);
		dataDisplayPanel2.setPanel(pt2, mode);
	}

	@Override
	public void setPlotPanelRegression(int mode, PlotType pt1, PlotType pt2) {
		dataDisplayPanel1.setPanel(pt1, mode);
		dataDisplayPanel2.setPanel(pt2, mode);
	}

	@Override
	public void setPlotPanelMultiVar(int mode, PlotType pt1) {
		dataDisplayPanel1.setPanel(pt1, mode);
	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels(PlotType plotType1, PlotType plotType2) {
		model.setDataPlotPanels(plotType1, plotType2);
	}

	// Create DataPanel to display the current data set(s) and allow
	// temporary editing.
	protected DataPanelW buildDataPanel() {

		if (dataPanel != null) {
			// TODO handle any orphaned data panel geos
			dataPanel = null;
		}
		if (!model.isMultiVar()) {
			dataPanel = new DataPanelW(app, this);
		}

		return dataPanel;
	}

	@Override
	public void loadDataTable(ArrayList<GeoElement> dataArray) {
		if (dataPanel == null) {
			buildDataPanel();
		}
		// TODO: Implement! dataPanel.loadDataTable(dataArray);
	}

	protected DataPanelW getDataPanel() {
		return dataPanel;
	}

	private void updateLayout() {
		clear();
		int regressiodIdx = model.isRegressionMode() && regressionPanel != null
				? regressionPanel.getRegressionIdx() : -1;

		mainSplit.clear();
		boolean stat = model.showStatPanel();
		boolean data = model.showDataPanel();

		if (data && dataPanel == null) {
			buildDataPanel();
		}

		if (model.isMultiVar()) {
			comboPanelSplit.clear();
		
			if (stat) {
				// set the size of
				comboPanelSplit.addSouth(statisticsPanel, statisticsPanel
						.estimateHeight(model.getDataTitles().length));
				comboPanelSplit.add(dataDisplayPanel1);
			} else {
				comboPanelSplit.add(dataDisplayPanel1);
			}
			mainSplit.add(comboPanelSplit);
		} else {
			if (stat && data) {
				mainSplit.addWest(statisticsPanel, 300);
				mainSplit.addEast(comboPanelSplit, 300);
				mainSplit.add(dataPanel);
			} else if (stat && !data) {
				mainSplit.addWest(statisticsPanel, 300);
				mainSplit.add(comboPanelSplit);
			} else if (!stat && data) {
				mainSplit.addWest(dataPanel, 300);
				mainSplit.add(comboPanelSplit);
			} else {
				mainSplit.add(comboPanelSplit);
			}
			mainSplit.setWidgetMinSize(comboPanelSplit, 500);
		}
		add(mainSplit);

		// ===========================================
		// regression panel
		if (model.isRegressionMode()) {
			regressionPanel = new RegressionPanelW(app, this);
			add(regressionPanel);
			if (regressiodIdx != -1) {
				regressionPanel.setRegressionIdx(regressiodIdx);
			}
			mainSplit.setHeight("80%");
		} else {
			mainSplit.setHeight("100%");
		}

		deferredDataPanelOnResize();
	} 

	public DataAnalysisControllerW getDaCtrl() {
		return daCtrl;
	}

	public DataSource getDataSource() {
		return model.getDataSource();
	}

	public GroupType groupType() {
		return daCtrl.getDataSource().getGroupType();
	}

	public DataDisplayPanelW getDataDisplayPanel1() {
		return dataDisplayPanel1;
	}

	public DataDisplayPanelW getDataDisplayPanel2() {
		return dataDisplayPanel2;
	}

	public RegressionPanelW getRegressionPanel() {
		return regressionPanel;
	}

	public StatisticsPanelW getStatisticsPanel() {
		return statisticsPanel;
	}

	/**
	 * Component representation of this view
	 * @return reference to self
	 */
	public Widget getDataAnalysisViewComponent() {
		return this;
	}

	@Override
	public DataAnalysisControllerW getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public AppW getApp() {
		return app;
	}

	@Override
	public void updateStatDataPanelVisibility() {
		updateLayout();
		dataDisplayPanel1.update();
	}

	@Override
	public void updateGUI() {
		if (stylebar != null) {
			stylebar.updateGUI();
		}
		deferredOnResize();
	}

	@Override
	public void setLabels() {
		if (model.isIniting()) {
			return;
		}

		if (model.isRegressionMode() && regressionPanel != null) {
			regressionPanel.setLabels();
		}

		if (stylebar != null) {
			stylebar.setLabels();
		}
	}

	@Override
	public void remove(GeoElement geo) {
		model.remove(geo);
	}

	@Override
	public void update(GeoElement geo) {
		model.update(geo);
	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// TODO
	}

	@Override
	public void add(GeoElement geo) {
		// do nothing
	}

	@Override
	public void clearView() {
		// do nothing
	}

	@Override
	public void rename(GeoElement geo) {
		// do nothing
	}

	@Override
	public void repaintView() {
		// do nothing
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	@Override
	public void reset() {
		// do nothing
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// do nothing
	}

	/**
	 * Attach this and helper views to kernel.
	 */
	public void attachView() {
		model.updateFromSettings();
		kernel.attach(this);

		// attachView to plot panels
		dataDisplayPanel1.attachView();
		if (dataDisplayPanel2 != null) {
			dataDisplayPanel2.attachView();
		}
	}

	/**
	 * Detach this, detach helper views to kernel and clear helper objects.
	 */
	public void detachView() {
		dataDisplayPanel1.detachView();
		if (dataDisplayPanel2 != null) {
			dataDisplayPanel2.detachView();
		}
		daCtrl.removeStatGeos();

		kernel.detach(this);
	}

	public String[] getDataTitles() {
		return daCtrl.getDataTitles();
	}

	@Override
	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public DataAnalysisModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            data analysis model
	 */
	public void setModel(DataAnalysisModel model) {
		this.model = model;
	}

	@Override
	public void onModeChange(PlotType plotType1, PlotType plotType2) {
		dataPanel = null;
		buildStatisticsPanel();
		Log.error("" + plotType1);
		setDataPlotPanels(plotType1, plotType2);
		updateLayout();
	}

	@Override
	public void showComboPanel2(boolean show) {
		comboPanelSplit.clear();

		int w = mainSplit.getOffsetWidth();
		int h = mainSplit.getOffsetHeight();
		if (show) {
			dataDisplayPanel1.resize(w, h / 2, true);
			dataDisplayPanel1.resize(w, h / 2, true);
			comboPanelSplit.addNorth(dataDisplayPanel1, h / 2.0);
			comboPanelSplit.add(dataDisplayPanel2);
		} else {
			dataDisplayPanel1.resize(w, h, true);
			comboPanelSplit.add(dataDisplayPanel1);
		}

		updateGUI();
	}

	public String format(double value) {
		return model.format(value);
	}

	@Override
	public GColor createColor(int idx) {
		return colors[idx];
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	public boolean isShowing() {
		return app.showView(App.VIEW_DATA_ANALYSIS);
	}

	@Override
	public void onResize()  {
		for (Widget w: getChildren()) {
			if (w instanceof RequiresResize) {
				((RequiresResize) w).onResize();
			}
		}
	}

	/**
	 * For calling the onResize method in a deferred way.
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/**
	 * Resize data panel after delay.
	 */
	public void deferredDataPanelOnResize() {
		Scheduler.get().scheduleDeferred(deferredDataPanelOnRes);
	}

	/**
	 * Update a display other than the given one.
	 * 
	 * @param display
	 *            display NOT to be updated
	 */
	public void updateOtherDataDisplay(DataDisplayPanelW display) {
		if (!model.showDataDisplayPanel2()) {
			return;
		}
		if (display == dataDisplayPanel1) {
			dataDisplayPanel2.update();
		} else {
			dataDisplayPanel1.update();
		}
	}

	@Override
	public void getPrintable(FlowPanel pPanel, Runnable enablePrintBtn) {
		// nothing to do here
	}

	@Override
	public DataDisplayModel getDisplayModel(int index) {
		return index == 0 ? this.dataDisplayPanel1.getModel()
				: this.dataDisplayPanel2.getModel();
	}
}

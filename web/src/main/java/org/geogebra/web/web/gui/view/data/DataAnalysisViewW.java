package org.geogebra.web.web.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.IDataAnalysisListener;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataSource;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataAnalysisViewW extends FlowPanel implements View, 
ProvidesResize, RequiresResize, SetLabels, IDataAnalysisListener {

	private static final long serialVersionUID = 1L;

	// ggb
	private AppW app;
	private Kernel kernel;
	private DataAnalysisModel model;
	protected DataAnalysisControllerW daCtrl;
	private DataAnalysisStyleBarW stylebar;

	// colors
	public static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
	public static final GColor TABLE_HEADER_COLOR = new GColorW(240, 240, 240);
	public static final GColor HISTOGRAM_COLOR = GeoGebraColorConstants.BLUE;
	public static final GColor BOXPLOT_COLOR = GeoGebraColorConstants.CRIMSON;
	public static final GColor BARCHART_COLOR = GeoGebraColorConstants.DARKGREEN;

	public static final GColor DOTPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor NQPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor REGRESSION_COLOR = GColorW.RED;
	public static final GColor OVERLAY_COLOR = GeoGebraColorConstants.DARKBLUE;

	private GColor[] colors = { TABLE_GRID_COLOR, TABLE_HEADER_COLOR,
			HISTOGRAM_COLOR, BOXPLOT_COLOR, BARCHART_COLOR, DOTPLOT_COLOR,
			NQPLOT_COLOR, REGRESSION_COLOR, OVERLAY_COLOR, GColor.BLACK,
			GColor.WHITE };
	// main GUI panels
	private DataPanelW dataPanel;
	private StatisticsPanelW statisticsPanel;
	private RegressionPanelW regressionPanel;
	private DataDisplayPanelW dataDisplayPanel1, dataDisplayPanel2;

	private SplitLayoutPanel comboPanelSplit, mainSplit;

	/**
	 * For calling the onResize method in a deferred way
	 */
	Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
		public void execute() {
			onResize();
		}
	};


	Scheduler.ScheduledCommand deferredDataPanelOnRes = new Scheduler.ScheduledCommand() {
		public void execute() {
			if (model.isMultiVar() && model.showStatPanel()) {
				App.debug("Showing MultiVar stat panel");
				dataDisplayPanel1.resize(getOffsetWidth(), getOffsetHeight() - statisticsPanel.getOffsetHeight(), true);
				
			} else {
				dataDisplayPanel1.onResize();
				dataDisplayPanel2.onResize();
			}
		
			if (model.showDataPanel()) {
				dataPanel.onResize();
			}
		}
		
	};

	private DataSource dataSource;



	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisViewW(AppW app, int mode) {
		this.app = app;
		this.kernel = app.getKernel();

		daCtrl = new DataAnalysisControllerW(app, this);
		model = new DataAnalysisModel(app, mode, this, daCtrl);


		dataSource = new DataSource(app);

		daCtrl.loadDataLists(true);

		setView(dataSource, mode, true);
		model.setIniting(false);
	}


	/*************************************************
	 * END constructor
	 */



	public void changeMode(int mode) {
		model = new DataAnalysisModel(app, mode, this, daCtrl);
		setView(dataSource, mode, true);

	}
	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {

		dataSource.setDataListFromSelection(mode);
		dataDisplayPanel1 = new DataDisplayPanelW(this);
		dataDisplayPanel2 = new DataDisplayPanelW(this);


		comboPanelSplit = new SplitLayoutPanel();
		comboPanelSplit.setStyleName("comboSplitLayout");
		comboPanelSplit.add(dataDisplayPanel1);

		mainSplit = new SplitLayoutPanel();
		mainSplit.add(comboPanelSplit);
		mainSplit.setWidgetMinSize(comboPanelSplit, 500);
		mainSplit.setStyleName("daMainSplit");
		add(mainSplit);
		model.setView(dataSource, mode, forceModeUpdate);
		//		updateFonts();
		setLabels();
		updateGUI();

	}

	public Widget getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBarW(app, this);
		}
		return stylebar;
	}

	private void createGUI() {

		buildStatisticsPanel();

	}

	private void buildStatisticsPanel() {
		if (statisticsPanel != null) {
			// TODO handle any orphaned geo children of stat panel
			statisticsPanel = null;
		}

		statisticsPanel = new StatisticsPanelW(app, this);
	}

	public void setPlotPanelOVNotNumeric(int mode) {
		dataDisplayPanel1.setPanel(PlotType.BARCHART, mode);
		dataDisplayPanel2.setPanel(PlotType.BARCHART, mode);

	}

	public void setPlotPanelOVRawData(int mode) {
		dataDisplayPanel1.setPanel(PlotType.HISTOGRAM, mode);
		dataDisplayPanel2.setPanel(PlotType.BOXPLOT, mode);

	}

	public void setPlotPanelOVFrequency(int mode) {
		dataDisplayPanel1.setPanel(PlotType.BARCHART, mode);
		dataDisplayPanel2.setPanel(PlotType.BOXPLOT, mode);

	}

	public void setPlotPanelOVClass(int mode) {
		dataDisplayPanel1.setPanel(PlotType.HISTOGRAM, mode);
		dataDisplayPanel2.setPanel(PlotType.HISTOGRAM, mode);

	}

	public void setPlotPanelRegression(int mode) {
		dataDisplayPanel1.setPanel(PlotType.SCATTERPLOT, mode);
		dataDisplayPanel2.setPanel(PlotType.RESIDUAL, mode);
	}

	public void setPlotPanelMultiVar(int mode) {
		dataDisplayPanel1.setPanel(PlotType.MULTIBOXPLOT, mode);

	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels() {
		model.setDataPlotPanels();
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

	public void loadDataTable(ArrayList<GeoElement> dataArray) {
		if (dataPanel == null) {
			buildDataPanel();
		}
		// TODO: Implement!
		//		dataPanel.loadDataTable(dataArray);
	}

	protected DataPanelW getDataPanel() {
		return dataPanel;
	}

	// =================================================
	// GUI
	// =================================================

	private void updateLayout() {
		clear();
		int regressiodIdx = model.isRegressionMode() && 
				regressionPanel != null ? regressionPanel.getRegressionIdx() :- 1;

		mainSplit.clear();
		boolean stat = model.showStatPanel();
		boolean data = model.showDataPanel();
		Label lbData= new Label("Data");

		if (data && dataPanel == null) {
			buildDataPanel();
		}

		if (model.isMultiVar()) {
			comboPanelSplit.clear();
		
			if (stat) {
				comboPanelSplit.addNorth(dataDisplayPanel1, 500);
				comboPanelSplit.add(statisticsPanel);
			} else {
				comboPanelSplit.add(dataDisplayPanel1);
				
			}
			mainSplit.add(comboPanelSplit);
		} else {

			if (stat && data) {
				mainSplit.addWest(statisticsPanel, 300);
				mainSplit.addEast(comboPanelSplit, 300);
				mainSplit.add(dataPanel);

			} else

				if (stat && !data) {
					mainSplit.addWest(statisticsPanel, 300);
					mainSplit.add(comboPanelSplit);
				} else

					if (!stat && data) {
						mainSplit.addWest(dataPanel, 300);
						mainSplit.add(comboPanelSplit);
					} else {
						mainSplit.add(comboPanelSplit);
					}
			mainSplit.setWidgetMinSize(comboPanelSplit, 500);


			// ===========================================
			// regression panel
		}
		add(mainSplit);
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

	public void showSourcePanel() {
	}

	public void showMainPanel() {
	}

	// ======================================
	// Getters/setters
	// ======================================

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
	 * 
	 * @return reference to self
	 */
	public Widget getDataAnalysisViewComponent() {
		return this;
		//	return statisticsPanel;
	}

	public DataAnalysisControllerW getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public AppW getApp() {
		return app;
	}

	// public int getMode() {
	// return mode;
	// }

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	public void updateStatDataPanelVisibility() {
		updateLayout();
	}

	public void doPrint() {
		//		List<Printable> l = new ArrayList<Printable>();
		//		l.add(this);
		//		PrintPreview.get(app, App.VIEW_DATA_ANALYSIS, PageFormat.LANDSCAPE)
		//				.setVisible(true);
	}

	// =================================================
	// Event Handlers and Updates
	// =================================================

	public void updateGUI() {

		if (stylebar != null) {
			stylebar.updateGUI();
		}
		deferredOnResize();
	}

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

	// =================================================
	// Number Format
	//
	// (use GeoGebra rounding settings unless decimals < 4)
	// =================================================

	// =================================================
	// View Implementation
	// =================================================

	public void remove(GeoElement geo) {
		model.remove(geo);
	}

	public void update(GeoElement geo) {
		model.update(geo);
	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void add(GeoElement geo) {
		// do nothing
	}

	public void clearView() {
		// do nothing
	}

	public void rename(GeoElement geo) {
		// do nothing
	}

	public void repaintView() {

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	public void reset() {
		// do nothing
	}

	public void setMode(int mode, ModeSetter m) {
		// do nothing
	}

	public void attachView() {
		kernel.attach(this);

		// attachView to plot panels
		dataDisplayPanel1.attachView();
		if (dataDisplayPanel2 != null)
			dataDisplayPanel2.attachView();
	}

	public void detachView() {

		dataDisplayPanel1.detachView();
		if (dataDisplayPanel2 != null)
			dataDisplayPanel2.detachView();
		daCtrl.removeStatGeos();

		kernel.detach(this);
	}

	public String[] getDataTitles() {
		return daCtrl.getDataTitles();
	}

	public void updateSelection() {
	}

	// =================================================
	// Printing
	// =================================================

	//	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
	//		if (pageIndex > 0)
	//			return (NO_SUCH_PAGE);
	//
	//		Graphics2D g2d = (Graphics2D) g;
	//		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	//
	//		// construction title
	//		int y = 0;
	//		Construction cons = kernel.getConstruction();
	//		String title = cons.getTitle();
	//		if (!title.equals("")) {
	//			Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
	//					app.getBoldFont().getSize() + 2);
	//			g2d.setFont(titleFont);
	//			g2d.setColor(Color.black);
	//			// Font fn = g2d.getFont();
	//			FontMetrics fm = g2d.getFontMetrics();
	//			y += fm.getAscent();
	//			g2d.drawString(title, 0, y);
	//		}
	//
	//		// construction author and date
	//		String author = cons.getAuthor();
	//		String date = cons.getDate();
	//		String line = null;
	//		if (!author.equals("")) {
	//			line = author;
	//		}
	//		if (!date.equals("")) {
	//			if (line == null)
	//				line = date;
	//			else
	//				line = line + " - " + date;
	//		}
	//
	//		if (line != null) {
	//			g2d.setFont(app.getPlainFont());
	//			g2d.setColor(Color.black);
	//			// Font fn = g2d.getFont();
	//			FontMetrics fm = g2d.getFontMetrics();
	//			y += fm.getHeight();
	//			g2d.drawString(line, 0, y);
	//		}
	//		if (y > 0) {
	//			g2d.translate(0, y + 20); // space between title and drawing
	//		}
	//
	//		// scale the dialog so that it fits on one page.
	//		double xScale = pageFormat.getImageableWidth() / this.getWidth();
	//		double yScale = (pageFormat.getImageableHeight() - (y + 20))
	//				/ this.getHeight();
	//		double scale = Math.min(xScale, yScale);
	//
	//		this.paint(g2d, scale);
	//
	//		return (PAGE_EXISTS);
	//	}
	//
	//	/**
	//	 * Paint the dialog with given scale factor (used for printing).
	//	 */
	//	public void paint(Graphics graphics, double scale) {
	//
	//		Graphics2D g2 = (Graphics2D) graphics;
	//		g2.scale(scale, scale);
	//		super.paint(graphics);
	//
	//	}
	//
	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}

	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public boolean suggestRepaint() {
		return false;
	}

	public DataAnalysisModel getModel() {
		return model;
	}

	public void setModel(DataAnalysisModel model) {
		this.model = model;
	}

	public void onModeChange() {
		dataPanel = null;
		buildStatisticsPanel();
		setDataPlotPanels();
		updateLayout();

	}

	public void showComboPanel2(boolean show) {
		comboPanelSplit.clear();

		int w = mainSplit.getOffsetWidth();
		int h = mainSplit.getOffsetHeight();
		if (show) {

			dataDisplayPanel1.resize(w, h/2, true);
			dataDisplayPanel1.resize(w, h/2, true);
			comboPanelSplit.addNorth(dataDisplayPanel1,  h/2);
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

	public GColor createColor(int idx) {
		GColor c = colors[idx];
		return new GColorW(c.getRed(), c.getGreen(), c.getBlue());
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onResize()  {
		for (Widget w: getChildren()) {
			if (w instanceof RequiresResize) {
				((RequiresResize)w).onResize();
			}
		}
	}

	/**
	 * For calling the onResize method in a deferred way
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	public void deferredDataPanelOnResize() {
		Scheduler.get().scheduleDeferred(deferredDataPanelOnRes);
	}

	public void updateOtherDataDisplay(DataDisplayPanelW display) {
		if (!model.showDataDisplayPanel2()) {
			return;
		}
		if (display == dataDisplayPanel1) {
			dataDisplayPanel2.update();
		} else {
			dataDisplayPanel1.update();
			;
		}

	}
}

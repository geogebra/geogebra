package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.IDataAnalysisListener;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataSource;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.desktop.export.PrintPreviewD;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.main.AppD;

/**
 * View to display plots and statistical analysis of data.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisViewD extends JPanel
		implements View, Printable, SetLabels, IDataAnalysisListener {

	private static final long serialVersionUID = 1L;

	// ggb
	private AppD app;
	private Kernel kernel;
	private DataAnalysisModel model;
	protected DataAnalysisControllerD daCtrl;
	private DataAnalysisStyleBar stylebar;

	// colors
	public static final Color TABLE_GRID_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
	public static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
	public static final Color HISTOGRAM_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GColor.BLUE);
	public static final Color BOXPLOT_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GGB_RED);
	public static final Color BARCHART_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GGB_GREEN);

	public static final Color DOTPLOT_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color NQPLOT_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color REGRESSION_COLOR = Color.RED;
	public static final Color OVERLAY_COLOR = org.geogebra.desktop.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.DARKBLUE);

	private Color[] colors = { TABLE_GRID_COLOR, TABLE_HEADER_COLOR,
			HISTOGRAM_COLOR, BOXPLOT_COLOR, BARCHART_COLOR, DOTPLOT_COLOR,
			NQPLOT_COLOR, REGRESSION_COLOR, OVERLAY_COLOR, Color.BLACK,
			Color.WHITE };

	// main GUI panels
	private DataPanelD dataPanel;
	private StatisticsPanel statisticsPanel;
	private RegressionPanelD regressionPanel;
	private DataDisplayPanelD dataDisplayPanel1, dataDisplayPanel2;

	private JSplitPane statDataPanel, displayPanel, comboPanelSplit;
	private JPanel mainPanel;

	private int defaultDividerSize;

	private static final String MainCard = "Card with main panel";
	private static final String SourceCard = "Card with data type options";

	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisViewD(AppD app, int mode) {
		this.app = app;
		this.kernel = app.getKernel();

		daCtrl = new DataAnalysisControllerD(app, this);
		model = new DataAnalysisModel(app, this, daCtrl);

		dataDisplayPanel1 = new DataDisplayPanelD(this, 0);
		dataDisplayPanel2 = new DataDisplayPanelD(this, 1);

		setView(null, mode, false);
		model.setIniting(false);

	}

	/*************************************************
	 * END constructor
	 */

	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {
		app.getSettings().getDataAnalysis().setMode(mode);
		model.setView(dataSource, mode,
				app.getSettings().getDataAnalysis(), forceModeUpdate);
		updateFonts();
		updateGUI();
		revalidate();

	}

	public JComponent getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBar(app, this);
		}
		return stylebar;
	}

	private void buildStatisticsPanel() {
		if (statisticsPanel != null) {
			// TODO handle any orphaned geo children of stat panel
			statisticsPanel = null;
		}

		statisticsPanel = new StatisticsPanel(app, this);
		statisticsPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2));
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
	protected DataPanelD buildDataPanel() {

		if (dataPanel != null) {
			// TODO handle any orphaned data panel geos
			dataPanel = null;
		}
		if (!model.isMultiVar()) {
			dataPanel = new DataPanelD(app, this);
			dataPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		return dataPanel;

	}

	@Override
	public void loadDataTable(ArrayList<GeoElement> dataArray) {
		if (dataPanel == null) {
			buildDataPanel();
		}
		dataPanel.loadDataTable(dataArray);
	}

	protected DataPanelD getDataPanel() {
		return dataPanel;
	}

	// =================================================
	// GUI
	// =================================================

	private void updateLayout() {

		this.removeAll();

		// ===========================================
		// statData panel

		if (!model.isMultiVar()) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setResizeWeight(0.5);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		if (model.isMultiVar()) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setDividerSize(0);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		// ===========================================
		// regression panel

		if (model.isRegressionMode()) {
			regressionPanel = new RegressionPanelD(app, this);
		}

		// ===========================================
		// plotComboPanel panel

		// create a splitPane to hold the two plotComboPanels
		comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				dataDisplayPanel1, dataDisplayPanel2);

		comboPanelSplit.setDividerLocation(0.5);
		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());

		// grab the default divider size
		defaultDividerSize = comboPanelSplit.getDividerSize();

		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);

		// display panel
		// ============================================
		if (!model.isMultiVar()) {
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statDataPanel, plotComboPanel);
			displayPanel.setResizeWeight(0.5);
		} else {
			displayPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					plotComboPanel, statDataPanel);
			displayPanel.setResizeWeight(1);

		}
		displayPanel.setBorder(BorderFactory.createEmptyBorder());

		// main panel
		// ============================================
		mainPanel = new JPanel(new BorderLayout());
		// mainPanel.add(getStyleBar(), BorderLayout.NORTH);
		mainPanel.add(displayPanel, BorderLayout.CENTER);

		if (model.isRegressionMode()) {
			mainPanel.add(regressionPanel, BorderLayout.SOUTH);
		}

		// dataTypePanel = new DataViewSettingsPanel(app,
		// StatDialog.MODE_ONEVAR);
		JPanel p = new JPanel(new FullWidthLayout());
		// p.add(dataTypePanel);

		this.setLayout(new CardLayout());
		add(mainPanel, MainCard);
		add(p, SourceCard);
		showMainPanel();

		model.setShowComboPanel2(model.showDataDisplayPanel2());
		updateStatDataPanelVisibility();

	}

	public void showSourcePanel() {
		CardLayout c = (CardLayout) this.getLayout();
		c.show(this, SourceCard);
	}

	public void showMainPanel() {
		CardLayout c = (CardLayout) this.getLayout();
		c.show(this, MainCard);

	}

	// ======================================
	// Getters/setters
	// ======================================

	public DataAnalysisControllerD getDaCtrl() {
		return daCtrl;
	}

	public DataSource getDataSource() {
		return model.getDataSource();
	}

	public GroupType groupType() {
		return daCtrl.getDataSource().getGroupType();
	}

	public DataDisplayPanelD getDataDisplayPanel1() {
		return dataDisplayPanel1;
	}

	public DataDisplayPanelD getDataDisplayPanel2() {
		return dataDisplayPanel2;
	}

	public RegressionPanelD getRegressionPanel() {
		return regressionPanel;
	}

	public StatisticsPanel getStatisticsPanel() {
		return statisticsPanel;
	}

	/**
	 * Component representation of this view
	 * 
	 * @return reference to self
	 */
	public JComponent getDataAnalysisViewComponent() {
		return this;
	}

	@Override
	public DataAnalysisControllerD getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public AppD getApp() {
		return app;
	}

	// public int getMode() {
	// return mode;
	// }

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	@Override
	public void updateStatDataPanelVisibility() {

		if (statDataPanel == null) {
			return;
		}

		if (!model.isMultiVar()) {

			if (model.showDataPanel()) {
				if (statDataPanel.getRightComponent() == null) {
					statDataPanel.setRightComponent(dataPanel);
					statDataPanel.resetToPreferredSizes();
				}
			} else {
				if (statDataPanel.getRightComponent() != null) {
					statDataPanel.setRightComponent(null);
					statDataPanel.resetToPreferredSizes();
				}
			}

			if (model.showStatPanel()) {
				if (statDataPanel.getLeftComponent() == null) {
					statDataPanel.setLeftComponent(statisticsPanel);
					statDataPanel.resetToPreferredSizes();
				}
			} else {
				if (statDataPanel.getLeftComponent() != null) {
					statDataPanel.setLeftComponent(null);
					statDataPanel.resetToPreferredSizes();
				}
			}

			// hide/show divider
			if (model.showDataPanel() && model.showStatPanel()) {
				statDataPanel.setDividerSize(defaultDividerSize);
			} else {
				statDataPanel.setDividerSize(0);
			}

			// hide/show statData panel
			if (model.showDataPanel() || model.showStatPanel()) {
				if (displayPanel.getLeftComponent() == null) {
					displayPanel.setLeftComponent(statDataPanel);
					// displayPanel.resetToPreferredSizes();
					displayPanel.setDividerLocation(
							displayPanel.getLastDividerLocation());
					displayPanel.setDividerSize(defaultDividerSize);
				}

			} else { // statData panel is empty, so hide it
				displayPanel.setLastDividerLocation(
						displayPanel.getDividerLocation());
				displayPanel.setLeftComponent(null);
				displayPanel.setDividerSize(0);
			}

		} else { // handle multi-variable case

			if (model.showStatPanel()) {
				if (displayPanel.getBottomComponent() == null) {
					displayPanel.setBottomComponent(statDataPanel);
					// displayPanel.resetToPreferredSizes();
					displayPanel.setDividerLocation(
							displayPanel.getLastDividerLocation());
					displayPanel.setDividerSize(defaultDividerSize);
				}
			} else {
				displayPanel.setLastDividerLocation(
						displayPanel.getDividerLocation());
				displayPanel.setBottomComponent(null);
				displayPanel.setDividerSize(0);

			}

		}

		updateFonts();

		displayPanel.resetToPreferredSizes();
	}

	public void doPrint() {
		List<Printable> l = new ArrayList<Printable>();
		l.add(this);
		PrintPreviewD.get(app, App.VIEW_DATA_ANALYSIS, PageFormat.LANDSCAPE)
				.setVisible(true);
	}

	// =================================================
	// Event Handlers and Updates
	// =================================================

	@Override
	public void updateGUI() {

		if (stylebar != null) {
			stylebar.updateGUI();
		}
		revalidate();
		repaint();
	}

	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this, font);
		if (stylebar != null) {
			stylebar.reinit();

		}

		dataDisplayPanel1.updateFonts(font);
		dataDisplayPanel2.updateFonts(font);
		if (dataPanel != null) {
			dataPanel.updateFonts(font);
		}

		if (statisticsPanel != null) {
			statisticsPanel.updateFonts(font);
		}

		if (regressionPanel != null) {
			regressionPanel.updateFonts(font);
		}
		setLabels();
	}

	public void setFontRecursive(Container c, Font font) {
		Component[] components = c.getComponents();
		for (Component com : components) {
			com.setFont(font);
			if (com instanceof StatPanelInterface) {
				((StatPanelInterface) com).updateFonts(font);
			}
			if (com instanceof Container) {
				setFontRecursive((Container) com, font);
			}
		}

	}

	@Override
	public void setLabels() {

		if (model.isIniting()) {
			return;
		}

		// setTitle(app.getMenu("OneVariableStatistics"));

		if (model.isRegressionMode() && regressionPanel != null) {
			regressionPanel.setLabels();
		}

		if (stylebar != null) {
			stylebar.setLabels();
		}

		// call setLabels() for all child panels
		setLabelsRecursive(this);

	}

	public void setLabelsRecursive(Container c) {

		Component[] components = c.getComponents();
		for (Component com : components) {
			if (com instanceof StatPanelInterface) {
				// System.out.println(c.getClass().getSimpleName());
				((StatPanelInterface) com).setLabels();
			} else if (com instanceof Container) {
				setLabelsRecursive((Container) com);
			}
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

	public void attachView() {
		// clearView();
		// kernel.notifyAddAll(this);
		model.updateFromSettings();
		kernel.attach(this);

		// attachView to plot panels
		dataDisplayPanel1.attachView();
		if (dataDisplayPanel2 != null) {
			dataDisplayPanel2.attachView();
		}
	}

	public void detachView() {

		dataDisplayPanel1.detachView();
		if (dataDisplayPanel2 != null) {
			dataDisplayPanel2.detachView();
		}
		daCtrl.removeStatGeos();

		kernel.detach(this);

		// clearView();
		// kernel.notifyRemoveAll(this);
	}

	public String[] getDataTitles() {
		return daCtrl.getDataTitles();
	}

	public void updateSelection() {
		// updateDialog(true);
	}

	// =================================================
	// Printing
	// =================================================

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex0) {
		int pageIndex = ((AppD)kernel.getApplication()).getPrintPreview().adjustIndex(pageIndex0);

		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// construction title
		int y = 0;
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!"".equals(title)) {
			Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
					app.getBoldFont().getSize() + 2);
			g2d.setFont(titleFont);
			g2d.setColor(Color.black);
			// Font fn = g2d.getFont();
			FontMetrics fm = g2d.getFontMetrics();
			y += fm.getAscent();
			g2d.drawString(title, 0, y);
		}

		// construction author and date
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!"".equals(author)) {
			line = author;
		}
		if (!"".equals(date)) {
			if (line == null) {
				line = date;
			} else {
				line = line + " - " + date;
			}
		}

		if (line != null) {
			g2d.setFont(app.getPlainFont());
			g2d.setColor(Color.black);
			// Font fn = g2d.getFont();
			FontMetrics fm = g2d.getFontMetrics();
			y += fm.getHeight();
			g2d.drawString(line, 0, y);
		}
		if (y > 0) {
			g2d.translate(0, y + 20); // space between title and drawing
		}

		// scale the dialog so that it fits on one page.
		double xScale = pageFormat.getImageableWidth() / this.getWidth();
		double yScale = (pageFormat.getImageableHeight() - (y + 20))
				/ this.getHeight();
		double scale = Math.min(xScale, yScale);

		this.paint(g2d, scale);

		return (PAGE_EXISTS);
	}

	/**
	 * Paint the dialog with given scale factor (used for printing).
	 */
	public void paint(Graphics graphics, double scale) {

		Graphics2D g2 = (Graphics2D) graphics;
		g2.scale(scale, scale);
		super.paint(graphics);

	}

	@Override
	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}

	public JPopupMenu getExportMenu() {
		return dataDisplayPanel1.getExportMenu();
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
		// only for web
	}

	@Override
	public DataAnalysisModel getModel() {
		return model;
	}

	public void setModel(DataAnalysisModel model) {
		this.model = model;
	}

	@Override
	public void onModeChange(PlotType plotType1, PlotType plotType2) {
		dataPanel = null;
		buildStatisticsPanel();

		setDataPlotPanels(plotType1, plotType2);
		updateLayout();

	}

	@Override
	public void showComboPanel2(boolean show) {
		if (show) {
			if (comboPanelSplit == null) {
				// Application.debug("splitpane null");
			}
			comboPanelSplit.setBottomComponent(dataDisplayPanel2);
			comboPanelSplit.setDividerLocation(200);
			comboPanelSplit.setDividerSize(4);
		} else {
			comboPanelSplit.setBottomComponent(null);
			comboPanelSplit.setLastDividerLocation(
					comboPanelSplit.getDividerLocation());
			comboPanelSplit.setDividerLocation(0);
			comboPanelSplit.setDividerSize(0);
		}

	}

	public String format(double value) {
		return model.format(value);
	}

	@Override
	public GColor createColor(int idx) {
		Color c = colors[idx];
		return GColor.newColor(c.getRed(), c.getGreen(), c.getBlue());
	}

	public void updateOtherDataDisplay(DataDisplayPanelD display) {
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
	public DataDisplayModel getDisplayModel(int index) {
		return index == 0 ? this.dataDisplayPanel1.getModel()
				: this.dataDisplayPanel2.getModel();
	}

}

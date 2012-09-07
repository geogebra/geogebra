package geogebra.gui.view.data;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.util.SpecialNumberFormat;
import geogebra.gui.util.SpecialNumberFormatInterface;
import geogebra.main.AppD;

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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

/**
 * View to display plots and statistical analysis of data.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisViewD extends JPanel implements View, Printable,
		SpecialNumberFormatInterface {

	private static final long serialVersionUID = 1L;

	// ggb
	private AppD app;
	private Kernel kernel;
	private StatGeo statGeo;
	private DataAnalysisControllerD daCtrl;

	private DataAnalysisStyleBar stylebar;

	// modes
	public static final int MODE_ONEVAR = EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS;
	public static final int MODE_REGRESSION = EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS;
	public static final int MODE_MULTIVAR = EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS;
	public static final int MODE_GROUPDATA = 3;
	private int mode = -1;

	
	//private int sourceType = DataSource.SOURCE_RAWDATA;

	// flags
	private boolean showDataPanel = false;

	private boolean showStatPanel = false;
	private boolean showComboPanel2 = false;
	protected boolean isIniting = true;
	private boolean leftToRight = true;
	

	private boolean doSpecialNumberFormat = false;

	// colors
	public static final Color TABLE_GRID_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
	public static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
	public static final Color HISTOGRAM_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.BLUE);
	public static final Color BOXPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.CRIMSON);
	public static final Color BARCHART_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.DARKGREEN);
	
	public static final Color DOTPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color NQPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color REGRESSION_COLOR = Color.RED;
	public static final Color OVERLAY_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.DARKBLUE);

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

	// public static final int regressionTypes = 9;
	private Regression regressionMode = Regression.NONE;
	private int regressionOrder = 2;

	// main GUI panels
	protected DataPanel dataPanel;
	protected StatisticsPanel statisticsPanel;
	protected RegressionPanel regressionPanel;
	protected DataDisplayPanel comboStatPanel, comboStatPanel2;
	private JSplitPane statDataPanel, displayPanel, comboPanelSplit;
	private DataSourcePanel dataTypePanel;
	private JPanel mainPanel;

	private int defaultDividerSize;

	final static String MainCard = "Card with main panel";
	final static String SourceCard = "Card with data type options";

	// number format
	private SpecialNumberFormat nf;

	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisViewD(AppD app, int mode) {

		isIniting = true;
		this.app = app;
		this.kernel = app.getKernel();


		nf = new SpecialNumberFormat(app, this);

		daCtrl = new DataAnalysisControllerD(app, this);

		comboStatPanel = new DataDisplayPanel(this);
		comboStatPanel2 = new DataDisplayPanel(this);

		setView(null, mode, true);
		isIniting = false;

	}

	/*************************************************
	 * END constructor
	 */

	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {

		daCtrl.setDataSource(dataSource);

		if (dataSource == null) {
			daCtrl.setValidData(false);
		} else {
			daCtrl.setValidData(true);
		}

		// reinit the GUI if mode is changed
		if (this.mode != mode || forceModeUpdate) {

			this.mode = mode;
			dataPanel = null;
			buildStatisticsPanel();
			daCtrl.updateDataLists();
			setDataPlotPanels();
			updateLayout();

			// TODO: why do this here?
			daCtrl.updateDataAnalysisView();

		} else {
			// just update data source
			daCtrl.updateDataAnalysisView();
		}

		// TODO is this needed?
		setLeftToRight(true);

		updateFonts();
		setLabels();
		updateGUI();

		revalidate();

	}

	public JComponent getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBar(app, this);
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
		if (mode != MODE_GROUPDATA) {
			statisticsPanel = new StatisticsPanel(app, this);
			statisticsPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 2,
					2));
		}
	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels() {

		switch (mode) {

		case MODE_ONEVAR:
			if (!isNumericData()) {
				comboStatPanel.setPanel(DataDisplayPanel.PLOT_BARCHART, mode);
				comboStatPanel2.setPanel(DataDisplayPanel.PLOT_BARCHART, mode);

			} else if (sourceType() == DataSource.SOURCE_RAWDATA) {
				comboStatPanel.setPanel(DataDisplayPanel.PLOT_HISTOGRAM, mode);
				comboStatPanel2.setPanel(DataDisplayPanel.PLOT_BOXPLOT, mode);

			} else if (sourceType() == DataSource.SOURCE_VALUE_FREQUENCY) {
				comboStatPanel.setPanel(DataDisplayPanel.PLOT_BARCHART, mode);
				comboStatPanel2.setPanel(DataDisplayPanel.PLOT_BOXPLOT, mode);
				
			} else if (sourceType() == DataSource.SOURCE_CLASS_FREQUENCY) {
				comboStatPanel.setPanel(DataDisplayPanel.PLOT_HISTOGRAM, mode);
				comboStatPanel2.setPanel(DataDisplayPanel.PLOT_BOXPLOT, mode);
			}
			break;

		case MODE_REGRESSION:
			comboStatPanel.setPanel(DataDisplayPanel.PLOT_SCATTERPLOT, mode);
			comboStatPanel2.setPanel(DataDisplayPanel.PLOT_RESIDUAL, mode);
			break;

		case MODE_MULTIVAR:
			comboStatPanel.setPanel(DataDisplayPanel.PLOT_MULTIBOXPLOT, mode);
			showComboPanel2 = false;
			break;

		case MODE_GROUPDATA:
			comboStatPanel.setPanel(DataDisplayPanel.PLOT_HISTOGRAM, mode);
			showComboPanel2 = false;
			break;

		}
	}

	// Create DataPanel to display the current data set(s) and allow
	// temporary editing.
	protected DataPanel buildDataPanel() {

		if (dataPanel != null) {
			// TODO handle any orphaned data panel geos
			dataPanel = null;
		}
		if (mode != MODE_MULTIVAR && mode != DataAnalysisViewD.MODE_GROUPDATA) {
			dataPanel = new DataPanel(app, this);
			dataPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		return dataPanel;

	}

	protected DataPanel getDataPanel() {
		if (dataPanel == null) {
			buildDataPanel();
		}
		return dataPanel;
	}

	// =================================================
	// GUI
	// =================================================

	private void updateLayout() {

		this.removeAll();

		// ===========================================
		// statData panel

		if (mode != MODE_MULTIVAR) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setResizeWeight(0.5);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		if (mode == MODE_MULTIVAR) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setDividerSize(0);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		// ===========================================
		// regression panel

		if (mode == MODE_REGRESSION) {
			regressionPanel = new RegressionPanel(app, this);
		}

		// ===========================================
		// plotComboPanel panel

		// create a splitPane to hold the two plotComboPanels
		comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				comboStatPanel, comboStatPanel2);

		comboPanelSplit.setDividerLocation(0.5);
		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());

		// grab the default divider size
		defaultDividerSize = comboPanelSplit.getDividerSize();

		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);

		// display panel
		// ============================================
		if (mode != MODE_MULTIVAR) {
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

		if (mode == MODE_REGRESSION) {
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

		setShowComboPanel2(showComboPanel2);
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

	public DataAnalysisControllerD getDaCtrl(){
		return daCtrl;
	}
	
	public DataSource getDataSource(){
		return daCtrl.getDataSource();
	}
	
	private int sourceType(){
		return daCtrl.getDataSource().getSourceType();
	}
	
	/**
	 * Component representation of this view
	 * 
	 * @return reference to self
	 */
	public JComponent getDataAnalysisViewComponent() {
		return this;
	}

	public boolean showComboPanel2() {
		return showComboPanel2;
	}

	public boolean showDataPanel() {
		return showDataPanel;
	}

	public void setShowDataPanel(boolean isVisible) {
		if (showDataPanel == isVisible) {
			return;
		}
		showDataPanel = isVisible;
		updateStatDataPanelVisibility();
	}

	public void setShowStatistics(boolean isVisible) {
		if (showStatPanel == isVisible) {
			return;
		}
		showStatPanel = isVisible;
		updateStatDataPanelVisibility();
	}

	public boolean showStatPanel() {
		return showStatPanel;
	}

	/**
	 * Converts double to formatted String
	 * 
	 * @param x
	 *            number to be converted
	 * @return formatted number string
	 */
	public String format(double x) {

		// apply local numeric format
		if (doSpecialNumberFormat) {
			return nf.format(x);
		}

		// apply GeoGebra numeric format
		return app.getKernel().format(x, StringTemplate.numericDefault);
	}

	public int getPrintDecimals() {
		return nf.getPrintDecimals();
	}

	public int getPrintFigures() {
		return nf.getPrintFigures();
	}

	public DataAnalysisControllerD getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public StatGeo getStatGeo() {
		if (statGeo == null)
			statGeo = new StatGeo(app);
		return statGeo;
	}

	public int getRegressionOrder() {
		return regressionOrder;
	}

	public void setRegressionMode(int regressionMode) {

		for (Regression l : Regression.values()) {
			if (l.ordinal() == regressionMode) {
				this.regressionMode = l;

				daCtrl.setRegressionGeo();
				daCtrl.updateAllStatPanels(true);

				return;
			}
		}

		App.warn("no mode set in setRegressionMode()");
		this.regressionMode = Regression.NONE;

	}

	public Regression getRegressionMode() {
		return regressionMode;
	}

	public void setRegressionOrder(int regressionOrder) {
		this.regressionOrder = regressionOrder;
	}

	public AppD getApp() {
		return app;
	}

	public boolean isLeftToRight() {
		return leftToRight;
	}

	public void setLeftToRight(boolean leftToRight) {
		daCtrl.setLeftToRight(leftToRight);
	}

	public int getMode() {
		return mode;
	}

	public boolean doSpecialNumberFormat() {
		return doSpecialNumberFormat;
	}

	public void setDoSpecialNumberFormat(boolean doSpecialNumberFormat) {
		this.doSpecialNumberFormat = doSpecialNumberFormat;
	}

	public void setShowDataOptionsDialog(boolean showDialog) {
		// if (showDialog) {
		// showSourcePanel();
		// this.dataTypePanel;
		// } else {
		// showMainPanel();
		// }

		app.getDialogManager().showDataSourceDialog(mode, false);

	}

	public boolean isNumericData() {
		if(daCtrl.getDataSource() == null){
			return false;
		}
		return daCtrl.getDataSource().isNumericData();
	}

	
	
	// =================================================
	// Handlers for Component Visibility
	// =================================================

	public void setShowComboPanel2(boolean showComboPanel2) {

		this.showComboPanel2 = showComboPanel2;

		if (showComboPanel2) {
			if (comboPanelSplit == null) {
				// Application.debug("splitpane null");
			}
			comboPanelSplit.setBottomComponent(comboStatPanel2);
			comboPanelSplit.setDividerLocation(200);
			comboPanelSplit.setDividerSize(4);
		} else {
			comboPanelSplit.setBottomComponent(null);
			comboPanelSplit.setLastDividerLocation(comboPanelSplit
					.getDividerLocation());
			comboPanelSplit.setDividerLocation(0);
			comboPanelSplit.setDividerSize(0);
		}

	}

	public void updateStatDataPanelVisibility() {

		if (statDataPanel == null)
			return;

		if (mode != MODE_MULTIVAR) {

			if (showDataPanel) {
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

			if (showStatPanel) {
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
			if (showDataPanel && showStatPanel)
				statDataPanel.setDividerSize(defaultDividerSize);
			else
				statDataPanel.setDividerSize(0);

			// hide/show statData panel
			if (showDataPanel || showStatPanel) {
				if (displayPanel.getLeftComponent() == null) {
					displayPanel.setLeftComponent(statDataPanel);
					// displayPanel.resetToPreferredSizes();
					displayPanel.setDividerLocation(displayPanel
							.getLastDividerLocation());
					displayPanel.setDividerSize(defaultDividerSize);
				}

			} else { // statData panel is empty, so hide it
				displayPanel.setLastDividerLocation(displayPanel
						.getDividerLocation());
				displayPanel.setLeftComponent(null);
				displayPanel.setDividerSize(0);
			}
		}

		setLabels();
		updateFonts();

		displayPanel.resetToPreferredSizes();
	}

	public void doPrint() {
		new geogebra.export.PrintPreview(app, this, PageFormat.LANDSCAPE)
				.setVisible(true);
	}

	// =================================================
	// Event Handlers and Updates
	// =================================================

	/**
	 * Updates the dialog when the number format options have been changed
	 */
	public void changedNumberFormat() {
		daCtrl.updateDataAnalysisView();
	}

	public void updateGUI() {

		if (stylebar != null) {
			stylebar.updateGUI();
		}
	}

	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this, font);

	}

	public void setFontRecursive(Container c, Font font) {
		Component[] components = c.getComponents();
		for (Component com : components) {
			com.setFont(font);
			if (com instanceof StatPanelInterface) {
				((StatPanelInterface) com).updateFonts(font);
			}
			if (com instanceof Container)
				setFontRecursive((Container) com, font);
		}
	}

	public void setLabels() {

		if (isIniting) {
			return;
		}

		// setTitle(app.getMenu("OneVariableStatistics"));

		if (mode == MODE_REGRESSION && regressionPanel != null) {
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
			} else if (com instanceof Container)
				setLabelsRecursive((Container) com);
		}
	}

	// =================================================
	// View Implementation
	// =================================================

	public void remove(GeoElement geo) {
		// Application.debug("removed geo: " + geo.toString());
		daCtrl.handleRemovedDataGeo(geo);
	}

	public void update(GeoElement geo) {

		if (!isIniting && daCtrl.isInDataSource(geo)) {
			App.error("updated geo:" + geo.toString());
			daCtrl.updateDataAnalysisView();
		}
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
		// do nothing
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	public void reset() {
		// do nothing
	}

	public void setMode(int mode) {
		// do nothing
	}

	public void attachView() {
		// clearView();
		// kernel.notifyAddAll(this);
		kernel.attach(this);

		// attachView to plot panels
		comboStatPanel.attachView();
		if (comboStatPanel2 != null)
			comboStatPanel2.attachView();
	}

	public void detachView() {

		comboStatPanel.detachView();
		if (comboStatPanel2 != null)
			comboStatPanel2.detachView();
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

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// construction title
		int y = 0;
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
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
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + " - " + date;
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

	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}

	public JPopupMenu getExportMenu() {
		return comboStatPanel.getExportMenu();
	}

}

package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.util.Language;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SpecialNumberFormat;
import geogebra.gui.util.SpecialNumberFormatInterface;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class StatDialog extends JPanel implements ActionListener, View,
		Printable, SpecialNumberFormatInterface {

	private static final long serialVersionUID = 1L;
	// ggb
	private AppD app;
	private Kernel kernel;
	private StatGeo statGeo;
	private StatDialogController sdc;

	private DataAnalysisStyleBar stylebar;

	// modes
	public static final int MODE_ONEVAR = 0;
	public static final int MODE_REGRESSION = 1;
	public static final int MODE_MULTIVAR = 2;
	public static final int MODE_GROUPDATA = 3;
	private int mode = MODE_ONEVAR;

	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showComboPanel2 = false;
	protected boolean isIniting;
	protected boolean leftToRight = true;

	// colors
	public static final Color TABLE_GRID_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
	public static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
	public static final Color HISTOGRAM_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.BLUE);
	public static final Color BOXPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.CRIMSON);
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
	};

	// public static final int regressionTypes = 9;
	private Regression regressionMode = Regression.NONE;
	private int regressionOrder = 2;

	// oneVar title panel objects
	private JLabel lblOneVarTitle;
	private MyTextField fldOneVarTitle;

	// button panel objects
	private JButton btnClose, btnPrint;
	private PopupMenuButton btnOptions;
	private StatDialogOptionsPanel dialogOptionsPanel;

	// main GUI panels
	protected DataPanel dataPanel;
	protected StatisticsPanel statisticsPanel;
	protected RegressionPanel regressionPanel;
	protected StatComboPanel comboStatPanel, comboStatPanel2;
	private JSplitPane statDataPanel, displayPanel, comboPanelSplit;
	private JPanel buttonPanel;
	private int defaultDividerSize;

	private Dimension defaultDialogDimension;

	// number format
	private SpecialNumberFormat nf;

	/*************************************************
	 * Construct the dialog
	 * 
	 * @param app
	 * @param mode
	 */
	public StatDialog(AppD app, int mode) {

		isIniting = true;
		this.app = app;
		this.kernel = app.getKernel();
		this.mode = mode;

		nf = new SpecialNumberFormat(app, this);

		sdc = new StatDialogController(app, this);

		defaultDialogDimension = new Dimension(700, 500);

		boolean dataOK = sdc.setDataSource();
		if (dataOK) {
			// load data from the data source (based on currently selected geos)
			sdc.loadDataLists();
		} else {
			// TODO is dispose needed ?
			// dispose();
			return;
		}

		createGUI();
		sdc.updateAllStatPanels(false);

	}

	/*************************************************
	 * END StatDialog constructor
	 */

	
	
	
	public void setDataAnalysisMode(int mode) {

		if (app.getSelectedGeos().size() == 0)
			return;

		if(this.mode != mode){
			this.mode = mode;
			this.removeAll();
			sdc.removeStatGeos();
			dataPanel = null;
			statisticsPanel = null;
			comboStatPanel = null;
			comboStatPanel2 = null;
			
			boolean dataOK = sdc.setDataSource();
			if (dataOK) {
				// load data from the data source (based on currently selected geos)
				sdc.loadDataLists();
			} else {
				// TODO is dispose needed ?
				// dispose();
				return;
			}

			createGUI();
			sdc.updateAllStatPanels(false);
			this.revalidate();

		}else{
			this.mode = mode;
			updateDialog(true);
		}
		
				

		// TODO is this needed?
		setLeftToRight(true);
	}

	public JComponent getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBar(app, this);
		}
		return stylebar;
	}

	private void createGUI() {

		// Create two StatCombo panels with default plots.
		// StatCombo panels display various plots and tables selected by a
		// comboBox.

		switch (mode) {

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(this,
					StatComboPanel.PLOT_HISTOGRAM, mode, true);
			comboStatPanel2 = new StatComboPanel(this,
					StatComboPanel.PLOT_BOXPLOT, mode, true);
			break;

		case MODE_REGRESSION:
			// showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this,
					StatComboPanel.PLOT_SCATTERPLOT, mode, true);
			comboStatPanel2 = new StatComboPanel(this,
					StatComboPanel.PLOT_RESIDUAL, mode, true);
			break;

		case MODE_MULTIVAR:
			showComboPanel2 = false;
			comboStatPanel = new StatComboPanel(this,
					StatComboPanel.PLOT_MULTIBOXPLOT, mode, true);
			break;

		case MODE_GROUPDATA:
			showComboPanel2 = false;
			comboStatPanel = new StatComboPanel(this,
					StatComboPanel.PLOT_HISTOGRAM, mode, true);
			break;

		}

		// Create StatisticPanel to display statistics and inference results
		// from the current data set
		if (mode != MODE_GROUPDATA) {
			statisticsPanel = new StatisticsPanel(app, this);
			statisticsPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 2,
					2));
		}
		// Init the GUI and attach this view to the kernel

		initGUI();
		updateFonts();
		btnClose.requestFocus();
		//attachView();
		isIniting = false;
		setLabels();
		updateGUI();

	}

	// Create DataPanel to display the current data set(s) and allow
	// temporary editing.
	protected DataPanel getDataPanel() {

		if (dataPanel == null && mode != MODE_MULTIVAR) {
			dataPanel = new DataPanel(app, this);
			dataPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		return dataPanel;

	}

	// =================================================
	// GUI
	// =================================================

	private void initGUI() {

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

		// create a splitPane to hold the two plotComboPanels
		comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				comboStatPanel, comboStatPanel2);

		comboPanelSplit.setDividerLocation(0.5);
		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());

		// grab the default divider size
		defaultDividerSize = comboPanelSplit.getDividerSize();

		// ===========================================
		// button panel

		btnClose = new JButton();
		btnClose.addActionListener(this);

		createOptionsButton();

		JPanel rightButtonPanel = new JPanel(new FlowLayout());
		rightButtonPanel.add(btnOptions);
		rightButtonPanel.add(btnClose);

		buttonPanel = new JPanel(new BorderLayout());
		// buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);
		if (mode == MODE_ONEVAR) {
			JPanel oneVarTitlePanel = createOneVarTitlePanel();
			buttonPanel.add(oneVarTitlePanel, BorderLayout.NORTH);
		}
		if (mode == MODE_REGRESSION) {
			regressionPanel = new RegressionPanel(app, this);
			buttonPanel.add(regressionPanel, BorderLayout.NORTH);
		}

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
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(displayPanel, BorderLayout.CENTER);
		// mainPanel.add(new StatDialogStyleBar(app, this), BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		setShowComboPanel2(showComboPanel2);
		updateStatDataPanelVisibility();

	}

	private JPanel createOneVarTitlePanel() {

		lblOneVarTitle = new JLabel();
		fldOneVarTitle = new MyTextField(app);

		JPanel titlePanel = new JPanel(new BorderLayout(5, 0));
		titlePanel.add(lblOneVarTitle, BorderLayout.WEST);
		titlePanel.add(fldOneVarTitle, BorderLayout.CENTER);

		// titlePanel.setBorder(BorderFactory.createEtchedBorder());
		titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		return titlePanel;
	}

	private void createOptionsButton() {

		if (btnOptions == null) {
			btnOptions = new PopupMenuButton(app);
			btnOptions.setKeepVisible(true);
			btnOptions.setStandardButton(true);
			btnOptions.setFixedIcon(GeoGebraIcon.createUpDownTriangleIcon(
					false, true));
			btnOptions.setDownwardPopup(false);
		}

		btnOptions.removeAllMenuItems();
		btnOptions.setText(app.getMenu("Options"));

		JCheckBoxMenuItem menuItem;

		// rounding
		btnOptions.addPopupMenuItem(nf.createMenuDecimalPlaces());

		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowData"));
		menuItem.setSelected(showDataPanel);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDataPanel = !showDataPanel;
				updateStatDataPanelVisibility();
			}
		});
		if (this.mode != MODE_MULTIVAR)
			btnOptions.addPopupMenuItem(menuItem);

		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowStatistics"));
		menuItem.setSelected(showStatPanel);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStatPanel = !showStatPanel;
				updateStatDataPanelVisibility();
			}
		});
		menuItem.setEnabled(true);
		if (this.mode != MODE_MULTIVAR)
			btnOptions.addPopupMenuItem(menuItem);

		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowPlot2"));
		menuItem.setSelected(showComboPanel2);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setShowComboPanel2(!showComboPanel2);
			}
		});
		menuItem.setEnabled(true);
		if (this.mode != MODE_MULTIVAR)
			btnOptions.addPopupMenuItem(menuItem);

		JMenuItem item = new JMenuItem(app.getMenu("Print") + "...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doPrint();
			}
		});
		btnOptions.addPopupMenuItem(item);

	}

	// ======================================
	// Getters/setters
	// ======================================

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

	public String format(double x) {

		return nf.format(x);
	}

	public int getPrintDecimals() {
		return nf.getPrintDecimals();
	}

	public int getPrintFigures() {
		return nf.getPrintFigures();
	}

	public StatDialogController getStatDialogController() {
		return sdc;
	}

	public GeoElement getRegressionModel() {
		return sdc.getRegressionModel();
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

	public void setLeftToRight(boolean leftToRight) {
		sdc.setLeftToRight(leftToRight);
	}

	public int getMode() {
		return mode;
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

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == btnClose) {
			setVisible(false);
		}

		btnClose.requestFocus();
	}

	/**
	 * Updates the dialog when the number format options have been changed
	 */
	public void changedNumberFormat() {
		updateDialog(false);
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		if (isVisible) {
			// Application.debug("statDialog visible");
			// spView.setColumnSelect(true);
			//this.attachView();
			// if(!isIniting)
			// updateDialog();

		} else {
			// Application.printStacktrace("statDialog not visible");
			// spView.setColumnSelect(false);
			
			//this.detachView();
		}
	}

	private void updateGUI() {
		if (isIniting)
			return;
		if (mode == StatDialog.MODE_ONEVAR) {
			fldOneVarTitle.setText(sdc.getDataTitles()[0]);
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

		switch (mode) {
		case MODE_ONEVAR:
			// setTitle(app.getMenu("OneVariableStatistics"));
			lblOneVarTitle.setText(app.getMenu("DataTitle") + ": ");
			break;

		case MODE_REGRESSION:
			// setTitle(app.getMenu("RegressionAnalysis"));
			regressionPanel.setLabels();
			break;

		case MODE_MULTIVAR:
			// setTitle(app.getMenu("MultiVariableStatistics"));
			break;
		}

		this.createOptionsButton();

		btnClose.setText(app.getMenu("Close"));
		// btnPrint.setText(app.getMenu("Print"));
		// btnOptions.setText(app.getMenu("Options"));

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
		sdc.handleRemovedDataGeo(geo);
	}

	public void update(GeoElement geo) {
		// Application.debug("updated geo:" + geo.toString());
		if (!isIniting && sdc.isInDataSource(geo)) {
			// Application.debug("this geo is in data source: " +
			// geo.toString());
			sdc.updateDialog(false);
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
		sdc.removeStatGeos();
		
		kernel.detach(this);
		
		// clearView();
		// kernel.notifyRemoveAll(this);
	}

	public void updateDialog(boolean doSetDataSource) {
		sdc.updateDialog(doSetDataSource);
	}

	public String[] getDataTitles() {
		return sdc.getDataTitles();
	}

	// =================================================
	// Printing
	// =================================================

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);
		else {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());

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

			buttonPanel.setVisible(false);
			this.paint(g2d, scale);
			buttonPanel.setVisible(true);

			return (PAGE_EXISTS);
		}
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

}

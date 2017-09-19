package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatPanelSettings;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Feature;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * JPanel to display settings options for a ComboStatPanel
 * 
 * @author G. Sturr
 * 
 */
public class OptionsPanelD extends JPanel implements PropertyChangeListener,
		ActionListener, FocusListener, StatPanelInterface {
	private static final long serialVersionUID = 1L;

	private AppD app;
	private StatPanelSettings settings;

	// histogram panel GUI
	private JCheckBox ckCumulative, ckManual, ckOverlayNormal, ckOverlayPolygon,
			ckShowFrequencyTable, ckShowHistogram;
	private JRadioButton rbRelative, rbNormalized, rbFreq, rbLeftRule,
			rbRightRule;
	private JLabel lblFreqType, lblOverlay, lblClassRule;
	private JPanel freqPanel, showPanel, dimPanel;

	// graph panel GUI
	private JCheckBox ckAutoWindow, ckShowGrid;
	private JLabel lblXMin, lblXMax, lblYMin, lblYMax, lblXInterval,
			lblYInterval;
	private MyTextFieldD fldXMin, fldXMax, fldYMin, fldYMax, fldXInterval,
			fldYInterval;
	private JRadioButton rbStandToStand, rbLogToStand, rbStandToLog, rbLogToLog; // coordinate
																					// option
	private JPanel coordPanel;
	private boolean showYAxisSettings = true;

	// bar chart panel GUI
	private JLabel lblBarWidth;
	private MyTextFieldD fldBarWidth;
	private JCheckBox ckAutoBarWidth;
	private JPanel barChartWidthPanel;

	// box plot panel GUI
	private JCheckBox ckShowOutliers;

	// scatterplot panel GUI
	private JCheckBox ckShowLines;

	// panels
	private JPanel histogramPanel, graphPanel, classesPanel, scatterplotPanel,
			barChartPanel, boxPlotPanel;
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	// misc fields
	private static final int tab = 5;
	private boolean isUpdating = false;

	private DataAnalysisModel daModel;

	private LocalizationD loc;

	private final static int fieldWidth = 8;

	/************************************************************
	 * Constructs an OptionPanel
	 * 
	 * @param app
	 *            App
	 * @param settings
	 *            settings
	 */
	public OptionsPanelD(AppD app, DataAnalysisModel model,
			StatPanelSettings settings) {

		this.app = app;
		this.loc = app.getLocalization();
		this.daModel = model;
		this.settings = settings;

		// create option panels
		createHistogramPanel();
		createGraphPanel();
		createScatterplotPanel();
		createBarChartPanel();
		createBoxPlotPanel();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(histogramPanel);
		mainPanel.add(scatterplotPanel);
		mainPanel.add(barChartPanel);
		mainPanel.add(boxPlotPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		tabbedPane = new JTabbedPane();
		tabbedPane.addFocusListener(this);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());

		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
				SystemColor.controlShadow));

		// update
		setLabels();
		updateGUI();
		// this.setPreferredSize(tabbedPane.getPreferredSize());

		this.requestFocusInWindow();

	}

	public void setPanel(PlotType plotType) {

		tabbedPane.removeAll();
		this.setVisible(true);

		// add plot-specific tab
		String tabTitle = plotType.getTranslatedKey(loc);
		tabbedPane.insertTab(tabTitle, null, new JScrollPane(mainPanel), null,
				0);
		classesPanel.setVisible(false);
		histogramPanel.setVisible(false);
		scatterplotPanel.setVisible(false);
		barChartPanel.setVisible(false);
		boxPlotPanel.setVisible(false);

		rbNormalized.setVisible(false);
		ckOverlayNormal.setVisible(false);
		ckShowHistogram.setVisible(false);
		ckCumulative.setVisible(false);
		ckOverlayPolygon.setVisible(false);

		// add graph tab
		tabbedPane.addTab(loc.getMenu("Graph"), new JScrollPane(graphPanel));
		graphPanel.setVisible(true);
		showYAxisSettings = true;

		// set visibility for plot-specific panels
		switch (plotType) {

		case HISTOGRAM:
			classesPanel.setVisible(true);
			histogramPanel.setVisible(true);
			rbNormalized.setVisible(true);
			ckOverlayNormal.setVisible(true);
			ckShowHistogram.setVisible(true);
			ckCumulative.setVisible(true);
			ckOverlayPolygon.setVisible(true);

			layoutHistogramPanel();

			break;

		case BOXPLOT:
		case MULTIBOXPLOT:
			boxPlotPanel.setVisible(true);
			break;

		case BARCHART:
			barChartPanel.setVisible(true);
			layoutBarChartPanel();
			break;

		case SCATTERPLOT:
			scatterplotPanel.setVisible(true);
			break;

		// graph tab only
		case DOTPLOT:
		case NORMALQUANTILE:
		case RESIDUAL:
			tabbedPane.removeTabAt(0);
			break;

		case STEMPLOT:
			this.setVisible(false);
			break;

		}

		setLabels();
		updateGUI();
	}

	private void createHistogramPanel() {

		// create components
		ckCumulative = new JCheckBox();
		ckCumulative.addActionListener(this);

		lblFreqType = new JLabel();
		rbFreq = new JRadioButton();
		rbFreq.addActionListener(this);

		rbNormalized = new JRadioButton();
		rbNormalized.addActionListener(this);

		rbRelative = new JRadioButton();
		rbRelative.addActionListener(this);

		ButtonGroup g = new ButtonGroup();
		g.add(rbFreq);
		g.add(rbNormalized);
		g.add(rbRelative);

		lblOverlay = new JLabel();
		ckOverlayNormal = new JCheckBox();
		ckOverlayNormal.addActionListener(this);

		ckOverlayPolygon = new JCheckBox();
		ckOverlayPolygon.addActionListener(this);

		ckShowFrequencyTable = new JCheckBox();
		ckShowFrequencyTable.addActionListener(this);

		ckShowHistogram = new JCheckBox();
		ckShowHistogram.addActionListener(this);

		ckManual = new JCheckBox();
		ckManual.addActionListener(this);

		lblClassRule = new JLabel();
		rbLeftRule = new JRadioButton();
		rbLeftRule.addActionListener(this);
		rbRightRule = new JRadioButton();
		rbRightRule.addActionListener(this);

		ButtonGroup g2 = new ButtonGroup();
		g2.add(rbLeftRule);
		g2.add(rbRightRule);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;

		// tab = tab-like constraint
		GridBagConstraints tab1 = (GridBagConstraints) c.clone();
		tab1.insets = new Insets(0, 20, 0, 0);

		// create frequency type panel
		freqPanel = new JPanel(new GridBagLayout());
		freqPanel.add(ckCumulative, c);
		freqPanel.add(rbFreq, tab1);
		freqPanel.add(rbRelative, tab1);
		freqPanel.add(rbNormalized, tab1);

		// create show panel
		showPanel = new JPanel(new GridBagLayout());
		showPanel.add(ckShowHistogram, c);
		showPanel.add(ckShowFrequencyTable, c);
		showPanel.add(ckOverlayPolygon, c);
		showPanel.add(ckOverlayNormal, c);

		// create classes panel
		classesPanel = new JPanel(new GridBagLayout());
		classesPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("FrequencyType")));
		classesPanel.add(ckManual, c);
		c.insets.top += 8; // vertical gap
		classesPanel.add(lblClassRule, c);
		c.insets.top -= 8; // undo vertical gap
		classesPanel.add(rbLeftRule, tab1);
		classesPanel.add(rbRightRule, tab1);
		layoutHistogramPanel();

	}

	private void layoutHistogramPanel() {

		Box vBox = Box.createVerticalBox();
		vBox.add(classesPanel);
		vBox.add(freqPanel);
		vBox.add(showPanel);

		if (histogramPanel == null) {
			histogramPanel = new JPanel(new BorderLayout());
		}
		histogramPanel.removeAll();
		histogramPanel.add(vBox, BorderLayout.NORTH);
		histogramPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	private void layoutBarChartPanel() {

		Box vBox = Box.createVerticalBox();
		vBox.add(barChartWidthPanel);
		// vBox.add(freqPanel);
		vBox.add(showPanel);

		if (barChartPanel == null) {
			barChartPanel = new JPanel(new BorderLayout());
		}
		barChartPanel.removeAll();
		barChartPanel.add(vBox, BorderLayout.NORTH);
		barChartPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	private void createBarChartPanel() {

		// create components
		ckAutoBarWidth = new JCheckBox();
		ckAutoBarWidth.addActionListener(this);
		lblBarWidth = new JLabel();
		fldBarWidth = new MyTextFieldD(app, fieldWidth);
		fldBarWidth.setEditable(true);
		fldBarWidth.addActionListener(this);
		fldBarWidth.addFocusListener(this);

		// barChartWidthPanel
		barChartWidthPanel = new JPanel();
		barChartWidthPanel
				.setLayout(new BoxLayout(barChartWidthPanel, BoxLayout.Y_AXIS));
		barChartWidthPanel.add(LayoutUtil.flowPanel(ckAutoBarWidth));
		barChartWidthPanel
				.add(LayoutUtil.flowPanel(tab, lblBarWidth, fldBarWidth));

		layoutBarChartPanel();

	}

	private void createBoxPlotPanel() {

		// create components
		ckShowOutliers = new JCheckBox();
		ckShowOutliers.addActionListener(this);

		// layout
		Box p = Box.createVerticalBox();
		p.add(LayoutUtil.flowPanel(ckShowOutliers));

		boxPlotPanel = new JPanel(new BorderLayout());
		boxPlotPanel.add(p, BorderLayout.NORTH);

	}

	private void createScatterplotPanel() {

		// create components
		ckShowLines = new JCheckBox();
		ckShowLines.addActionListener(this);

		// layout
		Box p = Box.createVerticalBox();
		p.add(insetPanel(tab, ckShowLines));

		scatterplotPanel = new JPanel(new BorderLayout());
		scatterplotPanel.add(p, BorderLayout.NORTH);
	}

	private void createGraphPanel() {

		// create components
		ckAutoWindow = new JCheckBox();
		ckAutoWindow.addActionListener(this);

		ckShowGrid = new JCheckBox();
		ckShowGrid.addActionListener(this);

		lblXMin = new JLabel();
		fldXMin = new MyTextFieldD(app, fieldWidth);
		fldXMin.setEditable(true);
		fldXMin.addActionListener(this);
		fldXMin.addFocusListener(this);

		lblXMax = new JLabel();
		fldXMax = new MyTextFieldD(app, fieldWidth);
		fldXMax.addActionListener(this);
		fldXMax.addFocusListener(this);

		lblYMin = new JLabel();
		fldYMin = new MyTextFieldD(app, fieldWidth);
		fldYMin.addActionListener(this);
		fldYMin.addFocusListener(this);

		lblYMax = new JLabel();
		fldYMax = new MyTextFieldD(app, fieldWidth);
		fldYMax.addActionListener(this);
		fldYMax.addFocusListener(this);

		lblXInterval = new JLabel();
		fldXInterval = new MyTextFieldD(app, fieldWidth);
		fldXInterval.addActionListener(this);
		fldXInterval.addFocusListener(this);

		lblYInterval = new JLabel();
		fldYInterval = new MyTextFieldD(app, fieldWidth);
		fldYInterval.addActionListener(this);
		fldYInterval.addFocusListener(this);

		/*
		 * Coordinate alternative
		 */
		rbStandToStand = new JRadioButton();
		rbStandToStand.addActionListener(this);
		rbLogToStand = new JRadioButton();
		rbLogToStand.addActionListener(this);
		rbStandToLog = new JRadioButton();
		rbStandToLog.addActionListener(this);
		rbLogToLog = new JRadioButton();
		rbLogToLog.addActionListener(this);

		ButtonGroup logAxesButtons = new ButtonGroup();
		logAxesButtons.add(rbStandToStand);
		logAxesButtons.add(rbLogToStand);
		logAxesButtons.add(rbStandToLog);
		logAxesButtons.add(rbLogToLog);

		// create graph options panel
		JPanel graphOptionsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		graphOptionsPanel.add(ckShowGrid, c);
		c.insets = new Insets(0, 0, 4, 0);
		graphOptionsPanel.add(ckAutoWindow, c);

		// create window dimensions panel
		dimPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		c1.weightx = 0;
		c1.insets = new Insets(2, 10, 0, 0);
		c1.anchor = GridBagConstraints.EAST;

		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.weightx = 1;
		c2.insets = c1.insets;
		c2.anchor = GridBagConstraints.WEST;

		// x dimensions
		dimPanel.add(lblXMin, c1);
		dimPanel.add(fldXMin, c2);

		c1.gridy++;
		c2.gridy++;
		dimPanel.add(lblXMax, c1);
		dimPanel.add(fldXMax, c2);

		c1.gridy++;
		c2.gridy++;
		dimPanel.add(lblXInterval, c1);
		dimPanel.add(fldXInterval, c2);

		// y dimensions
		c1.insets.top += 8; // add vertical gap
		c1.gridy++;
		c2.gridy++;
		dimPanel.add(lblYMin, c1);
		dimPanel.add(fldYMin, c2);
		c1.insets.top -= 8; // remove vertical gap

		c1.gridy++;
		c2.gridy++;
		dimPanel.add(lblYMax, c1);
		dimPanel.add(fldYMax, c2);

		c1.gridy++;
		c2.gridy++;
		dimPanel.add(lblYInterval, c1);
		dimPanel.add(fldYInterval, c2);

		// create coordinate mode panel
		coordPanel = new JPanel(new GridBagLayout());
		coordPanel.add(rbStandToStand, c);
		coordPanel.add(rbLogToStand, c);
		coordPanel.add(rbStandToLog, c);
		coordPanel.add(rbLogToLog, c);

		// put the sub-panels together
		Box vBox = Box.createVerticalBox();
		vBox.add(graphOptionsPanel);
		vBox.add(dimPanel);
		if (app.has(Feature.LOG_AXES)) {
			vBox.add(coordPanel);
		}

		graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(vBox, BorderLayout.NORTH);
		graphPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	private static JComponent insetPanel(int inset, JComponent... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		p.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return p;
	}

	@Override
	public void setLabels() {

		// titled borders
		classesPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Classes")));
		showPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Show")));
		freqPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("FrequencyType")));
		dimPanel.setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("Dimensions")));
		coordPanel.setBorder(BorderFactory
				.createTitledBorder(loc.getMenu("Coordinate Mode")));

		// histogram options
		ckManual.setText(loc.getMenu("SetClasssesManually"));
		lblFreqType.setText(loc.getMenu("FrequencyType") + ":");

		rbFreq.setText(loc.getMenu("Count"));
		rbNormalized.setText(loc.getMenu("Normalized"));
		rbRelative.setText(loc.getMenu("Relative"));

		ckCumulative.setText(loc.getMenu("Cumulative"));
		lblOverlay.setText(loc.getMenu("Overlay"));
		ckOverlayNormal.setText(loc.getMenu("NormalCurve"));
		ckOverlayPolygon.setText(loc.getMenu("FrequencyPolygon"));
		ckShowFrequencyTable.setText(loc.getMenu("FrequencyTable"));
		ckShowHistogram.setText(loc.getMenu("Histogram"));

		lblClassRule.setText(loc.getMenu("ClassRule") + ":");
		rbRightRule.setText(SpreadsheetViewInterface.RIGHT_CLASS_RULE);
		rbLeftRule.setText(SpreadsheetViewInterface.LEFT_CLASS_RULE);

		// bar chart
		lblBarWidth.setText(loc.getMenu("Width"));
		ckAutoBarWidth.setText(loc.getMenu("AutoDimension"));

		// graph options
		ckAutoWindow.setText(loc.getMenu("AutoDimension"));
		ckShowGrid.setText(loc.getMenu("ShowGrid"));
		lblXMin.setText(loc.getMenu("xmin") + ":");
		lblXMax.setText(loc.getMenu("xmax") + ":");
		lblYMin.setText(loc.getMenu("ymin") + ":");
		lblYMax.setText(loc.getMenu("ymax") + ":");

		lblXInterval.setText(loc.getMenu("xstep") + ":");
		lblYInterval.setText(loc.getMenu("ystep") + ":");

		rbStandToStand.setText(loc.getMenu("Standard To Standard"));
		rbLogToStand.setText(loc.getMenu("Logarithmic To Standard"));
		rbStandToLog.setText(loc.getMenu("Standard To Logarithmic"));
		rbLogToLog.setText(loc.getMenu("Logarithmic To Logarithmic"));

		// scatterplot options
		ckShowLines.setText(loc.getMenu("LineGraph"));

		// boxplot options
		ckShowOutliers.setText(loc.getMenu("ShowOutliers"));

		repaint();
	}

	private void updateGUI() {

		// set updating flag so we don't have to add/remove action listeners
		isUpdating = true;

		// histogram/barchart
		ckManual.setSelected(settings.isUseManualClasses());
		rbFreq.setSelected(
				settings.getFrequencyType() == StatPanelSettings.TYPE_COUNT);
		rbRelative.setSelected(
				settings.getFrequencyType() == StatPanelSettings.TYPE_RELATIVE);
		rbNormalized.setSelected(settings
				.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
		rbLeftRule.setSelected(settings.isLeftRule());
		ckCumulative.setSelected(settings.isCumulative());
		ckOverlayNormal.setSelected(settings.isHasOverlayNormal());
		ckOverlayPolygon.setSelected(settings.isHasOverlayPolygon());
		ckShowGrid.setSelected(settings.showGrid);
		ckAutoWindow.setSelected(settings.isAutomaticWindow());
		ckShowFrequencyTable.setSelected(settings.isShowFrequencyTable());
		ckShowHistogram.setSelected(settings.isShowHistogram());

		if (settings.dataSource != null) {
			ckManual.setVisible(
					settings.getDataSource().getGroupType() != GroupType.CLASS);
			freqPanel.setVisible(settings.getDataSource()
					.getGroupType() == GroupType.RAWDATA);
		}
		// normal overlay
		ckOverlayNormal.setEnabled(settings
				.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);

		// bar chart width
		ckAutoBarWidth.setSelected(settings.isAutomaticBarWidth());
		fldBarWidth.setText("" + settings.getBarWidth());
		fldBarWidth.setEnabled(!ckAutoBarWidth.isSelected());

		// window dimension
		lblYMin.setVisible(showYAxisSettings);
		fldYMin.setVisible(showYAxisSettings);
		lblYMax.setVisible(showYAxisSettings);
		fldYMax.setVisible(showYAxisSettings);
		lblYInterval.setVisible(showYAxisSettings);
		fldYInterval.setVisible(showYAxisSettings);

		dimPanel.setEnabled(!ckAutoWindow.isSelected());
		fldXMin.setEnabled(!ckAutoWindow.isSelected());
		fldXMax.setEnabled(!ckAutoWindow.isSelected());
		fldXInterval.setEnabled(!ckAutoWindow.isSelected());
		fldYMin.setEnabled(!ckAutoWindow.isSelected());
		fldYMax.setEnabled(!ckAutoWindow.isSelected());
		fldYInterval.setEnabled(!ckAutoWindow.isSelected());

		lblXMin.setEnabled(!ckAutoWindow.isSelected());
		lblXMax.setEnabled(!ckAutoWindow.isSelected());
		lblXInterval.setEnabled(!ckAutoWindow.isSelected());
		lblYMin.setEnabled(!ckAutoWindow.isSelected());
		lblYMax.setEnabled(!ckAutoWindow.isSelected());
		lblYInterval.setEnabled(!ckAutoWindow.isSelected());

		// coordinate mode
		rbStandToStand.setSelected(settings
				.getCoordMode() == StatPanelSettings.CoordMode.STANDTOSTAND);
		rbLogToStand.setSelected(settings
				.getCoordMode() == StatPanelSettings.CoordMode.LOGTOSTAND);
		rbStandToLog.setSelected(settings
				.getCoordMode() == StatPanelSettings.CoordMode.STANDTOLOG);
		rbLogToLog.setSelected(settings
				.getCoordMode() == StatPanelSettings.CoordMode.LOGTOLOG);

		// update automatic dimensions
		fldXMin.setText("" + daModel.format(settings.xMin));
		fldXMax.setText("" + daModel.format(settings.xMax));
		fldXInterval.setText("" + daModel.format(settings.xAxesInterval));

		fldYMin.setText("" + daModel.format(settings.yMin));
		fldYMax.setText("" + daModel.format(settings.yMax));
		fldYInterval.setText("" + daModel.format(settings.yAxesInterval));

		// show outliers
		ckShowOutliers.setSelected(settings.isShowOutliers());

		isUpdating = false;
		repaint();
	}

	private void doTextFieldActionPerformed(JTextField source) {
		if (isUpdating) {
			return;
		}
		try {
			String inputText = source.getText().trim();
			NumberValue nv;
			nv = app.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			double value = nv.getDouble();

			// TODO better validation

			if (source == fldXMin) {
				settings.xMin = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldXMax) {
				settings.xMax = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYMax) {
				settings.yMax = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYMin) {
				settings.yMin = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldXInterval && value >= 0) {
				settings.xAxesInterval = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYInterval && value >= 0) {
				settings.yAxesInterval = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldBarWidth && value >= 0) {
				settings.setBarWidth(value);
				firePropertyChange("settings", true, false);
			}
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (isUpdating) {
			return;
		}

		Object source = e.getSource();
		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == ckManual) {
			settings.setUseManualClasses(ckManual.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckCumulative) {
			settings.setCumulative(ckCumulative.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == rbFreq) {
			settings.setFrequencyType(StatPanelSettings.TYPE_COUNT);
			firePropertyChange("settings", true, false);
		} else if (source == rbRelative) {
			settings.setFrequencyType(StatPanelSettings.TYPE_RELATIVE);
			firePropertyChange("settings", true, false);
		} else if (source == rbNormalized) {
			settings.setFrequencyType(StatPanelSettings.TYPE_NORMALIZED);
			firePropertyChange("settings", true, false);
		} else if (source == ckOverlayNormal) {
			settings.setHasOverlayNormal(ckOverlayNormal.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckOverlayPolygon) {
			settings.setHasOverlayPolygon(ckOverlayPolygon.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowGrid) {
			settings.showGrid = ckShowGrid.isSelected();
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoWindow) {
			settings.setAutomaticWindow(ckAutoWindow.isSelected());
			settings.xAxesIntervalAuto = ckAutoWindow.isSelected();
			settings.yAxesIntervalAuto = ckAutoWindow.isSelected();
			firePropertyChange("settings", true, false);
		} else if (source == ckShowFrequencyTable) {
			settings.setShowFrequencyTable(ckShowFrequencyTable.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowHistogram) {
			settings.setShowHistogram(ckShowHistogram.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == rbLeftRule || source == rbRightRule) {
			settings.setLeftRule(rbLeftRule.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowLines) {
			settings.setShowScatterplotLine(ckShowLines.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowOutliers) {
			settings.setShowOutliers(ckShowOutliers.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoBarWidth) {
			settings.setAutomaticBarWidth(ckAutoBarWidth.isSelected());
			firePropertyChange("settings", true, false);
		} else if (source == rbStandToStand) {
			settings.setCoordMode(StatPanelSettings.CoordMode.STANDTOSTAND);
			firePropertyChange("settings", true, false);
		} else if (source == rbLogToStand) {
			settings.setCoordMode(StatPanelSettings.CoordMode.LOGTOSTAND);
			firePropertyChange("settings", true, false);
		} else if (source == rbStandToLog) {
			settings.setCoordMode(StatPanelSettings.CoordMode.STANDTOLOG);
			firePropertyChange("settings", true, false);
		} else if (source == rbLogToLog) {
			settings.setCoordMode(StatPanelSettings.CoordMode.LOGTOLOG);
			firePropertyChange("settings", true, false);
		} else {
			firePropertyChange("settings", true, false);
		}

		updateGUI();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			((JTextField) e.getSource()).selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) (e.getSource()));
		}
	}

	@Override
	public void updateFonts(Font font) {
		setLabels();
	}

	@Override
	public void updatePanel() {
		// TODO Auto-generated method stub

	}

}

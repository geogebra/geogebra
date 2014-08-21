package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import geogebra.common.gui.view.data.StatPanelSettings;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * JPanel to display settings options for a ComboStatPanel
 * 
 * @author G. Sturr
 * 
 */
public class OptionsPanelW extends FlowPanel implements
/*		ActionListener, FocusListener,*/ StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;

	private AppW app;
	private DataAnalysisViewW statDialog;
	private StatPanelSettings settings;

	// histogram panel GUI
	private CheckBox ckCumulative, ckManual, ckOverlayNormal,
			ckOverlayPolygon, ckShowFrequencyTable, ckShowHistogram;
	private RadioButton rbRelative, rbNormalized, rbFreq, rbLeftRule,
			rbRightRule;
	private Label lblFreqType, lblOverlay, lblClassRule;
	private FlowPanel freqPanel, showPanel, dimPanel;
	private Label lbClassTitle, lbFreqTitle, lbShowTitle, lbDimTitle;
	// graph panel GUI
	private CheckBox ckAutoWindow, ckShowGrid;
	private Label lblXMin, lblXMax, lblYMin, lblYMax, lblXInterval,
			lblYInterval;
	private AutoCompleteTextFieldW fldXMin, fldXMax, fldYMin, fldYMax, fldXInterval,
			fldYInterval;
	private boolean showYAxisSettings = true;

	// bar chart panel GUI
	private Label lblBarWidth;
	private AutoCompleteTextFieldW fldBarWidth;
	private CheckBox ckAutoBarWidth;
	private FlowPanel barChartWidthPanel;

	// box plot panel GUI
	private CheckBox ckShowOutliers;

	// scatterplot panel GUI
	private CheckBox ckShowLines;

	// panels
	private FlowPanel histogramPanel, graphPanel, classesPanel, scatterplotPanel,
			barChartPanel, boxPlotPanel;
	private FlowPanel mainPanel;
	private TabPanel tabPanel;

	// misc fields
	private static final int tab = 5;
	private boolean isUpdating = false;

	private DataAnalysisModel daModel;

	private final static int fieldWidth = 8;

	/************************************************************
	 * Constructs an OptionPanel
	 * 
	 * @param app
	 *            App
	 * @param settings
	 * @param statDialog
	 *            statDialog
	 * @param settings
	 *            settings
	 */
	public OptionsPanelW(AppW app, DataAnalysisModel model,
			StatPanelSettings settings) {

		this.app = app;
		this.daModel = model;
		this.settings = settings;

		// create option panels
		createHistogramPanel();
		createGraphPanel();
		createScatterplotPanel();
		createBarChartPanel();
		createBoxPlotPanel();

		mainPanel = new FlowPanel();
		mainPanel.add(histogramPanel);
//		mainPanel.add(scatterplotPanel);
//		mainPanel.add(barChartPanel);
//		mainPanel.add(boxPlotPanel);

		tabPanel = new TabPanel();
		add(tabPanel);
//		this.setLayout(new BorderLayout());
//		this.add(tabPanel, BorderLayout.CENTER);
//		this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
//				SystemColor.controlShadow));

		// update
		setLabels();
		updateGUI();

		// this.setPreferredSize(tabbedPane.getPreferredSize());


	}

	public void setPanel(PlotType plotType) {

		tabPanel.clear();
		this.setVisible(true);

//		// add plot-specific tab
		String tabTitle = plotType.getTranslatedKey(app);
		ScrollPanel spHistogram = new ScrollPanel();
		spHistogram.add(mainPanel);
		tabPanel.add(spHistogram, tabTitle);
		//		tabPanel.insertTab(tabTitle, null, new JScrollPane(mainPanel), null,
//				0);
//		classesPanel.setVisible(false);
//		histogramPanel.setVisible(false);
//		scatterplotPanel.setVisible(false);
//		barChartPanel.setVisible(false);
//		boxPlotPanel.setVisible(false);
//
//		rbNormalized.setVisible(false);
//		ckOverlayNormal.setVisible(false);
//		ckShowHistogram.setVisible(false);
//		ckCumulative.setVisible(false);
//		ckOverlayPolygon.setVisible(false);
//
//		// add graph tab
		ScrollPanel spGraph = new ScrollPanel();
		spGraph.add(graphPanel);
		tabPanel.add(spGraph, app.getMenu("Graph"));
		tabPanel.selectTab(0);
		//		graphPanel.setVisible(true);
//		showYAxisSettings = true;
//
//		// set visibility for plot-specific panels
//		switch (plotType) {
//
//		case HISTOGRAM:
//			classesPanel.setVisible(true);
//			histogramPanel.setVisible(true);
//			rbNormalized.setVisible(true);
//			ckOverlayNormal.setVisible(true);
//			ckShowHistogram.setVisible(true);
//			ckCumulative.setVisible(true);
//			ckOverlayPolygon.setVisible(true);
//
//			layoutHistogramPanel();
//
//			break;
//
//		case BOXPLOT:
//		case MULTIBOXPLOT:
//			boxPlotPanel.setVisible(true);
//			break;
//
//		case BARCHART:
//			barChartPanel.setVisible(true);
//			layoutBarChartPanel();
//			break;
//
//		case SCATTERPLOT:
//			scatterplotPanel.setVisible(true);
//			break;
//
//		// graph tab only
//		case DOTPLOT:
//		case NORMALQUANTILE:
//		case RESIDUAL:
//			tabPanel.removeTabAt(0);
//			break;
//
//		case STEMPLOT:
//			this.setVisible(false);
//			break;
//
//		}
//
		setLabels();
		updateGUI();
	}

	private void createHistogramPanel() {
		histogramPanel = new FlowPanel();
		// create components
		ckCumulative = new CheckBox();

		lblFreqType = new Label();
		
		lbClassTitle = new Label();
		lbClassTitle.setStyleName("panelTitle");
		
		lbFreqTitle = new Label();
		lbFreqTitle.setStyleName("panelTitle");
	
		lbShowTitle = new Label();
		lbShowTitle.setStyleName("panelTitle");
	
		lbDimTitle = new Label();
		lbDimTitle.setStyleName("panelTitle");
		
		rbFreq = new RadioButton("group1");

		rbNormalized = new RadioButton("group1");

		rbRelative = new RadioButton("group1");
		lblOverlay = new Label();
		ckOverlayNormal = new CheckBox();
		
		ckOverlayPolygon = new CheckBox();
		
		ckShowFrequencyTable = new CheckBox();
		
		ckShowHistogram = new CheckBox();
		
		ckManual = new CheckBox();
		
		lblClassRule = new Label();
		rbLeftRule = new RadioButton("rule");
		rbRightRule = new RadioButton("rule");

//		GridBagConstraints c = new GridBagConstraints();
//		c.gridx = 0;
//		c.weightx = 1;
//		c.anchor = GridBagConstraints.LINE_START;

		// tab = tab-like constraint
//		GridBagConstraints tab = (GridBagConstraints) c.clone();
//		tab.insets = new Insets(0, 20, 0, 0);

		// create frequency type panel
		freqPanel = new FlowPanel();
		freqPanel.add(lbFreqTitle);
		freqPanel.add(ckCumulative);
		freqPanel.add(LayoutUtil.panelRowIndent(rbFreq));
		freqPanel.add(LayoutUtil.panelRowIndent(rbRelative));
		freqPanel.add(LayoutUtil.panelRowIndent(rbNormalized));

		// create show panel
		showPanel = new FlowPanel();
		showPanel.add(lbShowTitle);
		showPanel.add(LayoutUtil.panelRowIndent(ckShowHistogram));
		showPanel.add(LayoutUtil.panelRowIndent(ckShowFrequencyTable));
		showPanel.add(LayoutUtil.panelRowIndent(ckOverlayPolygon));
		showPanel.add(LayoutUtil.panelRowIndent(ckOverlayNormal));

		// create classes panel
		classesPanel = new FlowPanel();
		classesPanel.setStyleName("daOptionsGroup");
		classesPanel.add(lbClassTitle);
		classesPanel.add(LayoutUtil.panelRowIndent(ckManual));
		classesPanel.add(lblClassRule);
		classesPanel.add(LayoutUtil.panelRowIndent(rbLeftRule));
		classesPanel.add(LayoutUtil.panelRowIndent(rbRightRule));
		layoutHistogramPanel();

	}

	private void layoutHistogramPanel() {

//		Box vBox = Box.createVerticalBox();
//		vBox.add(classesPanel);
//		vBox.add(freqPanel);
//		vBox.add(showPanel);
//
		if (histogramPanel == null) {
			histogramPanel = new FlowPanel();
		}
//		histogramPanel.removeAll();
//		histogramPanel.add(vBox, BorderLayout.NORTH);
//		histogramPanel.setBorder(BorderFactory.createEmptyBorder());
		FlowPanel p = new FlowPanel();
		p.add(classesPanel);
		p.add(freqPanel);
		p.add(showPanel);
		histogramPanel.add(p);
	}

	private void layoutBarChartPanel() {

//		Box vBox = Box.createVerticalBox();
//		vBox.add(barChartWidthPanel);
//		// vBox.add(freqPanel);
//		vBox.add(showPanel);
//
//		if (barChartPanel == null) {
//			barChartPanel = new JPanel(new BorderLayout());
//		}
//		barChartPanel.removeAll();
//		barChartPanel.add(vBox, BorderLayout.NORTH);
//		barChartPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	private void createBarChartPanel() {

		// create components
//		ckAutoBarWidth = new CheckBox();
//		ckAutoBarWidth.addActionListener(this);
//		lblBarWidth = new Label();
//		fldBarWidth = new MyTextField(app, fieldWidth);
//		fldBarWidth.setEditable(true);
//		fldBarWidth.addActionListener(this);
//		fldBarWidth.addFocusListener(this);
//
//		// barChartWidthPanel
//		barChartWidthPanel = new JPanel();
//		barChartWidthPanel.setLayout(new BoxLayout(barChartWidthPanel,
//				BoxLayout.Y_AXIS));
//		barChartWidthPanel.add(LayoutUtil.flowPanel(ckAutoBarWidth));
//		barChartWidthPanel.add(LayoutUtil.flowPanel(tab, lblBarWidth,
//				fldBarWidth));
//
//		layoutBarChartPanel();

	}

	private void createBoxPlotPanel() {

		// create components
//		ckShowOutliers = new CheckBox();
//		ckShowOutliers.addActionListener(this);
//
//		// layout
//		Box p = Box.createVerticalBox();
//		p.add(LayoutUtil.flowPanel(ckShowOutliers));
//
//		boxPlotPanel = new JPanel(new BorderLayout());
//		boxPlotPanel.add(p, BorderLayout.NORTH);

	}

	private void createScatterplotPanel() {

		// create components
//		ckShowLines = new CheckBox();
//		ckShowLines.addActionListener(this);
//
//		// layout
//		Box p = Box.createVerticalBox();
//		p.add(insetPanel(tab, ckShowLines));
//
//		scatterplotPanel = new JPanel(new BorderLayout());
//		scatterplotPanel.add(p, BorderLayout.NORTH);
	}

	private void createGraphPanel() {

		// create components
//		ckAutoWindow = new CheckBox();
//		ckAutoWindow.addActionListener(this);
//
//		ckShowGrid = new CheckBox();
//		ckShowGrid.addActionListener(this);
//
//		lblXMin = new Label();
//		fldXMin = new MyTextField(app, fieldWidth);
//		fldXMin.setEditable(true);
//		fldXMin.addActionListener(this);
//		fldXMin.addFocusListener(this);
//
//		lblXMax = new Label();
//		fldXMax = new MyTextField(app, fieldWidth);
//		fldXMax.addActionListener(this);
//		fldXMax.addFocusListener(this);
//
//		lblYMin = new Label();
//		fldYMin = new MyTextField(app, fieldWidth);
//		fldYMin.addActionListener(this);
//		fldYMin.addFocusListener(this);
//
//		lblYMax = new Label();
//		fldYMax = new MyTextField(app, fieldWidth);
//		fldYMax.addActionListener(this);
//		fldYMax.addFocusListener(this);
//
//		lblXInterval = new Label();
//		fldXInterval = new MyTextField(app, fieldWidth);
//		fldXInterval.addActionListener(this);
//		fldXInterval.addFocusListener(this);
//
//		lblYInterval = new Label();
//		fldYInterval = new MyTextField(app, fieldWidth);
//		fldYInterval.addActionListener(this);
//		fldYInterval.addFocusListener(this);
//
//		// create graph options panel
//		JPanel graphOptionsPanel = new JPanel(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		c.gridx = 0;
//		c.weightx = 1;
//		c.anchor = GridBagConstraints.LINE_START;
//		graphOptionsPanel.add(ckShowGrid, c);
//		c.insets = new Insets(0, 0, 4, 0);
//		graphOptionsPanel.add(ckAutoWindow, c);
//
//		// create window dimensions panel
//		dimPanel = new JPanel(new GridBagLayout());
//		GridBagConstraints c1 = new GridBagConstraints();
//		c1.gridx = 0;
//		c1.gridy = 0;
//		c1.weightx = 0;
//		c1.insets = new Insets(2, 10, 0, 0);
//		c1.anchor = GridBagConstraints.EAST;
//
//		GridBagConstraints c2 = new GridBagConstraints();
//		c2.gridx = 1;
//		c2.gridy = 0;
//		c2.weightx = 1;
//		c2.insets = c1.insets;
//		c2.anchor = GridBagConstraints.WEST;
//
//		// x dimensions
//		dimPanel.add(lblXMin, c1);
//		dimPanel.add(fldXMin, c2);
//
//		c1.gridy++;
//		c2.gridy++;
//		dimPanel.add(lblXMax, c1);
//		dimPanel.add(fldXMax, c2);
//
//		c1.gridy++;
//		c2.gridy++;
//		dimPanel.add(lblXInterval, c1);
//		dimPanel.add(fldXInterval, c2);
//
//		// y dimensions
//		c1.insets.top += 8; // add vertical gap
//		c1.gridy++;
//		c2.gridy++;
//		dimPanel.add(lblYMin, c1);
//		dimPanel.add(fldYMin, c2);
//		c1.insets.top -= 8; // remove vertical gap
//
//		c1.gridy++;
//		c2.gridy++;
//		dimPanel.add(lblYMax, c1);
//		dimPanel.add(fldYMax, c2);
//
//		c1.gridy++;
//		c2.gridy++;
//		dimPanel.add(lblYInterval, c1);
//		dimPanel.add(fldYInterval, c2);
//
//		// put the sub-panels together
//		Box vBox = Box.createVerticalBox();
//		vBox.add(graphOptionsPanel);
//		vBox.add(dimPanel);
//
		graphPanel = new FlowPanel();
		graphPanel.add(new Label("GraphPanel is comming soon"));

	}


	public void setLabels() {
//
//		// titled borders
		lbClassTitle.setText(app.getMenu("Classes"));
		lbShowTitle.setText(app.getMenu("Show"));
		lbFreqTitle.setText(app.getMenu("FrequencyType"));
		lbDimTitle.setText(app.getPlain("Dimensions"));
//
//		// histogram options
		ckManual.setText(app.getMenu("SetClasssesManually"));
		lblFreqType.setText(app.getMenu("FrequencyType") + ":");
//
		rbFreq.setText(app.getMenu("Count"));
		rbNormalized.setText(app.getMenu("Normalized"));
		rbRelative.setText(app.getMenu("Relative"));

		ckCumulative.setText(app.getMenu("Cumulative"));
		lblOverlay.setText(app.getMenu("Overlay"));
		ckOverlayNormal.setText(app.getMenu("NormalCurve"));
		ckOverlayPolygon.setText(app.getMenu("FrequencyPolygon"));
		ckShowFrequencyTable.setText(app.getMenu("FrequencyTable"));
		ckShowHistogram.setText(app.getMenu("Histogram"));

		lblClassRule.setText(app.getMenu("ClassRule") + ":");
		rbRightRule.setText(app.getMenu("RightClassRule"));
		rbLeftRule.setText(app.getMenu("LeftClassRule"));
//
//		// bar chart
//		lblBarWidth.setText(app.getMenu("Width"));
//		ckAutoBarWidth.setText(app.getMenu("AutoDimension"));
//
//		// graph options
//		ckAutoWindow.setText(app.getMenu("AutoDimension"));
//		ckShowGrid.setText(app.getPlain("ShowGrid"));
//		lblXMin.setText(app.getPlain("xmin") + ":");
//		lblXMax.setText(app.getPlain("xmax") + ":");
//		lblYMin.setText(app.getPlain("ymin") + ":");
//		lblYMax.setText(app.getPlain("ymax") + ":");
//
//		lblXInterval.setText(app.getPlain("xstep") + ":");
//		lblYInterval.setText(app.getPlain("ystep") + ":");
//
//		// scatterplot options
//		ckShowLines.setText(app.getMenu("LineGraph"));
//
//		// boxplot options
//		ckShowOutliers.setText(app.getPlain("ShowOutliers"));
//
//		repaint();
	}

	private void updateGUI() {
//
//		// set updating flag so we don't have to add/remove action listeners
//		isUpdating = true;
//
//		// histogram/barchart
//		ckManual.setSelected(settings.isUseManualClasses());
//		rbFreq.setSelected(settings.getFrequencyType() == StatPanelSettings.TYPE_COUNT);
//		rbRelative
//				.setSelected(settings.getFrequencyType() == StatPanelSettings.TYPE_RELATIVE);
//		rbNormalized
//				.setSelected(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
//		rbLeftRule.setSelected(settings.isLeftRule());
//		ckCumulative.setSelected(settings.isCumulative());
//		ckOverlayNormal.setSelected(settings.isHasOverlayNormal());
//		ckOverlayPolygon.setSelected(settings.isHasOverlayPolygon());
//		ckShowGrid.setSelected(settings.showGrid);
//		ckAutoWindow.setSelected(settings.isAutomaticWindow());
//		ckShowFrequencyTable.setSelected(settings.isShowFrequencyTable());
//		ckShowHistogram.setSelected(settings.isShowHistogram());
//
//		if (settings.dataSource != null) {
//			ckManual.setVisible(settings.getDataSource().getGroupType() != GroupType.CLASS);
//			freqPanel
//					.setVisible(settings.getDataSource().getGroupType() == GroupType.RAWDATA);
//		}
//		// normal overlay
//		ckOverlayNormal
//				.setEnabled(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
//
//		// bar chart width
//		ckAutoBarWidth.setSelected(settings.isAutomaticBarWidth());
//		fldBarWidth.setText("" + settings.getBarWidth());
//		fldBarWidth.setEnabled(!ckAutoBarWidth.isSelected());
//
//		// window dimension
//		lblYMin.setVisible(showYAxisSettings);
//		fldYMin.setVisible(showYAxisSettings);
//		lblYMax.setVisible(showYAxisSettings);
//		fldYMax.setVisible(showYAxisSettings);
//		lblYInterval.setVisible(showYAxisSettings);
//		fldYInterval.setVisible(showYAxisSettings);
//
//		dimPanel.setEnabled(!ckAutoWindow.isSelected());
//		fldXMin.setEnabled(!ckAutoWindow.isSelected());
//		fldXMax.setEnabled(!ckAutoWindow.isSelected());
//		fldXInterval.setEnabled(!ckAutoWindow.isSelected());
//		fldYMin.setEnabled(!ckAutoWindow.isSelected());
//		fldYMax.setEnabled(!ckAutoWindow.isSelected());
//		fldYInterval.setEnabled(!ckAutoWindow.isSelected());
//
//		lblXMin.setEnabled(!ckAutoWindow.isSelected());
//		lblXMax.setEnabled(!ckAutoWindow.isSelected());
//		lblXInterval.setEnabled(!ckAutoWindow.isSelected());
//		lblYMin.setEnabled(!ckAutoWindow.isSelected());
//		lblYMax.setEnabled(!ckAutoWindow.isSelected());
//		lblYInterval.setEnabled(!ckAutoWindow.isSelected());
//
//		// update automatic dimensions
//		fldXMin.setText("" + daModel.format(settings.xMin));
//		fldXMax.setText("" + daModel.format(settings.xMax));
//		fldXInterval.setText("" + daModel.format(settings.xAxesInterval));
//
//		fldYMin.setText("" + daModel.format(settings.yMin));
//		fldYMax.setText("" + daModel.format(settings.yMax));
//		fldYInterval.setText("" + daModel.format(settings.yAxesInterval));
//
//		// show outliers
//		ckShowOutliers.setSelected(settings.isShowOutliers());
//
//		isUpdating = false;
//		repaint();
	}


	public void updatePanel() {
		// TODO Auto-generated method stub

	}


}

package geogebra.web.gui.view.data;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataDisplayModel;
import geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import geogebra.common.gui.view.data.DataVariable.GroupType;
import geogebra.common.gui.view.data.StatPanelSettings;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class OptionsPanelW extends FlowPanel implements ClickHandler, BlurHandler,
	StatPanelInterfaceW {
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

	private DataDisplayModel dyModel;

	private final static int fieldWidth = 8;

	private class PropertyChangeHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
	       actionPerformed(event.getSource());
        }
		
	}
	
	private class PropertyKeyHandler implements KeyHandler {
		private Object source;
		public PropertyKeyHandler(Object source) {
			this.source = source;
		}
		public void keyReleased(KeyEvent e) {
	        if (e.isEnterKey()) {
	        	actionPerformed(source);
	        }
        }
	}
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
			DataDisplayModel dyModel) {

		this.app = app;
		this.daModel = model;
		this.dyModel = dyModel;
		this.settings = dyModel.getSettings();

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
		mainPanel.setStyleName("daScrollPanel");
		spHistogram.add(mainPanel);
		tabPanel.add(spHistogram, tabTitle);
		classesPanel.setVisible(false);
		histogramPanel.setVisible(false);
//		scatterplotPanel.setVisible(false);
//		barChartPanel.setVisible(false);
//		boxPlotPanel.setVisible(false);

		rbNormalized.setVisible(false);
		ckOverlayNormal.setVisible(false);
		ckShowHistogram.setVisible(false);
		ckCumulative.setVisible(false);
		ckOverlayPolygon.setVisible(false);

		// add graph tab
		ScrollPanel spGraph = new ScrollPanel();
		spGraph.setStyleName("daScrollPanel");
		spGraph.add(graphPanel);
		tabPanel.add(spGraph, app.getMenu("Graph"));
		tabPanel.selectTab(0);
		graphPanel.setVisible(true);
		showYAxisSettings = true;
//
//		// set visibility for plot-specific panels
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
		}

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

		PropertyChangeHandler handler = new PropertyChangeHandler();
		ckManual.addClickHandler(handler);
		ckCumulative.addClickHandler(handler);
		ckShowHistogram.addClickHandler(handler);
		ckOverlayPolygon.addClickHandler(handler);
		rbFreq.addClickHandler(handler);
		rbRelative.addClickHandler(handler);
		rbNormalized.addClickHandler(handler);
		rbLeftRule.addClickHandler(handler);
		rbRightRule.addClickHandler(handler);

	}

	private void layoutHistogramPanel() {
		if (histogramPanel == null) {
			histogramPanel = new FlowPanel();
		}

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
		ckAutoWindow = new CheckBox();
		ckAutoWindow.addClickHandler(this);

		ckShowGrid = new CheckBox();
		ckShowGrid.addClickHandler(this);

		lblXMin = new Label();
		fldXMin = new AutoCompleteTextFieldW(fieldWidth, app);
		fldXMin.setEditable(true);
		fldXMin.addKeyHandler(new PropertyKeyHandler(fldXMin));
		fldXMin.addBlurHandler(this);

		lblXMax = new Label();
		fldXMax = new AutoCompleteTextFieldW(fieldWidth, app);
		fldXMax.addKeyHandler(new PropertyKeyHandler(fldXMax));
		fldXMax.addBlurHandler(this);

		lblYMin = new Label();
		fldYMin = new AutoCompleteTextFieldW(fieldWidth, app);
		fldYMin.addKeyHandler(new PropertyKeyHandler(fldYMin));
		fldYMin.addBlurHandler(this);

		lblYMax = new Label();
		fldYMax = new AutoCompleteTextFieldW(fieldWidth, app);
		fldYMax.addKeyHandler(new PropertyKeyHandler(fldYMax));
		fldYMax.addBlurHandler(this);

		lblXInterval = new Label();
		fldXInterval = new AutoCompleteTextFieldW(fieldWidth, app);
		fldXInterval.addKeyHandler(new PropertyKeyHandler(fldXInterval));
		fldXInterval.addBlurHandler(this);

		lblYInterval = new Label();
		fldYInterval = new AutoCompleteTextFieldW(fieldWidth, app);
		fldYInterval.addKeyHandler(new PropertyKeyHandler(fldYInterval));
		fldYInterval.addBlurHandler(this);

		// create graph options panel
		FlowPanel graphOptionsPanel = new FlowPanel();
		graphOptionsPanel.add(ckShowGrid);
		graphOptionsPanel.add(ckAutoWindow);

		// create window dimensions panel
		dimPanel = new FlowPanel();
		dimPanel.add(LayoutUtil.panelRow(lblXMin, fldXMin));
		dimPanel.add(LayoutUtil.panelRow(lblXMax, fldXMax));
		dimPanel.add(LayoutUtil.panelRow(lblXInterval, fldXInterval));

		// y dimensions
		dimPanel.add(LayoutUtil.panelRow(lblYMin, fldYMin));
		dimPanel.add(LayoutUtil.panelRow(lblYMax, fldYMax));
		dimPanel.add(LayoutUtil.panelRow(lblYInterval, fldYInterval));

		// put the sub-panels together
		graphPanel = new FlowPanel();
		graphPanel.add(graphOptionsPanel);
		graphPanel.add(dimPanel);
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
		ckAutoWindow.setText(app.getMenu("AutoDimension"));
		ckShowGrid.setText(app.getPlain("ShowGrid"));
		lblXMin.setText(app.getPlain("xmin") + ":");
		lblXMax.setText(app.getPlain("xmax") + ":");
		lblYMin.setText(app.getPlain("ymin") + ":");
		lblYMax.setText(app.getPlain("ymax") + ":");

		lblXInterval.setText(app.getPlain("xstep") + ":");
		lblYInterval.setText(app.getPlain("ystep") + ":");
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
		ckManual.setValue(settings.isUseManualClasses());
		rbFreq.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_COUNT);
		rbRelative
				.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_RELATIVE);
		rbNormalized
				.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
		rbLeftRule.setValue(settings.isLeftRule());
		ckCumulative.setValue(settings.isCumulative());
		ckOverlayNormal.setValue(settings.isHasOverlayNormal());
		ckOverlayPolygon.setValue(settings.isHasOverlayPolygon());
//		ckShowGrid.setValue(settings.showGrid);
//		ckAutoWindow.setValue(settings.isAutomaticWindow());
//		ckShowFrequencyTable.setValue(settings.isShowFrequencyTable());
		ckShowHistogram.setValue(settings.isShowHistogram());
//
		if (settings.dataSource != null) {
			ckManual.setVisible(settings.getDataSource().getGroupType() != GroupType.CLASS);
			freqPanel
					.setVisible(settings.getDataSource().getGroupType() == GroupType.RAWDATA);
		}
		// normal overlay
		ckOverlayNormal
				.setEnabled(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);

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


	public void actionPerformed(Object source) {

		if (isUpdating)
			return;

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == ckManual) {
			settings.setUseManualClasses(ckManual.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckCumulative) {
			settings.setCumulative(ckCumulative.getValue());
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
			settings.setHasOverlayNormal(ckOverlayNormal.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckOverlayPolygon) {
			settings.setHasOverlayPolygon(ckOverlayPolygon.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowGrid) {
			settings.showGrid = ckShowGrid.getValue();
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoWindow) {
			settings.setAutomaticWindow(ckAutoWindow.getValue());
			settings.xAxesIntervalAuto = ckAutoWindow.getValue();
			settings.yAxesIntervalAuto = ckAutoWindow.getValue();
			firePropertyChange("settings", true, false);
		} else if (source == ckShowFrequencyTable) {
			settings.setShowFrequencyTable(ckShowFrequencyTable.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowHistogram) {
			settings.setShowHistogram(ckShowHistogram.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == rbLeftRule || source == rbRightRule) {
			settings.setLeftRule(rbLeftRule.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowLines) {
			settings.setShowScatterplotLine(ckShowLines.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowOutliers) {
			settings.setShowOutliers(ckShowOutliers.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoBarWidth) {
			settings.setAutomaticBarWidth(ckAutoBarWidth.getValue());
			firePropertyChange("settings", true, false);
		} else {
			firePropertyChange("settings", true, false);
		}

		updateGUI();
	}


	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
	    // TODO Auto-generated method stub
	    
    }

	private void firePropertyChange(String string, boolean b, boolean c) {
	    dyModel.updatePlot(true);
    }

	public void updatePanel() {
		// TODO Auto-generated method stub

	}

	public void onBlur(BlurEvent event) {
	       actionPerformed(event.getSource());
	    
    }

	public void onClick(ClickEvent event) {
	       actionPerformed(event.getSource());

    }

}

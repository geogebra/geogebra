package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatPanelSettings;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * JPanel to display settings options for a ComboStatPanel
 * 
 * @author G. Sturr
 * 
 */
public class OptionsPanelW extends FlowPanel
		implements ClickHandler, BlurHandler, StatPanelInterfaceW {

	private AppW app;
	private StatPanelSettings settings;

	// histogram panel GUI
	private ComponentCheckbox ckCumulative;
	private ComponentCheckbox ckManual;
	private ComponentCheckbox ckOverlayNormal;
	private ComponentCheckbox ckOverlayPolygon;
	private ComponentCheckbox ckShowFrequencyTable;
	private ComponentCheckbox ckShowHistogram;
	private RadioButton rbRelative;
	private RadioButton rbNormalized;
	private RadioButton rbFreq;
	private RadioButton rbLeftRule;
	private RadioButton rbRightRule;
	private Label lblFreqType;
	private Label lblOverlay;
	private Label lblClassRule;
	private FlowPanel freqPanel;
	private FlowPanel showPanel;
	private Label lbClassTitle;
	private Label lbFreqTitle;
	private Label lbShowTitle;
	private Label lbDimTitle;
	// graph panel GUI
	private ComponentCheckbox ckAutoWindow;
	private ComponentCheckbox ckShowGrid;
	private Label lblXMin;
	private Label lblXMax;
	private Label lblYMin;
	private Label lblYMax;
	private Label lblXInterval;
	private Label lblYInterval;
	AutoCompleteTextFieldW fldXMin;
	AutoCompleteTextFieldW fldXMax;

	private AutoCompleteTextFieldW fldYMin;
	private AutoCompleteTextFieldW fldYMax;
	private AutoCompleteTextFieldW fldXInterval;
	private AutoCompleteTextFieldW fldYInterval;
	private boolean showYAxisSettings = true;

	// bar chart panel GUI
	private Label lblBarWidth;
	private AutoCompleteTextFieldW fldBarWidth;
	private ComponentCheckbox ckAutoBarWidth;
	private FlowPanel barChartWidthPanel;

	// box plot panel GUI
	private ComponentCheckbox ckShowOutliers;

	// scatterplot panel GUI
	private ComponentCheckbox ckShowLines;

	// panels
	private FlowPanel histogramPanel;
	private FlowPanel graphPanel;
	private FlowPanel classesPanel;
	private FlowPanel scatterplotPanel;
	private FlowPanel barChartPanel;
	private FlowPanel boxPlotPanel;
	private FlowPanel mainPanel;
	private TabPanel tabPanel;

	private boolean isUpdating = false;

	private DataAnalysisModel daModel;

	private DataDisplayModel dyModel;

	private ScrollPanel spHistogram;

	private ScrollPanel spGraph;
	private ListBox cbLogAxes;
	private Localization loc;

	private final static int FIELD_WIDTH = 8;

	private class PropertyChangeHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			actionPerformed(event.getSource());
		}

	}

	private class PropertyKeyHandler implements KeyHandler {
		private Object source;

		public PropertyKeyHandler(Object source) {
			this.source = source;
		}

		@Override
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
	 *            Application
	 * @param model
	 *            data model
	 * @param dyModel
	 *            display model
	 */
	public OptionsPanelW(AppW app, DataAnalysisModel model,
			DataDisplayModel dyModel) {

		this.app = app;
		this.loc = app.getLocalization();
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
		mainPanel.add(scatterplotPanel);
		mainPanel.add(barChartPanel);
		mainPanel.add(boxPlotPanel);

		tabPanel = new TabPanel();
		tabPanel.setStyleName("daOptionsTabPanel");
		add(tabPanel);

		// update
		setLabels();
		updateGUI();
	}

	/**
	 * Update panel for given plot type.
	 * 
	 * @param plotType
	 *            plot type
	 */
	public void setPanel(PlotType plotType) {

		tabPanel.clear();
		this.setVisible(true);

		// add plot-specific tab
		String tabTitle = plotType.getTranslatedKey(loc);
		spHistogram = new ScrollPanel();
		mainPanel.setStyleName("daScrollPanel");
		spHistogram.add(mainPanel);
		tabPanel.add(spHistogram, tabTitle);
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
		spGraph = new ScrollPanel();
		spGraph.setStyleName("daScrollPanel");
		spGraph.add(graphPanel);
		tabPanel.add(spGraph, loc.getMenu("Graph"));
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
			tabPanel.remove(0);
			break;

		case STEMPLOT:
			this.setVisible(false);
			break;

		}

		tabPanel.selectTab(0);
		setLabels();
		updateGUI();
	}

	private void createHistogramPanel() {
		histogramPanel = new FlowPanel();
		// create components
		ckCumulative = new ComponentCheckbox(loc, settings.isCumulative(), "Cumulative",
				(selected) -> {
					settings.setCumulative(selected);
					firePropertyChange();
				});

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
		ckOverlayNormal = new ComponentCheckbox(loc, settings.isHasOverlayNormal(), "NormalCurve",
				(selected) -> {
					settings.setHasOverlayNormal(selected);
					firePropertyChange();
				});

		ckOverlayPolygon = new ComponentCheckbox(loc, settings.isHasOverlayPolygon(),
				"FrequencyPolygon", (selected) -> {
					settings.setHasOverlayPolygon(selected);
					firePropertyChange();
				});

		ckShowFrequencyTable = new ComponentCheckbox(loc, false, "FrequencyTable",
				(selected) -> {
					settings.setShowFrequencyTable(selected);
					firePropertyChange();
				});

		ckShowHistogram = new ComponentCheckbox(loc, settings.isShowHistogram(), "Histogram",
				(selected) -> {
					settings.setShowHistogram(selected);
					firePropertyChange();
				});

		ckManual = new ComponentCheckbox(loc, settings.isUseManualClasses(), "SetClasssesManually",
				(selected) -> {
					settings.setUseManualClasses(selected);
					firePropertyChange();
				});

		lblClassRule = new Label();
		rbLeftRule = new RadioButton("rule");
		rbRightRule = new RadioButton("rule");

		// create frequency type panel
		freqPanel = new FlowPanel();
		freqPanel.add(lbFreqTitle);
		freqPanel.add(ckCumulative);
		freqPanel.add(LayoutUtilW.panelRowIndent(rbFreq));
		freqPanel.add(LayoutUtilW.panelRowIndent(rbRelative));
		freqPanel.add(LayoutUtilW.panelRowIndent(rbNormalized));

		// create show panel
		showPanel = new FlowPanel();
		showPanel.add(lbShowTitle);
		showPanel.add(LayoutUtilW.panelRowIndent(ckShowHistogram));
		showPanel.add(LayoutUtilW.panelRowIndent(ckShowFrequencyTable));
		showPanel.add(LayoutUtilW.panelRowIndent(ckOverlayPolygon));
		showPanel.add(LayoutUtilW.panelRowIndent(ckOverlayNormal));

		// create classes panel
		classesPanel = new FlowPanel();
		classesPanel.setStyleName("daOptionsGroup");
		classesPanel.add(lbClassTitle);
		classesPanel.add(LayoutUtilW.panelRowIndent(ckManual));
		classesPanel.add(lblClassRule);
		classesPanel.add(LayoutUtilW.panelRowIndent(rbLeftRule));
		classesPanel.add(LayoutUtilW.panelRowIndent(rbRightRule));
		layoutHistogramPanel();

		PropertyChangeHandler handler = new PropertyChangeHandler();
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
		if (barChartPanel == null) {
			barChartPanel = new FlowPanel();
		}
		barChartPanel.clear();
		barChartPanel.add(barChartWidthPanel);
		barChartPanel.add(showPanel);
	}

	private void createBarChartPanel() {
		ckAutoBarWidth = new ComponentCheckbox(loc, true, "AutoDimension",
				(selected) -> {
					settings.setAutomaticBarWidth(selected);
					firePropertyChange();
				});
		lblBarWidth = new Label();
		fldBarWidth = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		fldBarWidth.setEditable(true);
		fldBarWidth.enableGGBKeyboard();
		fldBarWidth.addKeyHandler(new PropertyKeyHandler(fldBarWidth));
		fldBarWidth.addBlurHandler(this);

		// barChartWidthPanel
		barChartWidthPanel = new FlowPanel();
		barChartWidthPanel.add(ckAutoBarWidth);
		barChartWidthPanel.add(LayoutUtilW.panelRow(lblBarWidth, fldBarWidth));

		layoutBarChartPanel();

	}

	private void createBoxPlotPanel() {

		// create components
		ckShowOutliers = new ComponentCheckbox(loc, settings.isShowOutliers(), "ShowOutliers",
				(selected) -> {
					settings.setShowOutliers(selected);
					firePropertyChange();
				});

		// layout

		boxPlotPanel = new FlowPanel();
		boxPlotPanel.add(ckShowOutliers);

	}

	private void createScatterplotPanel() {
		ckShowLines = new ComponentCheckbox(loc, settings.isShowScatterplotLine(), "LineGraph",
				(selected) -> {
					settings.setShowScatterplotLine(selected);
					firePropertyChange();
				});

		scatterplotPanel = new FlowPanel();
		scatterplotPanel.add(ckShowLines);
	}

	private void createGraphPanel() {
		ckAutoWindow = new ComponentCheckbox(loc, settings.isAutomaticWindow(), "AutoDimension",
				(selected) -> {
					settings.setAutomaticWindow(selected);
					settings.xAxesIntervalAuto = selected;
					settings.yAxesIntervalAuto = selected;
					firePropertyChange();
				});

		ckShowGrid = new ComponentCheckbox(loc, settings.showGrid, "ShowGrid",
				(selected) -> {
					settings.showGrid = selected;
					firePropertyChange();
				});

		lblXMin = new Label();
		fldXMin = InputPanelW.newTextComponent(app);
		fldXMin.setEditable(true);
		fldXMin.enableGGBKeyboard();
		fldXMin.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				actionPerformed(fldXMin);
			}
		});
		fldXMin.addBlurHandler(event -> actionPerformed(fldXMin));

		lblXMax = new Label();
		fldXMax = InputPanelW.newTextComponent(app);
		fldXMax.enableGGBKeyboard();
		fldXMax.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				actionPerformed(fldXMax);
			}
		});
		fldXMax.addBlurHandler(event -> actionPerformed(fldXMax));

		lblYMin = new Label();
		fldYMin = InputPanelW.newTextComponent(app);
		fldYMin.enableGGBKeyboard();
		fldYMin.addKeyHandler(new PropertyKeyHandler(fldYMin));
		fldYMin.addBlurHandler(this);

		lblYMax = new Label();
		fldYMax = InputPanelW.newTextComponent(app);
		fldYMax.enableGGBKeyboard();
		fldYMax.addKeyHandler(new PropertyKeyHandler(fldYMax));
		fldYMax.addBlurHandler(this);

		lblXInterval = new Label();
		fldXInterval = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		fldXInterval.enableGGBKeyboard();
		fldXInterval.addKeyHandler(new PropertyKeyHandler(fldXInterval));
		fldXInterval.addBlurHandler(this);

		lblYInterval = new Label();
		fldYInterval = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		fldYInterval.enableGGBKeyboard();
		fldYInterval.addKeyHandler(new PropertyKeyHandler(fldYInterval));
		fldYInterval.addBlurHandler(this);

		// create graph options panel
		FlowPanel graphOptionsPanel = new FlowPanel();
		graphOptionsPanel.add(ckShowGrid);
		graphOptionsPanel.add(ckAutoWindow);

		// create window dimensions panel
		FlowPanel dimPanel = new FlowPanel();
		dimPanel.add(LayoutUtilW.panelRow(lblXMin, fldXMin));
		dimPanel.add(LayoutUtilW.panelRow(lblXMax, fldXMax));
		dimPanel.add(LayoutUtilW.panelRow(lblXInterval, fldXInterval));

		// y dimensions
		dimPanel.add(LayoutUtilW.panelRow(lblYMin, fldYMin));
		dimPanel.add(LayoutUtilW.panelRow(lblYMax, fldYMax));
		dimPanel.add(LayoutUtilW.panelRow(lblYInterval, fldYInterval));

		cbLogAxes = new ListBox();

		// put the sub-panels together
		graphPanel = new FlowPanel();
		graphPanel.add(graphOptionsPanel);
		graphPanel.add(dimPanel);
		if (app.has(Feature.LOG_AXES)) {
			cbLogAxes.addItem("Standard To Standard");
			cbLogAxes.addItem("Logarithmic To Standard");
			cbLogAxes.addItem("Standard To Logarithmic");
			cbLogAxes.addItem("Logarithmic To Logarithmic");
			cbLogAxes.addChangeHandler(event -> onComboBoxChange());
			FlowPanel modePanel = new FlowPanel();
			modePanel.add(cbLogAxes);
			graphPanel.add(modePanel);
		}
	}

	protected void onComboBoxChange() {
		int index = cbLogAxes.getSelectedIndex();
		settings.setCoordMode(StatPanelSettings.CoordMode.values()[index]);
		this.firePropertyChange();
		updateGUI();

	}

	@Override
	public void setLabels() {
		// titled borders
		lbClassTitle.setText(loc.getMenu("Classes"));
		lbShowTitle.setText(loc.getMenu("Show"));
		lbFreqTitle.setText(loc.getMenu("FrequencyType"));
		lbDimTitle.setText(loc.getMenu("Dimensions"));

		// histogram options
		ckManual.setLabels();
		lblFreqType.setText(loc.getMenu("FrequencyType") + ":");

		rbFreq.setText(loc.getMenu("Count"));
		rbNormalized.setText(loc.getMenu("Normalized"));
		rbRelative.setText(loc.getMenu("Relative"));

		ckCumulative.setLabels();
		lblOverlay.setText(loc.getMenu("Overlay"));
		ckOverlayNormal.setLabels();
		ckOverlayPolygon.setLabels();
		ckShowFrequencyTable.setLabels();
		ckShowHistogram.setLabels();

		lblClassRule.setText(loc.getMenu("ClassRule") + ":");
		rbRightRule.setText(SpreadsheetViewInterface.RIGHT_CLASS_RULE);
		rbLeftRule.setText(SpreadsheetViewInterface.LEFT_CLASS_RULE);

		// bar chart
		lblBarWidth.setText(loc.getMenu("Width"));
		ckAutoBarWidth.setLabels();

		// graph options
		ckAutoWindow.setLabels();
		ckShowGrid.setLabels();
		lblXMin.setText(loc.getMenu("xmin") + ":");
		lblXMax.setText(loc.getMenu("xmax") + ":");
		lblYMin.setText(loc.getMenu("ymin") + ":");
		lblYMax.setText(loc.getMenu("ymax") + ":");

		lblXInterval.setText(loc.getMenu("xstep") + ":");
		lblYInterval.setText(loc.getMenu("ystep") + ":");

		// scatterplot options
		ckShowLines.setLabels();

		// boxplot options
		ckShowOutliers.setLabels();

	}

	private void updateGUI() {
		// set updating flag so we don't have to add/remove action listeners
		isUpdating = true;

		// histogram/barchart
		ckManual.setSelected(settings.isUseManualClasses());
		rbFreq.setValue(
				settings.getFrequencyType() == StatPanelSettings.TYPE_COUNT);
		rbRelative.setValue(
				settings.getFrequencyType() == StatPanelSettings.TYPE_RELATIVE);
		rbNormalized.setValue(settings
				.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
		rbLeftRule.setValue(settings.isLeftRule());
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
		ckOverlayNormal.setDisabled(settings
				.getFrequencyType() != StatPanelSettings.TYPE_NORMALIZED);

		// bar chart width
		ckAutoBarWidth.setSelected(settings.isAutomaticBarWidth());
		fldBarWidth.setText("" + settings.getBarWidth());
		fldBarWidth.setEditable(!ckAutoBarWidth.isSelected());

		// window dimension
		lblYMin.setVisible(showYAxisSettings);
		fldYMin.setVisible(showYAxisSettings);
		lblYMax.setVisible(showYAxisSettings);
		fldYMax.setVisible(showYAxisSettings);
		lblYInterval.setVisible(showYAxisSettings);
		fldYInterval.setVisible(showYAxisSettings);

		fldXMin.setEditable(!ckAutoWindow.isSelected());
		fldXMax.setEditable(!ckAutoWindow.isSelected());
		fldXInterval.setEditable(!ckAutoWindow.isSelected());
		fldYMin.setEditable(!ckAutoWindow.isSelected());
		fldYMax.setEditable(!ckAutoWindow.isSelected());
		fldYInterval.setEditable(!ckAutoWindow.isSelected());

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
	}

	/**
	 * @param source
	 *            event source
	 */
	public void actionPerformed(Object source) {

		if (isUpdating) {
			return;
		}

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		} else if (source == rbFreq) {
			settings.setFrequencyType(StatPanelSettings.TYPE_COUNT);
			firePropertyChange();
		} else if (source == rbRelative) {
			settings.setFrequencyType(StatPanelSettings.TYPE_RELATIVE);
			firePropertyChange();
		} else if (source == rbNormalized) {
			settings.setFrequencyType(StatPanelSettings.TYPE_NORMALIZED);
			firePropertyChange();
		} else if (source == rbLeftRule || source == rbRightRule) {
			settings.setLeftRule(rbLeftRule.getValue());
			firePropertyChange();
		} else {
			firePropertyChange();
		}

		updateGUI();
	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
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
				firePropertyChange();
			} else if (source == fldXMax) {
				settings.xMax = value;
				firePropertyChange();
			} else if (source == fldYMax) {
				settings.yMax = value;
				firePropertyChange();
			} else if (source == fldYMin) {
				settings.yMin = value;
				firePropertyChange();
			} else if (source == fldXInterval && value >= 0) {
				settings.xAxesInterval = value;
				firePropertyChange();
			} else if (source == fldYInterval && value >= 0) {
				settings.yAxesInterval = value;
				firePropertyChange();
			} else if (source == fldBarWidth && value >= 0) {
				settings.setBarWidth(value);
				firePropertyChange();
			}
			updateGUI();

		} catch (NumberFormatException e) {
			Log.debug(e);
		}
	}

	private void firePropertyChange() {
		dyModel.updatePlot(true);
	}

	@Override
	public void updatePanel() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBlur(BlurEvent event) {
		actionPerformed(event.getSource());
	}

	@Override
	public void onClick(ClickEvent event) {
		actionPerformed(event.getSource());
	}

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void resize(int width, int height) {
		spHistogram.setHeight(height + "px");
		spGraph.setHeight(height + "px");
		if (width > 0) {
			tabPanel.setWidth(width + "px");
		}
	}

}

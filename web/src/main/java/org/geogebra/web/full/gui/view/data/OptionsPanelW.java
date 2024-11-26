package org.geogebra.web.full.gui.view.data;

import java.util.Arrays;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatPanelSettings;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.TabPanel;

/**
 * JPanel to display settings options for a ComboStatPanel
 * 
 * @author G. Sturr
 * 
 */
public class OptionsPanelW extends FlowPanel
		implements StatPanelInterfaceW {

	private final AppW app;
	private final StatPanelSettings settings;

	// histogram panel GUI
	private ComponentCheckbox ckCumulative;
	private ComponentCheckbox ckManual;
	private ComponentCheckbox ckOverlayNormal;
	private ComponentCheckbox ckOverlayPolygon;
	private ComponentCheckbox ckShowFrequencyTable;
	private ComponentCheckbox ckShowHistogram;
	private RadioButtonPanel<Integer> freqRadioButtonPanel;
	private RadioButtonPanel<Boolean> classRadioButtonPanel;
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
	private final FlowPanel mainPanel;
	private final TabPanel tabPanel;

	private boolean isUpdating = false;

	private final DataAnalysisModel daModel;

	private final DataDisplayModel dyModel;

	private ScrollPanel spHistogram;

	private ScrollPanel spGraph;
	private ListBox cbLogAxes;
	private final Localization loc;

	private final static int FIELD_WIDTH = 8;

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
		classesPanel.setVisible(false);
		histogramPanel.setVisible(false);
		scatterplotPanel.setVisible(false);
		barChartPanel.setVisible(false);
		boxPlotPanel.setVisible(false);

		freqRadioButtonPanel.disableNthRadioButton(2, true);
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
		boolean showHistogramTab = true;

		// set visibility for plot-specific panels
		switch (plotType) {

		case HISTOGRAM:
			classesPanel.setVisible(true);
			histogramPanel.setVisible(true);
			freqRadioButtonPanel.disableNthRadioButton(2, false);
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
			showHistogramTab = false;
			break;

		case STEMPLOT:
			this.setVisible(false);
			break;

		}
		if (showHistogramTab) {
			tabPanel.add(spHistogram, tabTitle);
		}
		tabPanel.add(spGraph, loc.getMenu("Graph"));

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
					updateGUI(); // make sure Normal Curve is enabled/disabled
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

		RadioButtonData<Integer> freqData = new RadioButtonData<>("Count",
				StatPanelSettings.TYPE_COUNT);
		RadioButtonData<Integer> relData = new RadioButtonData<>("Relative",
				StatPanelSettings.TYPE_RELATIVE);
		RadioButtonData<Integer> normData = new RadioButtonData<>("Normalized",
				StatPanelSettings.TYPE_NORMALIZED);
		freqRadioButtonPanel = new RadioButtonPanel<>(loc,
				Arrays.asList(freqData, relData, normData), StatPanelSettings.TYPE_COUNT,
				(value) -> {
					settings.setFrequencyType(value);
					firePropertyChange();
					updateGUI(); // make sure Normal Curve is enabled/disabled
				}
		);

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

		ckShowFrequencyTable = new ComponentCheckbox(loc, settings.isShowFrequencyTable(),
				"FrequencyTable", (selected) -> {
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
		RadioButtonData<Boolean> rightData = new RadioButtonData<>(
				SpreadsheetViewInterface.RIGHT_CLASS_RULE, false);
		RadioButtonData<Boolean> leftData = new RadioButtonData<>(
				SpreadsheetViewInterface.LEFT_CLASS_RULE, true);
		classRadioButtonPanel = new RadioButtonPanel<>(loc,
				Arrays.asList(rightData, leftData), settings.isLeftRule(), (isLeft) -> {
			settings.setLeftRule(isLeft);
			firePropertyChange();
		});

		// create frequency type panel
		freqPanel = new FlowPanel();
		freqPanel.add(lbFreqTitle);
		freqPanel.add(ckCumulative);
		freqPanel.add(LayoutUtilW.panelRowIndent(freqRadioButtonPanel));

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
		classesPanel.add(classRadioButtonPanel);
		layoutHistogramPanel();
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
					updateGUI(); // enable bar width
				});
		lblBarWidth = new Label();
		fldBarWidth = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		initHandlers(fldBarWidth);

		// barChartWidthPanel
		barChartWidthPanel = new FlowPanel();
		barChartWidthPanel.add(ckAutoBarWidth);
		barChartWidthPanel.add(LayoutUtilW.panelRow(lblBarWidth, fldBarWidth));

		layoutBarChartPanel();

	}

	private void initHandlers(AutoCompleteTextFieldW input) {
		input.enableGGBKeyboard();
		input.addKeyHandler(evt -> {
			if (evt.isEnterKey()) {
				actionPerformed(input);
			}
		});
		input.addBlurHandler(evt -> actionPerformed(input));
	}

	private void createBoxPlotPanel() {
		ckShowOutliers = new ComponentCheckbox(loc, settings.isShowOutliers(), "ShowOutliers",
				(selected) -> {
					settings.setShowOutliers(selected);
					firePropertyChange();
				});

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
					updateGUI(); // enable/disable dimension fields
				});

		ckShowGrid = new ComponentCheckbox(loc, settings.showGrid, "ShowGrid",
				(selected) -> {
					settings.showGrid = selected;
					firePropertyChange();
				});

		lblXMin = new Label();
		fldXMin = InputPanelW.newTextComponent(app);
		initHandlers(fldXMin);

		lblXMax = new Label();
		fldXMax = InputPanelW.newTextComponent(app);
		initHandlers(fldXMax);

		lblYMin = new Label();
		fldYMin = InputPanelW.newTextComponent(app);
		initHandlers(fldYMin);

		lblYMax = new Label();
		fldYMax = InputPanelW.newTextComponent(app);
		initHandlers(fldYMax);

		lblXInterval = new Label();
		fldXInterval = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		initHandlers(fldXInterval);

		lblYInterval = new Label();
		fldYInterval = new AutoCompleteTextFieldW(FIELD_WIDTH, app);
		initHandlers(fldYInterval);

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
		if (PreviewFeature.isAvailable(PreviewFeature.LOG_AXES)) {
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

		freqRadioButtonPanel.setLabels();
		classRadioButtonPanel.setLabels();

		ckCumulative.setLabels();
		lblOverlay.setText(loc.getMenu("Overlay"));
		ckOverlayNormal.setLabels();
		ckOverlayPolygon.setLabels();
		ckShowFrequencyTable.setLabels();
		ckShowHistogram.setLabels();

		lblClassRule.setText(loc.getMenu("ClassRule") + ":");

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
		ckOverlayNormal.setDisabled(!settings.isOverlayEnabled());

		// bar chart width
		ckAutoBarWidth.setSelected(settings.isAutomaticBarWidth());
		fldBarWidth.setText(daModel.format(settings.getBarWidth()));
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
		fldXMin.setText(daModel.format(settings.xMin));
		fldXMax.setText(daModel.format(settings.xMax));
		fldXInterval.setText(daModel.format(settings.xAxesInterval));

		fldYMin.setText(daModel.format(settings.yMin));
		fldYMax.setText(daModel.format(settings.yMax));
		fldYInterval.setText(daModel.format(settings.yAxesInterval));

		// show outliers
		ckShowOutliers.setSelected(settings.isShowOutliers());

		isUpdating = false;
	}

	/**
	 * @param source
	 *            event source
	 */
	public void actionPerformed(AutoCompleteTextFieldW source) {
		if (isUpdating) {
			return;
		}
		doTextFieldActionPerformed(source);
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
			boolean valid = true;
			if (source == fldXMin) {
				settings.xMin = value;
			} else if (source == fldXMax) {
				settings.xMax = value;
			} else if (source == fldYMax) {
				settings.yMax = value;
			} else if (source == fldYMin) {
				settings.yMin = value;
			} else if (source == fldXInterval && value >= 0) {
				settings.xAxesInterval = value;
			} else if (source == fldYInterval && value >= 0) {
				settings.yAxesInterval = value;
			} else if (source == fldBarWidth && value >= 0) {
				settings.setBarWidth(value);
			} else {
				valid = false;
			}
			if (valid) {
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

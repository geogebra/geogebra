package org.geogebra.web.full.gui.view.probcalculator;

import java.util.HashMap;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ProbabilityModeGroup;
import org.geogebra.web.full.gui.util.ToggleButton;
import org.geogebra.web.full.gui.view.data.PlotPanelEuclidianViewW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * ProbablityCalculatorView for web
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalculatorView
		implements ChangeHandler, InsertHandler {

	/**
	 * separator for list boxes
	 */
	public static final String SEPARATOR = "--------------------";
	private static final int CONTROL_PANEL_HEIGHT = 150;
	private static final int TABLE_PADDING_AND_SCROLLBAR = 32;

	private Label lblDist;
	private ToggleButton btnCumulative;
	private ProbabilityModeGroup modeGroup;
	private Label[] lblParameterArray;
	private MathTextFieldW[] fldParameterArray;
	private ListBox comboDistribution;
	private Label lblMeanSigma;
	/** control panel */
	FlowPanel controlPanel;
	/** export action */
	ScheduledCommand exportToEVAction;
	/** plot panel */
	FlowPanel plotPanelPlus;
	private FlowPanel plotSplitPane;
	private FlowPanel mainSplitPane;
	private FlowPanel probCalcPanel;
	private final StatisticsCalculatorW statCalculator;
	private MyTabLayoutPanel tabbedPane;
	private HandlerRegistration comboDistributionHandler;
	private GPopupMenuW btnExport;
	private ToggleButton btnNormalOverlay;
	private ToggleButton btnLineGraph;
	private ToggleButton btnStepGraph;
	private ToggleButton btnBarGraph;
	private ResultPanelW resultPanel;

	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public ProbabilityCalculatorViewW(AppW app) {
		super(app);

		tabbedPane = new MyTabLayoutPanel(30, Unit.PX);
		tabbedPane.addStyleName("PropabilityCalculatorViewW");

		createGUIElements();
		createExportToEvAction();
		createLayoutPanels();
		buildProbCalcPanel();
		isIniting = false;

		statCalculator = new StatisticsCalculatorW(app);

		tabbedPane = new MyTabLayoutPanel(30, Unit.PX);
		tabbedPane.addStyleName("probCalcViewTab");
		tabbedPane.add(probCalcPanel, loc.getMenu("Distribution"));
		tabbedPane.add(statCalculator.getWrappedPanel(),
				loc.getMenu("Statistics"));

		tabbedPane.onResize();

		setLabels();

		attachView();
		settingsChanged(getApp().getSettings().getProbCalcSettings());

		tabbedPane.selectTab(getApp().getSettings().getProbCalcSettings()
				.getCollection().isActive() ? 1 : 0);
	}

	@Override
	public void setLabels() {
		tabbedPane.setTabText(0, loc.getMenu("Distribution"));

		statCalculator.setLabels();
		tabbedPane.setTabText(1, loc.getMenu("Statistics"));

		setLabelArrays();

		lblDist.setText(loc.getMenu("Distribution") + ": ");
		resultPanel.setLabels();

		setDistributionComboBoxMenu();

		if (getTable() != null) {
			getTable().setLabels();
		}

		btnCumulative.setTitle(loc.getMenu("Cumulative"));

		modeGroup.setLabels();

		btnLineGraph.setTitle(loc.getMenu("LineGraph"));
		btnStepGraph.setTitle(loc.getMenu("StepGraph"));
		btnBarGraph.setTitle(loc.getMenu("BarChart"));

		btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));
		for (int i = 0; i < ProbabilityManager.getParmCount(selectedDist); i++) {
			lblParameterArray[i]
					.setText(getParameterLabels()[selectedDist.ordinal()][i]);
		}
	}

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 *
	 * This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.
	 */
	private void createExportToEvAction() {
		exportToEVAction = new ScheduledCommand() {
			
			private final HashMap<String, Object> value = new HashMap<>();
			
			public Object getValue(String key) {
				return value.get(key);
			}
			
			public void putValue(String key, Object val) {
				this.value.put(key, val);
			}
			
			@Override
			public void execute() {
				Integer euclidianViewID = (Integer) this
						.getValue("euclidianViewID");

				// if null ID then use EV1 unless shift is down, then use EV2
				if (euclidianViewID == null) {
					euclidianViewID = GlobalKeyDispatcherW.getShiftDown()
							? getApp().getEuclidianView2(1).getViewID()
							: getApp().getEuclidianView1().getViewID();
				}

				// do the export
				exportGeosToEV(euclidianViewID);

				// null out the ID property
				this.putValue("euclidianViewID", null);
			}
		};
	}

	private void buildProbCalcPanel() {
		tabbedPane.clear();
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		plotSplitPane.add(controlPanel);
		plotSplitPane.addStyleName("plotSplitPane");
		mainSplitPane = new FlowPanel();
		mainSplitPane.addStyleName("mainSplitPanel");
		mainSplitPane.add(plotSplitPane);

		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		
		probCalcPanel.add(mainSplitPane);
	}

	private void createLayoutPanels() {
		//control panel
		createControlPanel();
		setPlotPanel(new PlotPanelEuclidianViewW(kernel));

		FlowPanel plotPanelOptions = new FlowPanel();
		plotPanelOptions.setStyleName("plotPanelOptions");
		plotPanelOptions.add(lblMeanSigma);
		if (!getApp().isExam() && app.getConfig().getAppCode().equals("classic")) {
			plotPanelOptions.add(btnExport.getPopupMenu());
		}
		plotPanelOptions.add(btnNormalOverlay);
		plotPanelOptions.add(btnBarGraph);
		plotPanelOptions.add(btnStepGraph);
		plotPanelOptions.add(btnLineGraph);
		updateGraphButtons();
		
		plotPanelPlus = new FlowPanel();
		plotPanelPlus.addStyleName("PlotPanelPlus");
		plotPanelPlus.add(plotPanelOptions);
		plotPanelPlus.add(getPlotPanel().getComponent());
		
		//table panel
		setTable(new ProbabilityTableW(app, this));
	}

	private void createControlPanel() {
		//distribution combobox panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.addStyleName("cbPanel");
		cbPanel.add(btnCumulative);
		cbPanel.add(comboDistribution);
		FlowPanel parameterPanel = new FlowPanel();
		parameterPanel.addStyleName("parameterPanel");
		comboDistribution.addStyleName("groupEnd");
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}
		cbPanel.add(parameterPanel);

		modeGroup.add(resultPanel);

		controlPanel = new FlowPanel();
		controlPanel.addStyleName("controlPanel");
		controlPanel.add(cbPanel);
		controlPanel.add(new ClearPanel());
		controlPanel.add(modeGroup);
		controlPanel.add(new ClearPanel());
	}
	
	private static class ClearPanel extends FlowPanel {
		public ClearPanel() {
			super();
			this.setStyleName("clear");
		}
	}

	private void createGUIElements() {
		setLabelArrays();
		resultPanel = new ResultPanelW(app, this);
		comboDistribution = new ListBox();
		comboDistribution.addStyleName("comboDistribution");
		comboDistributionHandler = comboDistribution.addChangeHandler(this);
		
		lblDist = new Label();
		btnCumulative = new ToggleButton(GuiResources.INSTANCE.cumulative_distribution());

		modeGroup = new ProbabilityModeGroup(loc);
		modeGroup.add(PROB_LEFT, GuiResources.INSTANCE.interval_left(), "LeftProb");
		modeGroup.add(PROB_INTERVAL, GuiResources.INSTANCE.interval_between(), "IntervalProb");
		modeGroup.add(PROB_TWO_TAILED, GuiResources.INSTANCE.interval_two_tailed(),
				"TwoTailedProb");
		modeGroup.add(PROB_RIGHT, GuiResources.INSTANCE.interval_right(), "RightProb");
		modeGroup.endGroup();

		btnCumulative.addFastClickHandler((e) -> setCumulative(btnCumulative.isSelected()));
		modeGroup.addFastClickHandler((source) -> {
			if (modeGroup.handle(source) && !isCumulative) {
				changeProbabilityType();
				updateProbabilityType(resultPanel);
				updateGUI();
			}
		});

		lblParameterArray = new Label[maxParameterCount];
		fldParameterArray = new MathTextFieldW[maxParameterCount];
		
		for (int i = 0; i < maxParameterCount; i++) {
			lblParameterArray[i] = new Label();
			fldParameterArray[i] = new MathTextFieldW(app);
			fldParameterArray[i].setPxWidth(64);
			resultPanel.addInsertHandler(fldParameterArray[i]);
			//TODO fldParameterArray[i].getTextBox().setTabIndex(i + 1);
		}

		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");

		createExportMenu();

		btnNormalOverlay = new ToggleButton(GuiResources.INSTANCE.normal_overlay());
		btnNormalOverlay.addStyleName("probCalcStylbarBtn");
		btnNormalOverlay.addFastClickHandler(event -> onOverlayClicked());

		btnLineGraph = new ToggleButton(GuiResources.INSTANCE.line_graph());
		btnLineGraph.addStyleName("probCalcStylbarBtn");
		btnLineGraph.addFastClickHandler(event -> setGraphType(GRAPH_LINE));

		btnStepGraph = new ToggleButton(GuiResources.INSTANCE.step_graph());
		btnStepGraph.addStyleName("probCalcStylbarBtn");
		btnStepGraph.addFastClickHandler(event -> setGraphType(GRAPH_STEP));

		btnBarGraph = new ToggleButton(GuiResources.INSTANCE.bar_chart());
		btnBarGraph.addStyleName("probCalcStylbarBtn");
		btnBarGraph.addFastClickHandler(event -> setGraphType(GRAPH_BAR));
	}
	
	/**
	 * Overlay button action
	 */
	protected void onOverlayClicked() {
		setShowNormalOverlay(btnNormalOverlay.isSelected());
		updateAll(false);
	}

	/**
	 * @return the wrapper panel of this view
	 */
	public TabLayoutPanel getWrapperPanel() {
		return tabbedPane;
	}

	@Override
	public ResultPanelW getResultPanel() {
		return resultPanel;
	}

	@Override
	protected void updateOutput() {
		updateDistribution();
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
	}

	@Override
	protected void changeProbabilityType() {
		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			int oldProbMode = probMode;
			if (oldProbMode == PROB_TWO_TAILED) {
				removeTwoTailedGraph();
			}
			probMode = modeGroup.getValue();

			if (probMode == PROB_TWO_TAILED) {
				addTwoTailedGraph();
			}

			validateLowHigh(oldProbMode);
		}
	}

	@Override
	protected void onDistributionUpdate() {
		btnNormalOverlay.setVisible(isOverlayDefined());
		lblMeanSigma.setText(getMeanSigma());
		getPlotPanel().repaintView();
	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		if (showTable) {
			mainSplitPane
					.add(((ProbabilityTableW) getTable()).getWrappedPanel());
		} else {
			mainSplitPane
					.remove(((ProbabilityTableW) getTable()).getWrappedPanel());
		}
		tabbedPane.onResize();
	}

	@Override
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		getPlotPanel().commonFields
				.updateSettings(getPlotPanel(), plotSettings);
	}

	@Override
	protected void updateDiscreteTable() {
		if (!isDiscreteProbability()) {
			return;
		}
		int[] firstXLastX = generateFirstXLastXCommon();
		getTable().setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
		tabbedPane.onResize();
	}

	@Override
	public PlotPanelEuclidianViewW getPlotPanel() {
		return (PlotPanelEuclidianViewW) super.getPlotPanel();
	}

	@Override
	protected void updateGUI() {
		updateParameters();
		updateLowHighResult();
		updateDistributionCombo();
		updateGraphButtons();
		btnCumulative.setSelected(isCumulative);
		modeGroup.setMode(probMode);
		btnNormalOverlay.setSelected(isShowNormalOverlay());
	}

	private void updateGraphButtons() {
		btnLineGraph.setVisible(getProbManager().isDiscrete(getSelectedDist()));
		btnStepGraph.setVisible(getProbManager().isDiscrete(getSelectedDist()));
		btnBarGraph.setVisible(getProbManager().isDiscrete(getSelectedDist()));

		btnLineGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_LINE);
		btnStepGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_STEP);
		btnBarGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_BAR);
	}

	private void updateDistributionCombo() {
		if (!comboDistribution.getValue(comboDistribution.getSelectedIndex())
				.equals(getDistributionMap().get(selectedDist))) {
			ListBoxApi.select(
					getDistributionMap().get(selectedDist), comboDistribution);
		}
	}

	private void updateParameters() {
		for (int i = 0; i < maxParameterCount; ++i) {

			boolean hasParm = i < ProbabilityManager.getParmCount(selectedDist);

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			if (hasParm) {
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i].setText(getParameterLabels()[selectedDist
						.ordinal()][i]);
				// set field
				fldParameterArray[i].setText(format(parameters[i]));
			}
		}
	}

	private void updateLowHighResult() {
		tabbedPane.deferredOnResize();
		updateResult(resultPanel);
	}

	private void updateLowHigh() {
		resultPanel.updateLowHigh(format(low), format(high));
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (comboDistribution.getSelectedIndex() > -1) {
			changeDistribution();
		}
	}

	private void changeDistribution() {
		if (!selectedDist
				.equals(this.getReverseDistributionMap().get(comboDistribution
						.getValue(comboDistribution.getSelectedIndex())))) {
			selectedDist = getReverseDistributionMap().get(comboDistribution
					.getValue(comboDistribution.getSelectedIndex()));
			parameters = ProbabilityManager.getDefaultParameters(selectedDist, cons);
			setProbabilityCalculator(selectedDist, parameters,
					isCumulative);
			tabbedPane.onResize();
		}

	}

	private void setDistributionComboBoxMenu() {
		comboDistributionHandler.removeHandler();
		comboDistribution.clear();
		comboDistribution.addItem(getDistributionMap().get(Dist.NORMAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.STUDENT));
		comboDistribution.addItem(getDistributionMap().get(Dist.CHISQUARE));
		comboDistribution.addItem(getDistributionMap().get(Dist.F));
		comboDistribution.addItem(getDistributionMap().get(Dist.EXPONENTIAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.CAUCHY));
		comboDistribution.addItem(getDistributionMap().get(Dist.WEIBULL));
		comboDistribution.addItem(getDistributionMap().get(Dist.GAMMA));
		comboDistribution.addItem(getDistributionMap().get(Dist.LOGNORMAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.LOGISTIC));
		
		comboDistribution.addItem(SEPARATOR);
		NodeList<OptionElement> options = SelectElement.as(comboDistribution.getElement())
				.getOptions();
		options.getItem(options.getLength() - 1)
				.setAttribute("disabled", "disabled");
		comboDistribution.addItem(getDistributionMap().get(Dist.BINOMIAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.PASCAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.POISSON));
		comboDistribution.addItem(getDistributionMap().get(Dist.HYPERGEOMETRIC));

		ListBoxApi.select(getDistributionMap().get(selectedDist),
				comboDistribution);
		comboDistribution.addChangeHandler(this);
	}

	/**
	 * @return wheter distribution tab is open
	 */
	@Override
	public boolean isDistributionTabOpen() {
		return tabbedPane.getSelectedIndex() == 0;
	}

	/**
	 * @return ProbabilitiManager
	 */
	@Override
	public ProbabilityManager getProbManager() {
		return probManager;
	}

	private class MyTabLayoutPanel extends TabLayoutPanel implements ClickHandler {

		public MyTabLayoutPanel(int splitterSize, Unit px) {
			super(splitterSize, px);
			this.addDomHandler(this, ClickEvent.getType());
		}

		@Override
		public final void onResize() {
			tabResized();
		}

		public void deferredOnResize() {
			Scheduler.get().scheduleDeferred(this::onResize);
		}

		@Override
		public void onClick(ClickEvent event) {
			getApp().setActiveView(App.VIEW_PROBABILITY_CALCULATOR);
		}
	}

	/**
	 * @return plot panel view
	 */
	public EuclidianViewW getPlotPanelEuclidianView() {
		return getPlotPanel();
	}

	/**
	 * Tab resize callback
	 */
	public void tabResized() {
		int tableWidth = isDiscreteProbability() ? ((ProbabilityTableW) getTable()).getStatTable()
				.getTable().getOffsetWidth() + TABLE_PADDING_AND_SCROLLBAR : 0;
		int width = mainSplitPane.getOffsetWidth()
				- tableWidth
				- 5;
		int height = probCalcPanel.getOffsetHeight() - 20;
		if (width > 0) {
			getPlotPanel().setPreferredSize(new Dimension(width,
					Math.min(Math.max(100, height - CONTROL_PANEL_HEIGHT),
							PlotPanelEuclidianViewW.DEFAULT_HEIGHT)));
			getPlotPanel().repaintView();
			getPlotPanel().getEuclidianController().calculateEnvironment();
			plotSplitPane.setWidth(width + "px");
		}

		if (height > 0 && isDiscreteProbability()) {
			((ProbabilityTableW) getTable()).getWrappedPanel()
					.setPixelSize(tableWidth, height);
		}
	}

	@Override
	public void doTextFieldActionPerformed(MathTextFieldW source, boolean intervalCheck) {
		if (isIniting) {
			return;
		}
		String inputText = source.getText().trim();
		boolean update = true;
		if (!"".equals(inputText)) {
			// allow input such as sqrt(2)
			GeoNumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(
					inputText, intervalCheck ? source : ErrorHelper.silent());
			GeoNumberValue numericValue = nv != null
					? nv : new GeoNumeric(cons, Double.NaN);
			double value = numericValue.getDouble();
			if (!Double.isNaN(value)) {
				source.resetError();
			}
			if (resultPanel.isFieldLow(source)) {

				checkBounds(numericValue, intervalCheck, false);
			}

			else if (resultPanel.isFieldHigh(source)) {
				checkBounds(numericValue, intervalCheck, true);
			}

			// handle inverse probability
			else if (resultPanel.isFieldResult(source)) {
				update = false;
				if (value < 0 || value > 1) {
					if (!intervalCheck) {
						updateLowHigh();
						return;
					}
					updateGUI();
				} else {
					if (probMode == PROB_LEFT) {
						setHigh(inverseProbability(value));
					}
					if (probMode == PROB_RIGHT) {
						setLow(inverseProbability(1 - value));
					}
					updateLowHigh();
					setXAxisPoints();
				}
			} else {
				// handle parameter entry
				for (int i = 0; i < parameters.length; ++i) {
					if (source == fldParameterArray[i]) {
						if (isValidParameterChange(value, i)) {
							parameters[i] = numericValue;
							if (intervalCheck) {
								updateAll(true);
							} else {
								updateOutput();
								updateLowHighResult();
							}
						}

					}
				}
			}
			if (intervalCheck) {
				updateIntervalProbability();
				if (update) {
					updateGUI();
				}
			}
		}
	}

	@Override
	public void setInterval(double low, double high) {
		this.setLow(low);
		this.setHigh(high);
		resultPanel.updateLowHigh("" + low, "" + high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
	}
	
	@Override
	public boolean suggestRepaint() {
		return false;
	}

	/**
	 * Resize callback
	 */
	public void onResize() {
		// in most cases it is enough to updatePlotSettings, but when
		// setPersective is called early
		// during Win8 app initialization, we also need to update the tabbed
		// pane and make the whole process deferred
		getApp().invokeLater(() -> {
			tabResized();
			updatePlotSettings();
		});
	}

	private void checkBounds(GeoNumberValue value, boolean intervalCheck, boolean high) {
		boolean valid = high ? isValidInterval(probMode, getLow(), value.getDouble())
				: isValidInterval(probMode, value.getDouble(), getHigh());

		if (valid) {
			if (high) {
				setHigh(value);
			} else {
				setLow(value);
			}
			setXAxisPoints();
		}
		if (intervalCheck) {
			updateGUI();
			if (isTwoTailedMode()) {
				updateGreaterSign(resultPanel);
			}
		} else {
			updateIntervalProbability();
			if (isTwoTailedMode()) {
				resultPanel.updateTwoTailedResult(getProbabilityText(leftProbability),
						getProbabilityText(rightProbability));
				resultPanel.updateResult(getProbabilityText(leftProbability
						+ rightProbability));
				updateGreaterSign(resultPanel);
			} else {
				resultPanel.updateResult(getProbabilityText(probability));
			}
		}
	}

	private void createExportMenu() {
		btnExport = new GPopupMenuW((AppW) app, true) {
			@Override
			public int getPopupLeft() {
				return getPopupMenu().getAbsoluteLeft();
			}
		};
		btnExport.getPopupMenu().addStyleName("probCalcStylbarBtn");

		AriaMenuBar menu = new AriaMenuBar();

		if (!getApp().isApplet()) {
			AriaMenuItem miToGraphich = new AriaMenuItem(
					loc.getMenu("CopyToGraphics"), false,
					() -> exportToEVAction.execute());

			menu.addItem(miToGraphich);
		}
		if (((AppW) app).getLAF().copyToClipboardSupported()) {
			AriaMenuItem miAsPicture = new AriaMenuItem(
					loc.getMenu("ExportAsPicture"), false, () -> {
						String url = getPlotPanel()
								.getExportImageDataUrl(3, true, false);
						((AppW) getApp()).getFileManager()
								.showExportAsPictureDialog(url,
										getApp().getExportTitle(),
										"png", "ExportAsPicture", getApp());
					});
			menu.addItem(miAsPicture);
		}

		String image = "<img src=\""
				+ MaterialDesignResources.INSTANCE.prob_calc_export().getSafeUri()
						.asString()
				+ "\" >";
		btnExport.addItem(new AriaMenuItem(image, true, menu));
		btnExport.getPopupMenu().removeStyleName("gwt-MenuBar");
		btnExport.getPopupMenu().addStyleName("gwt-ToggleButton");
		btnExport.getPopupMenu().addStyleName("MyToggleButton");
	}

	@Override
	public StatisticsCalculator getStatCalculator() {
		return statCalculator;
	}

	/**
	 * @return application
	 */
	protected App getApp() {
		return app;
	}

}

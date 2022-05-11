package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ToggleButton;
import org.geogebra.web.full.gui.view.data.PlotPanelEuclidianViewW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * ProbablityCalculatorView for web
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalculatorView {
	public static final String SEPARATOR = "--------------------";
	private Label lblMeanSigma;
	/** export action */
	ScheduledCommand exportToEVAction;
	/** plot panel */
	FlowPanel plotPanelPlus;
	protected FlowPanel plotSplitPane;
	protected FlowPanel mainSplitPane;
	protected FlowPanel probCalcPanel;
	protected final StatisticsCalculatorW statCalculator;
	private GPopupMenuW btnExport;
	private ToggleButton btnNormalOverlay;
	private ToggleButton btnLineGraph;
	private ToggleButton btnStepGraph;
	private ToggleButton btnBarGraph;

	private DistributionPanel distrPanel;

	/**
	 * @param app creates new probabilitycalculatorView
	 */
	protected ProbabilityCalculatorViewW(AppW app) {
		super(app);
		createGUIElements();
		createExportToEvAction();
		createLayoutPanels();
		buildProbCalcPanel();
		isIniting = false;

		statCalculator = new StatisticsCalculatorW(app);
	}

	/**
	 * Factory method
	 * @param app application
	 * @return new PC view
	 */
	public static ProbabilityCalculatorViewW create(AppW app) {
		ProbabilityCalculatorViewW view = new ProbabilityCalculatorViewW(app);
		view.init();
		return view;
	}

	@Override
	public void setLabels() {
		statCalculator.setLabels();
		setLabelArrays();

		distrPanel.setLabels();

		if (getTable() != null) {
			getTable().setLabels();
		}

		btnLineGraph.setTitle(loc.getMenu("LineGraph"));
		btnStepGraph.setTitle(loc.getMenu("StepGraph"));
		btnBarGraph.setTitle(loc.getMenu("BarChart"));
		if (app.getConfig().hasDistributionView()) {
			AriaHelper.setTitle(btnNormalOverlay, loc.getMenu("OverlayNormalCurve"));
		} else {
			btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));
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
		exportToEVAction = () -> {
			// if null ID then use EV1 unless shift is down, then use EV2
			int euclidianViewID = GlobalKeyDispatcherW.getShiftDown()
						? getApp().getEuclidianView2(1).getViewID()
						: getApp().getEuclidianView1().getViewID();
			// do the export
			exportGeosToEV(euclidianViewID);
		};
	}

	private void buildProbCalcPanel() {
		distrPanel = new DistributionPanel(this, loc);
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		plotSplitPane.add(distrPanel);
		plotSplitPane.addStyleName("plotSplitPane");
		mainSplitPane = new FlowPanel();
		mainSplitPane.addStyleName("mainSplitPanel");
		mainSplitPane.add(plotSplitPane);

		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		probCalcPanel.add(mainSplitPane);
	}

	private void createLayoutPanels() {
		setPlotPanel(new PlotPanelEuclidianViewW(kernel));

		FlowPanel plotPanelOptions = new FlowPanel();
		plotPanelOptions.setStyleName("plotPanelOptions");
		if (!app.getConfig().hasDistributionView()) {
			plotPanelOptions.add(lblMeanSigma);
		}
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

	protected void init() {
		setLabels();
		attachView();
		settingsChanged(getApp().getSettings().getProbCalcSettings());
	}

	private void createGUIElements() {
		setLabelArrays();

		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");

		createExportMenu();

		btnNormalOverlay = new ToggleButton(app.getConfig().hasDistributionView()
				? GuiResources.INSTANCE.normal_overlay_black()
				: GuiResources.INSTANCE.normal_overlay());
		btnNormalOverlay.addStyleName("probCalcStylbarBtn");
		if (app.getConfig().hasDistributionView()) {
			btnNormalOverlay.removeStyleName("MyToggleButton");
			btnNormalOverlay.addStyleName("suite");
		}
		btnNormalOverlay.addFastClickHandler(event -> {
			Dom.toggleClass(btnNormalOverlay, "selected", btnNormalOverlay.isSelected());
			onOverlayClicked();
		});

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
	public Widget getWrapperPanel() {
		return plotPanelPlus;
	}

	@Override
	public ResultPanelW getResultPanel() {
		return distrPanel.getResultPanel();
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
			probMode = distrPanel.getModeGroupValue();

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
		tabResized();
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
		tabResized();
	}

	@Override
	public PlotPanelEuclidianViewW getPlotPanel() {
		return (PlotPanelEuclidianViewW) super.getPlotPanel();
	}

	@Override
	protected void updateGUI() {
		updateLowHighResult();
		distrPanel.updateGUI();
		updateGraphButtons();
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

	/**
	 *update distribution drop-down
	 * @param comboDistribution - distribution drop-down
	 */
	public void updateDistributionCombo(ListBox comboDistribution) {
		if (!comboDistribution.getValue(comboDistribution.getSelectedIndex())
				.equals(getDistributionMap().get(selectedDist))) {
			ListBoxApi.select(
					getDistributionMap().get(selectedDist), comboDistribution);
		}
	}

	/**
	 * update low and high
	 */
	public void updateLowHighResult() {
		Scheduler.get().scheduleDeferred(this::tabResized);
		updateResult(getResultPanel());
	}

	/**
	 * handle distribution selection
	 * @param comboDistribution - distribution drop-down
	 */
	public void changeDistribution(ListBox comboDistribution) {
		if (!selectedDist
				.equals(this.getReverseDistributionMap().get(comboDistribution
						.getValue(comboDistribution.getSelectedIndex())))) {
			selectedDist = getReverseDistributionMap().get(comboDistribution
					.getValue(comboDistribution.getSelectedIndex()));
			parameters = ProbabilityManager.getDefaultParameters(selectedDist, cons);
			setProbabilityCalculator(selectedDist, parameters,
					isCumulative);
			tabResized();
		}
	}

	/**
	 * @return wheter distribution tab is open
	 */
	@Override
	public boolean isDistributionTabOpen() {
		return true;
	}

	/**
	 * @return ProbabilitiManager
	 */
	@Override
	public ProbabilityManager getProbManager() {
		return probManager;
	}

	/**
	 * @return plot panel view
	 */
	public EuclidianViewW getPlotPanelEuclidianView() {
		return getPlotPanel();
	}

	@Override
	public void setInterval(double low, double high) {
		setLow(low);
		setHigh(high);
		getResultPanel().updateLowHigh("" + low, "" + high);
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

	/**
	 * Tab resized callback
	 */
	public void tabResized() {
		int width = plotPanelPlus.getOffsetWidth() - 5;
		int height = plotPanelPlus.getOffsetHeight() - 20;
		if (width > 0) {
			resizePlotPanel(width, height);
		}
	}

	void resizePlotPanel(int width, int height) {
		getPlotPanel().setPreferredSize(new Dimension(width,
				Math.min(Math.max(100, height),
						PlotPanelEuclidianViewW.DEFAULT_HEIGHT)));
		getPlotPanel().repaintView();
		getPlotPanel().getEuclidianController().calculateEnvironment();
		plotSplitPane.setWidth(width + "px");
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

	public GeoNumberValue[] getParameters() {
		return parameters;
	}
}

package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.view.data.PlotPanelEuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AsyncManager;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Probability Calculator View for web
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalculatorView {
	public static final String SEPARATOR = "--------------------";
	/** export action */
	ScheduledCommand exportToEVAction;
	/** plot panel */
	FlowPanel plotPanelPlus;

	protected FlowPanel probCalcPanel;
	private ToggleButton btnNormalOverlay;
	private ToggleButton btnLineGraph;
	private ToggleButton btnStepGraph;
	private ToggleButton btnBarGraph;

	private DistributionPanel distrPanel;
	protected FlowPanel plotPanelOptions;

	/**
	 * @param app creates new probabilitycalculatorView
	 */
	protected ProbabilityCalculatorViewW(AppW app) {
		super(app);
		createGUIElements();
		createExportToEvAction();
		createLayoutPanels();
	}

	/**
	 * Factory method
	 * @param app application
	 * @return new PC view
	 */
	public static ProbabilityCalculatorViewW create(AppW app) {
		ProbabilityCalculatorViewW view = new ProbabilityCalculatorViewW(app);
		view.isIniting = false;
		view.init();
		return view;
	}

	@Override
	public void disableInterval(boolean disable) {
		distrPanel.disableInterval(disable);
	}

	@Override
	public void setLabels() {
		setLabelArrays();
		if (distrPanel != null) {
			distrPanel.setLabels();
		}

		ProbabilityTable table = getTable();
		if (table != null) {
			table.setLabels();
		}

		btnLineGraph.setTitle(loc.getMenu("LineGraph"));
		btnStepGraph.setTitle(loc.getMenu("StepGraph"));
		btnBarGraph.setTitle(loc.getMenu("BarChart"));
		if (app.getConfig().hasDistributionView()) {
			AriaHelper.setTitle(btnNormalOverlay, loc.getMenu("OverlayNormalCurve"));
		} else {
			btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));
		}
		btnNormalOverlay.getElement().setAttribute("tooltip-position", "right");
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
			// do the export, preload Take, Pascal/Binomial, Integral, ...
			AsyncManager manager = ((AppW) app).getAsyncManager();
			manager.prefetch(() -> exportGeosToEV(euclidianViewID),
					"advanced", "stats", "cas");
		};
	}

	private void createLayoutPanels() {
		setPlotPanel(new PlotPanelEuclidianViewW(kernel));

		plotPanelOptions = new FlowPanel();
		plotPanelOptions.setStyleName("plotPanelOptions");

		plotPanelOptions.add(btnNormalOverlay);
		if (!app.getConfig().hasDistributionView()) {
			plotPanelOptions.add(btnBarGraph);
			plotPanelOptions.add(btnStepGraph);
			plotPanelOptions.add(btnLineGraph);
			updateGraphButtons();
		}

		plotPanelPlus = new FlowPanel();
		plotPanelPlus.addStyleName("PlotPanelPlus");
		plotPanelPlus.add(plotPanelOptions);
		plotPanelPlus.add(getPlotPanel().getComponent());
	}

	protected void init() {
		setLabels();
		attachView();
		settingsChanged(getApp().getSettings().getProbCalcSettings());
	}

	private void createGUIElements() {
		setLabelArrays();

		btnNormalOverlay = new ToggleButton(app.getConfig().hasDistributionView()
				? GuiResources.INSTANCE.normal_overlay_black()
				: GuiResources.INSTANCE.normal_overlay());
		btnNormalOverlay.addStyleName("probCalcStylbarBtn");
		if (app.getConfig().hasDistributionView()) {
			btnNormalOverlay.removeStyleName("ToggleButton");
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
		return distrPanel == null ? null : distrPanel.getResultPanel();
	}

	@Override
	protected void updateOutput(boolean updateDistributionView) {
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
		} else if (distrPanel != null) {
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
		getPlotPanel().repaintView();
	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		// TODO APPS-3708
	}

	@Override
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		getPlotPanel().commonFields
				.updateSettings(getPlotPanel(), plotSettings);
	}

	@Override
	public void updateDiscreteTable() {
		if (!isDiscreteProbability() || getTable() == null) {
			return;
		}
		int[] firstXLastX = generateFirstXLastXCommon();
		getTable().setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
		selectProbabilityTableRows();
		tabResized();
	}

	@Override
	public PlotPanelEuclidianViewW getPlotPanel() {
		return (PlotPanelEuclidianViewW) super.getPlotPanel();
	}

	@Override
	protected void updateGUI() {
		updateLowHighResult();
		if (distrPanel != null) {
			distrPanel.updateGUI();
		}
		updateGraphButtons();
		btnNormalOverlay.setSelected(isShowNormalOverlay());
	}

	private void updateGraphButtons() {
		btnLineGraph.setVisible(isDiscreteProbability());
		btnStepGraph.setVisible(isDiscreteProbability());
		btnBarGraph.setVisible(isDiscreteProbability());

		btnLineGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_LINE);
		btnStepGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_STEP);
		btnBarGraph.setSelected(getGraphType()
				== ProbabilityCalculatorView.GRAPH_BAR);
	}

	/**
	 * update low and high
	 */
	public void updateLowHighResult() {
		Scheduler.get().scheduleDeferred(this::tabResized);
		if (getResultPanel() != null) {
			updateResult(getResultPanel());
		}
	}

	/**
	 * @return whether distribution tab is open
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
		// setPerspective is called early
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
		int height = plotPanelPlus.getOffsetHeight();
		if (width > 0) {
			resizePlotPanel(width, height);
		}
	}

	void resizePlotPanel(int width, int maxHeight) {
		int height = maxHeight > PlotPanelEuclidianViewW.DEFAULT_HEIGHT
				? Math.max(PlotPanelEuclidianViewW.DEFAULT_HEIGHT, maxHeight / 2)
				: Math.max(maxHeight, 40);
		getPlotPanel().setPreferredSize(new Dimension(width, height));
		getPlotPanel().getCanvasElement().getStyle().setMarginTop((maxHeight - height) / 2.0,
				Unit.PX);
		getPlotPanel().repaintView();
		getPlotPanel().getEuclidianController().calculateEnvironment();
	}

	@Override
	public StatisticsCalculator getStatCalculator() {
		return null;
	}

	/**
	 * @return application
	 */
	protected App getApp() {
		return app;
	}

	public void setDistributionPanel(DistributionPanel widgets) {
		this.distrPanel = widgets;
	}

	/** table only for discrete distribution
	 * @return whether the selected distribution discrete is
	 */
	public boolean hasTableView() {
		return isDiscreteProbability();
	}

}

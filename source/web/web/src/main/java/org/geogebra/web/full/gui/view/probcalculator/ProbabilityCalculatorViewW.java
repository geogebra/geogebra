/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.probcalculator;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.gui.view.data.PlotPanelEuclidianViewW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
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
	private @CheckForNull IconButton overlayIconButton;
	private @CheckForNull ToggleButton btnNormalOverlay;
	private ToggleButton btnLineGraph;
	private ToggleButton btnStepGraph;
	private ToggleButton btnBarGraph;

	protected FlowPanel plotPanelOptions;

	/**
	 * @param app creates new probability calculator view
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
		view.settingsChanged(app.getSettings().getProbCalcSettings());
		return view;
	}

	@Override
	public void setLabels() {
		setLabelArrays();

		ProbabilityTable table = getTable();
		if (table != null) {
			table.setLabels();
		}

		btnLineGraph.setTitle(loc.getMenu("LineGraph"));
		btnStepGraph.setTitle(loc.getMenu("StepGraph"));
		btnBarGraph.setTitle(loc.getMenu("BarChart"));
		if (overlayIconButton != null) {
			overlayIconButton.setLabels();
		} else if (btnNormalOverlay != null) {
			btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));
			btnNormalOverlay.getElement().setAttribute("tooltip-position", "right");
		}
	}

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 *
	 * <p>This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.</p>
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

		if (!app.getConfig().hasDistributionView()) {
			plotPanelOptions.add(btnNormalOverlay);
			plotPanelOptions.add(btnBarGraph);
			plotPanelOptions.add(btnStepGraph);
			plotPanelOptions.add(btnLineGraph);
			updateGraphButtons();
		} else {
			plotPanelOptions.add(overlayIconButton);
		}

		plotPanelPlus = new FlowPanel();
		plotPanelPlus.addStyleName("PlotPanelPlus");
		plotPanelPlus.add(plotPanelOptions);
		plotPanelPlus.add(getPlotPanel().getComponent());
	}

	protected void init() {
		setLabels();
		attachView();
	}

	private void createGUIElements() {
		setLabelArrays();

		if (app.getConfig().hasDistributionView()) {
			overlayIconButton = new IconButton((AppW) app, null,
					new ImageIconSpec(GuiResources.INSTANCE.normal_overlay_black()),
					"OverlayNormalCurve");
			overlayIconButton.addStyleName("probCalcStylbarBtn");
			overlayIconButton.setTooltipPositionRight();
			overlayIconButton.addFastClickHandler(source -> onOverlayClicked());
			new FocusableWidget(AccessibilityGroup.PROBABILITY_OVERLAY, null, overlayIconButton)
					.attachTo((AppW) app);
		} else {
			btnNormalOverlay = new ToggleButton(GuiResources.INSTANCE.normal_overlay());
			btnNormalOverlay.addStyleName("probCalcStylbarBtn");
			btnNormalOverlay.addFastClickHandler(event -> {
				Dom.toggleClass(btnNormalOverlay, "selected", btnNormalOverlay.isSelected());
				onOverlayClicked();
			});
		}

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
		setShowNormalOverlay(!isShowNormalOverlay());
		updateAll(false);
	}

	/**
	 * @return the wrapper panel of this view
	 */
	public Widget getWrapperPanel() {
		return plotPanelPlus;
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
	protected void onDistributionUpdate() {
		if (overlayIconButton != null) {
			overlayIconButton.setVisible(isOverlayDefined());
		} else if (btnNormalOverlay != null) {
			btnNormalOverlay.setVisible(isOverlayDefined());
		}
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
		updateGraphButtons();
		if (btnNormalOverlay != null) {
			btnNormalOverlay.setSelected(isShowNormalOverlay());
		} else if (overlayIconButton != null) {
			overlayIconButton.setActive(isShowNormalOverlay());
		}
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

	@Override
	public ProbabilityManager getProbManager() {
		return probManager;
	}

	@Override
	public void setInterval(double low, double high) {
		setLow(low);
		setHigh(high);
		if (getResultPanel() != null) {
			getResultPanel().updateLowHigh("" + low, "" + high);
		}
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
		if (app.isUnbundled()) {
			getPlotPanel().getCanvasElement().getStyle().setMarginTop((maxHeight - height) / 2.0,
					Unit.PX);
		}
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

	/** table only for discrete distribution
	 * @return whether the selected distribution discrete is
	 */
	public boolean hasTableView() {
		return isDiscreteProbability();
	}

}

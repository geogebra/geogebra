package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.web.full.gui.view.probcalculator.DistributionPanelSuite;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;

public class DistributionTab extends ToolbarPanel.ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private DistributionPanelSuite distrPanel;

	/**
	 * Constructor
	 * @param toolbarPanel - parent toolbar panel
	 */
	public DistributionTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		createContent();
	}

	private void createContent() {
		ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) toolbarPanel.getApp()
				.getGuiManager().getProbabilityCalculator();
		distrPanel = new DistributionPanelSuite(view, toolbarPanel.getApp().getLocalization());
		distrPanel.setLabels();
		view.setDistributionPanel(distrPanel);
		distrPanel.updateGUI(); // make sure the correct interval is selected
		view.updateProbabilityType(distrPanel.getResultPanel());
		view.updateLowHighResult();
		add(distrPanel);
	}

	@Override
	protected void onActive() {
		// to do fill
	}

	@Override
	public void setLabels() {
		distrPanel.setLabels();
	}

	@Override
	public void open() {
		// to do fill
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}
}

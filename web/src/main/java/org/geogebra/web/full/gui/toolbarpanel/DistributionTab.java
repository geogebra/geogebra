package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.web.full.gui.view.probcalculator.DistributionPanelSuite;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;

public class DistributionTab extends ToolbarPanel.ToolbarTab {

	private final ToolbarPanel toolbarPanel;

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
		ProbabilityCalculatorViewW view =
				(ProbabilityCalculatorViewW) toolbarPanel.getApp().getGuiManager().getProbabilityCalculator();
		add(new DistributionPanelSuite(view, toolbarPanel.getApp().getLocalization()));
	}

	@Override
	protected void onActive() {
		// to do fill
	}

	@Override
	public void setLabels() {
		// to do fill
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

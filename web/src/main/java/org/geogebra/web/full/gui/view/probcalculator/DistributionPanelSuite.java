package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.main.Localization;

public class DistributionPanelSuite extends DistributionPanel {


	public DistributionPanelSuite(
			ProbabilityCalculatorViewW view, Localization loc) {
		super(view, loc);
	}

	@Override
	public void buildGUI() {
		buildDistrComboBox();
	}

	@Override
	public void buildCumulativeWidget() {
		// todo APPS-3712
	}
}

package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.main.Localization;

public class DistributionPanelSuite extends DistributionPanel {

	public DistributionPanelSuite(ProbabilityCalculatorViewW view, Localization loc) {
		super(view, loc);
		addStyleName("suiteDistrTab");
	}

	@Override
	public void buildGUI() {
		buildDistrComboBox(this);
	}

	@Override
	public void initCumulativeWidget() {
		// todo APPS-3712
	}
}

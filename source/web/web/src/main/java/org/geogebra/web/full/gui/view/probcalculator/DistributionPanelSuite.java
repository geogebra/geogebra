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

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.shared.components.ComponentSwitch;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class DistributionPanelSuite extends DistributionPanel {
	private ComponentSwitch cumulativeWidget;
	private Label cumulativeLbl;
	private Label intervalsLbl;

	/**
	 * constructor
	 * @param view - prob calc view
	 * @param loc - localization
	 */
	public DistributionPanelSuite(ProbabilityCalculatorViewW view, Localization loc) {
		super(view, loc);
		addStyleName("suiteDistrTab");
	}

	@Override
	public void buildGUI() {
		buildDistrComboBox(this);
		initCumulativeWidget();
		resultPanel = new ResultPanelW(getView().getApp(), this);
		intervalsLbl = BaseWidgetFactory.INSTANCE.newSecondaryText("", "intervalsLbl");
		add(intervalsLbl);
		buildModeGroupWithResult();
		super.buildParameterPanel(this);
		add(resultPanel);
	}

	@Override
	public void initCumulativeWidget() {
		FlowPanel cumulativeRow = new FlowPanel();
		cumulativeRow.addStyleName("row");
		cumulativeWidget = new ComponentSwitch(false, (source) -> {
			getView().setCumulative(source);
			disableInterval(source);
		});
		cumulativeLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(
				getView().getApp().getLocalization().getMenu("Cumulative"));
		cumulativeRow.add(cumulativeLbl);
		cumulativeRow.add(cumulativeWidget);
		add(cumulativeRow);
	}

	@Override
	public void setLabels() {
		super.setLabels();
		Localization loc = getView().getApp().getLocalization();
		cumulativeWidget.setTitle(loc.getMenu("Cumulative"));
		cumulativeLbl.setText(loc.getMenu("Cumulative"));
		intervalsLbl.setText(loc.getMenu("Intervals"));
	}

	@Override
	protected void updateCumulative() {
		cumulativeWidget.setSwitchOn(getView().isCumulative());
	}
}

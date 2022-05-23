package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.main.Localization;
import org.geogebra.web.shared.components.ComponentSwitch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

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
		intervalsLbl = new Label();
		intervalsLbl.setStyleName("intervalsLbl");
		add(intervalsLbl);
		buildModeGroupWithResult();
		super.buildParameterPanel(this);
	}

	@Override
	public void initCumulativeWidget() {
		FlowPanel cumulativeRow = new FlowPanel();
		cumulativeRow.addStyleName("row");
		cumulativeWidget = new ComponentSwitch(false, (source) ->
				getView().setCumulative(cumulativeWidget.isSwitchOn()));
		cumulativeLbl = new Label(getView().getApp().getLocalization().getMenu("Cumulative"));
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

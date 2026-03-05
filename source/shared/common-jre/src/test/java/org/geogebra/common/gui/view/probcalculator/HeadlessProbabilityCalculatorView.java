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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.mockito.Mockito;

class HeadlessProbabilityCalculatorView extends ProbabilityCalculatorView {

	private final StatisticsCalculator calculator;

	public HeadlessProbabilityCalculatorView(App app) {
		this(app, null);
	}

	public HeadlessProbabilityCalculatorView(App app, StatisticsCalculator calc) {
		super(app);
		this.app = app;
		this.calculator = calc;
		setPlotPanel(new EuclidianViewNoGui(app.newEuclidianController(kernel), 42,
				new EuclidianSettings(app), new GGraphicsCommon()));
	}

	@Override
	protected void changeProbabilityType() {
		// no UI
	}

	@Override
	public ResultPanel getResultPanel() {
		return Mockito.mock(ResultPanel.class);
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
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		// no UI
	}

	@Override
	public void setInterval(double low2, double high2) {
		// no UI
	}

	@Override
	protected void updateDiscreteTable() {
		// no UI
	}

	@Override
	protected void updateGUI() {
		// no UI
	}

	@Override
	protected boolean isDistributionTabOpen() {
		return false;
	}

	@Override
	protected StatisticsCalculator getStatCalculator() {
		return calculator;
	}

	@Override
	public ProbabilityManager getProbManager() {
		return null;
	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		// no UI
	}

	@Override
	protected void onDistributionUpdate() {
		// no UI
	}

	@Override
	public void setLabels() {
		// no UI
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

}

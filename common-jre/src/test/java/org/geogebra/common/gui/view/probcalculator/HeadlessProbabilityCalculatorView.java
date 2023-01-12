package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;

class HeadlessProbabilityCalculatorView extends ProbabilityCalculatorView {

	public HeadlessProbabilityCalculatorView(App app) {
		super(app);
		this.app = app;
		setPlotPanel(new EuclidianViewNoGui(app.newEuclidianController(kernel), 42,
				new EuclidianSettings(app), new GGraphicsCommon()));
	}

	@Override
	protected void changeProbabilityType() {

	}

	@Override
	public ResultPanel getResultPanel() {
		return new HeadlessResultPanel();
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

	}

	@Override
	public void setInterval(double low2, double high2) {

	}

	@Override
	protected void updateDiscreteTable() {

	}

	@Override
	protected void updateGUI() {

	}

	@Override
	protected boolean isDistributionTabOpen() {
		return false;
	}

	@Override
	protected StatisticsCalculator getStatCalculator() {
		return null;
	}

	@Override
	public ProbabilityManager getProbManager() {
		return null;
	}

	@Override
	protected void addRemoveTable(boolean showTable) {

	}

	@Override
	protected void onDistributionUpdate() {

	}

	@Override
	public void setLabels() {

	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	private static class HeadlessResultPanel implements ResultPanel {
		@Override
		public void showInterval() {

		}

		@Override
		public void showTwoTailed() {

		}

		@Override
		public void showTwoTailedOnePoint() {

		}

		@Override
		public void showLeft() {

		}

		@Override
		public void showRight() {

		}

		@Override
		public void setResultEditable(boolean value) {

		}

		@Override
		public void updateResult(String text) {

		}

		@Override
		public void updateLowHigh(String low, String high) {

		}

		@Override
		public void updateTwoTailedResult(String low, String high) {

		}

		@Override
		public boolean isFieldLow(Object source) {
			return false;
		}

		@Override
		public boolean isFieldHigh(Object source) {
			return false;
		}

		@Override
		public boolean isFieldResult(Object source) {
			return false;
		}

		@Override
		public void setGreaterThan() {

		}

		@Override
		public void setGreaterOrEqualThan() {

		}
	}
}

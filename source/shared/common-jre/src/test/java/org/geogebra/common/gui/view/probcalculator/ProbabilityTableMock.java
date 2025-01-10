package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

class ProbabilityTableMock extends ProbabilityTable {
	private int low;
	private int high;

	public ProbabilityTableMock(App app,
			ProbabilityCalculatorView probCalc) {
		super(app, probCalc);
		probCalc.setTable(this);
		probCalc.updateAll(false);
	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		this.low = lowValue;
		this.high = highValue;
	}

	@Override
	public void setTwoTailedSelection(int lowValue, int highValue) {
		this.low = lowValue;
		this.high = highValue;
	}

	@Override
	public void setTable(ProbabilityCalculatorSettings.Dist distType2, GeoNumberValue[] params2,
			int xMin2, int xMax2) {
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		// stub
	}

	public boolean isRangeHighlighted(int low, int high) {
		return isHighlightedFrom(low) && this.high == (double) high;
	}

	public String highlightRange() {
		return "(" + low + ", " + high + ")";
	}

	public boolean isHighlightedFrom(int from) {
		return this.low == (double) from;
	}
}

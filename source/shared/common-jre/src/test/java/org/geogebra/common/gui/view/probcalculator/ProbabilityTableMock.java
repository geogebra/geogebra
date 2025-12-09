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

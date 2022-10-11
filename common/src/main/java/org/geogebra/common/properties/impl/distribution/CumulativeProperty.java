package org.geogebra.common.properties.impl.distribution;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/** Cumulative property of the distribution view */
public class CumulativeProperty extends AbstractProperty implements BooleanProperty {

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new cumulative property
	 * @param localization localization
	 * @param view probability calculator view
	 */
	public CumulativeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Cumulative");
		this.view = view;
	}

	@Override
	public boolean getValue() {
		return view.isCumulative();
	}

	@Override
	public void setValue(boolean value) {
		view.setCumulative(value);
	}
}

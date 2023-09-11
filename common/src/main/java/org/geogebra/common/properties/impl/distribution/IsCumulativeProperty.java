package org.geogebra.common.properties.impl.distribution;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/** The cumulative property of the distribution view. */
public class IsCumulativeProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new cumulative property
	 * @param localization localization
	 * @param view probability calculator view
	 */
	public IsCumulativeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Cumulative");
		this.view = view;
	}

	@Override
	public Boolean getValue() {
		return view.isCumulative();
	}

	@Override
	protected void doSetValue(Boolean value) {
		view.setCumulative(value);
	}
}

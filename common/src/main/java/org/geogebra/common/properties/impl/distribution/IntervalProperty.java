package org.geogebra.common.properties.impl.distribution;

import java.util.List;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * Interval property.
 */
public class IntervalProperty extends AbstractEnumeratedProperty<Integer> implements
		IconsEnumeratedProperty<Integer> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_PROBABILITY_MODE_LEFT,
			PropertyResource.ICON_PROBABILITY_MODE_INTERVAL,
			PropertyResource.ICON_PROBABILITY_MODE_TWO_TAILED,
			PropertyResource.ICON_PROBABILITY_MODE_RIGHT
	};

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new interval property.
	 * @param localization localization
	 * @param view view
	 */
	public IntervalProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Interval");
		this.view = view;
		setValues(List.of(
				ProbabilityCalculatorView.PROB_LEFT,
				ProbabilityCalculatorView.PROB_INTERVAL,
				ProbabilityCalculatorView.PROB_TWO_TAILED,
				ProbabilityCalculatorView.PROB_RIGHT
		));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public Integer getValue() {
		return view.getProbMode();
	}

	@Override
	protected void doSetValue(Integer value) {
		view.setProbabilityMode(value);
	}

	@Override
	public boolean isEnabled() {
		return !view.isCumulative();
	}
}

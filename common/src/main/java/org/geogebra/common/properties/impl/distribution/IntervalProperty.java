package org.geogebra.common.properties.impl.distribution;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

/**
 * Interval property.
 */
public class IntervalProperty extends AbstractEnumerableProperty implements
		IconsEnumerableProperty {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_PROBABILITY_MODE_LEFT,
			PropertyResource.ICON_PROBABILITY_MODE_INTERVAL,
			PropertyResource.ICON_PROBABILITY_MODE_TWO_TAILED,
			PropertyResource.ICON_PROBABILITY_MODE_RIGHT
	};

	private static final List<Integer> values = Arrays.asList(
			ProbabilityCalculatorView.PROB_LEFT,
			ProbabilityCalculatorView.PROB_INTERVAL,
			ProbabilityCalculatorView.PROB_TWO_TAILED,
			ProbabilityCalculatorView.PROB_RIGHT
	);

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new interval property.
	 * @param localization localization
	 * @param view view
	 */
	public IntervalProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Interval");
		this.view = view;
		setValues(new String[icons.length]);
	}

	@Override
	public PropertyResource[] getIcons() {
		return icons;
	}

	@Override
	public int getIndex() {
		int probabilityMode = view.getProbMode();
		return values.indexOf(probabilityMode);
	}

	@Override
	protected void setValueSafe(String value, int index) {
		int probabilityMode = values.get(index);
		view.setProbabilityMode(probabilityMode);
	}

	@Override
	public boolean isEnabled() {
		return !view.isCumulative();
	}
}

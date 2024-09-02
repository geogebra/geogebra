package org.geogebra.common.properties.impl.distribution;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

import org.geogebra.common.exam.restrictions.ValueFilter;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.AbstractGroupedEnumeratedProperty;
import org.geogebra.common.util.OrderedCollector;

/**
 * Property for the distribution type.
 */
public class DistributionTypeProperty extends AbstractGroupedEnumeratedProperty<Dist> {

	private final ProbabilityCalculatorView view;

	private void updateGroupDividerIndices() {
		Optional<Dist> firstDiscreteDistribution = getValues().stream().filter(value ->
				Set.of(Dist.BINOMIAL, Dist.PASCAL, Dist.HYPERGEOMETRIC, Dist.POISSON)
						.contains(value)).findFirst();
		firstDiscreteDistribution.ifPresent(dist ->
				setGroupDividerIndices(new int[]{ getValues().indexOf(dist) }));
	}

	/**
	 * Constructs an DistributionTypeProperty
	 * @param localization the localization used
	 * @param view probability calculator view
	 */
	public DistributionTypeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Distribution");
		this.view = view;
		LinkedHashMap<Dist, String> values = Arrays.stream(Dist.values()).collect(
				OrderedCollector.toMap(
					dist -> dist,
					dist -> dist.translationKey));
		setNamedValues(values);
		updateGroupDividerIndices();
	}

	@Override
	public void addValueFilter(ValueFilter valueFilter) {
		super.addValueFilter(valueFilter);
		updateGroupDividerIndices();
	}

	@Override
	public void removeValueFilter(ValueFilter valueFilter) {
		super.removeValueFilter(valueFilter);
		updateGroupDividerIndices();
	}

	@Override
	public Dist getValue() {
		return view.getSelectedDist();
	}

	@Override
	protected void doSetValue(Dist value) {
		if (value != view.getSelectedDist()) {
			view.setProbabilityCalculator(value, null, view.isCumulative());
		}
	}
}

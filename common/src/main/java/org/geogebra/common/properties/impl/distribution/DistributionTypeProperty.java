package org.geogebra.common.properties.impl.distribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.AbstractGroupedEnumerableProperty;

/**
 * Property for the distribution type.
 */
public class DistributionTypeProperty extends AbstractGroupedEnumerableProperty {

	private static final List<Dist> CONTINUOUS_DISTRIBUTIONS =
			Arrays.asList(Dist.NORMAL,
					Dist.STUDENT,
					Dist.CHISQUARE,
					Dist.F,
					Dist.EXPONENTIAL,
					Dist.CAUCHY,
					Dist.WEIBULL,
					Dist.GAMMA,
					Dist.LOGNORMAL,
					Dist.LOGISTIC);
	private static final List<Dist> DISCRETE_DISTRIBUTIONS =
			Arrays.asList(Dist.BINOMIAL,
					Dist.PASCAL,
					Dist.POISSON,
					Dist.HYPERGEOMETRIC);

	private final ProbabilityCalculatorView view;
	private HashMap<Dist, String> distributionMap;

	/**
	 * Constructs an AbstractGroupedEnumerableProperty
	 * @param localization the localization used
	 */
	public DistributionTypeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Distribution");
		this.view = view;
		setValues();
	}

	private void setValues() {
		Map<Dist, String> map = getDistributionMap();
		ArrayList<String> values = new ArrayList<>();
		for (Dist distribution : CONTINUOUS_DISTRIBUTIONS) {
			values.add(getLocalization().getMenu(map.get(distribution)));
		}
		values.add(DIVIDER);
		for (Dist distribution : DISCRETE_DISTRIBUTIONS) {
			values.add(getLocalization().getMenu(map.get(distribution)));
		}

		setValues(values.toArray(new String[0]));
	}

	@Override
	public int getIndex() {
		Dist selected = view.getSelectedDist();
		int index = CONTINUOUS_DISTRIBUTIONS.indexOf(selected);
		if (index >= 0) {
			return index;
		}
		index = CONTINUOUS_DISTRIBUTIONS.size() + DISCRETE_DISTRIBUTIONS.indexOf(selected);
		return index + 1; // Divider
	}

	@Override
	protected void setValueSafe(String value, int index) {
		Dist selectedDist;
		if (index < CONTINUOUS_DISTRIBUTIONS.size()) {
			selectedDist = CONTINUOUS_DISTRIBUTIONS.get(index);
		} else {
			selectedDist = DISCRETE_DISTRIBUTIONS.get(index - CONTINUOUS_DISTRIBUTIONS.size() - 1);
		}
		if (selectedDist != view.getSelectedDist()) {
			view.setProbabilityCalculator(selectedDist, null, view.isCumulative());
		}
	}

	/**
	 * Creates a hash map that can return a JComboBox menu string for
	 * distribution type constant Key = display type constant Value = menu item
	 * string
	 *
	 * @return map distribution -&gt; localized name
	 */
	private HashMap<Dist, String> getDistributionMap() {
		if (distributionMap == null) {
			distributionMap = new HashMap<>();

			distributionMap.put(Dist.NORMAL, "Distribution.Normal");
			distributionMap.put(Dist.STUDENT, "Distribution.StudentT");
			distributionMap.put(Dist.CHISQUARE, "Distribution.ChiSquare");
			distributionMap.put(Dist.F, "Distribution.F");
			distributionMap.put(Dist.EXPONENTIAL, "Distribution.Exponential");
			distributionMap.put(Dist.CAUCHY, "Distribution.Cauchy");
			distributionMap.put(Dist.WEIBULL, "Distribution.Weibull");
			distributionMap.put(Dist.LOGISTIC, "Distribution.Logistic");
			distributionMap.put(Dist.LOGNORMAL, "Distribution.Lognormal");

			distributionMap.put(Dist.GAMMA, "Distribution.Gamma");
			distributionMap.put(Dist.BINOMIAL, "Distribution.Binomial");
			distributionMap.put(Dist.PASCAL, "Distribution.Pascal");
			distributionMap.put(Dist.POISSON, "Distribution.Poisson");
			distributionMap.put(Dist.HYPERGEOMETRIC, "Distribution.Hypergeometric");
		}
		return distributionMap;
	}
}

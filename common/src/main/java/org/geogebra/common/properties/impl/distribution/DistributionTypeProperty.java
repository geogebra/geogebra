package org.geogebra.common.properties.impl.distribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.properties.impl.AbstractGroupedEnumerableProperty;

/**
 * Property for the distribution type.
 */
public class DistributionTypeProperty extends AbstractGroupedEnumerableProperty {

	private static final List<ProbabilityCalculatorSettings.Dist> CONTINUOUS_DISTRIBUTIONS =
			Arrays.asList(new ProbabilityCalculatorSettings.Dist[]{
					ProbabilityCalculatorSettings.Dist.NORMAL,
					ProbabilityCalculatorSettings.Dist.STUDENT,
					ProbabilityCalculatorSettings.Dist.CHISQUARE,
					ProbabilityCalculatorSettings.Dist.F,
					ProbabilityCalculatorSettings.Dist.EXPONENTIAL,
					ProbabilityCalculatorSettings.Dist.CAUCHY,
					ProbabilityCalculatorSettings.Dist.WEIBULL,
					ProbabilityCalculatorSettings.Dist.GAMMA,
					ProbabilityCalculatorSettings.Dist.LOGNORMAL,
					ProbabilityCalculatorSettings.Dist.LOGISTIC
			});
	private static final List<ProbabilityCalculatorSettings.Dist> DISCRETE_DISTRIBUTIONS =
			Arrays.asList(new ProbabilityCalculatorSettings.Dist[]{
					ProbabilityCalculatorSettings.Dist.BINOMIAL,
					ProbabilityCalculatorSettings.Dist.PASCAL,
					ProbabilityCalculatorSettings.Dist.POISSON,
					ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC,
			});

	private final ProbabilityCalculatorView view;

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
		ProbabilityManager manager = view.getProbManager();
		Map<ProbabilityCalculatorSettings.Dist, String> map = manager.getDistributionMap();
		ArrayList<String> values = new ArrayList<>();
		for (ProbabilityCalculatorSettings.Dist distribution : CONTINUOUS_DISTRIBUTIONS) {
			values.add(map.get(distribution));
		}
		values.add(DIVIDER);
		for (ProbabilityCalculatorSettings.Dist distribution : DISCRETE_DISTRIBUTIONS) {
			values.add(map.get(distribution));
		}

		setValuesAndLocalize(values.toArray(new String[0]));
	}

	@Override
	public int getIndex() {
		ProbabilityCalculatorSettings.Dist selected = view.getSelectedDist();
		int index = CONTINUOUS_DISTRIBUTIONS.indexOf(selected);
		if (index >= 0) {
			return index;
		}
		index = CONTINUOUS_DISTRIBUTIONS.size() + DISCRETE_DISTRIBUTIONS.indexOf(selected);
		return index + 1; // Divider
	}

	@Override
	protected void setValueSafe(String value, int index) {
		ProbabilityCalculatorSettings.Dist selectedDist;
		if (index < CONTINUOUS_DISTRIBUTIONS.size()) {
			selectedDist = CONTINUOUS_DISTRIBUTIONS.get(index);
		} else {
			selectedDist = DISCRETE_DISTRIBUTIONS.get(index - CONTINUOUS_DISTRIBUTIONS.size() - 1);
		}
		GeoNumeric[] parameters = ProbabilityManager
				.getDefaultParameters(selectedDist, view
						.getPlotPanel().getKernel().getConstruction());
		view.setProbabilityCalculator(selectedDist, parameters, view.isCumulative());
	}
}

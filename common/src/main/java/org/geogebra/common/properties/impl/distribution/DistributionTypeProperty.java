package org.geogebra.common.properties.impl.distribution;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.AbstractGroupedEnumeratedProperty;

/**
 * Property for the distribution type.
 */
public class DistributionTypeProperty extends AbstractGroupedEnumeratedProperty<Dist> {

	private final ProbabilityCalculatorView view;

	/**
	 * Constructs an DistributionTypeProperty
	 * @param localization the localization used
	 * @param view probability calculator view
	 */
	public DistributionTypeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Distribution");
		this.view = view;
		setValues(
				// Continouus
				Dist.NORMAL, Dist.STUDENT, Dist.CHISQUARE, Dist.F, Dist.EXPONENTIAL, Dist.CAUCHY,
				Dist.WEIBULL, Dist.GAMMA, Dist.BETA, Dist.LOGNORMAL, Dist.LOGISTIC,
				// Discrete
				Dist.BINOMIAL, Dist.PASCAL, Dist.POISSON, Dist.HYPERGEOMETRIC);
		setValueNames(
				// Continouus
				"Distribution.Normal", "Distribution.StudentT", "Distribution.ChiSquare",
				"Distribution.F", "Distribution.Exponential", "Distribution.Cauchy",
				"Distribution.Weibull", "Distribution.Gamma", "Distribution.Beta",
				"Distribution.Lognormal", "Distribution.Logistic",
				// Discrete
				"Distribution.Binomial", "Distribution.Pascal", "Distribution.Poisson",
				"Distribution.Hypergeometric");
		setGroupDividerIndices(new int[]{11});
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

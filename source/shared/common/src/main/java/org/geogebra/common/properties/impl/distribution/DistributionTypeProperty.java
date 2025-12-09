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

package org.geogebra.common.properties.impl.distribution;

import static java.util.Map.entry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.ValueFilter;
import org.geogebra.common.properties.impl.AbstractGroupedEnumeratedProperty;

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
		setNamedValues(List.of(
				// Continuous
				entry(Dist.NORMAL, "Distribution.Normal"),
				entry(Dist.STUDENT, "Distribution.StudentT"),
				entry(Dist.CHISQUARE, "Distribution.ChiSquare"),
				entry(Dist.F, "Distribution.F"),
				entry(Dist.EXPONENTIAL, "Distribution.Exponential"),
				entry(Dist.CAUCHY, "Distribution.Cauchy"),
				entry(Dist.WEIBULL, "Distribution.Weibull"),
				entry(Dist.GAMMA, "Distribution.Gamma"),
				entry(Dist.BETA, "Distribution.Beta"),
				entry(Dist.LOGNORMAL, "Distribution.Lognormal"),
				entry(Dist.LOGISTIC, "Distribution.Logistic"),
				// Discrete
				entry(Dist.BINOMIAL, "Distribution.Binomial"),
				entry(Dist.PASCAL, "Distribution.Pascal"),
				entry(Dist.POISSON, "Distribution.Poisson"),
				entry(Dist.HYPERGEOMETRIC, "Distribution.Hypergeometric")
		));
		updateGroupDividerIndices();
	}

	@Override
	public void addValueFilter(@Nonnull ValueFilter valueFilter) {
		super.addValueFilter(valueFilter);
		updateGroupDividerIndices();
	}

	@Override
	public void removeValueFilter(@Nonnull ValueFilter valueFilter) {
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

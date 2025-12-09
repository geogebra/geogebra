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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;

/**
 * Factory to create discrete distributions.
 */
public class DiscreteDistributionFactory {

	private final DiscreteDistribution binomial;
	private final DiscreteDistribution pascal;
	private final DiscreteDistribution poisson;
	private final DiscreteDistribution hyperGeometric;

	/**
	 *
	 * @param cons the construction.
	 */
	public DiscreteDistributionFactory(Construction cons) {
		binomial = new BinomialDistribution(cons);
		pascal = new PascalDistribution(cons);
		poisson = new PoissonDistribution(cons);
		hyperGeometric = new HyperGeometricProbability(cons);
	}

	/**
	 * Create the desired distribution based on dist parameters
	 *
	 * @param distribution to create
	 * @param parameters to create with.
	 * @param isCumulative if the distribution is cumulative.
	 * @return the created distribution
	 */
	public DiscreteProbability create(Dist distribution, GeoNumberValue[] parameters,
			boolean isCumulative) {
		DistributionParameters params = new DistributionParameters(parameters, isCumulative);
		switch (distribution) {
		default:
		case BINOMIAL:
			return binomial.create(params);
		case PASCAL:
			return pascal.create(params);
		case POISSON:
			return poisson.create(params);
		case HYPERGEOMETRIC:
			return hyperGeometric.create(params);
		}
	}
}
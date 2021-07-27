package org.geogebra.common.gui.view.probcalculator;

/**
 * Interface to create discrete distributions.
 */
public interface DiscreteDistribution {

	/**
	 * Creates a discrete type of probability
	 * on the given parameters.
	 * @param params parameters of the distribution
	 * @return the distribution.
	 */
	DiscreteProbability create(DistributionParameters params);
}

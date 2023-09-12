package org.geogebra.common.gui.view.probcalculator;

/**
 * Caches and returns previously created distribution based on it's parameters.
 */
abstract public class CachingDiscreteDistribution implements DiscreteDistribution {

	private DiscreteProbability discreteProbability;
	private DistributionParameters oldParameters;

	@Override
	public DiscreteProbability create(DistributionParameters parameters) {
		if (!parameters.equals(oldParameters)) {
			oldParameters = parameters;
			discreteProbability = createProbability(parameters);
		}
		return discreteProbability;
	}

	/**
	 * @see DiscreteDistribution#create(DistributionParameters) createProbability
	 */
	protected abstract DiscreteProbability createProbability(DistributionParameters parameters);
}

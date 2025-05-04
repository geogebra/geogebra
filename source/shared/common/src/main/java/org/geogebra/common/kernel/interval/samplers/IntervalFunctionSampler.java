package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Interval function sampler.
 */
public interface IntervalFunctionSampler {

	/**
	 * @return interval tuples
	 */
	IntervalTupleList tuples();

	/**
	 * Extend to an interval. Try reusing values if possible, fall back to resample.
	 * @param domain new domain
	 */
	void extend(Interval domain);

	/**
	 * Reset the domain and rebuild all value tuples.
	 * @param domain new domain
	 */
	void resample(Interval domain);
}

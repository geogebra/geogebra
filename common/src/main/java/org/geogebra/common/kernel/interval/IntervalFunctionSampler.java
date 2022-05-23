package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.geos.GeoFunction;

public interface IntervalFunctionSampler {

	/**
	 * Evaluate on interval x with the same step that used before
	 * @param x the interval to be evaluated on.
	 * @return tuples evaluated on x.
	 */
	IntervalTupleList evaluateOn(Interval x);

	/**
	 * Evaluate on interval [high, low] with the same step that used before
	 * @param low  lower bound
	 * @param high higher bound
	 * @return tuples evaluated on [low, high].
	 */
	IntervalTupleList evaluateOn(double low, double high);

	/**
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	IntervalTupleList result();

	void update(IntervalTuple range);

	/**
	 *
	 * @param x to evaluate on.
	 * @return the evaluated value.
	 */
	Interval evaluatedValue(Interval x);

	IntervalTupleList extendDomain(double min, double max);

	void setInterval(double low, double high);

	GeoFunction getGeoFunction();
}

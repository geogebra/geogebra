package org.geogebra.common.kernel.interval.operators;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalArithmetic {

	/**
	 * Interval multiplication
	 *
	 * @param other to multiply this interval with.
	 * @return this as result.
	 */
	Interval multiply(Interval interval, Interval other);

	/**
	 * Interval division
	 *
	 * @param other to divide this interval with.
	 * @return this as result.
	 */
	Interval divide(Interval interval, Interval other);
}

package org.geogebra.common.kernel.interval;

public interface IntervalArithmetic {

	/**
	 * Interval multiplication
	 *
	 * @param other to multiply this interval with.
	 * @return this as result.
	 */
	Interval multiply(Interval other);

	/**
	 * Interval division
	 *
	 * @param other to divide this interval with.
	 * @return this as result.
	 */
	Interval divide(Interval other);
}

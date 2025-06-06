
package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.interval.operators.RMath;

public class IntervalHelper {

	/**
	 * Create an interval containing a single value
	 * @param singleton single value
	 * @return interval
	 */
	public static Interval interval(double singleton) {
		return new Interval(singleton);
	}

	/**
	 * @param low minimum
	 * @param high maximum
	 * @return inverted interval
	 */
	public static Interval invertedInterval(double low, double high) {
		return interval(low, high).invert();
	}

	/**
	 * @param low minimum
	 * @param high maximum
	 * @return interval
	 */
	public static Interval interval(double low, double high) {
		return new Interval(low, high);
	}

	/**
	 * Makes an interval [value - PRECISION, value + PRECISION]
	 * @param value to make an interval around.
	 * @return interval [value - PRECISION, value + PRECISION]
	 */
	public static Interval around(double value) {
		return around(value, IntervalConstants.PRECISION);
	}

	/**
	 * Makes an interval around a value as center and precision as radius.
	 *
	 * @param value to make an interval around.
	 * @param precision the radius around value
	 * @return interval [value - precision, value + precision]
	 */
	public static Interval around(double value, double precision) {
		double epsilon = RMath.prev(precision);
		return interval(value - epsilon, value + epsilon);
	}
}

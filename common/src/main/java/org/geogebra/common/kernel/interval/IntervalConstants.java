package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public class IntervalConstants {
	public static final double PI_LOW = Math.PI - 1E-16;
	public static final double PI_HIGH = Math.PI + 1E-16;
	public static final double PI_HALF_LOW = PI_LOW / 2.0;
	public static final double PI_HALF_HIGH = PI_HIGH / 2.0;
	public static final double PI_TWICE_LOW = PI_LOW * 2.0;
	public static final double PI_TWICE_HIGH = PI_HIGH * 2.0;

	/**
	 *
	 * @return a newly created empty interval.
	 */
	public static Interval empty() {
		return new Interval(POSITIVE_INFINITY, NEGATIVE_INFINITY);
	}

	/**
	 *
	 * @return a newly created zero interval.
	 */
	public static Interval zero() {
		return new Interval(0);
	}

	/**
	 *
	 * @return a newly created zero interval.
	 */
	public static Interval one() {
		return new Interval(1);
	}

	/**
	 *
	 * @return a newly created whole interval.
	 */
	public static Interval whole() {
		return new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
	}

	/**
	 *
	 * @return a newly created undefined interval.
	 */
	public static Interval undefined() {
		return new Interval(Double.NaN);
	}

	/**
	 *
	 * @return a newly created PI interval.
	 */
	public static Interval pi() {
		return new Interval(PI_LOW, PI_HIGH);
	}

	/**
	 *
	 * @return a newly created PI interval.
	 */
	public static Interval piTwice() {
		return new Interval(PI_TWICE_LOW, PI_TWICE_HIGH);
	}

	/**
	 *
	 * @return a newly created PI/2 interval.
	 */
	public static Interval piHalf() {
		return new Interval(PI_HALF_LOW, PI_HALF_HIGH);
	}
}

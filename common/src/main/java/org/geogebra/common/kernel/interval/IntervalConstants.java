package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public class IntervalConstants {
	public final static Interval WHOLE = new Interval(NEGATIVE_INFINITY,
			POSITIVE_INFINITY);
	public static final Interval ZERO = new Interval(0);
	public static final Interval ONE = new Interval(1);
	public static final double PI_LOW = Math.PI - 1E-16;
	public static final double PI_HIGH = Math.PI + 1E-16;
	public static final double PI_HALF_LOW = PI_LOW / 2.0;
	public static final double PI_HALF_HIGH = PI_HIGH / 2.0;
	public static final double PI_TWICE_LOW = PI_LOW * 2.0;
	public static final double PI_TWICE_HIGH = PI_HIGH * 2.0;
	public static final Interval PI = new Interval(PI_LOW, PI_HIGH);
	public static final Interval PI_HALF = new Interval(PI_HALF_LOW, PI_HALF_HIGH);
	public static final Interval PI_TWICE = new Interval(PI_TWICE_LOW, PI_TWICE_HIGH);

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
	 * @return a newly created whole interval.
	 */
	public static Interval whole() {
		return new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
	}
}

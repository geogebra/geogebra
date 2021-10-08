
package org.geogebra.common.kernel.interval;

public class IntervalHelper {

	public static Interval interval(double singleton) {
		return new Interval(singleton);
	}

	public static Interval invertedInterval(double low, double high) {
		return interval(low, high).invert();
	}

	public static Interval interval(double low, double high) {
		return new Interval(low, high);
	}

	public static Interval around(double value) {
		return around(value, IntervalConstants.PRECISION);
	}

	public static Interval around(double value, double precision) {
		return interval(value - precision, value + precision);
	}
}

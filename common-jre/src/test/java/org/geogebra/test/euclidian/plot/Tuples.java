package org.geogebra.test.euclidian.plot;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTuple;

/**
 * Class for creating tuples
 */
public class Tuples {

	/**
	 *
	 * Creating a non-inverted interval tuple (x, y)
	 *
	 * @param xLow low bound of interval x
	 * @param xHigh high bound of interval x
	 * @param yLow low bound of interval y
	 * @param yHigh high bound of interval y
	 * @return the tuple created from its params.
	 */
	public static IntervalTuple normal(double xLow, double xHigh, double yLow, double yHigh) {
		return new IntervalTuple(new Interval(xLow, xHigh), new Interval(yLow, yHigh));
	}

	/**
	 *
	 * Creating an inverted interval tuple (x, y)
	 *
	 * @param xLow low bound of interval x
	 * @param xHigh high bound of interval x
	 * @param yLow low bound of inverted interval y
	 * @param yHigh high bound of inverted interval y
	 * @return the inverted tuple created from its params.
	 */
	public static IntervalTuple inverted(double xLow, double xHigh, double yLow, double yHigh) {
		Interval y = new Interval(yLow, yHigh);
		y.invert();
		return new IntervalTuple(new Interval(xLow, xHigh), y);
	}

	/**
	 *
	 * Creating an undefined interval tuple (x, undefined).
	 *
	 * @param xLow low bound of interval x
	 * @param xHigh high bound of interval x
	 * @return the undefined tuple created from its params.
	 */
	public static IntervalTuple undefined(double xLow, double xHigh) {
		return new IntervalTuple(new Interval(xLow, xHigh), IntervalConstants.undefined());
	}
}

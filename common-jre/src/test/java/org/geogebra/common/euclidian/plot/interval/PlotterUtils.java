package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;

/**
 * Helper class to build dependencies for plotter related tests.
 *
 * @author laszlo
 */
public class PlotterUtils {

	/**
	 * Creates a new range for both x and y axis.
	 *
	 * @param lowX lowest x value of the x-range.
	 * @param highX highest x value of the x-range.
	 * @param lowY lowest y value of the y-range.
	 * @param highY highest y value of the y-range.
	 * @return a tuple with x,y range intervals.
	 */
	public static IntervalTuple newRange(double lowX, double highX, double lowY, double highY) {
		Interval x = new Interval(lowX, highX);
		Interval y = new Interval(lowY, highY);
		return new IntervalTuple(x, y);
	}

	/**
	 * Creates a new function sampler.
	 *
	 * @param function to sample.
	 * @param range to sample through.
	 * @param numberOfSamples max samples will take.
	 * @return the function sampler object.
	 */
	public static IntervalFunctionSampler newSampler(GeoFunction function, IntervalTuple range,
			int numberOfSamples) {
		return new IntervalFunctionSampler(function, range, numberOfSamples);
	}
}
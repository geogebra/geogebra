package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;

public class PlotterUtils {
	static IntervalPlotModel createModel(IntervalTuple range, IntervalFunctionSampler sampler,
			EuclidianView view) {
		return new IntervalPlotModel(range, sampler, view);
	}

	static IntervalTuple createRange(double lowX, double highX, double lowY, double highY) {
		Interval x = new Interval(lowX, highX);
		Interval y = new Interval(lowY, highY);
		return new IntervalTuple(x, y);
	}

	static IntervalFunctionSampler newSampler(GeoFunction function, IntervalTuple range,
			int numberOfSamples) {
		return new IntervalFunctionSampler(function, range, numberOfSamples);
	}
}

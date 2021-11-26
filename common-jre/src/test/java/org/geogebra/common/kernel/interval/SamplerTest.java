package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.debug.Log;

public class SamplerTest extends BaseUnitTest {
	protected void valuesShouldBeBetween(IntervalTupleList tuples, double low, double high) {
		List<IntervalTuple> result =
				tuples.stream().filter(
						entry -> (entry.y().getLow() < low - 1E-6
								|| entry.y().getHigh() > high + 1E-6)
								&& !(entry.y().isInverted() && entry.y().isWhole()))
						.collect(Collectors.toList());
		assertEquals(Collections.emptyList(), result);
	}

	protected IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax) {
		return functionValuesWithSampleCount(functionDescription,
				xmin, xmax, ymin, ymax,
				100);
	}

	protected IntervalTupleList functionValuesWithSampleCount(String functionDescription,
			double xmin, double xmax, double ymin, double ymax, int sampleCount) {
		GeoFunction function = add(functionDescription);
		IntervalTuple range = PlotterUtils.newRange(xmin, xmax, ymin, ymax);
		IntervalFunctionSampler sampler = PlotterUtils.newSampler(function, range,
				sampleCount);
		return sampler.result();
	}

	@SuppressWarnings("for sample tests")
	private void logSamples(IntervalTupleList result) {
		for (int i = 0; i < result.count(); i++) {
			IntervalTuple tuple = result.get(i);
			Log.debug("assertEquals("
					+ makeInterval(tuple)
					+ ", tuples.valueAt(" + i + "));");

		}
	}

	private String makeInterval(IntervalTuple tuple) {
		if (tuple.y().isUndefined()) {
			return "empty()";
		}

		StringBuilder sb = new StringBuilder();
		if (tuple.isInverted()) {
			sb.append("invertedInterval");
		} else {
			sb.append("interval");
		}
		sb.append("(");
		sb.append(tuple.y().getLow());
		sb.append(", ");
		sb.append(tuple.y().getHigh());
		sb.append(")");
		return sb.toString();
	}

	protected IntervalTupleList hiResFunction(String description) {
		return functionValuesWithSampleCount(description,
				-5500, 5500, -5000, 6000, 1920);
	}
}

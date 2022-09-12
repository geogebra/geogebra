package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class IntervalFunctionData {
	private final IntervalTupleList tuples;
	private EuclidianViewBounds bounds;
	private final GeoFunction geoFunction;

	public IntervalFunctionData(GeoFunction geoFunction, EuclidianViewBounds bounds,
			IntervalTupleList tuples) {
		this(geoFunction, tuples);
		this.bounds = bounds;
	}

	public IntervalFunctionData(GeoFunction geoFunction, IntervalTupleList tuples) {
		this.geoFunction = geoFunction;
		this.tuples = tuples;
	}

	public IntervalTupleList tuples() {
		return tuples;
	}

	public void append(Interval x, Interval y) {
		tuples.add(new IntervalTuple(x, y));
	}

	public void prepend(Interval x, Interval y) {
		tuples.prepend(new IntervalTuple(x, y));
	}

	public void clear() {
		tuples.clear();
	}

	public void extendLeft(Interval x, Interval y) {
		prepend(x, y);
		double low = tuples.last().x().getLow();
		if (low >= bounds.getXmax()) {
			tuples.removeLast();
		}
	}

	public void extendRight(Interval x, Interval y) {
		append(x, y);
		IntervalTuple first = tuples.first();
		if (first.x().getHigh() <= bounds.getXmin()) {
			tuples.removeFirst();
		}
	}

	public GeoFunction getGeoFunction() {
		return geoFunction;
	}

	public boolean isValid() {
		return tuples.isValid();
	}
}

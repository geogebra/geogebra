package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class IntervalFunctionData {
	private IntervalTupleList tuples;
	private EuclidianViewBounds bounds;

	public IntervalFunctionData(EuclidianViewBounds bounds) {
		this();
		this.bounds = bounds;
	}

	public IntervalFunctionData() {
		this.tuples = new IntervalTupleList();
	}

	public IntervalTupleList tuples() {
		return tuples;
	}

	public void append(Interval x, Interval y) {
		tuples.add(new IntervalTuple(x, y));
	}

	public void clear() {
		tuples.clear();
	}

	public void extendLeft(Interval x, Interval y) {
		tuples.add(0, new IntervalTuple(x, y));
		if (tuples.last().x().getLow() > bounds.getXmax()) {
			tuples.removeLast();
		}
	}

	public void extendRight(Interval x, Interval y) {
		append(x, y);
		if (tuples.first().x().getHigh() < bounds.getXmin()) {
			tuples.removeFirst();
		}
	}

	public int count() {
		return tuples.count();
	}
}

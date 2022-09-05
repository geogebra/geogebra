package org.geogebra.common.euclidian.plot.interval;

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

	public void append(IntervalTuple tuple) {
		tuples.add(tuple);
	}

	public void clear() {
		tuples.clear();
	}

	public void extendLeft(IntervalTuple tuple) {
		tuples.add(0, tuple);
		if (tuples.last().x().getLow() > bounds.getXmax()) {
			tuples.removeLast();
		}
	}

	public void extendRight(IntervalTuple tuple) {
		tuples.add(tuple);
		if (tuples.first().x().getHigh() < bounds.getXmin()) {
			tuples.removeFirst();
		}
	}

	public int count() {
		return tuples.count();
	}
}

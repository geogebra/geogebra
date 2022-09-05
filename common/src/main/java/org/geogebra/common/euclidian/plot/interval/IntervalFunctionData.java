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
}

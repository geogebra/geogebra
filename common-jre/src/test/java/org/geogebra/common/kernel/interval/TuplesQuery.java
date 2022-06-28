package org.geogebra.common.kernel.interval;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class TuplesQuery {
	private final IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	public Stream<IntervalTuple> invertedTuples() {
		return tuples.stream().filter(t -> t.y().isInverted());
	}

	public Stream<IntervalTuple> emptyTuples() {
		return tuples.stream().filter(t -> t.y().isUndefined());
	}

	public boolean noDefinedTuples() {
		return tuples.count() == emptyTuples().count();
	}
}

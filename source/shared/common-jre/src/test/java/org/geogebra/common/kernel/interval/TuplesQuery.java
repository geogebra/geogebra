package org.geogebra.common.kernel.interval;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class TuplesQuery {
	private final IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	private Stream<IntervalTuple> emptyTuples() {
		return tuples.stream().filter(t -> t.y().isUndefined());
	}

	/**
	 * @return whether all tuples have undefined y-value
	 */
	public boolean noDefinedTuples() {
		return tuples.count() == emptyTuples().count();
	}
}

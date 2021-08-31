package org.geogebra.common.kernel.interval;

import java.util.List;
import java.util.stream.Collectors;

public class TuplesQuery {
	private final IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	public List<IntervalTuple> invertedTuples() {
		return tuples.stream().filter(t -> t.y().isInverted()).collect(Collectors.toList());
	}

	public List<IntervalTuple> emptyTuples() {
		return tuples.stream().filter(t -> t.y().isEmpty()).collect(Collectors.toList());
	}
}

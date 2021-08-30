package org.geogebra.common.kernel.interval;

import java.util.List;
import java.util.stream.Collectors;

public class TuplesQuery {
	private IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	public List<IntervalTuple> inverted() {
		return tuples.stream().filter(t -> t.y().isInverted()).collect(Collectors.toList());
	}
}

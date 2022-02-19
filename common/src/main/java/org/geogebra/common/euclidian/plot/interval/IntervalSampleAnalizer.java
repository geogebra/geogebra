package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.IntervalTupleList;

public class IntervalSampleAnalizer {
	private IntervalTupleList tuples;

	public void setTuples(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	public boolean isAllWhole() {
		return tuples.stream().filter(tuple -> tuple.y().isWhole()).count() == count();
	}

	int count() {
		return tuples.count();
	}

	public boolean hasValidData() {
		return count() > 1 && !isAllWhole();
	}

	public boolean isDescendingFrom(int index) {
		return index > 1 && index < count() - 1
				&& tuples.valueAt(index).isGreaterThan(tuples.valueAt(index + 1));
	}

	public boolean isDivergentAt(int index) {
		return tuples.valueAt(index).isInverted()
			&& isDescendingFrom(index - 2) == isDescendingFrom(index + 1);
	}
}

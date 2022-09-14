package org.geogebra.common.euclidian.plot.interval;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class QueryFunctionDataImpl implements QueryFunctionData {
	private final IntervalTupleList tuples;
	private final TupleNeighbours neighbours = new TupleNeighbours();

	/**
	 *
	 * @param tuples (x, y) list to query.
	 */
	public QueryFunctionDataImpl(IntervalTupleList tuples) {
		this.tuples = tuples;
	}
	
	/**
	 *
	 * @param index to get point at
	 * @return corresponding point if index is valid, null otherwise.
	 */
	@Override
	public IntervalTuple at(int index) {
		return tuples.get(index);
	}

	@Override
	public boolean hasNext(int index) {
		return index < tuples.count();
	}

	@Override
	public boolean isInvertedAt(int index) {
		return index >= tuples.count() || at(index).isInverted();
	}

	/**
	 *
	 * @return count of points in model
	 */
	@Override
	public int getCount() {
		return tuples.count();
	}

	/**
	 *
	 * @param index of the tuple.
	 * @return if the tuple value of a given index is whole or not.
	 */
	@Override
	public boolean isWholeAt(int index) {
		return index >= tuples.count() || at(index).y().isWhole();
	}

	@Override
	public boolean hasValidData() {
		return tuples.isValid();
	}

	@Override
	public boolean nonDegenerated(int index) {
		return !isInvertedPositiveInfinity(index);
	}

	private boolean isInvertedPositiveInfinity(int index) {
		return isValidIndex(index)
				&& at(index).y().isPositiveInfinity()
				&& isInvertedAt(index);
	}

	private boolean isValidIndex(int index) {
		return index < tuples.count();
	}

	@Override
	public void forEach(IntConsumer action) {
		Interval xRange = IntervalPlotSettings.visibleXRange();
		if (xRange.isUndefined()) {
			allIndexes().forEach(action);
		} else {
			allIndexes().filter(index -> xRange.contains(at(index).x()))
					.forEach(action);
		}
	}

	private IntStream allIndexes() {
		return IntStream.range(0, tuples.count());
	}

	@Override
	public TupleNeighbours neighboursAt(int index) {
		neighbours.set(at(index - 1), at(index), at(index + 1));
		return neighbours;
	}
}

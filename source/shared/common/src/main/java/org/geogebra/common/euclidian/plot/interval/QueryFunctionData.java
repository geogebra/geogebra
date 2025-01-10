package org.geogebra.common.euclidian.plot.interval;

import java.util.function.IntConsumer;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

/**
 * Read-only query of the data of a given Interval Function.
 */
public interface QueryFunctionData {
	/**
	 *
	 * @param index to retrieve
	 * @return the (x, y) value of the function at the given index.
	 */
	IntervalTuple at(int index);

	/**
	 *
	 * @param index to check
	 * @return if function has data after index.
	 */
	boolean hasNext(int index);

	/**
	 *
	 * @param index to check
	 * @return if function value is an inverted interval at index.
	 */
	boolean isInvertedAt(int index);

	/**
	 *
	 * @return count of tuples of the evaluated function data.
	 */
	int getCount();

	/**
	 *
	 * @param index to check
	 * @return if function value is the whole interval at index.
	 */
	boolean isWholeAt(int index);

	/**
	 *
	 * @return if the function has valid, displayable data.
	 */
	boolean hasValidData();

	/**
	 *
	 * @param index to check
	 * @return if function value is not inverted positive infinity.
	 */
	boolean nonDegenerated(int index);

	/**
	 * Iterates through and calls the given action on every tuple of the function data.
	 *
	 * @param action to call on.
	 */
	void forEach(IntConsumer action);

	/**
	 * @param index to get the neighbours at.
	 * @return the neighbours around tuple given by index (including itself)
	 */
	TupleNeighbours neighboursAt(int index);
}

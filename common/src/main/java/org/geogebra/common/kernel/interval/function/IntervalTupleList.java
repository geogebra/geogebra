package org.geogebra.common.kernel.interval.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.util.debug.Log;

/**
 * List to hold IntervalTuples
 *
 * @author laszlo
 */
public class IntervalTupleList implements Iterable<IntervalTuple> {
	private List<IntervalTuple> list;
	private static IntervalTupleList emptyList = null;

	/**
	 * Constructor.
	 */
	public IntervalTupleList() {
		this.list = new ArrayList<>();
	}

	/**
	 *
	 * @param tuple to add as the only element of the list.
	 */
	public IntervalTupleList(IntervalTuple tuple) {
		this();
		list.add(tuple);
	}

	/**
	 *
	 * @return the empty list.
	 */
	public static IntervalTupleList emptyList() {
		if (emptyList == null) {
			emptyList = new IntervalTupleList();
		}

		return emptyList;
	}

	/**
	 *
	 * @param tuple to add
	 */
	public void add(IntervalTuple tuple) {
		list.add(tuple);
	}

	/**
	 *
	 * @param index of tuple to get.
	 * @return the tuple on the given index.
	 */
	public IntervalTuple get(int index) {
		return index > -1 && index < list.size() ? list.get(index) : null;
	}

	@Nonnull
	@Override
	public Iterator<IntervalTuple> iterator() {
		return list.iterator();
	}

	/**
	 *
	 * @return the size of the list
	 */
	public int count() {
		return list.size();
	}

	/**
	 *
	 * @return true if the list has no tuples.
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Adds points to the tail of the list.
	 *
	 * @param newPoints to append
	 */
	public void append(IntervalTupleList newPoints) {
		if (newPoints.isEmpty() || newPoints.isAllUndefined()) {
			return;
		}
		if (newPoints.stream().filter(t -> t.x().isUndefined()).count() > 0) {
			Log.debug("");
		}
		this.list.addAll(newPoints.list);
	}

	private boolean isAllUndefined() {
		for (IntervalTuple tuple: list) {
			if (hasTupleValue(tuple)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasTupleValue(IntervalTuple tuple) {
		return tuple != null && tuple.y() != null && !tuple.y().isUndefined();
	}

	/**
	 * Adds points to the head of the list.
	 *
	 * @param newPoints to prepend
	 */
	public void prepend(IntervalTupleList newPoints) {
		if (newPoints.isEmpty() || newPoints.isAllUndefined()) {
			return;
		}

		list.addAll(0, newPoints.list);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTupleList) {
			IntervalTupleList other = (IntervalTupleList) obj;
			return list.equals(other.list);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IntervalTuple point: list) {
			sb.append(point.toString());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		for (IntervalTuple point: list) {
			hashCode += point.hashCode();
		}
		return hashCode;
	}

	public void clear() {
		list.clear();
	}

	public Interval valueAt(int index) {
		return get(index).y();
	}

	/**
	 * Removing all (x, y) pairs such that the x interval ends higher than a given value.
	 *  - ie can cut tuples that are offscreen from right.
	 * @param high to remove from
	 */
	public void cutFrom(double high) {
		list = list.stream().filter(tuple -> tuple.x().getHigh() <= high)
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @param index of the tuple to check before.
	 * @return if the y values are ascending before index.
	 */
	public boolean isAscendingBefore(int index) {
		if (index < 2 || get(index - 1).isUndefined()) {
			return false;
		}

		Interval y1 = get(index - 2).y();
		Interval y2 = get(index - 1).y();
		return y2 != null && y2.isGreaterThan(y1);
	}

	/**
	 *
	 * @param index of the tuple to check after.
	 * @return if the y values are ascending after index.
	 */
	public boolean isAscendingAfter(int index) {
		if (index >= list.size() - 2 || list.get(index + 1).isUndefined()) {
			return false;
		}
		Interval y1 = get(index + 1).y();
		Interval y2 = get(index + 2).y();

		return y2 != null && y2.isGreaterThan(y1);
	}

	/**
	 *
	 * @return as a stream of {@link IntervalTuple}
	 */
	public Stream<IntervalTuple> stream() {
		return list.stream();
	}

	/**
	 * Removing all (x, y) pairs such that the x interval starts lower than a given value.
	 *  - ie can cut tuples that are offscreen from left.
	 * @param low to remove to.
	 */
	public void cutTo(double low) {
		list = list.stream().filter(tuple -> tuple.x().getLow() >= low)
				.collect(Collectors.toList());
	}

	/**
	 * Remove a sublist
	 * @param other to remove.
	 * @return the modified list.
	 */
	public boolean removeAll(IntervalTupleList other) {
		return list.removeAll(other.list);
	}

	/**
	 * Sets the piece index of all tupes in the list to the given value
	 *
	 * If the tuple is created during an if-else, or if-list evaluation,
	 * each cases have a different piece index.
	 * For example If(x < 0, x, 2x) this will be 0 for x < 0 and 1 otherwise.
	 *
	 * When no conditional evaluation, there is one list only and piece is 0 for all tuples.
	 *
	 * @param piece the index that the tuple belongs to.
	 */
	public void setPiece(int piece) {
		for (IntervalTuple tuple: list) {
			tuple.setPiece(piece);
		}
	}
}
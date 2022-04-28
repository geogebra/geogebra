package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

/**
 * List to hold IntervalTuples
 *
 * @author laszlo
 */
public class IntervalTupleList implements Iterable<IntervalTuple> {
	private List<IntervalTuple> list;

	/**
	 * Constructor.
	 */
	public IntervalTupleList() {
		this.list = new ArrayList<>();
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

	public boolean removeAll(IntervalTupleList other) {
		return list.removeAll(other.list);
	}
}
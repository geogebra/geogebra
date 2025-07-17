package org.geogebra.common.kernel.interval.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.interval.Interval;

/**
 * List to hold IntervalTuples
 *
 * @author laszlo
 */
public class IntervalTupleList implements Iterable<IntervalTuple> {
	private final List<IntervalTuple> list;

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

	@Override
	public @Nonnull Iterator<IntervalTuple> iterator() {
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

	public boolean isValid() {
		return countDefined() > 1;
	}

	private long countDefined() {
		return stream().filter(t -> !t.y().isUndefined()).count();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTupleList) {
			IntervalTupleList other = (IntervalTupleList) obj;
			return list.equals(other.list);
		}
		return false;
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

	/**
	 * Clear the list.
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * @param index index
	 * @return interval at given index
	 */
	public Interval valueAt(int index) {
		return get(index).y();
	}

	/**
	 *
	 * @return as a stream of {@link IntervalTuple}
	 */
	public Stream<IntervalTuple> stream() {
		return list.stream();
	}

	/**
	 *
	 * @return the first tuple in the list.
	 */
	public IntervalTuple first() {
		return list.get(0);
	}

	/**
	 *
	 * @return the last tuple in the list.
	 */
	public IntervalTuple last() {
		return list.get(count() - 1);
	}

	/**
	 * Add tuple to the beginning of the list.
	 * @param tuple to prepend.
	 */
	public void prepend(IntervalTuple tuple) {
		list.add(0, tuple);
	}

	/**
	 * Removes the last item.
	 */
	public void removeLast() {
		list.remove(list.size() - 1);
	}

	/**
	 * Removes the first item.
	 */
	public void removeFirst() {
		list.remove(0);
	}
}
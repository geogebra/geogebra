/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.interval.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

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
		return countNotEmpty() > 1;
	}

	private long countNotEmpty() {
		return stream().filter(t -> !t.isEmpty()).count();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTupleList other) {
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
	 * Returns the tuples in encounter order as a stream.
	 *
	 * @return stream view of the tuple list
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

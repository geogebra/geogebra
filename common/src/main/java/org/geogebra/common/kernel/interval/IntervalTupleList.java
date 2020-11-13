package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		return list.get(index);
	}

	@Override
	public Iterator<IntervalTuple> iterator() {
		return list.iterator();
	}

	/**
	 *
	 * @return the size of the list
	 */
	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Set to store AlgoElement objects for updating.
 */
public class AlgorithmSet implements Iterable<AlgoElement> {

	private Set<AlgoElement> set;

	private Link head;
	private Link tail;
	private int size;

	/**
	 * Creates new algorithm set
	 */
	public AlgorithmSet() {
		size = 0;
	}

	/**
	 * Returns number of algos
	 * 
	 * @return number of algos
	 */
	final public int getSize() {
		return size;
	}

	/**
	 * Returns true iff empty
	 * 
	 * @return true iff empty
	 */
	final public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Inserts algo into set sorted by constructionIndex. Note: this leads to a
	 * topological sorting of the algorithms which is important for updating.
	 * 
	 * @return true = the algo was added, false = the algo was already in the
	 *         set
	 * @param algo
	 *            algo to be added
	 */
	final public boolean add(AlgoElement algo) {
		if (contains(algo)) {
			return false;
		}

		if (addToEmpty(algo)) {
			return true;
		}

		/*
		 * Usually we can just add an algorithm at the end of the list to have
		 * it at the right place for updating. However, in certain cases an
		 * algorithm needs to be inserted at an earlier place right after a
		 * certain parentAlgo. For example, in a regular polygon, new segments
		 * can be created later that need to be inserted directly after the
		 * parent polygon.
		 */

		// check if algo needs to be inserted right after a certain parentAlgo
		AlgoElement parentAlgo = algo.getUpdateAfterAlgo();

		// Standard case: insert at end of list
		if (parentAlgo == null || parentAlgo == tail.algo || !contains(parentAlgo)) {
			addToEnd(algo);
		} else { // Special case: insert in the middle, right after parentAlgo
			insertAfterParentAlgo(algo, parentAlgo);
		}

		set.add(algo);
		size++;

		return true;
	}

	private boolean addSorted(AlgoElement algo) {
		if (contains(algo)) {
			return false;
		}

		if (addToEmpty(algo)) {
			return true;
		}

		/*
		 * Usually we can just add an algorithm at the end of the list to have
		 * it at the right place for updating. However, in certain cases an
		 * algorithm needs to be inserted at an earlier place right after a
		 * certain parentAlgo. For example, in a regular polygon, new segments
		 * can be created later that need to be inserted directly after the
		 * parent polygon.
		 */

		// check if algo needs to be inserted right after a certain parentAlgo
		AlgoElement parentAlgo = algo.getUpdateAfterAlgo();

		// Standard case: insert at end of list
		if (parentAlgo == tail.algo) {
			addToEnd(algo);
		} else if (parentAlgo == null || !contains(parentAlgo)) {
			Link cur = getHead();
			long id = algo.getID();
			if (cur.algo.getID() > algo.getID()) {
				head = new Link(algo, head);
			} else {
				while (cur.next != null && cur.next.algo.getID() < id) {
					cur = cur.next;
				}
				cur.next = new Link(algo, cur.next);
			}
		} else { // Special case: insert in the middle, right after parentAlgo
			insertAfterParentAlgo(algo, parentAlgo);
		}

		set.add(algo);
		size++;

		return true;
	}

	private boolean addToEmpty(AlgoElement algo) {
		if (getHead() == null) {
			if (set == null) {
				set = new HashSet<>();
			}
			set.add(algo);

			setHead(new Link(algo, null));
			tail = getHead();
			size++;
			return true;
		}
		return false;
	}

	private void addToEnd(AlgoElement algo) {
		tail.next = new Link(algo, null);
		tail = tail.next;
	}

	private void insertAfterParentAlgo(AlgoElement algo, AlgoElement parentAlgo) {
		// search for parentAlgo
		Link cur = getHead();
		while (cur.algo != parentAlgo) {
			cur = cur.next;
		}

		// now cur.algo == parentAlgo, insert right afterwards
		cur.next = new Link(algo, cur.next);
	}

	/**
	 * Inserts all algos of set at the end of this set.
	 * 
	 * @param algoSet
	 *            set of algos to be added
	 */
	public void addAll(AlgorithmSet algoSet) {
		Link cur = algoSet.getHead();
		while (cur != null) {
			add(cur.algo);
			cur = cur.next;
		}
	}

	/**
	 * Inserts all algos of set into this set.
	 * 
	 * @param algoSet
	 *            set of algos to be added
	 */
	public void addAllSorted(AlgorithmSet algoSet) {
		Link cur = algoSet.getHead();
		while (cur != null) {
			addSorted(cur.algo);
			cur = cur.next;
		}
	}

	/**
	 * Returns true if this set contains algo.
	 * 
	 * @return true iff this set contains algo.
	 * @param algo
	 *            algorithm
	 */
	final public boolean contains(AlgoElement algo) {
		if (size == 0 || algo == null) {
			return false;
		}

		return set.contains(algo);
	}

	/**
	 * Removes algo from set.
	 * 
	 * @return true if found and removed, false if not found
	 * @param algo
	 *            algo to be removed
	 */
	final public boolean remove(AlgoElement algo) {
		if (set == null) {
			return false;
		}

		if (!set.remove(algo)) {
			return false;
		}

		Link prev = null;
		Link cur = getHead();
		while (cur != null) {
			// found algo to remove
			if (cur.algo == algo) {
				if (prev == null) { // remove from head
					setHead(cur.next);
					if (getHead() == null) {
						tail = null;
					}
				} else { // standard case
					prev.next = cur.next;
					if (prev.next == null) {
						tail = prev;
					}
				}
				size--;
				return true;
			}
			// not yet found
			prev = cur;
			cur = cur.next;
		}

		return false;
	}

	/**
	 * Updates all algorithms of this set.
	 */
	final public void updateAll() {
		Link cur = getHead();
		while (cur != null) {
			cur.algo.update();
			cur = cur.next;
		}
	}

	/**
	 * Updates all algorithms of this set until the given algorithm is reached.
	 * 
	 * @param lastAlgoToUpdate
	 *            last algorithm to update
	 */
	final public void updateAllUntil(AlgoElement lastAlgoToUpdate) {
		Link cur = getHead();
		while (cur != null) {
			cur.algo.update();

			if (cur.algo == lastAlgoToUpdate) {
				return;
			}
			cur = cur.next;
		}
	}

	/**
	 * Adds all algorithms in this set to the given collection
	 * 
	 * @param collection
	 *            collection of algorithms
	 */
	final public void addAllToCollection(Collection<AlgoElement> collection) {
		Link cur = getHead();
		while (cur != null) {
			collection.add(cur.algo);
			cur = cur.next;
		}
	}

	/**
	 * Removes all algorithms in this set from the given collection
	 * 
	 * @param collection
	 *            collection of algorithms
	 */
	final public void removeAllFromCollection(
			Collection<AlgoElement> collection) {
		Link cur = getHead();
		while (cur != null) {
			collection.remove(cur.algo);
			cur = cur.next;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AlgorithmSet[");

		Link cur = getHead();
		while (cur != null) {
			sb.append("\n\t");
			sb.append(cur.algo + ", constIndex: "
					+ cur.algo.getConstructionIndex() + ", ceID: "
					+ cur.algo.getID());
			cur = cur.next;
		}
		sb.append("]");
		return sb.toString();
	}

	private static class Link {
		AlgoElement algo;
		Link next;

		Link(AlgoElement a, Link n) {
			algo = a;
			next = n;
		}
	}

	/**
	 * Returns iterator for this set
	 * 
	 * @return iterator for this set
	 */
	public AlgorithmSetIterator getIterator() {
		return new AlgorithmSetIterator();
	}

	/**
	 * @return the head
	 */
	public Link getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Link head) {
		this.head = head;
	}

	/**
	 * Iterator for this set
	 */
	public class AlgorithmSetIterator implements Iterator<AlgoElement> {
		private Link cur = getHead();

		@Override
		public void remove() {
			AlgorithmSet.this.remove(cur.algo);
			cur = cur.next;
		}

		@Override
		public boolean hasNext() {
			return cur != null;
		}

		@Override
		public AlgoElement next() {
			if (cur == null) {
				throw new NoSuchElementException();
			}
			AlgoElement ret = cur.algo;
			cur = cur.next;
			return ret;
		}
	}

	@Override
	public Iterator<AlgoElement> iterator() {
		return getIterator();
	}
}

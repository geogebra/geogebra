package org.geogebra.common.kernel.discrete.tsp.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Heap<E> {

	private static final int DEFAULT_CAPACITY = 10;

	private E[] entries;

	private int size;

	private final Map<E, Integer> table;

	private final Comparator<E> comparator;

	public Heap() {
		this(null);
	}

	public Heap(int initialCapacity) {
		this(initialCapacity, null);
	}

	public Heap(Comparator<E> comparator) {
		this(Heap.DEFAULT_CAPACITY, comparator);
	}

	@SuppressWarnings("unchecked")
	public Heap(int initialCapacity, Comparator<E> comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		this.size = 0;
		this.entries = (E[]) new Object[initialCapacity + 1];
		this.table = new HashMap<E, Integer>();
		this.comparator = comparator;
	}

	@SuppressWarnings("unchecked")
	public boolean add(E key) {
		E entry = key;
		Integer pointer = this.table.get(key);
		if (pointer != null) {
			int index = pointer.intValue();
			if (this.comparator == null) {
				if (((Comparable<E>) this.entries[index]).compareTo(entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			} else {
				if (this.comparator.compare(this.entries[index], entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			}
		} else {
			this.grow(++this.size);
			this.table.put(key, this.size);
			this.entries[this.size] = entry;
			this.fixUp(this.size);
		}
		return true;
	}

	private void swap(int index1, int index2) {
		final E tmp = this.entries[index1];
		this.entries[index1] = this.entries[index2];
		this.entries[index2] = tmp;
		this.table.put(this.entries[index1], index1);
		this.table.put(this.entries[index2], index2);
	}

	public E poll() {
		if (this.size == 0) {
			return null;
		}

		final E entry = this.entries[1];
		this.table.remove(entry);
		if (this.size > 1) {
			this.entries[1] = this.entries[this.size];
			this.table.put(this.entries[1], 1);
		}
		this.entries[this.size] = null;
		if (--this.size > 1) {
			this.fixDown(1);
		}
		return entry;
	}

	public E peek() {
		return this.entries[1];
	}

	public boolean containsKey(Object key) {
		return this.table.containsKey(key);
	}

	public void clear() {
		this.table.clear();
		for (int i = 0; i <= this.size; i++) {
			this.entries[i] = null;
		}
		this.size = 0;
	}


	@SuppressWarnings("unchecked")
	private void fixDown(int index) {
		int son;
		if (this.comparator == null) {
			while ((son = index << 1) <= this.size) {
				if (son < this.size
						&& ((Comparable<E>) this.entries[son])
								.compareTo(this.entries[son + 1]) > 0) {
					son++;
				}
				if (((Comparable<E>) this.entries[index])
						.compareTo(this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		} else {
			while ((son = index << 1) <= this.size) {
				if (son < this.size
						&& this.comparator.compare(this.entries[son],
								this.entries[son + 1]) > 0) {
					son++;
				}
				if (this.comparator.compare(this.entries[index],
						this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void fixUp(int index) {
		int parent;
		if (this.comparator == null) {
			while ((parent = index >> 1) > 0) {
				if (((Comparable<E>) this.entries[index])
						.compareTo(this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		} else {
			while ((parent = index >> 1) > 0) {
				if (this.comparator.compare(this.entries[index],
						this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		}
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	@SuppressWarnings("unchecked")
	private void grow(int index) {
		int newLength = this.entries.length;
		if (index < newLength) {
			return;
		}
		if (index == Integer.MAX_VALUE) {
			throw new Error("Out of memory");
		}
		while (newLength <= index) {
			if (newLength >= Integer.MAX_VALUE / 2) {
				newLength = Integer.MAX_VALUE;
			} else {
				newLength <<= 2;
			}
		}
		final E[] newEntrys = (E[]) new Object[newLength];
		System.arraycopy(this.entries, 0, newEntrys, 0, this.entries.length);

		this.entries = newEntrys;
	}

	@Override
	public String toString() {
		if (this.size == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder(this.entries[1].toString());
		for (int i = 2; i <= this.size; i++) {
			sb.append("," + this.entries[i].toString());
		}
		return sb.toString();
	}


	public int size() {
		return this.size;
	}
}

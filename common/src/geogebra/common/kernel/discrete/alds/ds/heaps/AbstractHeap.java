/*
 * Copyright 2008 the original author or authors.
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package geogebra.common.kernel.discrete.alds.ds.heaps;

import geogebra.common.kernel.discrete.alds.SortableObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains common functionality between Max and Min Heaps
 * 
 * @author Devender Gollapally
 * 
 */
public abstract class AbstractHeap implements Heap {
	protected List<SortableObject> list;

	public AbstractHeap() {
		list = new LinkedList<SortableObject>();
	}

	/**
	 * {@inheritDoc}
	 */
	public SortableObject extract() {
		if (list.size() == 0) {
			return null;
		}

		SortableObject object = list.get(0);
		if (list.size() - 1 > 0) {
			list.set(0, list.remove(list.size() - 1));
			heapdown(0);
		}
		return object;
	}

	protected abstract void heapdown(int i);

	/**
	 * {@inheritDoc} the runtime complexity of this operation is log(n), in the
	 * worst case we have to travers the height of the tree
	 */
	public void insert(SortableObject sortableObject) {
		int index = list.size();
		list.add(sortableObject);
		heapup(index);
	}

	protected abstract void heapup(int index);

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return this.list.size();
	}

	protected void swap(int pos1, int pos2) {
		SortableObject object = list.get(pos1);
		list.set(pos1, list.get(pos2));
		list.set(pos2, object);
	}

	protected int parent(int i) {
		return (i - 1) / 2;
	}

	protected int left(int i) {
		return (2 * i) + 1;
	}

	protected int right(int i) {
		return (2 * i) + 2;
	}
}
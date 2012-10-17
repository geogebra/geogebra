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

/**
 * A simple max heap implementation
 * 
 * @author Devender Gollapally
 * 
 */
final class MaxHeapImpl extends AbstractHeap implements Heap {

	/**
	 * use the child with the max value
	 * 
	 * @param i
	 */
	protected void heapdown(int i) {
		while (true) {
			int left = left(i);
			int right = right(i);
			int childToUse = i;

			if (left < list.size()
					&& list.get(i).getValue() < list.get(left).getValue()) {
				childToUse = left;
			}

			if (right < list.size()
					&& list.get(childToUse).getValue() < list.get(right)
							.getValue()) {
				childToUse = right;
			}

			if (childToUse == i) {
				break;
			} else {
				swap(i, childToUse);
				i = childToUse;
			}
		}
	}

	/**
	 * Run when a new element is inserted into the heap
	 */
	protected void heapup(int index) {
		int parentIndex = parent(index);
		if (parentIndex > -1
				&& list.get(index).getValue() > list.get(parentIndex)
						.getValue()) {
			swap(parentIndex, index);
			heapup(parentIndex);
		}
	}

}
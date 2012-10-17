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

/**
 * (From Mastering Algorithms with C) Heaps : Trees organized so that we can
 * determine the node with the largest value quickly. The cost to preserve this
 * property is less than that of keeping the data sorted. We can also organize a
 * heap so that we can determine the smallest value just as easily.
 * 
 * There are two kinds of heaps min and max heap, in a max heap for every node
 * the parent is greater than equal to the child node.
 * 
 * Heaps are stored in arrays, the left and right child of any node i is 2(i)+1
 * and 2i+2 and parent of any node at position i in an array is (i-1)/2
 * 
 * @author Devender Gollapally
 * 
 */
public interface Heap {

	/**
	 * Insert a new object into the heap, if this violates the heap property
	 * then another procedure is called to fix it.
	 * 
	 * @param sortableObject
	 */
	void insert(SortableObject sortableObject);

	/**
	 * Extract the root node from the heap, this will either be the object with
	 * the max value or the min value depending on weather it is a max heap or a
	 * min heap.
	 * 
	 * The root node is extracted from the heap and the root node is replaced
	 * with the very last node, then the heap property is checked by comparing
	 * the root node with both its child and replaced with the child that is the
	 * greatest difference.
	 * 
	 * @return
	 */
	SortableObject extract();

	/**
	 * Number of nodes in a heap
	 * 
	 * @return
	 */
	int size();
	
}
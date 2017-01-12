// Copyright 2002, FreeHEP.
package org.freehep.util;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Unsynchronized version of a Stack.
 *
 * @author Mark Donszelmann
 */
public class FastStack extends ArrayList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -111966774350178793L;

	public FastStack() {
		this(10);
	}

	public FastStack(int initialCapacity) {
		super(initialCapacity);
	}

	public Object push(Object item) {
		add(item);
		return item;
	}

	public Object pop() {
		Object obj = peek();
		int len = size();

		remove(len - 1);

		return obj;
	}

	public Object peek() {
		int len = size();
		if (len == 0) {
			throw new EmptyStackException();
		}
		return get(len - 1);
	}
}

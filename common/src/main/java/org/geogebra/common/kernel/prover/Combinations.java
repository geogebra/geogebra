package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of iterable combinations of a set. Based on
 * http://stackoverflow.com/a/7631893. Usage:
 * 
 * Set<Integer> a = new HashSet<Integer>(); a.add(1); a.add(2); a.add(3);
 * a.add(4);
 * 
 * Combinations b = new Combinations(a,2);
 * 
 * while (b.hasNext()) { Set c = (Set) b.next(); Log.debug(c); }
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 * 
 * @param <T>
 *            element type
 */

public class Combinations<T> implements Iterator<Set<T>> {

	private Set<T> set;
	private int r, n;
	private int[] num; // representation with numbers
	private boolean done = false;

	private ArrayList<T> list;

	/**
	 * Creates all combinations of a set of the given order
	 * 
	 * @param inputSet
	 *            the input set
	 * @param order
	 *            the order
	 */
	public Combinations(Set<T> inputSet, int order) {
		set = inputSet;
		n = inputSet.size();
		r = order;
		if (n < r) { // in this case we don't have to do anything
			done = true;
			return;
		}
		num = new int[r];
		for (int i = 0; i < r; i++) {
			num[i] = i + 1;
		}
		list = new ArrayList<>(n);
		list.addAll(set);
	}

	@Override
	public boolean hasNext() {
		return !done;
	}

	@Override
	public Set<T> next() {
		Set<T> ret = new HashSet<>();
		for (int i = 0; i < r; ++i) {
			ret.add(list.get(num[i] - 1));
		}
		done = nextNum();
		return ret;
	}

	private boolean nextNum() {
		int target = r - 1;
		num[target]++;
		if (num[target] > ((n - (r - target)) + 1)) {
			// Carry the One
			while (num[target] > ((n - (r - target)))) {
				target--;
				if (target < 0) {
					break;
				}
			}
			if (target < 0) {
				return true;
			}
			num[target]++;
			for (int i = target + 1; i < num.length; i++) {
				num[i] = num[i - 1] + 1;
			}
		}
		return false;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}
}

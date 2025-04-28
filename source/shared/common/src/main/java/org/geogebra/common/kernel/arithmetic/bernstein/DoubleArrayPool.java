package org.geogebra.common.kernel.arithmetic.bernstein;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * double[] pool to optimize array creation.
 */
public class DoubleArrayPool {

	private Map<Integer, Queue<double[]>> pool = new HashMap<>();

	/**
	 * Requests an array from the pool at given size.
	 *
	 * @param length of the requested array
	 * @return the array.
	 */
	public double[] request(int length) {
		Queue<double[]> queue = ensureQueueExists(length);
		return !queue.isEmpty() ? queue.poll() : new double[length];
	}

	private Queue<double[]> ensureQueueExists(int degree) {
		return pool.computeIfAbsent(degree, deg -> new ArrayDeque<>());
	}

	/**
	 *
	 * @param array to put back into the pool
	 */
	public void release(double[] array) {
		if (array == null) {
			return;
		}
		Queue<double[]> queue = ensureQueueExists(array.length);
		queue.offer(array);
	}
}

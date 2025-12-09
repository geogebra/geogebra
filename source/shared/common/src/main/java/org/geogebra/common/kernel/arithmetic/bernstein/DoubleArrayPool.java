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

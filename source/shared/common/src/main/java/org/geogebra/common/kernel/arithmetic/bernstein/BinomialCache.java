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

import org.geogebra.common.util.MyMath;

/**
 * Cache for binomials
 * MyMath.binomial(n, k) is quite expensive, so this class makes sure that for every (n, k)
 * pair, value is computed only once, and retrieved on next calls as a quick lookup.
 *
 * If n or k are beyond the capacity of the cache, it fails back to MyMath.binomial(n, k) call.
 */
public final class BinomialCache {
	public static final int MAX_N = 100;
	public static final int MAX_K = 100;
	private static BinomialCache INSTANCE = null;
	private double[][] table;

	private BinomialCache() {
		table = new double[MAX_N][MAX_K];
	}

	private static BinomialCache get() {
		if (INSTANCE == null) {
			INSTANCE = new BinomialCache();
		}
		return INSTANCE;
	}

	/**
	 *
	 * @param n int
	 * @param k int
	 * @return (n choose k) from cache
	 */
	public static double get(int n, int k) {
		return (n < MAX_N && k < MAX_K) ? get().binomial(n, k) : MyMath.binomial(n, k);
	}

	private double binomial(int n, int k) {
		double v = table[n][k];
		if (v == 0) {
			table[n][k] = MyMath.binomial(n, k);
		}
		return table[n][k];
	}
}
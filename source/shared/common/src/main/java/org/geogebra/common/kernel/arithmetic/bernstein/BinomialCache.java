package org.geogebra.common.kernel.arithmetic.bernstein;

import org.geogebra.common.util.MyMath;

/**
 * Cache for binomials
 * MyMath.binomial(n, k) is quite expensive, so this class makes sure that for every (n, k)
 * pair, value is computed only once, and retreived on next calls as a quick lookup.
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
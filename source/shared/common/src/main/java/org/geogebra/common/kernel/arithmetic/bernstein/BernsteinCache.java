package org.geogebra.common.kernel.arithmetic.bernstein;

/**
 * Cache to optimize operations of {@link BernsteinPolynomial1D} and {@link BernsteinPolynomial2D}
 */
public final class BernsteinCache {
	static double[] tmpPartialEval = null;
	static double[] tmpLastPartialEval = null;
	static double[] tmpPartialEval2Var = null;
	static double[] tmpLastPartialEval2Var = null;
	static BernsteinCoefficientCache bPlus;
	static BernsteinCoefficientCache bMinus;

	/**
	 * Lazy creation of temporary arrays for {@link BernsteinPolynomial1D} evaluation.
	 * @param length of the arrays.
	 */
	public static final void initPartialEvals(int length) {
		if (tmpPartialEval == null || tmpPartialEval.length != length) {
			tmpPartialEval = new double[length];
			tmpLastPartialEval = new double[length];
		}
	}

	/**
	 * Latzy creation of temporary arrays for {@link BernsteinPolynomial2D} evaluation.
	 * @param length of the arrays.
	 */
	public static final void initPartialEvals2Var(int length) {
		if (tmpPartialEval2Var == null || tmpPartialEval2Var.length != length) {
			tmpPartialEval2Var = new double[length];
			tmpLastPartialEval2Var = new double[length];
		}
	}

	/**
	 * Recreates arrays for b+ and b- for splitting the polynomial
	 * @param length of the arrays.
	 */
	public static void reinitSplitCache(int length) {
		if (bPlus != null) {
			return;
		}

		bPlus = new BernsteinCoefficientCache(length);
		bMinus = new BernsteinCoefficientCache(length);
	}
}

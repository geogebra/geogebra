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

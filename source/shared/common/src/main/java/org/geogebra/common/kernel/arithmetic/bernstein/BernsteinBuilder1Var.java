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

public class BernsteinBuilder1Var {

	private double[] powerBasisCoeffs;

	BernsteinPolynomial1D build(double[] powerCoeffs,
			int degree, char variable, double min, double max) {
		powerBasisCoeffs = powerCoeffs;
		double[] bernsteinCoeffs = createBernsteinCoeffs(degree, min, max);
		return new BernsteinPolynomial1D(bernsteinCoeffs, variable, min, max);
	}

	private double[] createBernsteinCoeffs(int degree, double min, double max) {
		double[] partialBernsteinCoeffs = new double[degree + 1];
		double[] lastValues = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= i; j++) {
				double b_ij = bernsteinCoefficient(i, j, degree, min, max, lastValues);
				partialBernsteinCoeffs[j] = b_ij;
			}

			System.arraycopy(partialBernsteinCoeffs, 0, lastValues, 0, lastValues.length);
		}
		return partialBernsteinCoeffs;
	}

	private double bernsteinCoefficient(int i, int j, int degree, double min, double max,
			double[] lastValues) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		double a_nMinusI = powerBasisCoeffs[degree - i];

		if (j == 0) {
			return a_nMinusI + min * lastValues[0];
		}

		if (j == i) {
			return a_nMinusI + max * lastValues[i - 1];
		}

		double binomial = MyMath.binomial(i, j);
		return binomial * a_nMinusI
				+ min * lastValues[j]
				+ max * lastValues[j - 1];
	}
}

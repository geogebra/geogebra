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

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Term;
import org.geogebra.common.util.MyMath;

public class BernsteinBuilder2Var {
	private final BernsteinBuilder1Var builder1Var;
	private BernsteinPolynomial1D[] powerBasisCoeffs;

	public BernsteinBuilder2Var(BernsteinBuilder1Var builder1Var) {
		this.builder1Var = builder1Var;
	}

	BernsteinPolynomial2D build(Polynomial polynomial, int degreeX, int degreeY,
			BoundsRectangle limits) {
		double[][] powerCoeffs = powerCoeffsFromTwoVarPolynomial(polynomial, degreeX, degreeY);

		powerBasisCoeffs = powerToBernsteinCoeffs(powerCoeffs, degreeX, degreeY,
				limits.getYmin(), limits.getYmax());

		BernsteinPolynomial1D[] bernsteinCoeffs =
				createBernsteinCoeffs2Var(degreeX, limits.getXmin(), limits.getXmax());

		return new BernsteinPolynomial2D(bernsteinCoeffs, limits.getXmin(), limits.getXmax(),
				degreeX);
	}

	double[][] powerCoeffsFromTwoVarPolynomial(Polynomial polynomial, int degreeX, int degreeY) {
		double[][] coeffs = new double[degreeX + 1][degreeY + 1];
		for (int i = 0; i < polynomial.length(); i++) {
			Term term = polynomial.getTerm(i);
			if (term != null) {
				int powerX =
						term.degree('x');
				int powerY = term.degree('y');
					coeffs[powerX][powerY] += term.getCoefficient().evaluateDouble();
				}
			}

		return coeffs;
	}

	private BernsteinPolynomial1D[] powerToBernsteinCoeffs(double[][] coeffs, int degreeX,
			int degreeY, double minY, double maxY) {
		BernsteinPolynomial1D[] polys = new BernsteinPolynomial1D[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			polys[i] = builder1Var.build(coeffs[i], degreeY, 'y', minY, maxY);
		}
		return polys;
	}

	private BernsteinPolynomial1D[] createBernsteinCoeffs2Var(int degreeX, double min, double max) {
		BernsteinPolynomialCache partialBernsteinCoeffs = new BernsteinPolynomialCache(degreeX + 1);
		for (int i = 0; i <= degreeX; i++) {
			for (int j = 0; j <= i; j++) {
				BernsteinPolynomial1D b_ij =
						bernsteinCoefficient(i, j, degreeX, min, max,
								partialBernsteinCoeffs.last);
				partialBernsteinCoeffs.set(j, b_ij);
			}
			partialBernsteinCoeffs.update();
		}
		return partialBernsteinCoeffs.current;
	}

	private BernsteinPolynomial1D bernsteinCoefficient(int i, int j, int degree,
			double min, double max, BernsteinPolynomial1D[] lastValues) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		BernsteinPolynomial1D a_nMinusI = powerBasisCoeffs[degree - i];

		if (j == 0) {
			return lastValues[0].multiply(min).plus(a_nMinusI);
		}

		if (j == i) {
			return lastValues[i - 1].multiply(max).plus(a_nMinusI);
		}

		double binomial = MyMath.binomial(i, j);
		return a_nMinusI.multiply(binomial)
				.plus(lastValues[j].multiply(min))
				.plus(lastValues[j - 1].multiply(max));
	}
}

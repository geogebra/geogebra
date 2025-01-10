package org.geogebra.common.kernel.arithmetic.bernstein;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Term;
import org.geogebra.common.util.MyMath;

public class BernsteinBuilder2Var {
	private final BernsteinBuilder1Var builder1Var;
	private BernsteinPolynomial[] powerBasisCoeffs;

	public BernsteinBuilder2Var(BernsteinBuilder1Var builder1Var) {
		this.builder1Var = builder1Var;
	}

	BernsteinPolynomial2Var build(Polynomial polynomial, int degreeX, int degreeY,
			BoundsRectangle limits) {
		double[][] powerCoeffs = powerCoeffsFromTwoVarPolynomial(polynomial, degreeX, degreeY);

		powerBasisCoeffs = powerToBernsteinCoeffs(powerCoeffs, degreeX, degreeY,
				limits.getYmin(), limits.getYmax());

		BernsteinPolynomial[] bernsteinCoeffs =
				createBernsteinCoeffs2Var(degreeX, limits.getXmin(), limits.getXmax());

		return new BernsteinPolynomial2Var(bernsteinCoeffs, limits.getXmin(), limits.getXmax(),
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

	private BernsteinPolynomial[] powerToBernsteinCoeffs(double[][] coeffs, int degreeX,
			int degreeY, double minY, double maxY) {
		BernsteinPolynomial[] polys = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			polys[i] = builder1Var.build(coeffs[i], degreeY, 'y', minY, maxY);
		}
		return polys;
	}

	private BernsteinPolynomial[] createBernsteinCoeffs2Var(int degreeX, double min, double max) {
		BernsteinPolynomialCache partialBernsteinCoeffs = new BernsteinPolynomialCache(degreeX + 1);
		for (int i = 0; i <= degreeX; i++) {
			for (int j = 0; j <= i; j++) {
				BernsteinPolynomial b_ij = bernsteinCoefficient(i, j, degreeX, min, max,
						partialBernsteinCoeffs.last);
				partialBernsteinCoeffs.set(j, b_ij);
			}
			partialBernsteinCoeffs.update();
		}
		return partialBernsteinCoeffs.current;
	}

	private BernsteinPolynomial bernsteinCoefficient(int i, int j, int degree,
			double min, double max, BernsteinPolynomial[] lastValues) {
		if (i == 0 && j == 0) {
			return powerBasisCoeffs[degree];
		}

		BernsteinPolynomial a_nMinusI = powerBasisCoeffs[degree - i];

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

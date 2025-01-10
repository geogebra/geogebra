package org.geogebra.common.kernel.arithmetic.bernstein;

import static org.geogebra.common.kernel.arithmetic.bernstein.BinomialCoefficientsSign.from1Var;

public final class BernsteinPolynomial1Var implements BernsteinPolynomial {
	private final double min;
	private final double max;
	final int degree;
	final char variableName;
	final double[] bernsteinCoeffs;
	double[] dividedCoeffs;
	private final BinomialCoefficientsSign sign;

	/**
	 * @param bernsteinCoeffs coeffs for x^k (1-x)^(degree - k), NOT divided by binomial coeffs
	 * @param variableName variable name
	 * @param min min value for original variable
	 * @param max max value for original variable
	 */
	public BernsteinPolynomial1Var(double[] bernsteinCoeffs,
			char variableName, double min, double max) {
		this.variableName = variableName;
		this.min = min;
		this.max = max;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
		this.dividedCoeffs = null;
		sign = from1Var(bernsteinCoeffs, degree);
	}

	/**
	 * Divided coefficients are needed only for evaluation and splitting.
	 *
	 */
	private void createLazyDivideCoeffs() {
		if (dividedCoeffs != null) {
			return;
		}
		dividedCoeffs = new double[degree + 1];
		for (int i = 0; i < degree + 1; i++) {
			dividedCoeffs[i] = bernsteinCoeffs[i] / BinomialCache.get(degree, i);
		}
	}

	@Override
	public double evaluate(double value) {
		createLazyDivideCoeffs();
		if (value == 0) {
			return dividedCoeffs[0];
		}

		if (value == 1) {
			return dividedCoeffs[degree];
		}

		double[] partialEval = new double[degree + 1];
		double[] lastPartialEval = new double[degree + 1];
		double scaledValue = value;
		double oneMinusScaledValue = 1 - scaledValue;

		copyArrayTo(dividedCoeffs, lastPartialEval);
		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				partialEval[j] = oneMinusScaledValue * lastPartialEval[j]
						+ scaledValue * lastPartialEval[j + 1];
			}
			copyArrayTo(partialEval, lastPartialEval);
		}
		return partialEval[0];
	}

	@Override
	public double evaluate(double x, double y) {
		// y is ignored in 1var case.
		return evaluate(x);
	}

	@Override
	public BernsteinPolynomial[] split() {
		BernsteinCoefficientCache bPlus = new BernsteinCoefficientCache(degree + 1);
		BernsteinCoefficientCache bMinus = new BernsteinCoefficientCache(degree + 1);

		createLazyDivideCoeffs();
		for (int i = 0; i < degree + 1; i++) {
			double[] coeffs = new double[1];
			coeffs[0] = dividedCoeffs[i];
			bPlus.setLast(i, coeffs);
			bMinus.setLast(i, coeffs);
		}

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				double[][] slices = getSlices(bPlus.last[j], bPlus.last[j + 1], bMinus.last[j],
						bMinus.last[j + 1]);

				bPlus.set(j, slices[0]);
				bMinus.set(j, slices[1]);
			}
			bPlus.update();
			bMinus.update();
		}

		return new BernsteinPolynomial[]{newInstance(bPlus.last[0]),
				newInstance(bMinus.last[0])};
	}

	private double[][] getSlices(double[] pcoeffs, double[] potherCoeffs,
			double[] mcoeffs, double[] motherCoeffs) {
		int degreeX = pcoeffs.length + 1;
		double[] slicePositive = new double[degreeX];
		double[] sliceNegative = new double[degreeX];

		for (int i = 0; i < degreeX; i++) {
			slicePositive[i] = getBernsteinPositiveHalf(pcoeffs, potherCoeffs, i);
			sliceNegative[i] = getBernsteinNegativeHalf(mcoeffs, motherCoeffs, i);
		}
		return new double[][]{slicePositive, sliceNegative};
	}

	private static double getBernsteinNegativeHalf(double[] mcoeffs, double[] motherCoeffs, int i) {
		return (getSafe(mcoeffs, i) + getSafe(motherCoeffs, i)) / 2
				+ getSafe(motherCoeffs, i - 1);
	}

	private static double getBernsteinPositiveHalf(double[] pcoeffs, double[] potherCoeffs, int i) {
		return getSafe(pcoeffs, i) + (i >= 1 ? (pcoeffs[i - 1] + potherCoeffs[i - 1]) / 2 : 0);
	}

	static double getSafe(double[] coeffs, int i) {
		return i < coeffs.length && i >= 0 ? coeffs[i] : 0;
	}

	private BernsteinPolynomial newInstance(double[] coeffs) {
		return new BernsteinPolynomial1Var(coeffs, variableName, min, max);
	}

	@Override
	public BernsteinPolynomial[][] split2D() {
		return null;
	}

	static void copyArrayTo(double[] src, double[] dest) {
		System.arraycopy(src, 0, dest, 0, dest.length);
	}

	@Override
	public BernsteinPolynomial derivative() {
		double[] derivedCoeffs = new double[degree];
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[i];
			double b2 = (i + 1) * bernsteinCoeffs[i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		return newInstance(derivedCoeffs);
	}

	@Override
	public BernsteinPolynomial multiply(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * value;
		}
		return newInstance(coeffs);

	}

	@Override
	public BernsteinPolynomial divide(double value) {
		return multiply(1.0 / value);
	}

	@Override
	public BernsteinPolynomial plus(BernsteinPolynomial bernsteinPolynomial) {
		if (bernsteinPolynomial == null) {
			return this;
		}
		double[] coeffs = new double[degree + 1];
		BernsteinPolynomial1Var polynomial1Var =
				(BernsteinPolynomial1Var) bernsteinPolynomial;

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i]
					+ polynomial1Var.bernsteinCoeffs[i];
		}
		return newInstance(coeffs);
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		return derivative();
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	@Override
	public int numberOfVariables() {
		return 1;
	}

	@Override
	public boolean hasNoSolution() {
		return sign.monotonic();
	}

	@Override
	public BernsteinPolynomial substitute(String variable, double value) {
		return null;
	}

	@Override
	public BernsteinPolynomial linearCombination(BernsteinPolynomial coeffs,
			BernsteinPolynomial otherPoly, BernsteinPolynomial otherCoeffs) {
		return null;
	}

	@Override
	public String toString() {
		return BernsteinToString.toString1Var(this);
	}

	@Override
	public BinomialCoefficientsSign getSign() {
		return sign;
	}

	@Override
	public BernsteinPolynomial linearCombination(double coeff, BernsteinPolynomial otherPoly,
			double otherCoeff) {
		if (otherPoly == null) {
			return this;
		}
		double[] coeffs = new double[degree + 1];

		BernsteinPolynomial1Var otherPoly1 = (BernsteinPolynomial1Var) otherPoly;
		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * coeff
					+ otherPoly1.bernsteinCoeffs[i] * otherCoeff;
		}
		return newInstance(coeffs);
	}

	@Override
	public int degreeX() {
		return degree;
	}

	@Override
	public int degreeY() {
		return 0;
	}

	@Override
	public BernsteinPolynomial[] splitCoefficients() {
		return null;
	}
}
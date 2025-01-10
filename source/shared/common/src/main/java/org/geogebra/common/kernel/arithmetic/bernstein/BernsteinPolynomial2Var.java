package org.geogebra.common.kernel.arithmetic.bernstein;

import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial1Var.copyArrayTo;

public class BernsteinPolynomial2Var implements BernsteinPolynomial {
	private final double minX;
	private final double maxX;
	final int degreeX;
	final BernsteinPolynomial[] bernsteinCoeffs;
	BernsteinPolynomial[] dividedCoeffs;
	private BinomialCoefficientsSign sign;

	public BernsteinPolynomial2Var(BernsteinPolynomial2Var polynomial, int min, int max) {
		this(polynomial.bernsteinCoeffs, min, max, polynomial.degreeX);
	}

	/**
	 * @param bernsteinCoeffs coefficients in x
	 * @param minX min value for original x
	 * @param maxX max value for original x
	 * @param degreeX degree in x
	 */
	public BernsteinPolynomial2Var(BernsteinPolynomial[] bernsteinCoeffs, double minX, double maxX,
			int degreeX) {
		this.minX = minX;
		this.maxX = maxX;
		this.degreeX = degreeX;
		this.bernsteinCoeffs = bernsteinCoeffs;
		this.dividedCoeffs = null;
		sign = BinomialCoefficientsSign.from2Var(bernsteinCoeffs);
	}

	/**
	 * Divided coefficients are needed only for evaluation and splitting.
	 * If there is no solution, none of those is called by the algo.
	 *
	 */
	private void createLazyDivideCoeffs() {
		if (dividedCoeffs != null) {
			return;
		}
		dividedCoeffs = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i < degreeX + 1; i++) {
			dividedCoeffs[i] = bernsteinCoeffs[i].divide(BinomialCache.get(degreeX, i));
		}
	}

	@Override
	public double evaluate(double valueX, double valueY) {
		createLazyDivideCoeffs();
		if (valueX == 0) {
			return dividedCoeffs[0].evaluate(valueY);
		}

		if (valueX == 1) {
			return dividedCoeffs[degreeX].evaluate(valueY);
		}

		double[] partialEval = new double[degreeX + 1];
		double[] lastPartialEval = new double[degreeX + 1];
		double scaledX = valueX;
		double scaledOneMinusX = 1 - scaledX;

		for (int i = 0; i < degreeX + 1; i++) {
			lastPartialEval[i] = dividedCoeffs[i].evaluate(valueY);
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				partialEval[j] = scaledOneMinusX * lastPartialEval[j]
						+ scaledX * lastPartialEval[j + 1];
			}
			copyArrayTo(partialEval, lastPartialEval);
		}
		return partialEval[0];
	}

	private BernsteinPolynomial substituteX(double value) {
		BernsteinPolynomial result = bernsteinCoeffs[0].multiply(Math.pow(1 - value, degreeX));

		double powX = value;
		double powOneMinusX = Math.pow(1 - value, degreeX - 1);
		double d = value == 1 ? 1 :  1 - value;
		for (int i = 1; i < degreeX + 1; i++) {
			result = result.linearCombination(1, bernsteinCoeffs[i],
					powX * powOneMinusX);
			powX *= value;
			powOneMinusX /= d;
		}
		return result;
	}

	private BernsteinPolynomial substituteY(double value) {
		double[] coeffs = new double[degreeX + 1];

		for (int i = 0; i < degreeX + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i].evaluate(value);
		}

		return new BernsteinPolynomial1Var(coeffs, 'x', minX, maxX);
	}

	@Override
	public double evaluate(double value) {
		return 0;
	}

	@Override
	public BernsteinPolynomial derivative() {
		return null;
	}

	@Override
	public BernsteinPolynomial multiply(double value) {
		BernsteinPolynomial[] coeffs = new BernsteinPolynomial[bernsteinCoeffs.length];
		for (int i = 0; i < bernsteinCoeffs.length; i++) {
			coeffs[i] = bernsteinCoeffs[i] != null ? bernsteinCoeffs[i].multiply(value) : null;
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
		BernsteinPolynomial[] coeffs = new BernsteinPolynomial[bernsteinCoeffs.length];
		if (bernsteinPolynomial.numberOfVariables() == 2) {
			BernsteinPolynomial2Var bpoly2Var =
					(BernsteinPolynomial2Var) bernsteinPolynomial;
			for (int i = 0; i < bernsteinCoeffs.length; i++) {
				BernsteinPolynomial coeff = bernsteinCoeffs[i];
				BernsteinPolynomial otherCoeff = bpoly2Var.bernsteinCoeffs[i];
				if (coeff != null && otherCoeff != null) {
					coeffs[i] = coeff.plus(otherCoeff);
				} else if (coeff == null) {
					coeffs[i] = otherCoeff;
				} else {
					coeffs[i] = coeff;

				}
			}
		} else {
			throw new IllegalArgumentException("Incompatible addition");
		}
		return newInstance(coeffs);
	}

	@Override
	public int numberOfVariables() {
		return 2;
	}

	@Override
	public boolean hasNoSolution() {
		return sign.monotonic();
	}

	@Override
	public BinomialCoefficientsSign getSign() {
		return sign;
	}

	@Override
	public BernsteinPolynomial derivative(String variable) {
		if ("x".equals(variable)) {
			return derivativeX();
		}
		return derivativeY();
	}

	@Override
	public BernsteinPolynomial[] split() {
		BernsteinCoefficientsCache2Var bPlus = new BernsteinCoefficientsCache2Var(degreeX + 1);
		BernsteinCoefficientsCache2Var bMinus = new BernsteinCoefficientsCache2Var(degreeX + 1);
		createLazyDivideCoeffs();
		for (int i = 0; i < degreeX + 1; i++) {
			BernsteinPolynomial[] coeffs = new BernsteinPolynomial[1];
			coeffs[0] = dividedCoeffs[i];
			bPlus.setLast(i, coeffs);
			bMinus.setLast(i, coeffs);
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				BernsteinPolynomial[][] slices = getSlices(bPlus.last[j], bPlus.last[j + 1],
						bMinus.last[j], bMinus.last[j + 1]);
				bPlus.set(j, slices[0]);
				bMinus.set(j, slices[1]);
			}
			bPlus.update();
			bMinus.update();
		}
		return new BernsteinPolynomial[]{newInstance(bPlus.last[0]),
				newInstance(bMinus.last[0])};
	}

	private BernsteinPolynomial[][] getSlices(BernsteinPolynomial[] pcoeffs,
			BernsteinPolynomial[] potherCoeffs, BernsteinPolynomial[] mcoeffs,
			BernsteinPolynomial[] motherCoeffs) {
		BernsteinPolynomial[] slicePositive = new BernsteinPolynomial[pcoeffs.length + 1];
		BernsteinPolynomial[] sliceNegative = new BernsteinPolynomial[mcoeffs.length + 1];

		slicePositive[0] = pcoeffs[0];
		for (int i = 0; i < pcoeffs.length; i++) {
			slicePositive[i + 1] = getSlicePositive(pcoeffs, potherCoeffs, i);
			sliceNegative[i] = getSliceNegative(mcoeffs, motherCoeffs, i);
		}

		sliceNegative[mcoeffs.length] = motherCoeffs[motherCoeffs.length - 1];

		return new BernsteinPolynomial[][]{slicePositive, sliceNegative};
	}

	private static BernsteinPolynomial getSliceNegative(BernsteinPolynomial[] mcoeffs,
			BernsteinPolynomial[] motherCoeffs, int i) {
		BernsteinPolynomial slice = plusAndDivideBy2(mcoeffs, motherCoeffs, i);
		if (i > 0) {
			slice = slice.plus(motherCoeffs[i - 1]);
		}

		return slice;
	}

	private static BernsteinPolynomial plusAndDivideBy2(BernsteinPolynomial[] mcoeffs,
			BernsteinPolynomial[] motherCoeffs, int i) {
		return mcoeffs[i].linearCombination(0.5, motherCoeffs[i], 0.5);
	}

	private static BernsteinPolynomial getSlicePositive(BernsteinPolynomial[] pcoeffs,
			BernsteinPolynomial[] potherCoeffs, int i) {

		BernsteinPolynomial slice = plusAndDivideBy2(pcoeffs, potherCoeffs, i);

		if (i < pcoeffs.length - 1) {
			slice = slice.plus(pcoeffs[i + 1]);
		}
		return slice;
	}

	private BernsteinPolynomial newInstance(BernsteinPolynomial[] coeffs) {
		return new BernsteinPolynomial2Var(coeffs, minX, maxX, degreeX);
	}

	@Override
	public BernsteinPolynomial[][] split2D() {
		BernsteinPolynomial[] mainSplit = split();
		return new BernsteinPolynomial[][] {mainSplit[0].splitCoefficients(),
				mainSplit[1].splitCoefficients()};
	}

	/**
	 * Splits the coefficients which are also Bernstein polynomials
	 * @return the split parts.
	 */
	@Override
	public BernsteinPolynomial[] splitCoefficients() {
		BernsteinPolynomial[] bPlusCoeffs = new BernsteinPolynomial[degreeX + 1];
		BernsteinPolynomial[] bMinusCoeffs = new BernsteinPolynomial[degreeX + 1];

		for (int i = 0; i < bernsteinCoeffs.length; i++) {
			BernsteinPolynomial coeff = bernsteinCoeffs[i];
			if (coeff != null) {
				BernsteinPolynomial[] splitCoeffs = coeff.split();
				bPlusCoeffs[i] = splitCoeffs[0];
				bMinusCoeffs[i] = splitCoeffs[1];
			}
		}

		BernsteinPolynomial bPlusInY =
				new BernsteinPolynomial2Var(bPlusCoeffs, minX, maxX, degreeX);

		BernsteinPolynomial bMinusInY =
				new BernsteinPolynomial2Var(bMinusCoeffs, minX, maxX, degreeX);

		return new BernsteinPolynomial[] {bPlusInY, bMinusInY};
	}

	private BernsteinPolynomial derivativeX() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX];
		for (int i = 0; i < degreeX; i++) {
			derivedCoeffs[i] = bernsteinCoeffs[i].linearCombination(-degreeX + i,
					bernsteinCoeffs[i + 1], i + 1);
		}
		return new BernsteinPolynomial2Var(derivedCoeffs, minX, maxX, degreeX - 1);
	}

	private BernsteinPolynomial derivativeY() {
		BernsteinPolynomial[] derivedCoeffs = new BernsteinPolynomial[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			BernsteinPolynomial b2 = bernsteinCoeffs[i];
			derivedCoeffs[i] = b2.derivative();
		}
		return new BernsteinPolynomial2Var(derivedCoeffs, minX, maxX, degreeX);
	}

	@Override
	public String toString() {
		return BernsteinToString.toString2Var(this);
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	@Override
	public BernsteinPolynomial substitute(String variable, double value) {
		if ("x".equals(variable)) {
			return substituteX(value);
		}
		return substituteY(value);
	}

	@Override
	public BernsteinPolynomial linearCombination(BernsteinPolynomial coeffs,
			BernsteinPolynomial otherPoly, BernsteinPolynomial otherCoeffs) {
		return null;
	}

	@Override
	public BernsteinPolynomial linearCombination(double coeff, BernsteinPolynomial otherPoly,
			double otherCoeff) {
		return null;
	}

	@Override
	public int degreeX() {
		return degreeX;
	}

	@Override
	public int degreeY() {
		return bernsteinCoeffs[0].degreeX();
	}
}
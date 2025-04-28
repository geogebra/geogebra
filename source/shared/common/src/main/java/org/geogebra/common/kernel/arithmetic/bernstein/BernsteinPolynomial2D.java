package org.geogebra.common.kernel.arithmetic.bernstein;

import java.util.Arrays;

import org.geogebra.common.kernel.arithmetic.Splittable;

public class BernsteinPolynomial2D extends BernsteinPolynomial<BernsteinPolynomial2D>
		implements Splittable<BernsteinPolynomial2D[]> {
	private final double minX;
	private final double maxX;
	final int degreeX;
	final BernsteinPolynomial1D[] bernsteinCoeffs;
	BernsteinPolynomial1D[] dividedCoeffs;
	private static BernsteinCoefficientsCache2Var bPlus = null;
	private static BernsteinCoefficientsCache2Var bMinus = null;

	/**
	 * @param bernsteinCoeffs coefficients in x
	 * @param minX min value for original x
	 * @param maxX max value for original x
	 * @param degreeX degree in x
	 */
	public BernsteinPolynomial2D(BernsteinPolynomial1D[] bernsteinCoeffs, double minX, double maxX,
			int degreeX) {
		this(bernsteinCoeffs, minX, maxX, degreeX,
				BinomialCoefficientsSign.from2Var(bernsteinCoeffs));
	}

	private BernsteinPolynomial2D(BernsteinPolynomial1D[] bernsteinCoeffs, double minX, double maxX,
			int degreeX, BinomialCoefficientsSign sign) {
		this.minX = minX;
		this.maxX = maxX;
		this.degreeX = degreeX;
		this.bernsteinCoeffs = bernsteinCoeffs;
		this.dividedCoeffs = null;
		setSign(sign);
	}

	/**
	 * Creates a 2D Bernstein polynomial from 1D coeffs iff it would have a solution, it returns
	 * null otherwise.
	 * Notice that we only interested in 2D Bernstein polynomials that might have a solution in
	 * case of plotting implicit polynomials.
	 * @param bernsteinCoeffs 1D Bernstein polynomials as coefficients for 2D.
	 * @param minX domain minimum of x variable
	 * @param maxX domain maximum of x variable
	 * @param maxDegreeX maximum degree of x variable
	 * @return the new {@link BernsteinPolynomial2D} if it may has a solution, null otherwise.
	 */
	public static final BernsteinPolynomial2D create(BernsteinPolynomial1D[] bernsteinCoeffs,
			double minX, double maxX,
			int maxDegreeX) {
		BinomialCoefficientsSign sign =
				BinomialCoefficientsSign.from2Var(bernsteinCoeffs);
		return sign.monotonic() ? null : new BernsteinPolynomial2D(bernsteinCoeffs, minX, maxX,
				maxDegreeX, sign);
	}

	/**
	 * Divided coefficients are needed only for evaluation and splitting.
	 * If there is no solution, none of those is called by the algo.
	 */
	private void createLazyDivideCoeffs() {
		if (dividedCoeffs != null) {
			return;
		}
		dividedCoeffs = new BernsteinPolynomial1D[degreeX + 1];
		Arrays.setAll(dividedCoeffs, i -> {
			dividedCoeffs[i] = bernsteinCoeffs[i].divide(BinomialCache.get(degreeX, i));
			return dividedCoeffs[i];
		});
		BernsteinCache.initPartialEvals2Var(degreeX + 1);
		if (bPlus == null || bPlus.size() != degreeX + 1) {
			bPlus = new BernsteinCoefficientsCache2Var(degreeX + 1);
			bMinus = new BernsteinCoefficientsCache2Var(degreeX + 1);

		}

	}

	/**
	 * Evaluates the polynomial.
	 * @param x to evaluate at.
	 * @param y to evaluate at.
	 * @return the value at (x, y)
	 */
	public double evaluate(double x, double y) {
		createLazyDivideCoeffs();

		if (x == 0) {
			return dividedCoeffs[0].evaluate(y);
		}

		if (x == 1) {
			return dividedCoeffs[degreeX].evaluate(y);
		}

		double[] partialEval = BernsteinCache.tmpPartialEval2Var;
		double[] lastPartialEval = BernsteinCache.tmpLastPartialEval2Var;
		double scaledX = x;
		double scaledOneMinusX = 1 - scaledX;

		for (int i = 0; i < degreeX + 1; i++) {
			lastPartialEval[i] = dividedCoeffs[i].evaluate(y);
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				partialEval[j] = scaledOneMinusX * lastPartialEval[j]
						+ scaledX * lastPartialEval[j + 1];
			}
			double[] temp = lastPartialEval;
			lastPartialEval = partialEval;
			partialEval = temp;
		}
		return partialEval[0];
	}

	private BernsteinPolynomial substituteX(double value) {
		BernsteinPolynomial1D result = bernsteinCoeffs[0].multiply(Math.pow(1 - value, degreeX));

		double powX = value;
		double powOneMinusX = Math.pow(1 - value, degreeX - 1);
		double d = value == 1 ? 1 : 1 - value;
		for (int i = 1; i < degreeX + 1; i++) {
			result.linearCombinationInPlace(1, bernsteinCoeffs[i],
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

		return new BernsteinPolynomial1D(coeffs, 'x', minX, maxX);
	}

	private BernsteinPolynomial1D[][] getSlices(BernsteinPolynomial1D[] pcoeffs,
			BernsteinPolynomial1D[] potherCoeffs, BernsteinPolynomial1D[] mcoeffs,
			BernsteinPolynomial1D[] motherCoeffs) {
		BernsteinPolynomial1D[] slicePositive = new BernsteinPolynomial1D[pcoeffs.length + 1];
		BernsteinPolynomial1D[] sliceNegative = new BernsteinPolynomial1D[mcoeffs.length + 1];

		slicePositive[0] = pcoeffs[0];
		for (int i = 0; i < pcoeffs.length; i++) {
			slicePositive[i + 1] = getSlicePositive(pcoeffs, potherCoeffs, i);
			sliceNegative[i] = getSliceNegative(mcoeffs, motherCoeffs, i);
		}

		sliceNegative[mcoeffs.length] = motherCoeffs[motherCoeffs.length - 1];

		return new BernsteinPolynomial1D[][]{slicePositive, sliceNegative};
	}

	private static BernsteinPolynomial1D getSliceNegative(BernsteinPolynomial1D[] mcoeffs,
			BernsteinPolynomial1D[] motherCoeffs, int i) {
		BernsteinPolynomial1D slice = plusAndDivideBy2(mcoeffs, motherCoeffs, i);
		if (i > 0) {
			slice = slice.plus(motherCoeffs[i - 1]);
		}

		return slice;
	}

	private static BernsteinPolynomial1D plusAndDivideBy2(BernsteinPolynomial1D[] mcoeffs,
			BernsteinPolynomial1D[] motherCoeffs, int i) {
		return mcoeffs[i].linearCombination(0.5, motherCoeffs[i], 0.5);
	}

	private static BernsteinPolynomial1D getSlicePositive(BernsteinPolynomial1D[] pcoeffs,
			BernsteinPolynomial1D[] potherCoeffs, int i) {

		BernsteinPolynomial1D slice = plusAndDivideBy2(pcoeffs, potherCoeffs, i);

		if (i < pcoeffs.length - 1) {
			slice = slice.plus(pcoeffs[i + 1]);
		}
		return slice;
	}

	private BernsteinPolynomial2D newInstance(BernsteinPolynomial1D[] coeffs) {
		return create(coeffs, minX, maxX, degreeX);
	}

	/**
	 * Splits polynomial into two:
	 * b(x) => b1(x1), b2(x2) where x is element of [a, b],
	 * and x1, x2 are elements of [a, (b-a)/ 2], ((b-a)/2, b] respectively.
	 * @return the two polynomials
	 */
	@Override
	public BernsteinPolynomial2D[][] split() {
		createLazyDivideCoeffs();
		for (int i = 0; i < degreeX + 1; i++) {
			BernsteinPolynomial1D[] coeffs = new BernsteinPolynomial1D[1];
			coeffs[0] = dividedCoeffs[i];
			bPlus.setLast(i, coeffs);
			bMinus.setLast(i, coeffs);
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				BernsteinPolynomial1D[][] slices = getSlices(bPlus.last[j], bPlus.last[j + 1],
						bMinus.last[j], bMinus.last[j + 1]);
				bPlus.set(j, slices[0]);
				bMinus.set(j, slices[1]);
			}
			bPlus.update();
			bMinus.update();
		}

		BernsteinPolynomial2D split00 = newInstance(bPlus.last[0]);
		BernsteinPolynomial2D split10 = newInstance(bMinus.last[0]);

		BernsteinPolynomial2D split01 = split00 != null ? split00.splitCoefficients() : null;
		BernsteinPolynomial2D split11 = split10 != null ? split10.splitCoefficients() : null;
		return new BernsteinPolynomial2D[][]{{split00, split01}, {split10, split11}};
	}

	/**
	 * Splits the coefficients which are also Bernstein polynomials
	 * @return the split parts.
	 */
	public BernsteinPolynomial2D splitCoefficients() {
		int length = degreeX + 1;
		BernsteinPolynomial1D[] bMinusCoeffs = new BernsteinPolynomial1D[length];
		for (int i = 0; i < length; i++) {
			BernsteinPolynomial1D[] splitCoeffs = bernsteinCoeffs[i].split();
			bernsteinCoeffs[i] = splitCoeffs[0];
			bMinusCoeffs[i] = splitCoeffs[1];
		}

		setSign(BinomialCoefficientsSign.from2Var(bernsteinCoeffs));

		BernsteinPolynomial2D bMinusInY =
				create(bMinusCoeffs, minX, maxX, degreeX);

		return bMinusInY;
	}

	/**
	 * @return true iff only dx or dy has a solution, but not both.
	 */
	public boolean onlyOnePartialDerivateHasSolution() {
		for (int i = 0; i < degreeX; i++) {
			BernsteinPolynomial dx = bernsteinCoeffs[i].linearCombination(-degreeX + i,
					bernsteinCoeffs[i + 1], i + 1);

			if (dx.hasNoSolution() != bernsteinCoeffs[i].hasDerivativeNoSolution()) {
				return true;
			}

		}
		return false;
	}

	@Override
	public String toString() {
		return BernsteinToString.toString2Var(this);
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	/**
	 * Substitutes the given variable with the specified value.
	 * @param variable to substitute.
	 * @param value to substitute with.
	 * @return the polynomial replaced variable by the given value.
	 */
	public BernsteinPolynomial substitute(String variable, double value) {
		if ("x".equals(variable)) {
			return substituteX(value);
		}
		return substituteY(value);
	}
}
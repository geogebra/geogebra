package org.geogebra.common.kernel.arithmetic.bernstein;

import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.bMinus;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.bPlus;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.tmpLastPartialEval;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.tmpPartialEval;
import static org.geogebra.common.kernel.arithmetic.bernstein.BinomialCoefficientsSign.from1Var;

import org.geogebra.common.kernel.arithmetic.Splittable;

public final class BernsteinPolynomial1D extends BernsteinPolynomial<BernsteinPolynomial1D>
		implements Splittable<BernsteinPolynomial1D> {
	private final double min;
	private final double max;
	final int degree;
	final char variableName;
	double[] bernsteinCoeffs;
	double[] dividedCoeffs;
	private double[][] singletonCoeffs;
	private boolean splitRan = false;
	private double[] tempSliceNegative;
	public static final DoubleArrayPool pool = new DoubleArrayPool();

	/**
	 * @param bernsteinCoeffs coeffs for x^k (1-x)^(degree - k), NOT divided by binomial coeffs
	 * @param variableName variable name
	 * @param min min value for original variable
	 * @param max max value for original variable
	 */
	public BernsteinPolynomial1D(double[] bernsteinCoeffs,
			char variableName, double min, double max) {
		this.variableName = variableName;
		this.min = min;
		this.max = max;
		this.degree = bernsteinCoeffs.length - 1;
		this.bernsteinCoeffs = bernsteinCoeffs;
		this.dividedCoeffs = null;
		super.setSign(from1Var(bernsteinCoeffs, degree));
	}

	/**
	 * Divided coefficients are needed only for evaluation and splitting.
	 */
	private void createLazyDivideCoeffs() {
		if (dividedCoeffs != null) {
			return;
		}
		dividedCoeffs = new double[degree + 1];
		for (int i = 0; i < dividedCoeffs.length; i++) {
			dividedCoeffs[i] = bernsteinCoeffs[i] / BinomialCache.get(degree, i);
		}
		BernsteinCache.initPartialEvals(degree + 1);

	}

	/**
	 * Evaluates the polynomial at a given value
	 *
	 * @param value to evaluate at.
	 * @return the result of the evaluation.
	 */
	public double evaluate(double value) {
		createLazyDivideCoeffs();
		if (value == 0) {
			return dividedCoeffs[0];
		}

		if (value == 1) {
			return dividedCoeffs[degree];
		}

		double[] partialEval = tmpPartialEval;
		double[] lastPartialEval = tmpLastPartialEval;
		double scaledValue = value;
		double oneMinusScaledValue = 1 - scaledValue;

		System.arraycopy(dividedCoeffs, 0, lastPartialEval, 0, lastPartialEval.length);

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				partialEval[j] = oneMinusScaledValue * lastPartialEval[j]
						+ scaledValue * lastPartialEval[j + 1];
			}
			double[] temp = lastPartialEval;
			lastPartialEval = partialEval;
			partialEval = temp;
		}
		return partialEval[0];
	}

	@Override
	public BernsteinPolynomial1D[] split() {
		createLazyDivideCoeffs();
		doBeforeSplit();
		BernsteinCache.reinitSplitCache(degree + 1);

		for (int i = 0; i < degree + 1; i++) {
			bPlus.setLastNoCopy(i, singletonCoeffs[i]);
			bMinus.setLastNoCopy(i, singletonCoeffs[i]);
		}

		tempSliceNegative = pool.request(degree + 1);

		for (int i = 1; i <= degree + 1; i++) {
			double[] plusLastJ1 = bPlus.last[degree - i + 1];
			double[] minusLastJ1 = bMinus.last[degree - i + 1];
			for (int j = degree - i; j >= 0; j--) {
				double[] plusLastJ = bPlus.last[j];
				double[] minusLastJ = bMinus.last[j];
				int newLength = i + 1;
				getSlices(plusLastJ, plusLastJ1, minusLastJ,
						minusLastJ1, newLength);
				bPlus.set(j, bernsteinCoeffs, degree + 1, newLength);
				bMinus.set(j, tempSliceNegative, degree + 1, newLength);
				plusLastJ1 = plusLastJ;
				minusLastJ1 = minusLastJ;
			}
			bPlus.update();
			bMinus.update();
		}
		pool.release(tempSliceNegative);
		bernsteinCoeffs = bPlus.last[0];
		setSign(from1Var(bernsteinCoeffs, degree + 1));
		return new BernsteinPolynomial1D[]{this, newInstance(bMinus.last[0])};
	}

	private void doBeforeSplit() {
		if (splitRan) {
			return;
		}
		splitRan = true;
		singletonCoeffs = new double[degree + 1][1];
		for (int i = 0; i < degree + 1; i++) {
			singletonCoeffs[i][0] = dividedCoeffs[i];
		}
	}

	private void getSlices(double[] pcoeffs, double[] potherCoeffs,
			double[] mcoeffs, double[] motherCoeffs, int degreeX) {
		double prevMOtherCoeff = 0;
		bernsteinCoeffs[0] = pcoeffs[0];
		tempSliceNegative[0] = (mcoeffs[0] + motherCoeffs[0]) * 0.5 + prevMOtherCoeff;
		double prevPCoeff = pcoeffs[0];
		double prevPOtherCoeff = potherCoeffs[0];
		prevMOtherCoeff = motherCoeffs[0];

		for (int i = 1; i < degreeX - 1; i++) {
			double pCoeff = pcoeffs[i];
			double mCoeff = mcoeffs[i];
			double mOtherCoeff = motherCoeffs[i];
			bernsteinCoeffs[i] = pCoeff + (prevPCoeff + prevPOtherCoeff) * 0.5;
			tempSliceNegative[i] = (mCoeff + mOtherCoeff) * 0.5 + prevMOtherCoeff;
			// Update previous coefficients for the next iteration
			prevPCoeff = pCoeff;
			prevPOtherCoeff = potherCoeffs[i];
			prevMOtherCoeff = mOtherCoeff;
		}

		bernsteinCoeffs[degreeX - 1] = (prevPCoeff + prevPOtherCoeff) * 0.5;
		tempSliceNegative[degreeX - 1] = prevMOtherCoeff;

	}

	private BernsteinPolynomial1D newInstance(double[] coeffs) {
		return new BernsteinPolynomial1D(coeffs, variableName, min, max);
	}

	/**
	 *
	 * @return true iff the derivate of this polynomial has no solution.
	 */
	public boolean hasDerivativeNoSolution() {
		double coeff = bernsteinCoeffs[0];
		double lastCoeff = coeff;
		boolean hasPositive = false, hasNegative = false;

		for (int j = 1; j < degree + 1; j++) {
			coeff = j * bernsteinCoeffs[j] - (degree - j + 1) * lastCoeff;
			if (coeff > 0) {
				hasPositive = true;
			} else if (coeff < 0) {
				hasNegative = true;
			}
			if (hasPositive && hasNegative) {
				return false;
			}
			lastCoeff = coeff;
		}
		return true;
	}

	/**
	 * Multiply polynomial with a value
	 *
	 * @param value to multiply with.
	 * @return the new, multiplied polynomial.
	 */
	public BernsteinPolynomial1D multiply(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * value;
		}
		return newInstance(coeffs);

	}

	/**
	 * Adds a Bernstein polynomial to this one.
	 * @param bernsteinPolynomial to add.
	 * @return the result polynomial.
	 */
	public BernsteinPolynomial1D plus(BernsteinPolynomial1D bernsteinPolynomial) {
		if (bernsteinPolynomial == null) {
			return this;
		}

		for (int i = 0; i < degree + 1; i++) {
			bernsteinCoeffs[i] = bernsteinCoeffs[i] + bernsteinPolynomial.bernsteinCoeffs[i];
		}
		return this;
	}

	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	@Override
	public String toString() {
		return BernsteinToString.toString1Var(this);
	}

	/**
	 * Linear combination two coeffs and another polynomial.
	 *
	 * @param coeff to multiply original with
	 * @param otherPoly to add
	 * @param otherCoeff to multiply other with
	 *
	 * @return the linear combination as a new polynomial.
	 */
	public BernsteinPolynomial1D linearCombination(double coeff, BernsteinPolynomial1D otherPoly,
			double otherCoeff) {

		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * coeff
					+ (otherPoly != null ? otherPoly.bernsteinCoeffs[i] * otherCoeff : 0);
		}
		return newInstance(coeffs);
	}

	/**
	 * Linear combination two coeffs and another polynomial, stored in this polynomial
	 *
	 * @param coeff to multiply original with
	 * @param otherPoly to add
	 * @param otherCoeff to multiply other with
	 */
	public void linearCombinationInPlace(double coeff, BernsteinPolynomial1D otherPoly,
			double otherCoeff) {
		if (otherPoly == null) {
			return;
		}

		for (int i = 0; i < degree + 1; i++) {
			bernsteinCoeffs[i] = bernsteinCoeffs[i] * coeff
					+ otherPoly.bernsteinCoeffs[i] * otherCoeff;
		}
	}

	/**
	 * Divide polynomial by a constant
	 * @param v divisor
	 * @return resulting polynomial (new instance)
	 */
	public BernsteinPolynomial1D divide(double v) {
		return multiply(1.0 / v);
	}
}
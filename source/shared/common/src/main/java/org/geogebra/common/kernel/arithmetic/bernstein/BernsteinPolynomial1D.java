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

import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.bMinus;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.bPlus;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.tmpLastPartialEval;
import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinCache.tmpPartialEval;
import static org.geogebra.common.kernel.arithmetic.bernstein.BinomialCoefficientsSign.from1Var;

import org.geogebra.common.kernel.arithmetic.Splittable;

/**
 * Single-variable polynomial in the Bernstein basis on {@code [0,1]}, with
 * utilities for evaluation, subdivision, linear combinations, and basic algebra.
 *
 * <p>The instance is <strong>mutable</strong> and optimised for plotting/ marching
 * hot paths where object reuse matters.</p>
 *
 * @apiNote Not thread-safe. Uses process-wide caches/work buffers; assume
 * single-threaded use during evaluation/split.
 * @implNote Coefficients {@code bernsteinCoeffs[k]} are the unnormalised
 * numerators for {@code x^k (1-x)^{n-k}}; {@code dividedCoeffs} hold the
 * control points (normalised by {@code C(n,k)}).
 */
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
	 * Creates a 1D Bernstein polynomial over an original variable range.
	 *
	 * @param bernsteinCoeffs coefficients of {@code x^k(1-x)^{n-k}} (length {@code n+1})
	 * @param variableName    source variable name (e.g., {@code 'x'})
	 * @param min             original variable minimum (pre-normalisation)
	 * @param max             original variable maximum (pre-normalisation)
	 *
	 * @apiNote {@code bernsteinCoeffs.length - 1} defines the degree. The instance
	 * remains mutable for in-place ops like {@link #plus(BernsteinPolynomial1D)}.
	 */
	public BernsteinPolynomial1D(
			double[] bernsteinCoeffs, char variableName, double min, double max) {
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
	 * Returns the value at a parameter in {@code [0,1]}.
	 *
	 * @param value parameter at which to evaluate
	 * @return {@code p(value)}
	 *
	 * @apiNote Endpoints return the first/last control point. For best numerical
	 * stability, pass values in {@code [0,1]}; behavior for inputs outside
	 * {@code [0,1]} follows the Bernstein form algebraically.
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
		double oneMinusValue = 1 - value;

		System.arraycopy(dividedCoeffs, 0, lastPartialEval, 0, lastPartialEval.length);

		for (int i = 1; i <= degree + 1; i++) {
			for (int j = degree - i; j >= 0; j--) {
				double v = lastPartialEval[j + 1];
				partialEval[j] = oneMinusValue * lastPartialEval[j] + value * (Double.isNaN(v) ? 0 : v);
			}
			double[] temp = lastPartialEval;
			lastPartialEval = partialEval;
			partialEval = temp;
		}
		return partialEval[0];
	}

	/**
	 * Subdivides this polynomial at {@code t = 1/2} and returns two halves.
	 * <p>The current instance is updated to the first half; the second half is
	 * returned as a new instance. Both halves represent the original polynomial
	 * restricted and re-parameterized to {@code [0,1]}.</p>
	 *
	 * @return an array {@code { leftHalf, rightHalf }}
	 *
	 * @apiNote Designed for recursive marching/clipping; avoids per-call
	 * allocations by reusing internal buffers.
	 * @implNote Degree is preserved by subdivision.
	 */
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
				getSlices(plusLastJ, plusLastJ1, minusLastJ, minusLastJ1, newLength);
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
		return new BernsteinPolynomial1D[] {this, newInstance(bMinus.last[0])};
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

	private void getSlices(
			double[] pcoeffs,
			double[] potherCoeffs,
			double[] mcoeffs,
			double[] motherCoeffs,
			int degreeX) {
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
	 * Reports whether the derivative is detected as root-free by a control-sequence test.
	 *
	 * @return {@code true} if the internal sign-pattern test finds no root indicators;
	 * {@code false} otherwise
	 *
	 * @apiNote This is a fast heuristic based on coefficient differences in the
	 * Bernstein basis; it may return false negatives/positives near degenerate cases.
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
	 * Returns the first derivative in Bernstein form.
	 *
	 * @return {@code p'(x)} as a new Bernstein polynomial of degree {@code n-1}
	 */
	public BernsteinPolynomial1D derivative() {
		if (bernsteinCoeffs == null) {
			return this;
		}

		double[] derivedCoeffs = new double[degree];
		for (int i = 0; i < degree; i++) {
			double b1 = (degree - i) * bernsteinCoeffs[i];
			double b2 = (i + 1) * bernsteinCoeffs[i + 1];
			derivedCoeffs[i] = b2 - b1;
		}

		return new BernsteinPolynomial1D(derivedCoeffs, variableName, min, max);
	}

	/**
	 * Returns {@code this * value}.
	 *
	 * @param value scalar multiplier
	 * @return new scaled polynomial
	 */
	public BernsteinPolynomial1D multiply(double value) {
		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * value;
		}
		return newInstance(coeffs);
	}

	/**
	 * Adds {@code bernsteinPolynomial} to this polynomial in place.
	 *
	 * @param bernsteinPolynomial addend (same degree; {@code null} is a no-op)
	 * @return {@code this}, after addition
	 *
	 * @apiNote For non-mutating style, create a copy first or use
	 * {@link #linearCombination(double, BernsteinPolynomial1D, double)}.
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

	/**
	 * @return {@code true} if the degree is zero; {@code false} otherwise.
	 */
	@Override
	public boolean isConstant() {
		return bernsteinCoeffs.length == 1;
	}

	/**
	 * Returns a human-readable Bernstein-basis representation for debugging.
	 */
	@Override
	public String toString() {
		return BernsteinToString.toString1Var(this);
	}

	/**
	 * Returns {@code coeff * this + otherCoeff * otherPoly} as a new polynomial.
	 *
	 * @param coeff      multiplier for {@code this}
	 * @param otherPoly  second addend (may be {@code null})
	 * @param otherCoeff multiplier for {@code otherPoly}
	 * @return linear combination as a new instance
	 *
	 * @apiNote Degrees must match when {@code otherPoly != null}.
	 */
	public BernsteinPolynomial1D linearCombination(
			double coeff, BernsteinPolynomial1D otherPoly, double otherCoeff) {

		double[] coeffs = new double[degree + 1];

		for (int i = 0; i < degree + 1; i++) {
			coeffs[i] = bernsteinCoeffs[i] * coeff
					+ (otherPoly != null ? otherPoly.bernsteinCoeffs[i] * otherCoeff : 0);
		}
		return newInstance(coeffs);
	}

	/**
	 * Replaces this polynomial with {@code coeff * this + otherCoeff * otherPoly}.
	 *
	 * @param coeff      multiplier for {@code this}
	 * @param otherPoly  second addend (no-op when {@code null})
	 * @param otherCoeff multiplier for {@code otherPoly}
	 *
	 * @apiNote Mutates this instance; degrees must match when {@code otherPoly != null}.
	 */
	public void linearCombinationInPlace(
			double coeff, BernsteinPolynomial1D otherPoly, double otherCoeff) {
		if (otherPoly == null) {
			return;
		}

		for (int i = 0; i < degree + 1; i++) {
			bernsteinCoeffs[i] = bernsteinCoeffs[i] * coeff + otherPoly.bernsteinCoeffs[i] * otherCoeff;
		}
	}

	/**
	 * Returns {@code this / v}.
	 *
	 * @param v non-zero divisor
	 * @return new scaled polynomial
	 * @throws ArithmeticException if {@code v == 0}
	 */
	public BernsteinPolynomial1D divide(double v) {
		return multiply(1.0 / v);
	}
}

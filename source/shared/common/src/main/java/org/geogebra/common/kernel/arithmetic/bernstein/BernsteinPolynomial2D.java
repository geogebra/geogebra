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

import java.util.Arrays;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Splittable;

/**
 * Bivariate polynomial in Bernstein form on {@code [0,1] x [0,1]} with
 * operations used by plotting/marching and clipping.
 * <p>
 * Coefficients are stored as an array of {@link BernsteinPolynomial1D} in
 * {@code y}, one for each Bernstein index in {@code x}. Supports evaluation,
 * subdivision (into quadrants), partial derivatives, and substitution.
 * </p>
 *
 * <h2>Mutability and performance</h2>
 * This implementation is <strong>mutable</strong> and reuses internal caches
 * to minimize allocations in hot paths.
 *
 * @apiNote Not thread-safe. Evaluation and subdivision reuse process-wide
 *          caches/work buffers; assume single-threaded use while calling those
 *          operations.
 * @implNote Degree in {@code x} is {@code degreeX}; degree in {@code y} is the
 *           degree of each element in {@link #bernsteinCoeffs}. Subdivision
 *           preserves degrees in both variables.
 */
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
	 * Creates a 2D Bernstein polynomial from its 1D-in-{@code y} coefficient array
	 * along {@code x}.
	 *
	 * @param bernsteinCoeffs array of {@code y}-polynomials for each Bernstein index in {@code x}
	 * @param minX            original (pre-normalized) domain minimum in {@code x}
	 * @param maxX            original (pre-normalized) domain maximum in {@code x}
	 * @param degreeX         degree in {@code x} (typically {@code bernsteinCoeffs.length - 1})
	 *
	 * @apiNote The instance is mutable to support in-place operations during marching.
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
	 * Copy constructor.
	 * @param poly source polynomial to copy
	 */
	public BernsteinPolynomial2D(BernsteinPolynomial2D poly) {
		this(poly.bernsteinCoeffs, poly.minX, poly.maxX, poly.degreeX);
	}

	/**
	 * Creates a 2D Bernstein polynomial over the supplied bounds rectangle.
	 *
	 * @param bernsteinCoeffs array of {@code y}-polynomials for each Bernstein index in {@code x}
	 * @param limits bounds used for normalized evaluation
	 * @param degreeX degree in {@code x}
	 */
	public BernsteinPolynomial2D(BernsteinPolynomial1D[] bernsteinCoeffs, BoundsRectangle limits,
			int degreeX) {
		this(bernsteinCoeffs, limits.getXmin(), limits.getXmax(), degreeX);
	}

	/**
	 * Factory that returns a 2D Bernstein polynomial only if it is a plausible
	 * candidate for having roots (useful for implicit-curve plotting).
	 *
	 * @param bernsteinCoeffs array of {@code y}-polynomials for each Bernstein index in {@code x}
	 * @param minX            domain minimum in {@code x}
	 * @param maxX            domain maximum in {@code x}
	 * @param maxDegreeX      degree in {@code x}
	 * @return a new {@link BernsteinPolynomial2D} when sign analysis indicates a
	 *         potential solution; {@code null} otherwise
	 *
	 * @apiNote This is a fast prefilter (sign/monotonic test). It may conservatively
	 *          return {@code null} even when extreme edge cases exist.
	 */
	public static BernsteinPolynomial2D create(BernsteinPolynomial1D[] bernsteinCoeffs,
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
	 * Returns the value {@code p(x,y)}.
	 *
	 * @param x parameter in {@code x} (typically {@code [0,1]})
	 * @param y parameter in {@code y} (typically {@code [0,1]})
	 * @return polynomial value at {@code (x,y)}
	 *
	 * @apiNote Endpoints use the appropriate control rows/columns. Inputs outside
	 *          {@code [0,1]} evaluate algebraically but may be less stable.
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
		double scaledOneMinusX = 1 - x;

		for (int i = 0; i < degreeX + 1; i++) {
			lastPartialEval[i] = dividedCoeffs[i].evaluate(y);
		}

		for (int i = 1; i <= degreeX + 1; i++) {
			for (int j = degreeX - i; j >= 0; j--) {
				partialEval[j] = scaledOneMinusX * lastPartialEval[j]
						+ x * lastPartialEval[j + 1];
			}
			double[] temp = lastPartialEval;
			lastPartialEval = partialEval;
			partialEval = temp;
		}
		return partialEval[0];
	}

	/**
	 * Substitutes {@code x = value} to obtain a 1D Bernstein polynomial in {@code y}.
	 *
	 * @param value x-parameter (typically in {@code [0,1]})
	 * @return {@code p(value, y)} as a 1D Bernstein polynomial
	 */
	private BernsteinPolynomial1D substituteX(double value) {
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

	/**
	 * Substitutes {@code y = value} to obtain a 1D Bernstein polynomial in {@code x}.
	 *
	 * @param value y-parameter (typically in {@code [0,1]})
	 * @return {@code p(x, value)} as a 1D Bernstein polynomial
	 */
	private BernsteinPolynomial1D substituteY(double value) {
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
	 * Subdivides along {@code x} and then along {@code y}, returning four
	 * sub-polynomials that cover the original domain as quadrants. Each
	 * sub-polynomial is re-parameterized to {@code [0,1] x [0,1]}.
	 *
	 * <p>Result layout:</p>
	 * <pre>
	 *   [[ x-left, y-left ],  [ x-left, y-right ],
	 *    [ x-right, y-left ], [ x-right, y-right ]]
	 * </pre>
	 *
	 * @return a {@code 2x2} array of sub-polynomials covering the original domain
	 *
	 * @apiNote Designed for recursive marching/clipping; uses cached buffers to
	 *          reduce allocations.
	 * @implNote Subdivision preserves degrees in both variables.
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
	 * Splits each coefficient polynomial (in {@code y}) of this instance and
	 * replaces it with its lower-half; returns a new polynomial built from the
	 * upper-halves.
	 *
	 * @return the polynomial corresponding to the complementary half in {@code y}
	 * @apiNote Mutates this instance (keeps one half); the returned value is the other half.
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

		return create(bMinusCoeffs, minX, maxX, degreeX);
	}

	/**
	 * Heuristically reports whether exactly one partial derivative indicates
	 * a solution (early-out check).
	 *
	 * @return {@code true} iff only {@code dp/dx} or only {@code dp/dy} suggests a root
	 *
	 * @apiNote Heuristic, based on sign/monotonic tests; may be conservative.
	 */
	public boolean onlyOnePartialDerivativeHasSolution() {
		for (int i = 0; i < degreeX; i++) {
			BernsteinPolynomial1D dx = bernsteinCoeffs[i].linearCombination(-degreeX + i,
					bernsteinCoeffs[i + 1], i + 1);

			if (dx.hasNoSolution() != bernsteinCoeffs[i].hasDerivativeNoSolution()) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Returns the partial derivative {@code dp/dx} in Bernstein form.
	 *
	 * @return a new polynomial of degree {@code degreeX - 1} in {@code x}
	 */
	public BernsteinPolynomial2D derivativeX() {
		BernsteinPolynomial1D[] derivedCoeffs = new BernsteinPolynomial1D[degreeX];
			for (int i = 0; i < degreeX; i++) {
				derivedCoeffs[i] = bernsteinCoeffs[i].linearCombination(-degreeX + i,
						bernsteinCoeffs[i + 1], i + 1);
			}
		return new BernsteinPolynomial2D(derivedCoeffs, minX, maxX, degreeX - 1);
	}

	/**
	 * Returns the partial derivative {@code dp/dy} in Bernstein form.
	 *
	 * @return a new polynomial with {@code y}-degree reduced by one
	 */
	public BernsteinPolynomial2D derivativeY() {
		BernsteinPolynomial1D[] derivedCoeffs = new BernsteinPolynomial1D[degreeX + 1];
		for (int i = 0; i <= degreeX; i++) {
			BernsteinPolynomial1D b2 = bernsteinCoeffs[i];
			derivedCoeffs[i] = b2.derivative();
		}
		return new BernsteinPolynomial2D(derivedCoeffs, minX, maxX, degreeX);
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
	 * Substitutes one variable with a fixed value and returns the resulting 1D polynomial.
	 *
	 * @param variable either {@code "x"} or {@code "y"}
	 * @param value    parameter value to substitute (typically in {@code [0,1]})
	 * @return {@code p(value, y)} when {@code "x"}, or {@code p(x, value)} when {@code "y"}
	 *
	 * @apiNote For other names, {@code "y"} is assumed.
	 */
	public BernsteinPolynomial1D substitute(String variable, double value) {
		if ("x".equals(variable)) {
			return substituteX(value);
		}
		return substituteY(value);
	}
}

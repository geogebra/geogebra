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

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Fits polynomials by QR decomposition in normalized coordinates and computes
 * sums and determinants for other regression algorithms.
 *
 * @author Hans-Petter Ulven
 * @version 20.02.10
 */
public final class RegressionMath {

	public static final int LINEAR = 1;
	public static final int EXP = 5;
	public static final int LOG = 6;
	public static final int POW = 7;

	// / --- Properties --- ///
	private boolean error = false;
	// private int regtype = LINEAR; //Default
	// parameters
	private double p1;
	private double p2;

	// Sums of x,x^2,...
	private double sigmax;
	private double sigmax2;
	private double sigmay;
	private double sigmay2;
	private double sigmaxy;
	private GeoList geolist;
	private double[] xlist;
	private double[] ylist;
	private int size;

	// Design matrix and response vector for polynomial least squares
	private double[][] marray;
	private double[][] yarray;

	// / --- Interface --- ///

	public double getP1() {
		return p1;
	}

	public double getP2() {
		return p2;
	}

	/**
	 * @return sum(x)
	 */
	public double getSigmaX() {
		return sigmax;
	}

	/**
	 * @return sum(x^2)
	 */
	public double getSigmaX2() {
		return sigmax2;
	}

	/**
	 * @return sum(y)
	 */
	public double getSigmaY() {
		return sigmay;
	}

	/**
	 * @return sum(y^2)
	 */
	public double getSigmaY2() {
		return sigmay2;
	}

	/**
	 * @return sum(xy)
	 */
	public double getSigmaXy() {
		return sigmaxy;
	}

	/**
	 * Fits a polynomial using QR decomposition in centered, scaled x-coordinates.
	 *
	 * @param gl
	 *            input data
	 * @param degree
	 *            polynomial degree
	 * @param cof
	 *            output coefficients
	 * @return the fitted polynomial, or null if fitting fails
	 */
	public @Nullable Function fitPolynomial(@NonNull GeoList gl, int degree, double @NonNull [] cof) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints();
		if (error || degree < 0 || size <= degree) {
			return null;
		}
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			if (!Double.isFinite(xlist[i]) || !Double.isFinite(ylist[i])) {
				return null;
			}
			min = Math.min(min, xlist[i]);
			max = Math.max(max, xlist[i]);
		}
		// Halve before adding to avoid overflowing for large coordinates.
		double center = min / 2 + max / 2;
		double scale = Math.max(max - center, center - min);
		if (degree == 0) {
			scale = 1;
		} else if (scale == 0 || !Double.isFinite(scale)) {
			return null;
		}
		try {
			makeMatrixArrays(degree, center, scale);
			RealMatrix matrix = new Array2DRowRealMatrix(marray, false);
			// The tolerance is relative to the normalized matrix, not the original x units.
			double threshold = Math.ulp(1.0) * Math.max(size, degree + 1) * matrix.getFrobeniusNorm();
			DecompositionSolver solver = new QRDecomposition(matrix, threshold).getSolver();
			if (!solver.isNonSingular()) {
				return null;
			}
			RealMatrix solution = solver.solve(new Array2DRowRealMatrix(yarray, false));
			if (!convertCoefficients(solution, degree, center, scale, cof)) {
				return null;
			}
			if (canExpandPolynomial(
					solution, degree, center, scale, Math.max(Math.abs(min), Math.abs(max)))) {
				return AlgoPolynomialFromCoordinates.buildPolyFunctionExpression(gl.getKernel(), cof, true);
			}
			return buildNormalizedPolynomial(gl.getKernel(), solution, degree, center, scale);
		} catch (RuntimeException exception) {
			Log.debug(exception.toString());
			return null;
		}
	}

	/** Use expanded notation only when the estimated rounding error is small. */
	private boolean canExpandPolynomial(
			@NonNull RealMatrix solution, int degree, double center, double scale, double maxAbsX) {
		if (degree == 0) {
			return true;
		}
		double maxAbsY = 0;
		for (double y : ylist) {
			maxAbsY = Math.max(maxAbsY, Math.abs(y));
		}
		// Bound the magnitudes before any cancellation in coefficient conversion or evaluation.
		double offsetMagnitude = (maxAbsX + Math.abs(center)) / scale;
		double magnitude = Math.abs(solution.getEntry(degree, 0));
		for (int i = degree - 1; i >= 0; i--) {
			magnitude = magnitude * offsetMagnitude + Math.abs(solution.getEntry(i, 0));
		}
		double roundingBound = magnitude * Math.ulp(1.0) * 8 * (degree + 1) * (degree + 1);
		return Double.isFinite(roundingBound) && roundingBound <= maxAbsY * 1E-12;
	}

	/** Build the evaluation expression without expanding powers of the normalization offset. */
	private @NonNull Function buildNormalizedPolynomial(
			@NonNull Kernel kernel,
			@NonNull RealMatrix solution,
			int degree,
			double center,
			double scale) {
		int highestPower = degree;
		while (highestPower > 0 && solution.getEntry(highestPower, 0) == 0) {
			highestPower--;
		}
		FunctionVariable x = new FunctionVariable(kernel);
		ExpressionNode normalizedX =
				new ExpressionNode(kernel, x, Operation.MINUS, new MyDouble(kernel, center));
		normalizedX =
				new ExpressionNode(kernel, normalizedX, Operation.DIVIDE, new MyDouble(kernel, scale));
		ExpressionNode polynomial =
				new ExpressionNode(kernel, new MyDouble(kernel, solution.getEntry(highestPower, 0)));
		for (int i = highestPower - 1; i >= 0; i--) {
			polynomial = new ExpressionNode(kernel, polynomial, Operation.MULTIPLY, normalizedX);
			polynomial = new ExpressionNode(
					kernel, polynomial, Operation.PLUS, new MyDouble(kernel, solution.getEntry(i, 0)));
		}
		return new Function(polynomial, x);
	}

	/** Substitute (x - center) / scale into the fitted polynomial using Horner's method. */
	private boolean convertCoefficients(
			@NonNull RealMatrix solution,
			int degree,
			double center,
			double scale,
			double @NonNull [] coefficients) {
		double[] converted = new double[degree + 1];
		converted[0] = solution.getEntry(degree, 0);
		double offset = center / scale;
		for (int i = degree - 1; i >= 0; i--) {
			for (int j = degree - i; j > 0; j--) {
				converted[j] = converted[j - 1] / scale - offset * converted[j];
			}
			converted[0] = solution.getEntry(i, 0) - offset * converted[0];
		}
		for (int i = 0; i < converted.length; i++) {
			if (!Double.isFinite(converted[i])) {
				return false;
			}
			if (converted[i] == 0) {
				converted[i] = 0;
			}
		}
		System.arraycopy(converted, 0, coefficients, 0, converted.length);
		return true;
	}

	/**
	 * Compute exponential fit and store result in p1, p2.
	 *
	 * @param gl
	 *            input data
	 * @return whether sums could be calculated
	 */
	public boolean doExp(GeoList gl) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		if (size == 0) {
			return false;
		}
		double ySign = ylist[0] < 0 ? -1 : 1;
		// Transform y->ln(y):
		for (int i = 0; i < size; i++) {
			double y = ylist[i] * ySign;
			if (y < 0.0d) { // log(minus)!
				return false;
			}
			ylist[i] = Math.log(y);
		} // for all y
		doSums(); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det22(1.0d * size, sigmax, sigmax, sigmax2);
		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		p1 = det22(sigmay, sigmax, sigmaxy, sigmax2) / n;
		p2 = det22(size, sigmay, sigmax, sigmaxy) / n;
		// transform back:
		p1 = Math.exp(p1) * ySign;
		// r=corrCoeff();
		return true;
	}

	/**
	 * Compute logarithmic fit and store result in p1, p2.
	 *
	 * @param gl
	 *            input data
	 * @return whether sums could be calculated
	 */
	public boolean doLog(GeoList gl) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		// Transform x->ln(x):
		for (int i = 0; i < size; i++) {
			double x = xlist[i];
			if (x < 0.0d) { // log(minus)!
				return false;
			}
			xlist[i] = Math.log(xlist[i]);
		} // for all x
		doSums(); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det22(1.0d * size, sigmax, sigmax, sigmax2);
		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		p1 = det22(sigmay, sigmax, sigmaxy, sigmax2) / n;
		p2 = det22(size, sigmay, sigmax, sigmaxy) / n;
		// No transformation of p1 or p2 necessary
		// r=corrCoeff();
		return true;
	}

	/**
	 * Compute power function fit and store result in p1, p2.
	 *
	 * @param gl
	 *            input data
	 * @return whether sums could be calculated
	 */
	public boolean doPow(GeoList gl) {
		error = false;
		geolist = gl;
		double x, y;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		if (size == 0) {
			return false;
		}
		double ySign = ylist[0] < 0 ? -1 : 1;
		// Transform y->ln(y) and x->ln(x):
		for (int i = 0; i < size; i++) {
			x = xlist[i];
			y = ylist[i] * ySign;
			if ((x < 0.0d) || (y < 0.0d)) { // log(minus)!
				return false;
			}
			xlist[i] = Math.log(x);
			ylist[i] = Math.log(y);
		} // for all x
		doSums(); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det22(1.0d * size, sigmax, sigmax, sigmax2);
		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		p1 = det22(sigmay, sigmax, sigmaxy, sigmax2) / n;
		p2 = det22(size, sigmay, sigmax, sigmaxy) / n;
		// Transform back:
		p1 = Math.exp(p1) * ySign;
		// r=corrCoeff();
		return true;
	}

	/**
	 *
	 * @param a11
	 *            matrix entry
	 * @param a12
	 *            matrix entry
	 * @param a21
	 *            matrix entry
	 * @param a22
	 *            matrix entry
	 * @return determinant
	 */
	public static double det22(double a11, double a12, double a21, double a22) {
		return a11 * a22 - a21 * a12;
	}

	/**
	 * Determinant of 4x4 matrix
	 *
	 * @param a11
	 *            matrix entry
	 * @param a12
	 *            matrix entry
	 * @param a13
	 *            matrix entry
	 * @param a21
	 *            matrix entry
	 * @param a22
	 *            matrix entry
	 * @param a23
	 *            matrix entry
	 * @param a31
	 *            matrix entry
	 * @param a32
	 *            matrix entry
	 * @param a33
	 *            matrix entry
	 * @return determinant
	 */
	public static double det33(
			double a11,
			double a12,
			double a13,
			double a21,
			double a22,
			double a23,
			double a31,
			double a32,
			double a33) {
		return a11 * (a22 * a33 - a32 * a23)
				- a12 * (a21 * a33 - a31 * a23)
				+ a13 * (a21 * a32 - a31 * a22);
	}

	/**
	 * Determinant of 4x4 matrix
	 *
	 * @param a11
	 *            matrix entry
	 * @param a12
	 *            matrix entry
	 * @param a13
	 *            matrix entry
	 * @param a14
	 *            matrix entry
	 * @param a21
	 *            matrix entry
	 * @param a22
	 *            matrix entry
	 * @param a23
	 *            matrix entry
	 * @param a24
	 *            matrix entry
	 * @param a31
	 *            matrix entry
	 * @param a32
	 *            matrix entry
	 * @param a33
	 *            matrix entry
	 * @param a34
	 *            matrix entry
	 * @param a41
	 *            matrix entry
	 * @param a42
	 *            matrix entry
	 * @param a43
	 *            matrix entry
	 * @param a44
	 *            matrix entry
	 * @return determinant
	 */
	public static double det44(
			double a11,
			double a12,
			double a13,
			double a14,
			double a21,
			double a22,
			double a23,
			double a24,
			double a31,
			double a32,
			double a33,
			double a34,
			double a41,
			double a42,
			double a43,
			double a44) {
		return a11 * a22 * a33 * a44
				- a11 * a22 * a34 * a43
				- a11 * a23 * a32 * a44
				+ a11 * a23 * a42 * a34
				+ a11 * a32 * a24 * a43
				- a11 * a24 * a33 * a42
				- a12 * a21 * a33 * a44
				+ a12 * a21 * a34 * a43
				+ a12 * a31 * a23 * a44
				- a12 * a31 * a24 * a43
				- a12 * a23 * a41 * a34
				+ a12 * a41 * a24 * a33
				+ a21 * a13 * a32 * a44
				- a21 * a13 * a42 * a34
				- a21 * a14 * a32 * a43
				+ a21 * a14 * a33 * a42
				- a13 * a22 * a31 * a44
				+ a13 * a22 * a41 * a34
				+ a13 * a31 * a24 * a42
				- a13 * a32 * a41 * a24
				+ a22 * a31 * a14 * a43
				- a22 * a14 * a41 * a33
				- a31 * a14 * a23 * a42
				+ a14 * a23 * a32 * a41;
	}

	// / --- Private --- ///

	/* Do whatever sums necessary */
	private void doSums() {
		sigmax = sigmax2 = sigmaxy = sigmay = sigmay2 = 0;
		for (int i = 0; i < size; i++) {
			double x = xlist[i];
			double y = ylist[i];
			sigmax += x;
			sigmax2 += x * x;
			sigmaxy += x * y;
			sigmay += y;
			sigmay2 += y * y;
		}
	}

	/* Get points to local array */
	private void getPoints() {
		// double x,y;
		double[] xy = new double[2];
		GeoElement geoelement;
		// GeoPoint geopoint;
		xlist = new double[size];
		ylist = new double[size];
		for (int i = 0; i < size; i++) {
			geoelement = geolist.get(i);
			if (geoelement instanceof GeoPoint) {
				((GeoPoint) geoelement).getInhomCoords(xy);
				xlist[i] = xy[0];
				ylist[i] = xy[1];
			} else {
				error = true;
				xlist[i] = 0.0d;
				ylist[i] = 0.0d;
			}
		}
	}

	// Make M with 1,x,x^2,... , and Y with y1,y2,.. for all datapoints
	private void makeMatrixArrays(int degree, double center, double scale) {
		marray = new double[size][degree + 1];
		yarray = new double[size][1]; // column matrix
		for (int i = 0; i < size; i++) {
			// Y:
			yarray[i][0] = ylist[i];
			// M:
			for (int j = 0; j < (degree + 1); j++) {
				marray[i][j] = j == 0 ? 1 : marray[i][j - 1] * ((xlist[i] - center) / scale);
			}
		}
	}
}

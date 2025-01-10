package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.debug.Log;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

/**
 * 
 * <h3>RegressionMath:</h3>
 * 
 * RegressionMath is a library of sums, determinants and parameter calculations
 * used by the FitXxx[GeoList]:GeoFunction commands.
 * 
 * Might be problems if callers are running in separate threads. Is this a
 * problem?
 * <b>--- Interface: ---</b>
 * <ul>
 * <li>RegressionMath(GeoList)
 * <li>det33(...), det44(...) determinants. (Faster than Gauss for n&lt;5)
 * <li>but det55 removed to keep code size down (web)
 * <li>doLinReg(),doQuadReg(),doCubicReg(),doQuartRet()
 * <li>doExpReg(),doLogReg(),doPowReg()
 * <li>getSigmaX(),getSigmaX2(),[getSigmaX3()..getSigmaX5()
 * <li>getSigmaY(),[getSigmaY2(),getSigmaXY(),getSigmaX2Y(),getSigmaX3Y
 * (),getSigmaX4Y()]
 * <li>getP1(),getP2(),getP3(),getP4(),getP5() //Parameters for regression
 * function
 * <li>getR() //regression coefficient. (When users get regression, they will
 * certainly ask for this...)
 * </ul>
 * 
 * @author Hans-Petter Ulven
 * @version 20.02.10
 */
public final class RegressionMath {

	public final static int LINEAR = 1;
	public final static int QUAD = 2;
	public final static int CUBIC = 3;
	public final static int EXP = 5;
	public final static int LOG = 6;
	public final static int POW = 7;

	// / --- Properties --- ///
	private boolean error = false;
	// private int regtype = LINEAR; //Default
	// parameters
	private double p1;
	private double p2;

	// Sums of x,x^2,...
	private double sigmax;
	private double sigmax2;
	private double sigmax3;
	private double sigmax4;
	private double sigmax5;
	private double sigmax6;
	private double sigmay;
	private double sigmay2;
	private double sigmaxy;
	private double sigmax2y;
	private double sigmax3y;
	private GeoList geolist;
	private double[] xlist;
	private double[] ylist;
	private int size;

	// For (M_T*M)*Par=(M_T*Y)
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
	 * Does the polynomial regression for degree &gt; 4
	 * 
	 * @param gl
	 *            input data
	 * @param degree
	 *            polynomial degree
	 * @param cof
	 *            output coefficients
	 * @return success
	 */
	public boolean doPolyN(GeoList gl, int degree, double[] cof) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		try {
			makeMatrixArrays(degree); // make marray and yarray
			RealMatrix M = new Array2DRowRealMatrix(marray, false);
			DecompositionSolver solver = new QRDecomposition(M).getSolver();
			RealMatrix Y = new Array2DRowRealMatrix(yarray, false);
			RealMatrix P = solver.solve(Y);
			for (int i = 0; i <= degree; i++) {
				cof[i] = P.getEntry(i, 0);
			}
		} catch (Throwable t) {
			Log.debug(t.toString());
			error = true;
		} // try-catch. ToDo: A bit more fine-grained error-handling...
		return !error;
	}

	/**
	 * Do linear regression, store result in p1,p2
	 * 
	 * @param gl
	 *            list of points
	 * @param cof
	 *            output coefficients
	 * @return whether calculating sums worked
	 */
	public boolean doLinear(GeoList gl, double[] cof) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		doSums(LINEAR); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det22(1.0d * size, sigmax, sigmax, sigmax2);
		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		cof[0] = det22(sigmay, sigmax, sigmaxy, sigmax2) / n;
		cof[1] = det22(size, sigmay, sigmax, sigmaxy) / n;
		// r=corrCoeff();
		return true;
	}

	/**
	 * Compute quadratic fit and store result in p1, p2, p3.
	 * 
	 * @param gl
	 *            input data
	 * @param cof
	 *            output coefficients
	 * @return whether sums could be calculated
	 */
	public boolean doQuad(GeoList gl, double[] cof) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		doSums(QUAD); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det33(1.0d * size, sigmax, sigmax2, sigmax, sigmax2, sigmax3,
				sigmax2, sigmax3, sigmax4);

		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		cof[0] = det33(sigmay, sigmax, sigmax2, sigmaxy, sigmax2, sigmax3,
				sigmax2y,
				sigmax3, sigmax4) / n;
		cof[1] = det33(1.0d * size, sigmay, sigmax2, sigmax, sigmaxy, sigmax3,
				sigmax2, sigmax2y, sigmax4) / n;
		cof[2] = det33(1.0d * size, sigmax, sigmay, sigmax, sigmax2, sigmaxy,
				sigmax2, sigmax3, sigmax2y) / n;
		// r=0.0d; // Not useful
		return true;
	}

	/**
	 * Calculate cubic regression and stre in p1, p2, p3, p4.
	 * 
	 * @param gl
	 *            input data
	 * @param cof
	 *            output coefficients
	 * @return whether sums could be calculated
	 */
	public boolean doCubic(GeoList gl, double[] cof) {
		error = false;
		geolist = gl;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		doSums(CUBIC); // calculate necessary sigmas
		if (error) {
			return false;
		}

		double n = det44(1.0d * size, sigmax, sigmax2, sigmax3, sigmax, sigmax2,
				sigmax3, sigmax4, sigmax2, sigmax3, sigmax4, sigmax5, sigmax3,
				sigmax4, sigmax5, sigmax6);

		if (Math.abs(n - 0.0d) < 1.0E-15d) {
			return false;
		}
		cof[0] = det44(sigmay, sigmax, sigmax2, sigmax3, sigmaxy, sigmax2,
				sigmax3,
				sigmax4, sigmax2y, sigmax3, sigmax4, sigmax5, sigmax3y, sigmax4,
				sigmax5, sigmax6) / n;
		cof[1] = det44(size, sigmay, sigmax2, sigmax3, sigmax, sigmaxy, sigmax3,
				sigmax4, sigmax2, sigmax2y, sigmax4, sigmax5, sigmax3, sigmax3y,
				sigmax5, sigmax6) / n;
		cof[2] = det44(size, sigmax, sigmay, sigmax3, sigmax, sigmax2, sigmaxy,
				sigmax4, sigmax2, sigmax3, sigmax2y, sigmax5, sigmax3, sigmax4,
				sigmax3y, sigmax6) / n;
		cof[3] = det44(size, sigmax, sigmax2, sigmay, sigmax, sigmax2, sigmax3,
				sigmaxy, sigmax2, sigmax3, sigmax4, sigmax2y, sigmax3, sigmax4,
				sigmax5, sigmax3y) / n;
		// r=0.0d; // Not useful
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
		double y = 0.0d;
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
			y = ylist[i] * ySign;
			if (y < 0.0d) { // log(minus)!
				return false;
			}
			ylist[i] = Math.log(y);
		} // for all y
		doSums(LINEAR); // calculate necessary sigmas
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
		double x = 0.0d;
		size = geolist.size();
		getPoints(); // getPoints from geolist
		if (error) {
			return false;
		}
		// Transform x->ln(x):
		for (int i = 0; i < size; i++) {
			x = xlist[i];
			if (x < 0.0d) { // log(minus)!
				return false;
			}
			xlist[i] = Math.log(xlist[i]);
		} // for all x
		doSums(LINEAR); // calculate necessary sigmas
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
		doSums(LINEAR); // calculate necessary sigmas
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
	public static double det22(
			double a11, double a12, double a21, double a22) {
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
			double a11, double a12, double a13, double a21, double a22,
			double a23, double a31, double a32, double a33) {
		return a11 * (a22 * a33 - a32 * a23) - a12 * (a21 * a33 - a31 * a23)
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
			double a11, double a12, double a13, double a14, double a21,
			double a22, double a23, double a24, double a31, double a32,
			double a33, double a34, double a41, double a42, double a43,
			double a44) {
		return a11 * a22 * a33 * a44 - a11 * a22 * a34 * a43
				- a11 * a23 * a32 * a44 + a11 * a23 * a42 * a34
				+ a11 * a32 * a24 * a43 - a11 * a24 * a33 * a42
				- a12 * a21 * a33 * a44 + a12 * a21 * a34 * a43
				+ a12 * a31 * a23 * a44 - a12 * a31 * a24 * a43
				- a12 * a23 * a41 * a34 + a12 * a41 * a24 * a33
				+ a21 * a13 * a32 * a44 - a21 * a13 * a42 * a34
				- a21 * a14 * a32 * a43 + a21 * a14 * a33 * a42
				- a13 * a22 * a31 * a44 + a13 * a22 * a41 * a34
				+ a13 * a31 * a24 * a42 - a13 * a32 * a41 * a24
				+ a22 * a31 * a14 * a43 - a22 * a14 * a41 * a33
				- a31 * a14 * a23 * a42 + a14 * a23 * a32 * a41;
	}

	// / --- Private --- ///

	/* Do whatever sums necessary */
	private void doSums(int degree) { // do whatever sums necessary
		double x, y, xx, xy;
		sigmax = sigmax2 = sigmax3 = sigmax4 = sigmax5 = sigmax6 = 0.0d;
		sigmaxy = sigmax2y = sigmax3y = sigmay = sigmay2 = 0.0d;
		for (int i = 0; i < size; i++) {
			x = xlist[i];
			y = ylist[i];
			xx = x * x;
			xy = x * y; // save some calculations
			switch (degree) { // fall through switch
			default:
				// do nothing
				break;
			case 4:
				//$FALL-THROUGH$
			case 3:
				sigmax3y += xx * xy;
				sigmax5 += xx * xx * x;
				sigmax6 += xx * xx * xx;
				//$FALL-THROUGH$
			case 2:
				sigmax2y += xx * y;
				sigmax3 += xx * x;
				sigmax4 += xx * xx;
				//$FALL-THROUGH$
			case 1:
				sigmay2 += y * y; // r only for linear
			}
			// always do these
			sigmax += x;
			sigmax2 += xx;
			sigmaxy += xy;
			sigmay += y;
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
	private void makeMatrixArrays(int degree) {
		marray = new double[size][degree + 1];
		yarray = new double[size][1]; // column matrix
		for (int i = 0; i < size; i++) {
			// Y:
			yarray[i][0] = ylist[i];
			// M:
			for (int j = 0; j < (degree + 1); j++) {
				marray[i][j] = Math.pow(xlist[i], j);
			}
		}
	}

}

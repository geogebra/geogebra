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

package org.geogebra.common.kernel.implicit;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * This class provides functionality for work with polynomials. It allows one to
 * compute degrees, leading coefficients, polynomial division and others.
 *
 */
public class PolynomialUtils {

	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * 
	 * @param cp
	 *            coefficients of dividend
	 * @param cd
	 *            coefficients of divisor
	 * @return quotient of cp/cd
	 */
	public static double[] polynomialDivision(double[] cp, double[] cd) {
		double[] cq;
		double[] cpclone;
		cpclone = new double[cp.length];
		for (int i = 0; i < cp.length; i++) {
			cpclone[i] = cp[i];
		}
		int degD = cd.length - 1;
		while (degD >= 0 && DoubleUtil.isZero(cd[degD])) {
			degD--;
		}
		if (degD < 0) { // => division by zero
			throw new ArithmeticException("divide by zero polynomial");
		}
		if (cpclone.length - 1 < degD) {
			return new double[] { 0 };
		}
		cq = new double[cpclone.length - degD];
		double lcd = cd[degD];
		int k = cpclone.length - 1;
		for (int i = cq.length - 1; i >= 0; i--) {
			cq[i] = cpclone[k] / lcd;
			for (int j = 0; j <= degD - 1; j++) {
				cpclone[j + i] = cpclone[j + i] - cq[i] * cd[j];
			}
			k--;
		}
		return cq;
	}

	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * 
	 * @param p
	 *            dividend
	 * @param d
	 *            divisor
	 * @return quotient of p/d
	 */
	public static PolynomialFunction polynomialDivision(PolynomialFunction p,
			PolynomialFunction d) {
		return new PolynomialFunction(
				polynomialDivision(p.getCoefficients(), d.getCoefficients()));
	}

	/**
	 * @param p
	 *            polynomial function
	 * @return degree of the function
	 */
	public static int getDegree(PolynomialFunction p) {
		return getDegree(p.getCoefficients());
	}

	/**
	 * @param c
	 *            coefficients of polynomial
	 * @return degree
	 */
	public static int getDegree(double[] c) {
		for (int i = c.length - 1; i >= 0; i--) {
			if (!DoubleUtil.isZero(c[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param c
	 *            coefficients of polynomial
	 * @return leading coefficient
	 */
	public static double getLeadingCoeff(double[] c) {
		int d = getDegree(c);
		if (d >= 0) {
			return c[d];
		}
		return 0;
	}

	/**
	 * @param p
	 *            polynomial function
	 * @return leading coefficient
	 */
	public static double getLeadingCoeff(PolynomialFunction p) {
		return getLeadingCoeff(p.getCoefficients());
	}

	/**
	 * @param c
	 *            coefficients of (one variable) polynomial p
	 * @param x
	 *            x
	 * @return p(x)
	 */
	public static double eval(double[] c, double x) {
		if (c.length == 0) {
			return 0;
		}
		double s = c[c.length - 1];
		for (int i = c.length - 2; i >= 0; i--) {
			s *= x;
			s += c[i];
		}
		return s;
	}

	/**
	 * @param coeff
	 *            coefficients
	 * @return array of arrays with minimal lengths that contains all
	 *         coefficients
	 */
	public static double[][] coeffMinDeg(double[][] coeff) {
		double[][] newCoeffMinDeg = null;
		for (int i = coeff.length - 1; i >= 0; i--) {
			for (int j = coeff[i].length - 1; j >= 0; j--) {
				if (!DoubleUtil.isZero(coeff[i][j])) {
					if (newCoeffMinDeg == null) {
						newCoeffMinDeg = new double[i + 1][];
					}
					if (newCoeffMinDeg[i] == null) {
						newCoeffMinDeg[i] = new double[j + 1];
					}
					newCoeffMinDeg[i][j] = coeff[i][j];
				}
			}
			if (newCoeffMinDeg != null && newCoeffMinDeg[i] == null) {
				newCoeffMinDeg[i] = new double[1];
				newCoeffMinDeg[i][0] = 0;
			}
		}
		if (newCoeffMinDeg == null) {
			newCoeffMinDeg = new double[1][1];
			newCoeffMinDeg[0][0] = 0;
		}
		return newCoeffMinDeg;
	}

	/**
	 * 
	 * @param pair
	 *            starting value for Newton's-Algorithm
	 * @param p1
	 *            polynomial
	 * @param line
	 *            defined by line[0]+x*line[1]+y*line[2]=0
	 * @return whether a common root of the polynomial and the line was found
	 */
	public static boolean rootPolishing(double[] pair, GeoImplicit p1,
			double[] line) {
		return rootPolishing(pair, p1, null, line);
	}

	/**
	 * 
	 * @param pair
	 *            starting value for Newton's-Algorithm
	 * @param p1
	 *            polynomial
	 * @param p2
	 *            other polynomial
	 * @return whether a common root of the polynomials was found
	 */
	public static boolean rootPolishing(double[] pair, GeoImplicit p1,
			GeoImplicit p2) {
		return rootPolishing(pair, p1, p2, null);
	}

	private static boolean rootPolishing(double[] pair, GeoImplicit p1,
			GeoImplicit p2, double[] line) {
		double x = pair[0], y = pair[1];
		double p, q;
		if (p1 == null) {
			return false;
		}
		if (p2 == null && (line == null || line.length != 3)) {
			return false;
		}
		p = p1.evaluateImplicitCurve(x, y);
		if (p2 != null) {
			q = p2.evaluateImplicitCurve(x, y);
		} else {
			q = line[0] + x * line[1] + y * line[2];
		}
		double lastErr = Double.MAX_VALUE;
		double err = Math.abs(p) + Math.abs(q);
		int n = 0;
		int MAX_ITERATIONS = 20;
		while (err < 10 * lastErr && err > Kernel.STANDARD_PRECISION
				&& ++n < MAX_ITERATIONS) {
			double px, py;
			double qx, qy;
			px = p1.derivativeX(x, y);
			py = p1.derivativeY(x, y);
			if (p2 != null) {
				qx = p2.derivativeX(x, y);
				qy = p2.derivativeY(x, y);
			} else {
				qx = line[1];
				qy = line[2];
			}
			double det = px * qy - py * qx;
			if (DoubleUtil.isZero(det)) {
				break;
			}
			x -= (p * qy - q * py) / det;
			y -= (q * px - p * qx) / det;
			lastErr = err;
			p = p1.evaluateImplicitCurve(x, y);
			if (p2 != null) {
				q = p2.evaluateImplicitCurve(x, y);
			} else {
				q = line[0] + x * line[1] + y * line[2];
			}
			err = Math.abs(p) + Math.abs(q);
		}
		pair[0] = x;
		pair[1] = y;
		return err < Kernel.STANDARD_PRECISION;
	}

	/**
	 * @param coeff
	 *            coefficients of implicit poly
	 * @param simplify
	 *            whether to replace by evaluation
	 * @return whether all values are numeric
	 */
	public static boolean checkNumericCoeff(ExpressionValue[][] coeff,
			boolean simplify) {
		for (int i = 0; i < coeff.length; i++) {
			for (int j = 0; j < coeff[i].length; j++) {
				if (coeff[i][j] != null) {
					// find constant parts of input and evaluate them right now
					if (simplify && !coeff[i][j]
							.any(Inspecting::isDynamicGeoElement)) {
						coeff[i][j] = coeff[i][j]
								.evaluate(StringTemplate.defaultTemplate);
					}

					// check that coefficient is a number: this may throw an
					// exception
					ExpressionValue eval = coeff[i][j]
							.evaluate(StringTemplate.defaultTemplate);

					// needed for GWT (ClassCastException not thrown)
					if (!(eval instanceof NumberValue)) {
						return false;
					}
				}
			}
		}
		return true;

	}

}

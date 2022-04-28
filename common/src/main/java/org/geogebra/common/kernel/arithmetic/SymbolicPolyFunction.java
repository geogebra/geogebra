/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.debug.Log;

/**
 * Class for polynomials with ExpressionValues as coefficients. Needed for root
 * finding.
 */
public class SymbolicPolyFunction extends PolyFunction {

	// symbolic coefficients, e.g. expression tree
	// used to recalc the coefficients of this polynomial
	private ExpressionNode[] symbCoeffs;

	/**
	 * Creates new symbolicpolynomial function
	 * 
	 * @param degree
	 *            degree of polynomial
	 */
	public SymbolicPolyFunction(int degree) {
		super(degree);
		symbCoeffs = new ExpressionNode[degree + 1];
	}

	/**
	 * Creates new polynomial function
	 * 
	 * @param coeff
	 *            array of coefficients
	 */
	public SymbolicPolyFunction(ExpressionNode[] coeff) {
		super(coeff.length - 1);
		symbCoeffs = coeff;
	}

	/**
	 * @return array of coefficients
	 */
	public ExpressionNode[] getSymbolicCoeffs() {
		return symbCoeffs;
	}

	/*
	 * public SymbolicPolyFunction getSymbolicDerivative(Kernel kernel) { int
	 * degree = getDegree(); SymbolicPolyFunction deriv; if (degree < 1) { deriv
	 * = new SymbolicPolyFunction(0); deriv.symbCoeffs[0] = new
	 * ExpressionNode(kernel, new MyDouble(kernel, 0)); return deriv; }
	 * 
	 * // standard case deriv = new SymbolicPolyFunction(degree - 1); for (int
	 * i=1; i <= degree; i++) { deriv.symbCoeffs[i-1] = new
	 * ExpressionNode(kernel, new MyDouble(kernel, i), ExpressionNode.MULTIPLY,
	 * symbCoeffs[i]); } return deriv; }
	 */

	/**
	 * Evaluates all symbolic coefficients and stores the results in this
	 * polynomial's double coefficients.
	 * 
	 * @return true when successful
	 */
	@Override
	final public boolean updateCoeffValues() {
		try {
			// evaluate all the coefficients
			double coeff;
			for (int i = 0; i < symbCoeffs.length; i++) {
				ExpressionValue eval = symbCoeffs[i]
						.evaluate(StringTemplate.defaultTemplate);
				coeff = eval.evaluateDouble();
				if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
					return false; // no valid values
				}
				coeffs[i] = coeff; // set polynomial coefficient
			}
			return true;
		} catch (Exception | Error e) {
			Log.warn("updateCoeffValues: " + e.getMessage());
			Log.debug(e);
			return false;
		}
	}

	@Override
	protected PolyFunction buildIntegral() {
		// standard case
		SymbolicPolyFunction integ = new SymbolicPolyFunction(getDegree() + 1);
		if (symbCoeffs.length > 0) {
			integ.symbCoeffs[0] = new ExpressionNode(symbCoeffs[0].getKernel(),
					0);
		}
		for (int i = 0; i <= getDegree(); i++) {
			integ.symbCoeffs[i + 1] = symbCoeffs[i]
					.divide(new MyDouble(symbCoeffs[i].getKernel(), i + 1));
			integ.coeffs[i + 1] = integ.symbCoeffs[i + 1].evaluateDouble();
		}
		return integ;
	}

	@Override
	protected ExpressionValue getCoeff(int i, boolean fraction, Kernel kernel) {
		if (fraction && symbCoeffs[i] != null
				&& symbCoeffs[i].asFraction() != null) {
			return symbCoeffs[i].asFraction();
		}
		return new MyDouble(kernel, coeffs[i]);
	}

	@Override
	protected PolyFunction buildDerivative() {
		if (getDegree() < 1) {
			return new SymbolicPolyFunction(0);
		}

		// standard case
		SymbolicPolyFunction deriv = new SymbolicPolyFunction(getDegree() - 1);
		for (int i = 1; i <= getDegree(); i++) {
			deriv.symbCoeffs[i - 1] = symbCoeffs[i]
					.multiply(new MyDouble(symbCoeffs[i].getKernel(), i));
			deriv.coeffs[i - 1] = deriv.symbCoeffs[i - 1].evaluateDouble();
		}
		return deriv;
	}

}

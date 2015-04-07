/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;

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
				coeff = ((NumberValue) eval).getDouble();
				if (Double.isNaN(coeff) || Double.isInfinite(coeff))
					return false; // no valid values
				coeffs[i] = coeff; // set polynomial coefficient
			}
			return true;
		} catch (Exception e) {
			App.debug("updateCoeffValues: ");
			e.printStackTrace();
			return false;
		} catch (Error err) {
			App.debug("updateCoeffValues: ");
			err.printStackTrace();
			return false;
		}
	}

}

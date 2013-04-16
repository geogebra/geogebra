/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.roots.RealRootDerivFunction;
import geogebra.common.plugin.Operation;

/**
 * Fast polynomial evaluation of Function
 */
public class PolyFunction implements RealRootDerivFunction {
	/** coefficients */
	protected double [] coeffs;
	private int degree; 
	
	private PolyFunction derivative, integral; 
	
	//private double []  // for value and derivative's value
	/**
	 * 
	 * @param degree degree
	 */
	public PolyFunction(int degree) {
		this.degree = degree;
		coeffs = new double[degree + 1];
	}
	/**
	 * 
	 * @param c coefficients
	 */
	public PolyFunction(double [] c) {
		coeffs = new double[c.length];
		for (int i=0; i < c.length; i++) {
			coeffs[i] = c[i];
		}
		degree = coeffs.length - 1;
	}
	
	/**
	 * Copy constructor
	 * @param pf function to copy
	 */
	public PolyFunction(PolyFunction pf) {
		degree = pf.degree;
		coeffs = pf.getCoeffsCopy();
	}
	
	/**
	 * 
	 * @return array of coefficients
	 */
	public double [] getCoeffs() {
		return coeffs;
	}
	
	/**
	 * 
	 * @return copy of coefficients array
	 */
	public double [] getCoeffsCopy() {
		double [] ret = new double[coeffs.length];
		for (int i=0; i < coeffs.length; i++) {
			ret[i] = coeffs[i];
		}
		return ret;
	}
	
	/**
	 * Returns true. This method is overwritten by the subclass SymbolicPolyFunction.
	 * @return true if coeffs were updated	 
	 */
	public boolean updateCoeffValues() {
		// nothing to do here, see SymbolicPolyFunction
		return true;
	}
	
	/**
	 * 
	 * @return degree of polynomial
	 */
	public int getDegree() {
		return degree;
	}
	
	/**
	 * 
	 * @return first derivative
	 */
	final public PolyFunction getDerivative() {
		if (derivative == null) {
			derivative = buildDerivative();
		}
		return derivative;
	}
	
	/**
	 * 
	 * @return first derivative
	 */
	final public PolyFunction getIntegral() {
		if (integral == null) {
			integral = buildIntegral();
		}
		return integral;
	}
	
	private PolyFunction buildDerivative() {
		if (degree < 1)
			return new PolyFunction(0);
		
		// standard case
		PolyFunction deriv = new PolyFunction(degree - 1);
		for (int i=1; i <= degree; i++) {
			deriv.coeffs[i-1] = i * coeffs[i];
		}
		return deriv;
	}
	
	private PolyFunction buildIntegral() {
		
		// standard case
		PolyFunction integ = new PolyFunction(degree + 1);
		for (int i=0; i <= degree; i++) {
			integ.coeffs[i+1] = coeffs[i] / (i+1);
		}
		return integ;
	}
	
	/**
	 * Evaluates polynomial and its derivative 
	 */		 
	final public double [] evaluateDerivFunc(double x){
		double[] ret = new double[2];
		ret[0] = coeffs[degree];
		ret[1] = 0;
		for (int i=degree-1; i >= 0; i--) {
			ret[1] = ret[1] * x + ret[0];
			ret[0] = ret[0] * x + coeffs[i];
		}
		return ret;
	}	
	
	final public double evaluateDerivative(double x) {
		return evaluateDerivFunc(x)[1];
	}
	
	/**
	 * Evaluates polynomial
	 */		 
	final public double evaluate(double x){		
		double p = coeffs[degree];
		for (int i=degree-1; i >= 0; i--) {
			p = p * x + coeffs[i];
		}
		return p;
	}	
	
	/**
	 * This routine evaluates this polynomial and its first order derivatives at x. 
	 * @param x point for function evaluation
	 * @param order of highest derivative
	 * @return array a with polynomial value as a[0] and nd derivatives as a[1..order].
	 */
	final public double[] evaluateDerivatives(double x, int order) {
		double pd[] = new double[order+1];

		int nnd, j, i;
		double cnst = 1.0;
		pd[0] = coeffs[degree];
		for (j = 1; j <= order; j++)
			pd[j] = 0.0;
		for (i = degree - 1; i >= 0; i--) {
			nnd = (order < (degree - i) ? order : degree - i);
			for (j = nnd; j >= 1; j--)
				pd[j] = pd[j] * x + pd[j - 1];
			pd[0] = pd[0] * x + coeffs[i];
		}
		for (i = 2; i <= order; i++) { //After the first derivative, factorial constants come in.
			cnst *= i;
			pd[i] *= cnst;
		}
		return pd;
	}
	
	/**
	 * @param kernel Kernel
	 * @param fv FunctionVariable, eg "t" in f(t)
	 * @return Function containing ExpressionNode built from coefficients
	 */
	public Function getFunction(Kernel kernel, FunctionVariable fv) {
		
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);

		if (degree == 0) {
			// constant
			ExpressionNode en = new ExpressionNode(kernel, new MyDouble(kernel, coeffs[0]));
			return new Function(en, fv);
		} else if (degree == 1) {
			// linear
			ExpressionNode en = fvEn.multiplyR(coeffs[1]).plus(coeffs[0]);
			return new Function(en, fv);
		}

		ExpressionNode en = fvEn.power((degree)).multiplyR(coeffs[degree]);

		if (degree > 2) {
			for (int i = degree - 1 ; i > 1 ; i--) {
				
				// don't use !Kernel.isZero() as we don't want to lose eg leading term
				if (coeffs[i] != 0) {
					ExpressionNode term = new ExpressionNode(kernel, fv, Operation.POWER, new MyDouble(kernel, i)).multiplyR(coeffs[i]);
					en = en.plus(term);
				}
			}
		}
		
		// linear coefficient
		if (!Kernel.isZero(coeffs[1])) {
			en = en.plus(fvEn.multiplyR(coeffs[1]));
		}
		
		// constant term
		if (!Kernel.isZero(coeffs[0])) {
			en = en.plus(coeffs[0]);
		}

		return new Function(en, fv);

	}

/*
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(coeffs[0]);
		for (int i=1; i<coeffs.length; i++) {
			sb.append(" + ");
			sb.append(coeffs[i]);
			sb.append(" x^");
			sb.append(i);
		}
		return sb.toString();
	}
	*/
	
}

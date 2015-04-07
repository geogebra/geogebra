package org.geogebra.common.kernel.arithmetic;

/**
 * Interface for functions and expressions that can be evaluated at given real
 * number
 */
public interface Evaluatable {
	/**
	 * Evaluates this function/expression at given position
	 * 
	 * @param x
	 *            position
	 * @return f(x)
	 */
	public double evaluate(double x);
}

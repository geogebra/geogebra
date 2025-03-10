package org.geogebra.common.kernel.arithmetic;

/**
 * Interface for functions in two variables
 */
public interface Evaluate2Var extends ExpressionValue {

	/**
	 * Evaluate at given point, be smart about y for f(y)=y^2
	 * 
	 * @param x
	 *            value of x (or first variable)
	 * @param y
	 *            value of y (or first variable)
	 * @return function value
	 */
	double evaluate(double x, double y);

	/**
	 * @return whether this is defined
	 */
	boolean isDefined();

	/**
	 * @return expression
	 */
	ExpressionNode getFunctionExpression();

	/**
	 * @return function
	 */
	FunctionNVar getFunction();

}

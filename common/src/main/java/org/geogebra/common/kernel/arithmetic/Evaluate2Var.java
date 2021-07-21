package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.StringTemplate;

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
	public double evaluate(double x, double y);

	/**
	 * @return whether this is defined
	 */
	public boolean isDefined();

	/**
	 * @return expression
	 */
	public ExpressionNode getFunctionExpression();

	/**
	 * @return function
	 */
	public FunctionNVar getFunction();

	/**
	 * @param template
	 *            output template
	 * @return variable string
	 */
	public String getVarString(StringTemplate template);

	/**
	 * @param x
	 *            first variable value
	 * @param y
	 *            second variable
	 * @param factor
	 *            number of factor if multiple factors are present
	 * @return evaluation result
	 */
	// public double evaluate(double x, double y, int factor);

	/**
	 * @param val
	 *            variable values
	 * @param factor
	 *            number of factor if multiple factors are present
	 * @return evaluation result
	 */
	// public double evaluate(double[] val, int factor);
}

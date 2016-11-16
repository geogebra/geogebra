package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.StringTemplate;

/**
 * Interface for functions in two variables
 */
public interface Evaluate2Var extends ExpressionValue {

	public double evaluate(double x, double y);

	public boolean isDefined();

	public ExpressionNode getFunctionExpression();

	public FunctionNVar getFunction();

	public Object getVarString(StringTemplate defaulttemplate);

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

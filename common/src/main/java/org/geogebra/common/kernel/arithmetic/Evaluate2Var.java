package org.geogebra.common.kernel.arithmetic;

/**
 * Interface for functions in two variables
 */
public interface Evaluate2Var {
	/**
	 * @param x
	 *            first variable value
	 * @param y
	 *            second variable value
	 * @return evaluation result
	 */
	public double evaluate(double x, double y);

	/**
	 * @param val
	 *            variable values
	 * @return evaluation result
	 */
	public double evaluate(double[] val);
}

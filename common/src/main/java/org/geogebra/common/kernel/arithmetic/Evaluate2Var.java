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
	 * @param x
	 *            first variable value
	 * @param y
	 *            second variable
	 * @param factor
	 *            number of factor if multiple factors are present
	 * @return evaluation result
	 */
	public double evaluate(double x, double y, int factor);

	/**
	 * @param val
	 *            variable values
	 * @param factor
	 *            number of factor if multiple factors are present
	 * @return evaluation result
	 */
	public double evaluate(double[] val, int factor);
}

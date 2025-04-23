package org.geogebra.common.kernel.arithmetic;

/**
 * Common interface for algos and commands.
 */
public interface HasArguments {

	/**
	 * @param i
	 *            index
	 * @return i-th argument
	 */
	ExpressionValue getArgument(int i);

	/**
	 * @return number of arguments
	 */
	int getArgumentNumber();
}

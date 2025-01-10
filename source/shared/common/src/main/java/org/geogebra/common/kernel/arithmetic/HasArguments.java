package org.geogebra.common.kernel.arithmetic;

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

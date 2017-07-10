package org.geogebra.common.kernel.roots;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Interface for differentiable function
 */
public interface RealRootDerivFunction extends UnivariateFunction {

	/**
	 * returns array with function's value and derivative's value
	 * 
	 * @param x
	 *            argument value
	 * @return [f(x), f'(x)]
	 */
	public double[] evaluateDerivFunc(double x);

}

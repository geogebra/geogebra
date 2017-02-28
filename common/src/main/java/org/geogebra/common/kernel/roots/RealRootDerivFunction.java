package org.geogebra.common.kernel.roots;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface RealRootDerivFunction extends UnivariateFunction {

	/**
	 * returns array with function's value and derivative's value
	 * 
	 * @param x
	 */
	public double[] evaluateDerivFunc(double x);

	/**
	 * returns derivative's value
	 * 
	 * @param x
	 */
	public double evaluateDerivative(double x);
}

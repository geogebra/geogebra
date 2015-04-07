package org.geogebra.common.kernel.roots;

public interface RealRootDerivFunction extends RealRootFunction {

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

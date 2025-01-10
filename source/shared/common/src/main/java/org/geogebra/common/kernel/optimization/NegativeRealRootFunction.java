package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Converts f into -f
 *
 */
public class NegativeRealRootFunction implements UnivariateFunction {

	private UnivariateFunction f;

	/**
	 * @param f
	 *            function to negate (=multiply by -1)
	 */
	public NegativeRealRootFunction(UnivariateFunction f) {
		this.f = f;
	}

	@Override
	final public double value(double x) {
		return -f.value(x);
	}

}

package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class NegativeRealRootFunction implements UnivariateFunction {

	private UnivariateFunction f;

	public NegativeRealRootFunction(UnivariateFunction f) {
		this.f = f;
	}

	@Override
	final public double value(double x) {
		return -f.value(x);
	}

}

package org.geogebra.common.kernel.roots;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public class RealRootAdapter implements UnivariateRealFunction {

	private RealRootFunction fun;

	public RealRootAdapter(RealRootFunction fun) {
		this.fun = fun;
	}

	public double value(double x) throws FunctionEvaluationException {
		double res = this.fun.evaluate(x);
		if (Double.isInfinite(res) || Double.isNaN(res)) {
			throw new FunctionEvaluationException(x);
		}
		return res;
	}

}

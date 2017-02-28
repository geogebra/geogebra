package org.geogebra.common.kernel.roots;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class RealRootDerivAdapter
		implements DifferentiableUnivariateFunction {

	RealRootDerivFunction derivFun;

	public RealRootDerivAdapter(RealRootDerivFunction derivFun) {
		this.derivFun = derivFun;
	}

	@Override
	public UnivariateFunction derivative() {
		return new UnivariateFunction() {
			@Override
			public double value(double x) {
				double res = derivFun.evaluateDerivative(x);
				// if (Double.isInfinite(res) || Double.isNaN(res)) {
				// throw new FunctionEvaluationException(x);
				// }
				return res;
			}
		};
	}

	@Override
	public double value(double x) {
		double res = derivFun.evaluate(x);
		// if (Double.isInfinite(res) || Double.isNaN(res)) {
		// throw new FunctionEvaluationException(x);
		// }
		return res;
	}

}

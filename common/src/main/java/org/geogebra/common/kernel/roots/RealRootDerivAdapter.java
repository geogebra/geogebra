package org.geogebra.common.kernel.roots;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public class RealRootDerivAdapter implements
		DifferentiableUnivariateRealFunction {

	RealRootDerivFunction derivFun;

	public RealRootDerivAdapter(RealRootDerivFunction derivFun) {
		this.derivFun = derivFun;
	}

	public UnivariateRealFunction derivative() {
		return new UnivariateRealFunction() {
			public double value(double x) throws FunctionEvaluationException {
				double res = derivFun.evaluateDerivative(x);
				if (Double.isInfinite(res) || Double.isNaN(res)) {
					throw new FunctionEvaluationException(x);
				}
				return res;
			}
		};
	}

	public double value(double x) throws FunctionEvaluationException {
		double res = derivFun.evaluate(x);
		if (Double.isInfinite(res) || Double.isNaN(res)) {
			throw new FunctionEvaluationException(x);
		}
		return res;
	}

}

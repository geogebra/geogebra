package org.geogebra.common.kernel.arithmetic;

public interface Evaluate2Var {
	public double evaluate(double x, double y);

	public double evaluate(double[] val);
}

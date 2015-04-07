package org.geogebra.common.main;

public interface AlgoKimberlingWeightsInterface {
	public double weight(int k, double a, double b, double c);

	public double weight(AlgoKimberlingWeightsParams kw);
}

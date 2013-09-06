package geogebra.common.kernel.barycentric;

public interface AlgoKimberlingWeightsInterface {
	public double weight(int k, double a, double b, double c);
	public double weight(AlgoKimberlingWeightsParams kw);
}

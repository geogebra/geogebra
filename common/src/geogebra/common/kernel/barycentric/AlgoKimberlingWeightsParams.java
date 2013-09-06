package geogebra.common.kernel.barycentric;

public class AlgoKimberlingWeightsParams {
	public AlgoKimberlingWeightsParams(int pk, double pa, double pb, double pc) {
		this.k = pk;
		this.a = pa;
		this.b = pb;
		this.c = pc;
	}
	int k;
	double a;
	double b;
	double c;
}

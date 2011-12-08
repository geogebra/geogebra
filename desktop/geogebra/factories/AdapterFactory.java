package geogebra.factories;

import geogebra.adapters.Complex;

public class AdapterFactory extends geogebra.common.factories.AdapterFactory {

	public AdapterFactory() { }

	public geogebra.common.adapters.Complex newComplex(double r, double i) {
		return new Complex(r, i);
	}
}

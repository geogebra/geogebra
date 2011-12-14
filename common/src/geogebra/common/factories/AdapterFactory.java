package geogebra.common.factories;

import geogebra.common.adapters.Complex;

public abstract class AdapterFactory {
	public static AdapterFactory prototype = null;

	public abstract Complex newComplex(double r, double i);
}

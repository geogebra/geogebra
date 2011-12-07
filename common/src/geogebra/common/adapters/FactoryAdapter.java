package geogebra.common.adapters;

public abstract class FactoryAdapter {
	public static FactoryAdapter prototype = null;

	public abstract Complex newComplex(double r, double i);
}

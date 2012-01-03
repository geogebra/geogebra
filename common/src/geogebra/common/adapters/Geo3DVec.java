package geogebra.common.adapters;

import geogebra.common.kernel.arithmetic.ExpressionValue;


public interface Geo3DVec extends ExpressionValue{
	public boolean isEqual(Geo3DVec vec);
	public double getX();
	public double getY();
	public double getZ();
}

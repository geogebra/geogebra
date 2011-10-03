package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

public interface Rotateable {
	public void rotate(NumberValue r);
	public GeoElement toGeoElement();
}


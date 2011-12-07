package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;

public interface Rotateable {
	public void rotate(NumberValue r);
	public GeoElement toGeoElement();
}


package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;

public interface Rotateable {
	public void rotate(NumberValue r);
	public GeoElement toGeoElement();
}


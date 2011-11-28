package geogebra.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint S);
	public GeoElement toGeoElement();
}

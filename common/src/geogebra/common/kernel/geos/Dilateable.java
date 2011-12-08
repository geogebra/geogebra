package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint2 S);
	public GeoElement toGeoElement();
}

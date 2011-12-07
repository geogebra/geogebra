package geogebra.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint S);
	public GeoElement toGeoElement();
}

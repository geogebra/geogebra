package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint S);
	public GeoElement toGeoElement();
}

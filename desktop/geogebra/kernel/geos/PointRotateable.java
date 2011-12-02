package geogebra.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPointInterface;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPointInterface S);
}

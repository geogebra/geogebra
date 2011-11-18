package geogebra.kernel.geos;

import geogebra.kernel.arithmetic.NumberValue;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPoint S);
}

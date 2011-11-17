package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoPoint;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPoint S);
}

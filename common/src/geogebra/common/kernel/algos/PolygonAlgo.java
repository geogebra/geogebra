package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoPoint;

public interface PolygonAlgo {

	public void calcArea();

	public void calcCentroid(GeoPoint p);

}

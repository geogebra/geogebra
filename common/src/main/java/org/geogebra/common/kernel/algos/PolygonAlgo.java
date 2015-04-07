package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoPoint;

public interface PolygonAlgo {

	public void calcArea();

	public void calcCentroid(GeoPoint p);

}

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Algo for direct creation of polygons (eg regular)
 */
public interface PolygonAlgo {
	/**
	 * Calculate area
	 */
	public void calcArea();

	/**
	 * Calculate centroid
	 * 
	 * @param p
	 *            output point
	 */
	public void calcCentroid(GeoPoint p);

}

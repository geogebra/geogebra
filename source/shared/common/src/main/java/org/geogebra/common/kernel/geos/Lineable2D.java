package org.geogebra.common.kernel.geos;

/**
 * Interface for objects that are line-like.
 */
public interface Lineable2D {

	/**
	 * Get underlying GeoElement.
	 *
	 * @return element
	 */
	GeoElement toGeoElement();

	/**
	 * @return x coordinate
	 */
	double getX();

	/**
	 * @return y coordinate
	 */
	double getY();
}

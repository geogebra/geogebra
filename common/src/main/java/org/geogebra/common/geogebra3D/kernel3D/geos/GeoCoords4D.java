package org.geogebra.common.geogebra3D.kernel3D.geos;

/**
 * Simple interface for geos that have 4 coords (3D points and vectors, 3D
 * planes, ...)
 * 
 * @author mathieu
 *
 */
public interface GeoCoords4D {

	/**
	 * sets the coords
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void setCoords(double x, double y, double z, double w);

}

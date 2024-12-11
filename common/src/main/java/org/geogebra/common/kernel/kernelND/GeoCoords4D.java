package org.geogebra.common.kernel.kernelND;

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
	 *            x-ccord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param w
	 *            w-coord (homogeneous)
	 */
	public void setCoords(double x, double y, double z, double w);

}

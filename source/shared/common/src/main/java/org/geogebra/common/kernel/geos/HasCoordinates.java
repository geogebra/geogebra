package org.geogebra.common.kernel.geos;

/**
 * Point with homogeneous 2D cartesian coordinates.
 */
public interface HasCoordinates {
	void setUndefined();

	void setCoords(double v, double v1, double v2);

	double getX();

	double getY();

	double getZ();
}

package org.geogebra.common.kernel.geos;

public interface HasCoordinates {
	void setUndefined();

	void setCoords(double v, double v1, double v2);

	double getX();

	double getY();

	double getZ();
}

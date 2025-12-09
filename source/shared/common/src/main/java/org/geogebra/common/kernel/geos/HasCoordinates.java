/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

/**
 * Point with homogeneous 2D cartesian coordinates.
 */
public interface HasCoordinates {

	/**
	 * Make all coordinates undefined.
	 */
	void setUndefined();

	/**
	 * Set homogeneous coordinates.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate (scale)
	 */
	void setCoords(double x, double y, double z);

	/**
	 * @return x-coordinate
	 */
	double getX();

	/**
	 * @return y-coordinate
	 */
	double getY();

	/**
	 * @return z-coordinate (scale)
	 */
	double getZ();
}

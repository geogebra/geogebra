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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 *         extends region interface for getPoint() ability
 */
public interface Region3D extends Region {

	/**
	 * return the 3D point from (x2d,y2d) 2D coords
	 * 
	 * @param x2d
	 *            x-coord
	 * @param y2d
	 *            y-coord
	 * @param coords
	 *            output coords
	 * @return the 3D point
	 */
	public Coords getPoint(double x2d, double y2d, Coords coords);

	/**
	 * return the normal projection of the (coords) point on the region
	 * 
	 * @param coords
	 *            coords of the point
	 * @return normal projection
	 */
	public Coords[] getNormalProjection(Coords coords);

	/**
	 * return the willingDirection projection of the (willing coords) point on
	 * the region
	 * 
	 * @param oldCoords
	 *            pld coords of the points
	 * @param willingCoords
	 *            willing coords of the point
	 * @param willingDirection
	 *            direction of the projection
	 * @return projection
	 */
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection);

}

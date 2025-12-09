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

import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * interface for 2D/3D arcs/sectors
 * 
 * @author mathieu
 *
 */
public interface GeoConicPartND {

	/**
	 * Sets parameters and calculates this object's value. For type
	 * CONIC_PART_ARC the value is the length, for CONIC_PART_SECTOR the value
	 * is an area. This method should only be called by the parent algorithm
	 * 
	 * @param start
	 *            start param
	 * @param end
	 *            end param
	 * @param positiveOrientation
	 *            true for positive orientation
	 */
	void setParameters(double start, double end,
			boolean positiveOrientation);

	/**
	 * Sector or arc
	 * 
	 * @return CONIC_PART_ARC or CONIC_PART_SECTOR
	 */
	int getConicPartType();

	/**
	 * @return start parameter
	 */
	double getParameterStart();

	/**
	 * @return end parameter
	 */
	double getParameterEnd();

	/**
	 * @return parameter extent
	 */
	double getParameterExtent();

	/**
	 * @return orientation
	 */
	boolean positiveOrientation();

	/**
	 * 
	 * @return arc/sector parameters
	 */
	GeoConicPartParameters getParameters();

	/**
	 * segment end point for degenerate case
	 * 
	 * @return coords of segment end point
	 */
	Coords getSegmentEnd3D();

	/**
	 * Returns arc length
	 * 
	 * @return arc length
	 */
	double getArcLength();

	/**
	 * Returns the area
	 * 
	 * @return area
	 */
	double getArea();

	/**
	 * set parameters in case of single point
	 */
	void setParametersToSinglePoint();

	/**
	 * 
	 * @param i
	 *            index of line
	 * @return the origin of lines in case of parallel lines
	 */
	Coords getOrigin3D(int i);

	/**
	 * @param type
	 *            hit type
	 */
	void setLastHitType(HitType type);
}

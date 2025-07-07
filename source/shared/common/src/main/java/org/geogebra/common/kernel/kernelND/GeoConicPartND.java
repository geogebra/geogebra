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

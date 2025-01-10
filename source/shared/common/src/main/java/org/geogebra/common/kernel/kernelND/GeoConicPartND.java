package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.PathParameter;
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
	public void setParameters(double start, double end,
			boolean positiveOrientation);

	/**
	 * Sector or arc
	 * 
	 * @return CONIC_PART_ARC or CONIC_PART_SECTOR
	 */
	public int getConicPartType();

	/**
	 * @return start parameter
	 */
	public double getParameterStart();

	/**
	 * @return end parameter
	 */
	public double getParameterEnd();

	/**
	 * @return parameter extent
	 */
	public double getParameterExtent();

	/**
	 * @return orientation
	 */
	public boolean positiveOrientation();

	/**
	 * super method
	 * 
	 * @param P
	 *            point
	 * @param pp
	 *            path parameter
	 */
	public void superPointChanged(Coords P, PathParameter pp);

	/**
	 * 
	 * @return arc/sector parameters
	 */
	public GeoConicPartParameters getParameters();

	/**
	 * segment end point for degenerate case
	 * 
	 * @return coords of segment end point
	 */
	public Coords getSegmentEnd3D();

	/**
	 * Returns arc length
	 * 
	 * @return arc length
	 */
	public double getArcLength();

	/**
	 * Returns the area
	 * 
	 * @return area
	 */
	public double getArea();

	/**
	 * set parameters in case of single point
	 */
	public void setParametersToSinglePoint();

	/**
	 * 
	 * @param i
	 *            index of line
	 * @return the origin of lines in case of parallel lines
	 */
	public Coords getOrigin3D(int i);

	/**
	 * @param type
	 *            hit type
	 */
	public void setLastHitType(HitType type);
}

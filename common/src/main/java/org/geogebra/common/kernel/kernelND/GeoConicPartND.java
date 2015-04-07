package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * interface for 2D/3D arcs/sectors
 * @author mathieu
 *
 */
public interface GeoConicPartND {

	/**
	 * Sets parameters and calculates this object's value. For type
	 * CONIC_PART_ARC the value is the length, for CONIC_PART_SECTOR the value
	 * is an area. This method should only be called by the parent algorithm
	 * 
	 * @param start start param
	 * @param end end param
	 * @param positiveOrientation true for positive orientation
	 */
	public void setParameters(double start, double end,
			boolean positiveOrientation);
	
	
	/**
	 * Sector or arc
	 * 
	 * @return CONIC_PART_ARC or CONIC_PART_SECTOR
	 */
	public int getConicPartType();
	
	public double getParameterStart();
	public double getParameterEnd();
	public double getParameterExtent();
	public boolean positiveOrientation();
	
	

	/**
	 * super method
	 * @param P
	 * @param pp
	 */
	public void superPointChanged(Coords P, PathParameter pp);
	

	/**
	 * 
	 * @return arc/sector parameters
	 */
	public GeoConicPartParameters getParameters();
	
	/**
	 * segment end point for degenerate case
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
}


package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.PointRotateable;


/**
 * Elements rotateable around arbitrary 3D object (point or line)
 */
public interface RotateableND extends PointRotateable{

	/**
	 * Rotates this element around
	 * @param r angle
	 * @param S center
	 * @param orientation orientation for the rotation
	 */
	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation);
	
	/**
	 * Rotates this element about a line
	 * @param r angle
	 * @param line line
	 */
	public void rotate(NumberValue r, GeoLineND line);
	
	
}

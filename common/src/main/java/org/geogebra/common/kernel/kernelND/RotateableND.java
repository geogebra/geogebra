package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Elements rotateable around arbitrary 3D object (point or line)
 */
public interface RotateableND extends PointRotateable {

	/**
	 * Rotates this element around
	 * 
	 * @param r
	 *            angle
	 * @param S
	 *            center
	 * @param orientation
	 *            orientation for the rotation
	 */
	public void rotate(NumberValue r, Coords S, GeoDirectionND orientation);

}

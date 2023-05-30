package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Rotatable;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Elements rotatable around arbitrary 3D object (point or line)
 */
public interface RotatableND extends Rotatable {

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

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
	 */
	public void rotate3D(NumberValue r, GeoPointND S);
}

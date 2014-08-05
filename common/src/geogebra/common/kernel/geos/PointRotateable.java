/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;

/**
 * Elements rotateable around arbitrary point
 */
public interface PointRotateable extends Rotateable {
	/**
	 * Rotates this element around (parallel to xOy plane)
	 * @param r angle
	 * @param S center
	 */
	public void rotate(NumberValue r, Coords S);
}

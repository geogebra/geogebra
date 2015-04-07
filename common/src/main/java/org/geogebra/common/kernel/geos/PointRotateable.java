/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Elements rotateable around arbitrary point
 */
public interface PointRotateable extends Rotateable {
	/**
	 * Rotates this element around (parallel to xOy plane)
	 * @param r angle
	 * @param S center
	 */
	public void rotate(NumberValue r, GeoPointND S);
}

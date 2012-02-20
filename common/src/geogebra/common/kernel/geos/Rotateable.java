/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;

/**
 * Elements rotateable around origin
 *
 */
public interface Rotateable extends ToGeoElement{
	/**
	 * Rotates this element around origin
	 * @param r angle
	 */
	public void rotate(NumberValue r);

}


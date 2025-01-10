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
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Elements rotatable around origin
 *
 */
public interface Rotatable extends GeoElementND {
	/**
	 * Rotates this element around origin
	 * 
	 * @param r
	 *            angle
	 */
	void rotate(NumberValue r);

	/**
	 * @param r angle
	 * @param S center of rotation
	 */
	void rotate(NumberValue r, GeoPointND S);

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface Mirrorable extends GeoElementND{
	/**
	 * Miror at point
	 * @param Q mirror
	 */
	public void mirror(Coords Q);
	/**
	 * Miror at line
	 * @param g mirror
	 */
	public void mirror(GeoLineND g);
}
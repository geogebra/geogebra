/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.geos;

/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface Mirrorable extends ToGeoElement{
	/**
	 * Miror at point
	 * @param Q mirror
	 */
	public void mirror(GeoPoint2 Q);
	/**
	 * Miror at line
	 * @param g mirror
	 */
	public void mirror(GeoLine g);
}
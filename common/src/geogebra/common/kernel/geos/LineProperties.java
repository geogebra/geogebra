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
 * GeoElement whose line properties (thickness, type)
 * can be set.
 *
 */
public interface LineProperties {
	/**
	 * Set line thicknes
	 * @param thickness new thickness
	 */
	public void setLineThickness(int thickness);
	/**
	 * Get line thickness
	 * @return line thickness
	 */
	public int getLineThickness();
	/**
	 * Set line type (see {@link geogebra.common.euclidian.EuclidianView#getLineTypes})
	 * @param type line type
	 */
	public void setLineType(int type);
	/**
	 * Get line type (see {@link geogebra.common.euclidian.EuclidianView#getLineTypes})
	 * @return line type
	 */
	public int getLineType();
}
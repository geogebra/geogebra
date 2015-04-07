/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;



/**
 * Interface for GeoElements that have an absolute screen position
 * (GeoImage, GeoText, GeoNumeric)
 */
public interface AbsoluteScreenLocateable extends GeoElementND{
	/**
	 * @param x x offset (in pixels)
	 * @param y y offset (in pixels)
	 */
	public void setAbsoluteScreenLoc(int x, int y);
	/**
	 * 
	 * @return x offset (in pixels)
	 */
	public int getAbsoluteScreenLocX();
	/**
	 * 
	 * @return y offset (in pixels)
	 */
	public int getAbsoluteScreenLocY();
	
	/**
	 * 
	 * @param x real world x coordinate
	 * @param y real world y coordinate
	 */
	public void setRealWorldLoc(double x, double y);
	/**
	 * 
	 * @return real world x coordinate
	 */
	public double getRealWorldLocX();
	/**
	 * 
	 * @return y real worldcoordinate
	 */
	public double getRealWorldLocY();
	
	/**
	 * @param flag true to make abs position active
	 */
	public void setAbsoluteScreenLocActive(boolean flag);
	
	/**
	 * @return true iff abs position is active
	 */
	public boolean isAbsoluteScreenLocActive();

	/**
	 * E.g. GeoNumeric implements this, but not all numbers can have abs. location
	 * @return true if this element can have absolute screen location
	 */
	public boolean isAbsoluteScreenLocateable();
}

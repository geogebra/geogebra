/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.kernelND;


/**
 * Interface for 3D vectors (not to be confused with GeoVec3D)
 */
public interface Geo3DVec extends GeoVecInterface{
	/**
	 * @param vec other vector
	 * @return true if this vector and other vector have same coordinates
	 */
	public boolean isEqual(Geo3DVec vec);
	/**
	 * @return x-coord
	 */
	public double getX();
	/**
	 * @return y-coord
	 */
	public double getY();
	/**
	 * @return z-coord
	 */
	public double getZ();
	
	/**
	 * @return length
	 */
	public double length();
	
	/**
	 * @return (Math.round(x), Math.round(y), Math.round(z))
	 */
	public Geo3DVec round();
		
	/**
	 * @return (Math.floor(x), Math.floor(y), Math.floor(z))
	 */
	public Geo3DVec floor();
		
	/**
	 * @return (Math.ceil(x), Math.ceil(y), Math.ceil(z))
	 */
	public Geo3DVec ceil();

	/**
	 * @return Math.atan2(y,x)
	 */
	public double arg();
		
	
}

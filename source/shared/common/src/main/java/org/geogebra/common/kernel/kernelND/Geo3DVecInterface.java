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
public interface Geo3DVecInterface extends GeoVecInterface {
	/**
	 * @param vec
	 *            other vector
	 * @return true if this vector and other vector have same coordinates
	 */
	public boolean isEqual(Geo3DVecInterface vec);

	/**
	 * @return x-coord
	 */
	@Override
	public double getX();

	/**
	 * @return y-coord
	 */
	@Override
	public double getY();

	/**
	 * @return z-coord
	 */
	@Override
	public double getZ();

	/**
	 * @return length
	 */
	public double length();

	/**
	 * @return (Math.round(x), Math.round(y), Math.round(z))
	 */
	public Geo3DVecInterface round();

	/**
	 * @return (Math.floor(x), Math.floor(y), Math.floor(z))
	 */
	public Geo3DVecInterface floor();

	/**
	 * @return (Math.ceil(x), Math.ceil(y), Math.ceil(z))
	 */
	public Geo3DVecInterface ceil();

	/**
	 * @return Math.atan2(y,x)
	 */
	public double arg();

	/**
	 * @param double1
	 *            coefficient
	 */
	public void mult(double double1);

	/**
	 * @param mode
	 *            coordinates mode
	 */
	public void setMode(int mode);

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyList;

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
	 * multiply 3D matrix by 3D point
	 * @param myList matrix
	 * @param rt 3D point
	 */
	public void multiplyMatrix(MyList myList, ExpressionValue rt);
}

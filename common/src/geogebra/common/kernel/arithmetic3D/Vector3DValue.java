/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * VectorValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.common.kernel.arithmetic3D;


import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.kernelND.Geo3DVec;

/**
 *
 * @author  Markus + ggb3D
 */
public interface Vector3DValue extends ExpressionValue { 
	/** 
	 * Converts vector to array of coords 
	 * @return array of coords
	 */
    public double[] getPointAsDouble();    
    public int getMode(); // SPHERICAL or CARTESIAN_3D
    //public void setMode(int mode);       
    /** converts vector to GeoVec3D 
     * @return vector
     */
	public Geo3DVec get3DVec();
}

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


import geogebra.common.kernel.arithmetic.VectorNDValue;
import geogebra.common.kernel.kernelND.Geo3DVec;

/**
 *
 * @author  Markus + ggb3D
 */
public interface Vector3DValue extends VectorNDValue { 
	/** 
	 * Converts vector to array of coords 
	 * @return array of coords
	 */
    public double[] getPointAsDouble();    

	public Geo3DVec getVector();
}

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

package geogebra.kernel.arithmetic3D;


import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.PointConvertibleToDouble;
import geogebra3D.kernel3D.Geo3DVec;

/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public interface Vector3DValue extends ExpressionValue, PointConvertibleToDouble { 
    public double[] getPointAsDouble();    
    //public int getMode(); // POLAR or CARTESIAN
    //public void setMode(int mode);       
	public Geo3DVec get3DVec();
}

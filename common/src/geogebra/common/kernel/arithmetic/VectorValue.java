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

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.geos.GeoVec2DInterface;

/**
 *
 * @author  Markus
 * @version 
 */
public interface VectorValue extends ExpressionValue { 
    public GeoVec2DInterface getVector();    
    public int getMode(); // POLAR or CARTESIAN
    public void setMode(int mode);  
}

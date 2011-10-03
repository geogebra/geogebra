/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Finds intersection points of two functions numerically
 * (using the roots of their difference)
 * 
 * @author Hans-Petter Ulven
 * @version 10.03.2011
 */
public class AlgoIntersectFunctions extends AlgoRoots {
                
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoIntersectFunctions(Construction cons, String[] labels, GeoFunction f, GeoFunction g,NumberValue left, NumberValue right) {
        super(cons, labels, f, g,left, right);                      
    }//Constructor
    
    public String getClassName() {
        return "AlgoIntersectFunctions";
    }//getClassName()
    
    
    public GeoPoint [] getIntersectionPoints() {
        return super.getRootPoints();
    }//getIntersectionPoints()


}//class AgoIntersetFunctions


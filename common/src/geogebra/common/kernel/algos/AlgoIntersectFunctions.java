/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Finds intersection points of two functions numerically
 * (using the roots of their difference)
 * 
 * @author Hans-Petter Ulven
 * @version 10.03.2011
 */
public class AlgoIntersectFunctions extends AlgoRoots {
                
    public AlgoIntersectFunctions(Construction cons, String[] labels, GeoFunction f, GeoFunction g,NumberValue left, NumberValue right) {
        super(cons, labels, f, g,left, right);                      
    }//Constructor
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoIntersectFunctions;
    }//getClassName()
    
    
    public GeoPoint [] getIntersectionPoints() {
        return super.getRootPoints();
    }//getIntersectionPoints()


}//class AgoIntersetFunctions


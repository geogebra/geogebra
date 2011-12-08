/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPoint2;

/**
 * Ellipse for given foci and first semi-axis length
 * @author  Markus
 * @version 
 */
public class AlgoEllipseFociLength extends AlgoConicFociLength {

	public AlgoEllipseFociLength(
		        Construction cons,
		        String label,
		        GeoPoint2 A,
		        GeoPoint2 B,
		        NumberValue a) {
		        super(cons, label, A, B, a);		       
		    }

	@Override
	public String getClassName() {
        return "AlgoEllipseFociLength";
    }
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
    }
    
	
	 @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("EllipseWithFociABandFirstAxisLengthC",A.getLabel(),B.getLabel(),a.toGeoElement().getLabel());	        	      
    }

}

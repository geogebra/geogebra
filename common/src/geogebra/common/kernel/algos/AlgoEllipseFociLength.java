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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationEllipseFociLength;

/**
 * Ellipse for given foci and first semi-axis length
 * @author  Markus
 * @version 
 */
public class AlgoEllipseFociLength extends AlgoConicFociLength {

	public AlgoEllipseFociLength(
		        Construction cons,
		        String label,
		        GeoPoint A,
		        GeoPoint B,
		        NumberValue a) {
		        super(cons, label, A, B, a);		       
		    }

	@Override
	public Algos getClassName() {
        return Algos.AlgoEllipseFociLength;
    }
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
    }
    
	
	 @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("EllipseWithFociABandFirstAxisLengthC",A.getLabel(tpl),
        		B.getLabel(tpl),a.toGeoElement().getLabel(tpl));	        	      
    }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationEllipseFociLength(element, scope);
	}
	
	@Override
	public boolean isLocusEquable() {
		return true;
	}

}

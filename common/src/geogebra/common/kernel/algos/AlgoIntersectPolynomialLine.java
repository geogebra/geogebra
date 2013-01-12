/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;



/**
 * Finds intersection points of a polynomial and a line
 * (using the roots of their difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectPolynomialLine extends AlgoRootsPolynomial {
                
    public AlgoIntersectPolynomialLine(Construction cons, GeoFunction f, GeoLine g) {
        super(cons, f, g);     
        addIncidence();
    }
    
	 /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	//TODO
	}
    
    @Override
	public Commands getClassName() {
        return Commands.Intersect;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    @Override
	public GeoPoint [] getIntersectionPoints() {
        return super.getRootPoints();
    }

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("IntersectionPointOfAB",input[0].getLabel(tpl),input[1].getLabel(tpl));

    }

}

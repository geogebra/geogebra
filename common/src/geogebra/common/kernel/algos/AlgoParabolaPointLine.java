/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoParabolaPointLine.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaPointLine extends AlgoParabolaPointLineND {
           
            
    public AlgoParabolaPointLine(Construction cons, String label, GeoPointND F, GeoLineND l) {
        super(cons, label, F, l);   
    }   
    
    public AlgoParabolaPointLine(Construction cons, GeoPointND F, GeoLineND l) {
        super(cons, F, l);
    }   
    
    @Override
	protected GeoConicND newGeoConic(Construction cons){
    	return new GeoConic(cons);
    }
    
    
    // compute parabola with focus F and line l
    @Override
	public final void compute() {                           
        parabola.setParabola((GeoPoint) F, (GeoLine) l);
    }   
    

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnParabolaPointLine(geo, this, scope);
	}
}

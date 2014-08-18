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

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoParabolaPointLineND;
import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.algos.EquationScopeInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaPointLine3D extends AlgoParabolaPointLineND {
           
            
    public AlgoParabolaPointLine3D(Construction cons, String label, GeoPointND F, GeoLineND l) {
        super(cons, label, F, l);   
    }   
    
    public AlgoParabolaPointLine3D(Construction cons, GeoPointND F, GeoLineND l) {
        super(cons, F, l);
    }   
    
    @Override
	protected GeoConicND newGeoConic(Construction cons){
    	GeoConic3D ret = new GeoConic3D(cons);
    	ret.setCoordSys(new CoordSys(2));
    	return ret;
    }
    
    
    // compute parabola with focus F and line l
    @Override
	public final void compute() {  
    	
    	Coords Fc = F.getInhomCoordsInD3();
    	Coords lo = l.getStartInhomCoords().getInhomCoordsInSameDimension();
    	Coords ld = l.getDirectionInD3();
    	
    	CoordSys cs = parabola.getCoordSys();
    	cs.resetCoordSys();
    	
    	cs.addPoint(Fc);
    	cs.addVector(ld);
    	cs.addPoint(lo);
    	
    	if (!cs.makeOrthoMatrix(false, false)){
    		parabola.setUndefined();
    		return;
    	}
    	
    	double y0 = cs.getNormalProjection(lo)[1].getY();
        parabola.setParabola(y0);
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

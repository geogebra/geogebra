/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a plane through a point and a line (or segment, ...) 
 *
 * @author  matthieu
 * @version 
 */
public class AlgoPlaneThroughPointAndLine extends AlgoPlaneThroughPoint {

 
    private GeoLineND line; // input
 

    public AlgoPlaneThroughPointAndLine(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,point);
        
        this.line=line;
        
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) line}, new GeoElement[] {getPlane()});

        // compute plane 
        compute();
        getPlane().setLabel(label);

    }

 



  
    protected final void compute() {
    	
    	CoordSys coordsys = getPlane().getCoordSys();
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(getPoint().getInhomCoordsInD(3));
		coordsys.addPoint(line.getPointInD(3, 0));
		coordsys.addPoint(line.getPointInD(3, 1));


		
		coordsys.makeOrthoMatrix(true,false);
		
		coordsys.makeEquationVector();
		

        
    }

    protected GeoElement getSecondInput(){
    	return (GeoElement) line;
    }
    
    
    final public String toString() {
    	return app.getPlain("PlaneThroughAB",getPoint().getLabel(),getSecondInput().getLabel());

    }

}

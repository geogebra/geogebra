/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Compute a plane through a point and parallel to another plane (or polygon)
 * 
 *
 * @author  matthieu
 * @version 
 */
public class AlgoPlaneThroughPointAndPlane extends AlgoPlaneThroughPoint {

 
    private GeoCoordSys2D cs; // input


    public AlgoPlaneThroughPointAndPlane(Construction cons, String label, GeoPointND point, GeoCoordSys2D cs) {
        super(cons,point);
        this.cs = cs;
        
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) cs}, new GeoElement[] {getPlane()});

        // compute plane 
        compute();
        getPlane().setLabel(label);
    }




  
    @Override
	public final void compute() {
    	
    	
    	
    	CoordSys coordsys = getPlane().getCoordSys();
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
    	
    	if (!cs.toGeoElement().isDefined())
    		return;
		
    	coordsys.addPoint(getPoint().getInhomCoordsInD(3));

    	coordsys.addVectorWithoutCheckMadeCoordSys(cs.getCoordSys().getVx());
    	coordsys.addVectorWithoutCheckMadeCoordSys(cs.getCoordSys().getVy());

 
		
		coordsys.makeOrthoMatrix(true,false);
		
		coordsys.makeEquationVector();
		

        
    }

    
    @Override
	protected GeoElement getSecondInput(){
    	return (GeoElement) cs;
    }

	// TODO Consider locusequability
}

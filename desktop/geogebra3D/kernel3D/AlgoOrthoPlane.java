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
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoOrthoPlane extends AlgoElement3D {

 
    private GeoPlane3D plane; // output       


    /**
     * 
     * @param cons
     */
    public AlgoOrthoPlane(Construction cons) {
        super(cons);
        plane = new GeoPlane3D(cons);
        
     }


    /**
     * 
     * @return the plane
     */
    public GeoPlane3D getPlane() {
    	return plane;
    }

    
    /**
     * 
     * @return normal vector to the plane
     */
    protected abstract Coords getNormal();
    
    /**
     * 
     * @return coords of a point on the plane
     */
    protected abstract Coords getPoint();

  
    protected final void compute() {
    	
    	CoordSys coordsys = plane.getCoordSys();
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
		//if cs has "no" direction vector, set undefined and return
    	Coords vz = getNormal();
		if (vz.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			plane.setUndefined();
			return;
		}
			
		

		//Application.debug(m.toString());

		coordsys.addPoint(getPoint());
		
		//gets an ortho matrix with coord sys direction vector
		Coords[] v = vz.completeOrthonormal();
		//Application.debug("v0=\n"+v[0]+"\nv1=\n"+v[1]);
		
		coordsys.addVectorWithoutCheckMadeCoordSys(v[0]);
		coordsys.addVectorWithoutCheckMadeCoordSys(v[1]);
		
		coordsys.makeOrthoMatrix(false,false);
		
		coordsys.makeEquationVector();
        
    }

}

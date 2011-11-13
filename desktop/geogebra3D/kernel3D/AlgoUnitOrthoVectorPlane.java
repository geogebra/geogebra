/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.main.Application;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the unit ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoUnitOrthoVectorPlane extends AlgoOrthoVectorPlane {

	
    AlgoUnitOrthoVectorPlane(Construction cons, String label, GeoCoordSys2D plane) {

    	super(cons,label,plane);
 
    }
    
 



    
    
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    protected Coords getCoords(){
    	return super.getCoords().normalized();
    }
    
    
    
    
    

	public String getClassName() {
    	
    	return "AlgoUnitOrthoVectorPlane";
	}

	
	
  
 

}

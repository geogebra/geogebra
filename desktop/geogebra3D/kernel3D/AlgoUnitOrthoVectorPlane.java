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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;




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
    
    @Override
	protected Coords getCoords(){
    	return super.getCoords().normalized();
    }
    
	@Override
	public Commands getClassName() {
    	
    	return Commands.UnitOrthogonalVector;
	}

}

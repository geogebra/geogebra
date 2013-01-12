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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;



/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoCircle3DPointPointDirection extends AlgoCircle3DPointDirection {

 

    /**
     * 
     * @param cons
     * @param label
     * @param point
     * @param pointThrough 
     * @param forAxis
     */
    public AlgoCircle3DPointPointDirection(Construction cons, String label, GeoPointND point, GeoPointND pointThrough, GeoDirectionND forAxis) {
        super(cons, label, point, (GeoElement) pointThrough, forAxis);
        

    }
    

    

    
    @Override
	protected final double getRadius() {
    	
    	GeoPointND pointThrough = (GeoPointND) getSecondInput();
    	Coords radius = pointThrough.getInhomCoordsInD(3).sub(getCenter().getInhomCoordsInD(3));
    	
    	
    	//check if direction is compatible (orthogonal) to center-second point
    	if (!Kernel.isZero(getDirection().getDirectionInD3().dotproduct(radius)))
    		return Double.NaN;
    	
    	radius.calcNorm();
    	
    	return radius.getNorm();

    }

    @Override
	public Commands getClassName() {
		return Commands.Circle;
	}


    
    /**
     * 
     * @return command string
     */
    @Override
	final protected String getCommandString(){
    	if (getForAxis() instanceof GeoCoordSys2D)
    		return "CircleWithCenterAThroughBParallelToC";
    	else
    		return "CircleWithCenterAThroughBAxisParallelToC";
    }

    
}

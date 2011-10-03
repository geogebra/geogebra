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
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


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
    

    

    
    protected final double getRadius() {
    	
    	GeoPointND pointThrough = (GeoPointND) getSecondInput();
    	Coords radius = pointThrough.getInhomCoordsInD(3).sub(getCenter().getInhomCoordsInD(3));
    	
    	//check if direction is compatible (orthogonal) to center-second point
    	if (!Kernel.isZero(getDirection().getDirectionInD3().dotproduct(radius)))
    		return Double.NaN;
    	
    	radius.calcNorm();
    	
    	return radius.getNorm();

    }

    public String getClassName() {
        return "AlgoCirclePointPointDirection";
    }


    
    /**
     * 
     * @return command string
     */
    final protected String getCommandString(){
    	if (getForAxis() instanceof GeoCoordSys2D)
    		return "CircleWithCenterAThroughBParallelToC";
    	else
    		return "CircleWithCenterAThroughBAxisParallelToC";
    }

    
}

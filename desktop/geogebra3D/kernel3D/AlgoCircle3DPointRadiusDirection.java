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
import geogebra.common.kernel.arithmetic.NumberValue;
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
public class AlgoCircle3DPointRadiusDirection extends AlgoCircle3DPointDirection {

 

    /**
     * 
     * @param cons
     * @param label
     * @param point
     * @param forAxis
     * @param radius
     */
    public AlgoCircle3DPointRadiusDirection(Construction cons, String label, GeoPointND point, NumberValue radius, GeoDirectionND forAxis) {
        super(cons, label, point, (GeoElement) radius, forAxis);
        

    }
    

    

    
    @Override
	protected final double getRadius() {
    	
    	return ((NumberValue) getSecondInput()).getDouble();

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
    		return "CircleWithCenterARadiusBParallelToC";
    	else
    		return "CircleWithCenterAandRadiusBAxisParallelToC";
    }

    
}

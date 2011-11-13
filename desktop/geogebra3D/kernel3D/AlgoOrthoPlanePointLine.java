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
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoPlanePointLine extends AlgoOrthoPlanePoint {



	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param line
	 */
    public AlgoOrthoPlanePointLine(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,label,point,(GeoElement) line);
    }




    protected Coords getNormal(){
    	return getSecondInput().getMainDirection();
    }


}

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
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoPlanePointVector extends AlgoOrthoPlanePoint {



	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param vector
	 */
    public AlgoOrthoPlanePointVector(Construction cons, String label, GeoPointND point, GeoVectorND vector) {
        super(cons,label,point,(GeoElement) vector);
    }




    @Override
	protected Coords getNormal(){
    	return ((GeoVectorND) getSecondInput()).getCoordsInD(3);
    }

	// TODO Consider locusequability


}

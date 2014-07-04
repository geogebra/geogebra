/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * Compute a line through a point and parallel to a vector
 *
 * @author  matthieu
 * @version 
 */
public class AlgoRayPointVector3D extends AlgoLinePointVector3D {

 

    public AlgoRayPointVector3D(Construction cons, String label, GeoPointND point, GeoVectorND v) {
        super(cons,label,point,v);
    }

    @Override
	public Commands getClassName() {
        return Commands.Ray;
    }

    @Override
	protected GeoLine3D createLine(Construction cons){
    	return new GeoRay3D(cons);
    }



}

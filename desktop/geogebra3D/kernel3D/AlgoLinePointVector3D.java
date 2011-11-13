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
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;


/**
 * Compute a line through a point and parallel to a vector
 *
 * @author  matthieu
 * @version 
 */
public class AlgoLinePointVector3D extends AlgoLinePoint {

 

    public AlgoLinePointVector3D(Construction cons, String label, GeoPointND point, GeoVectorND v) {
        super(cons,label,point, (GeoElement) v);
    }

    public String getClassName() {
        return "AlgoLinePointVector";
    }


	protected Coords getDirection() {
		return ((GeoVectorND) getInputParallel()).getCoordsInD(3);
	}



}

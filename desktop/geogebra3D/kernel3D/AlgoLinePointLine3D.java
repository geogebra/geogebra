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
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.kernel.Kernel;
import geogebra.main.Application;


/**
 * Compute a plane through a point and parallel to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoLinePointLine3D extends AlgoLinePoint {

 

    public AlgoLinePointLine3D(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,label,point, (GeoElement) line);
    }

    public String getClassName() {
        return "AlgoLinePointLine";
    }


	protected Coords getDirection() {
		return getInputParallel().getMainDirection();
	}



}

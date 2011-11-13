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
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLinePointLine3D extends AlgoOrtho {

 

    public AlgoOrthoLinePointLine3D(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,label,point, (GeoElement) line);
    }

    public String getClassName() {
        return "AlgoOrthoLinePointLine";
    }


    private GeoLineND getInputLine(){
    	return (GeoLineND) getInputOrtho();
    }

  
    protected final void compute() {
    	
    	GeoLineND line = getInputLine();
    	Coords o = line.getPointInD(3, 0);
    	Coords v1 = line.getPointInD(3, 1).sub(o);
    	Coords o2 = getPoint().getCoordsInD(3);
    	Coords v2 = o2.sub(o);
    	
    	Coords v3 = v1.crossProduct(v2);
    	Coords v = v3.crossProduct(v1);
    	
    	if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION))
    		getLine().setUndefined();
    	else
    		getLine().setCoord(getPoint().getCoordsInD(3), v.normalize());
        
    }

}

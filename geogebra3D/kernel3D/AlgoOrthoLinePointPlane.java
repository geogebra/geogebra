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
 * Compute a line through a point and orthogonal to a plane (or polygon, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLinePointPlane extends AlgoOrtho {

 

    public AlgoOrthoLinePointPlane(Construction cons, String label, GeoPointND point, GeoCoordSys2D cs) {
        super(cons,label,point, (GeoElement) cs);
    }

    public String getClassName() {
        return "AlgoOrthoLinePointPlane";
    }


    private GeoCoordSys2D getCS(){
    	return (GeoCoordSys2D) getInputOrtho();
    }

  
    protected final void compute() {
    	
    	CoordSys coordsys = getCS().getCoordSys();
    	
    	getLine().setCoord(getPoint().getCoordsInD(3), coordsys.getVz());
        
    }

}

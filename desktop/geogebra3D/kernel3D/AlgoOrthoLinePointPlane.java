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
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoPointND;


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

    @Override
	public Algos getClassName() {
        return Algos.AlgoOrthoLinePointPlane;
    }


    private GeoCoordSys2D getCS(){
    	return (GeoCoordSys2D) getInputOrtho();
    }

  
    @Override
	public final void compute() {
    	
    	CoordSys coordsys = getCS().getCoordSys();
    	
    	getLine().setCoord(getPoint().getInhomCoordsInD(3), coordsys.getVz());
        
    }

	// TODO Consider locusequability

}

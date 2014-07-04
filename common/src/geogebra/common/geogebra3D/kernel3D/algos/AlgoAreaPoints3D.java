/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.algos.AlgoAreaPoints;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoAreaPoints3D extends AlgoAreaPoints {

    public AlgoAreaPoints3D(Construction cons, String label, GeoPointND [] P) {       
        super(cons, label, P);
    }  
    
    
    private CoordSys coordSys;
    private GeoPoint[] points2D;
    
    @Override
	protected void createOutput(Construction cons){
    	super.createOutput(cons);
    	coordSys = new CoordSys(2);
    	points2D = new GeoPoint[P.length];
		for (int i = 0; i < P.length; i++) {
			points2D[i] = new GeoPoint(cons, true);
		}
    }
    
    
    
    @Override
	public final void compute() {  
    	if (GeoPolygon3D.updateCoordSys(coordSys, P, points2D)){
    		area.setValue(Math.abs(AlgoPolygon.calcAreaWithSign(points2D))); 
    	}
    	else{
    		area.setUndefined();
    	}
    }
    
}

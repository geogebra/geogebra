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
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoPlaneBisectorPointPoint extends AlgoOrthoPlane {

 
	private GeoPointND point1; // input
    private GeoPointND point2; // input   


    public AlgoOrthoPlaneBisectorPointPoint(Construction cons, String label, GeoPointND point1, GeoPointND point2) {
        super(cons);
        this.point1 = point1;
        this.point2 = point2;
        
        setInputOutput(new GeoElement[] {(GeoElement) point1, (GeoElement) point2}, new GeoElement[] {getPlane()});

        // compute plane 
        compute();
        getPlane().setLabel(label);
    }

    public String getClassName() {
        return "AlgoPlaneBisector";
    }




    protected Coords getNormal(){
    	return point2.getInhomCoordsInD(3).sub(point1.getInhomCoordsInD(3));
    }

    protected Coords getPoint(){
    	return point1.getInhomCoordsInD(3).add(point2.getInhomCoordsInD(3)).mul(0.5);
    }

}

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
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a plane through a point and orthogonal to ...
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoOrthoPlanePoint extends AlgoOrthoPlane {

 
	private GeoPointND point; // input
    private GeoElement secondInput; // input   


    /**
     * 
     * @param cons
     * @param label
     * @param point
     * @param secondInput
     */
    public AlgoOrthoPlanePoint(Construction cons, String label, GeoPointND point, GeoElement secondInput) {
        super(cons);
        this.point = point;
        this.secondInput = secondInput;
        
        setInputOutput(new GeoElement[] {(GeoElement) point, secondInput}, new GeoElement[] {getPlane()});

        // compute plane 
        compute();
        getPlane().setLabel(label);
    }

    public String getClassName() {
        return "AlgoOrthoPlanePoint";
    }





    protected Coords getPoint(){
    	return point.getInhomCoordsInD(3);
    }
    
    /**
     * 
     * @return second input
     */
    protected GeoElement getSecondInput(){
    	return secondInput;
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAPerpendicularToB",point.getLabel(),secondInput.getLabel());

    }
}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoPolarLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoPolarPointND;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  mathieu (adapted from AlgoPolarLine3D)
 */
public class AlgoPolarPoint3D extends AlgoPolarPointND {
	
	private GeoPoint polar2D;
	
	private Coords equation2D;
	
	private double[] polarCoords;
	
	private Coords polarOrigin, polarDirection;
    
    /** Creates new AlgoPolarLine */
    public AlgoPolarPoint3D(Construction cons, String label, GeoConicND c, GeoLineND line) {
        super(cons, label, c, line);
    }   
    
    @Override
	protected GeoPointND newGeoPoint(Construction cons){
    	
    	// we also need this
    	polar2D = new GeoPoint(cons);
    	polarCoords = new double[3];
    	
    	// create 3D line
    	return new GeoPoint3D(cons);
    }
    
    // calc polar line of P relativ to c
    @Override
	public final void compute() {   
    	
    	// check if line lies on conic coord sys
    	equation2D = line.getCartesianEquationVector(c.getCoordSys().getMatrixOrthonormal());
		if (equation2D == null){
			polar.setUndefined();
			return;
		}
    	
		// update polar point in conic coord sys
        c.polarPoint(equation2D, polar2D);
        
        polar2D.getCoords(polarCoords);

        // update 3D polar
   		((GeoPoint3D) polar).setCoords(c.getCoordSys().getPoint(polarCoords[0], polarCoords[1], polarCoords[2]));
    	
    	
        
    }
    
}

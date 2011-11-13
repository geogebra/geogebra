/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.kernelND.AlgoMidpointND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMidpoint extends AlgoMidpointND {
      
    AlgoMidpoint(Construction cons, String label, GeoPoint P, GeoPoint Q) {
    	this(cons, P, Q);
    	getPoint().setLabel(label);
    }
	
    AlgoMidpoint(Construction cons, GeoPoint P, GeoPoint Q) {
        super(cons,P,Q);
    }

	@Override
	protected GeoPointND newGeoPoint(Construction cons) {
		
		return new GeoPoint(cons);
	}
   
    @Override
	protected GeoPoint getPoint() {
        return (GeoPoint) super.getPoint();
    }
    
    @Override
	protected void copyCoords(GeoPointND point){
    	getPoint().setCoords((GeoPoint) point);
    }
    
    @Override
	protected void computeMidCoords(){
    	
    	GeoPoint P = (GeoPoint) getP();
        GeoPoint Q = (GeoPoint) getQ();
        
    	getPoint().setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
    }

}

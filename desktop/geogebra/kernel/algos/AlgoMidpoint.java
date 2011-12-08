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

package geogebra.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.AlgoMidpointND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMidpoint extends AlgoMidpointND {
      
    public AlgoMidpoint(Construction cons, String label, GeoPoint2 P, GeoPoint2 Q) {
    	this(cons, P, Q);
    	getPoint().setLabel(label);
    }
	
    AlgoMidpoint(Construction cons, GeoPoint2 P, GeoPoint2 Q) {
        super(cons,P,Q);
    }

	@Override
	protected GeoPointND newGeoPoint(Construction cons) {
		
		return new GeoPoint2(cons);
	}
   
    @Override
	public GeoPoint2 getPoint() {
        return (GeoPoint2) super.getPoint();
    }
    
    @Override
	protected void copyCoords(GeoPointND point){
    	getPoint().setCoords((GeoPoint2) point);
    }
    
    @Override
	protected void computeMidCoords(){
    	
    	GeoPoint2 P = (GeoPoint2) getP();
        GeoPoint2 Q = (GeoPoint2) getQ();
        
    	getPoint().setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
    }

}

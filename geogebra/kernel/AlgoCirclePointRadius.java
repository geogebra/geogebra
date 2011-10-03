/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author  Markus
 * added TYPE_SEGMENT Michael Borcherds 2008-03-14	
 * @version 
 */
public class AlgoCirclePointRadius extends AlgoSphereNDPointRadius {


	AlgoCirclePointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            NumberValue r) {
        	
            super(cons, label, M, r);
        }
        
    AlgoCirclePointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            GeoSegment segment, boolean dummy) {
        	
            super(cons, label, M, segment, dummy);
        }

        
    public AlgoCirclePointRadius(
            Construction cons,
            GeoPoint M,
            NumberValue r) {
        	
    	super(cons, M, r);
                
        }
    
    
    AlgoCirclePointRadius(
            Construction cons,
            GeoPoint M,
            GeoSegment rgeo, boolean dummy) {
        	
            super(cons,M,rgeo,dummy);
        }
    
    protected GeoQuadricND createSphereND(Construction cons){
    	return new GeoConic(cons);
    }
    

    public String getClassName() {
        return "AlgoCirclePointRadius";
    }

    public int getRelatedModeID() {
    	switch (super.getType()){
    	case AlgoSphereNDPointRadius.TYPE_RADIUS:
    		return EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
    	default:
    		return EuclidianConstants.MODE_COMPASSES;
    	}
    }
        
    public GeoConic getCircle() {
        return (GeoConic) getSphereND();
    }
 



    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("CircleWithCenterAandRadiusB",getM().getLabel(),getRGeo().getLabel());
    }
}

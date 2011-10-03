/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDistancePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistancePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND P, Q; // input
    private GeoNumeric dist; // output       

    AlgoDistancePoints(
        Construction cons,
        String label,
        GeoPointND P,
        GeoPointND Q) {
        super(cons);
        this.P = P;
        this.Q = Q;
        dist = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDistancePoints";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_DISTANCE;
    }
    

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = (GeoElement) Q;

        output = new GeoElement[1];
        output[0] = dist;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getDistance() {
        return dist;
    }
    /*
    GeoPoint getP() {
        return P;
    }
    GeoPoint getQ() {
        return Q;
    }
    */

    // calc length of vector v   
    protected final void compute() {
        dist.setValue(P.distance(Q));
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DistanceOfAandB",P.getLabel(),Q.getLabel());

    }
}

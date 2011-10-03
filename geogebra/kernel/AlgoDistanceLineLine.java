/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDistanceLineLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistanceLineLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g, h; // input
    private GeoNumeric dist; // output       

    AlgoDistanceLineLine(
        Construction cons,
        String label,
        GeoLine g,
        GeoLine h) {
        super(cons);
        this.h = h;
        this.g = g;
        dist = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDistanceLineLine";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_DISTANCE;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = h;
        input[1] = g;

        output = new GeoElement[1];
        output[0] = dist;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getDistance() {
        return dist;
    }
    GeoLine getg() {
        return g;
    }
    GeoLine geth() {
        return h;
    }

    // calc length of vector v   
    protected final void compute() {
        dist.setValue(g.distance(h));
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DistanceOfAandB",g.getLabel(),h.getLabel());
    }
}

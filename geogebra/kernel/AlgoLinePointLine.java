/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointLine.java
 *
 * line through P parallel to l
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
public class AlgoLinePointLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoLine l; // input
    private GeoLine g; // output       

    /** Creates new AlgoLinePointLine */
    AlgoLinePointLine(Construction cons, String label, GeoPoint P, GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        setIncidence();
        g.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence(g);
	}

    
    public String getClassName() {
        return "AlgoLinePointLine";
    }

	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARALLEL;
	}
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return g;
    }
    GeoPoint getP() {
        return P;
    }
    GeoLine getl() {
        return l;
    }

    // calc the line g through P and parallel to l   
    protected final void compute() {
        // homogenous:
        GeoVec3D.cross(P, l.y, -l.x, 0.0, g);
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("LineThroughAParallelToB",P.getLabel(),l.getLabel());

    }
}

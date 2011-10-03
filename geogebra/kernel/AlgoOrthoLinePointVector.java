/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointVector.java
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
public class AlgoOrthoLinePointVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P;  // input
    private GeoVector v; // input
    private GeoLine  g;     // output       
        
    /** Creates new AlgoJoinPoints */
    AlgoOrthoLinePointVector(Construction cons, String label,GeoPoint P,GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;                
        g = new GeoLine(cons); 
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        setIncidence();
        g.setLabel(label);
    }   
    
    private void setIncidence() {
    	P.addIncidence(g);
	}

    
    public String getClassName() {
        return "AlgoOrthoLinePointVector";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;
        
        output = new GeoElement[1];        
        output[0] = g;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoLine getLine() { return g; }
    GeoPoint getP() { return P; }
    GeoVector getv() { return v; }
    
    // line through P normal to v
    protected final void compute() {           
        GeoVec3D.cross(P, -v.y, v.x, 0.0, g);
    }   
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAPerpendicularToB",P.getLabel(),v.getLabel());

    }
}

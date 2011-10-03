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

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoPolarLine extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoPoint P;     // input
    private GeoLine polar;  // output
        
    /** Creates new AlgoPolarLine */
    AlgoPolarLine(Construction cons, String label, GeoConic c, GeoPoint P) {
        super(cons);
        this.P = P;
        this.c = c;                
        polar = new GeoLine(cons);
        
        setInputOutput(); // for AlgoElement
                
        compute();              
        polar.setLabel(label);
    }   
    
    public String getClassName() {
        return "AlgoPolarLine";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLAR_DIAMETER;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = c;
        
        output = new GeoElement[1];
        output[0] = polar;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPoint getPoint() { return P; }    
    GeoConic getConic() { return c; }
    GeoLine getLine() { return polar; }
    
    // calc polar line of P relativ to c
    protected final void compute() {     
        c.polarLine(P, polar);
    }
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("PolarLineOfARelativeToB",P.getLabel(),c.getLabel());

    }
}

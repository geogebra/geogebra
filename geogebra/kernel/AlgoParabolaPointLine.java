/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoParabolaPointLine.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaPointLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint F;  // input    
    private GeoLine l;  // input    
    private GeoConic parabola; // output             
            
    AlgoParabolaPointLine(Construction cons, String label, GeoPoint F, GeoLine l) {
        super(cons);
        this.F = F;
        this.l = l;                
        parabola = new GeoConic(cons); 
        setInputOutput(); // for AlgoElement
                
        compute();      
        parabola.setLabel(label);
    }   
    
    public String getClassName() {
        return "AlgoParabolaPointLine";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_PARABOLA;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = F;
        input[1] = l;
        
        output = new GeoElement[1];        
        output[0] = parabola;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoConic getParabola() { return parabola; }
    GeoPoint getFocus() { return F; }
    GeoLine getLine() { return l; }
    
    // compute parabola with focus F and line l
    protected final void compute() {                           
        parabola.setParabola(F, l);
    }   
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ParabolaWithFocusAandDirectrixB",F.getLabel(),l.getLabel());

    }
}

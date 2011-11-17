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

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaPointLine extends AlgoElement {

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
    
    @Override
	public String getClassName() {
        return "AlgoParabolaPointLine";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_PARABOLA;
    }   
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = F;
        input[1] = l;
        
        super.setOutputLength(1);
        super.setOutput(0, parabola);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoConic getParabola() { return parabola; }
    GeoPoint getFocus() { return F; }
    GeoLine getLine() { return l; }
    
    // compute parabola with focus F and line l
    @Override
	protected final void compute() {                           
        parabola.setParabola(F, l);
    }   
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ParabolaWithFocusAandDirectrixB",F.getLabel(),l.getLabel());

    }
}

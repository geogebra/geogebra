/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;



/**
 * Vector w = v starting at A
 * @author  Markus
 * @version 
 */
public class AlgoTranslateVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A;   // input
    private GeoVec3D v;  // input
    private GeoVector w;     // output        
            
    AlgoTranslateVector(Construction cons, String label,  GeoVec3D v, GeoPoint A) {
        super(cons);
        this.A = A;        
        this.v = v;
        
        // create new Point
        w = new GeoVector(cons);  
        
        try {     
            w.setStartPoint(A);
        } catch (CircularDefinitionException e) {}
        
        setInputOutput();
                
        compute();        
        w.setLabel(label);
    }           
    
    public String getClassName() {
        return "AlgoTranslateVector";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
    }

    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;        
        input[1] = A;        
        
        setOutputLength(1);        
        setOutput(0,w);        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoPoint getPoint() { return A; }
    GeoVec3D getVector() { return v; }
    GeoVector getTranslatedVector() { return w; }
        
    // simply copy v
    protected final void compute() {
        w.setCoords(v);        
    }       
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("TranslationOfAtoB",v.getLabel(),A.getLabel());

    }
}

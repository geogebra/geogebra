/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoVectorVector.java
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
public class AlgoOrthoVectorVector extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVector v; // input
    private GeoVector  n;     // output       
        
    /** Creates new AlgoOrthoVectorVector */
    AlgoOrthoVectorVector(Construction cons, String label, GeoVector v) {        
        super(cons);
        this.v = v;                
        n = new GeoVector(cons); 
        
        GeoPoint possStartPoint = v.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            n.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        n.z = 0.0d;
        compute();      
        n.setLabel(label);
    }   
    
    public String getClassName() {
        return "AlgoOrthoVectorVector";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = v;
        
        output = new GeoElement[1];        
        output[0] = n;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return n; }    
    GeoVector getv() { return v; }
    
    // line through P normal to v
    protected final void compute() {        
        n.x = -v.y;
        n.y = v.x;        
    }   
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("VectorPerpendicularToA",v.getLabel());

    }
}

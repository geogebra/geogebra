/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoVectorLine.java
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
public class AlgoOrthoVectorLine extends AlgoElement {
    
    private GeoLine g; // input
    private GeoVector  n;     // output       
    
    //private GeoPoint startPoint;
        
    /** Creates new AlgoOrthoVectorLine */
    AlgoOrthoVectorLine(Construction cons, String label, GeoLine g) {        
        super(cons);
        this.g = g;                
        n = new GeoVector(cons); 

        GeoPoint possStartPoint = g.getStartPoint();
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
    
    @Override
	public String getClassName() {
        return "AlgoOrthoVectorLine";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }
      
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = g;

        super.setOutputLength(1);
        super.setOutput(0, n);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return n; }    
    GeoLine getg() { return g; }
    
    // line through P normal to v
    @Override
	protected final void compute() {        
        n.x = g.x;
        n.y = g.y;        
    }       

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("VectorPerpendicularToA",g.getLabel());

    }
}

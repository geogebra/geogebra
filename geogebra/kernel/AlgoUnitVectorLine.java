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



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitVectorLine extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoVector  u;     // output       
    
    private double length; 
        
    /** Creates new AlgoOrthoVectorLine */
    AlgoUnitVectorLine(Construction cons, String label,GeoLine g) {        
        super(cons);
        this.g = g;                
        u = new GeoVector(cons); 
       
        GeoPoint possStartPoint = g.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            u.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        u.z = 0.0d;
        compute();      
        u.setLabel(label);
    }   
    
    public String getClassName() {
        return "AlgoUnitVectorLine";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = g;
        
        output = new GeoElement[1];        
        output[0] = u;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return u; }    
    GeoLine getg() { return g; }
    
    // line through P normal to v
    protected final void compute() {        
        length = GeoVec2D.length(g.x, g.y);
        u.x = g.y / length;
        u.y = -g.x / length;        
    }   
    
    final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("UnitVectorOfA",g.getLabel());

    }
}
